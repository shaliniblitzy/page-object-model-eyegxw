package com.storydoc.utils;

import com.storydoc.core.WebDriverManager;
import com.storydoc.exceptions.FrameworkException;
import com.storydoc.utils.LogUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for capturing and managing screenshots during test execution using Selenium WebDriver.
 * Provides methods for taking full browser screenshots, converting to Base64, and cleanup functions.
 */
public class ScreenshotUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "src/test/screenshots";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Captures a screenshot of the current browser window
     *
     * @param testName Name of the test to include in the screenshot filename
     * @return Path to the saved screenshot file
     * @throws FrameworkException if screenshot capture fails
     */
    public static String captureScreenshot(String testName) {
        LogUtils.debug("Capturing screenshot for test: " + testName);
        
        try {
            WebDriver driver = WebDriverManager.getDriver();
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            
            // Take screenshot as file
            File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
            
            // Generate unique filename
            String filename = generateScreenshotFilename(testName);
            
            // Ensure directory exists
            String directory = getScreenshotDirectory();
            
            // Create destination file path
            String destinationPath = directory + File.separator + filename;
            File destination = new File(destinationPath);
            
            // Copy the screenshot to destination
            FileUtils.copyFile(screenshot, destination);
            
            LogUtils.info("Screenshot captured: " + destinationPath);
            
            return destinationPath;
        } catch (IOException e) {
            LogUtils.error("Failed to capture screenshot: " + e.getMessage(), e);
            throw new FrameworkException("Failed to capture screenshot: " + e.getMessage(), e);
        } catch (Exception e) {
            LogUtils.error("Unexpected error while capturing screenshot: " + e.getMessage(), e);
            throw new FrameworkException("Unexpected error while capturing screenshot: " + e.getMessage(), e);
        }
    }
    
    /**
     * Captures a screenshot and returns it as a Base64 encoded string
     *
     * @return Base64 encoded screenshot string
     * @throws FrameworkException if screenshot capture fails
     */
    public static String captureScreenshotAsBase64() {
        LogUtils.debug("Capturing screenshot as Base64");
        
        try {
            WebDriver driver = WebDriverManager.getDriver();
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            
            // Take screenshot as Base64 string
            String base64Screenshot = screenshotDriver.getScreenshotAs(OutputType.BASE64);
            
            LogUtils.debug("Base64 screenshot captured successfully");
            
            return base64Screenshot;
        } catch (Exception e) {
            LogUtils.error("Failed to capture Base64 screenshot: " + e.getMessage(), e);
            throw new FrameworkException("Failed to capture Base64 screenshot: " + e.getMessage(), e);
        }
    }
    
    /**
     * Captures a screenshot specifically for a test result
     *
     * @param testName Name of the test
     * @param isFailure Whether the test has failed
     * @return Path to the saved screenshot file
     * @throws FrameworkException if screenshot capture fails
     */
    public static String captureScreenshotForTest(String testName, boolean isFailure) {
        String prefix = isFailure ? "FAIL_" : "PASS_";
        return captureScreenshot(prefix + testName);
    }
    
    /**
     * Gets the directory where screenshots are stored, creating it if it doesn't exist
     *
     * @return Path to the screenshot directory
     * @throws FrameworkException if directory creation fails
     */
    public static String getScreenshotDirectory() {
        File directory = new File(SCREENSHOT_DIR);
        
        if (!directory.exists()) {
            LogUtils.debug("Creating screenshot directory: " + SCREENSHOT_DIR);
            boolean created = directory.mkdirs();
            
            if (!created) {
                LogUtils.error("Failed to create screenshot directory: " + SCREENSHOT_DIR);
                throw new FrameworkException("Failed to create screenshot directory: " + SCREENSHOT_DIR);
            }
        }
        
        return directory.getAbsolutePath();
    }
    
    /**
     * Generates a unique filename for a screenshot based on test name and timestamp
     *
     * @param baseName Base name for the screenshot (usually test name)
     * @return Unique screenshot filename
     */
    public static String generateScreenshotFilename(String baseName) {
        // Clean base name by replacing invalid characters with underscore
        String cleanBaseName = baseName.replaceAll("[^a-zA-Z0-9.-]", "_");
        
        // Get current timestamp
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DATE_TIME_FORMATTER);
        
        // Combine name and timestamp
        return cleanBaseName + "_" + timestamp + ".png";
    }
    
    /**
     * Removes screenshots older than a specified number of days
     *
     * @param daysToKeep Number of days to keep screenshots (older ones will be deleted)
     * @return Number of files deleted
     */
    public static int cleanupOldScreenshots(int daysToKeep) {
        LogUtils.debug("Cleaning up screenshots older than " + daysToKeep + " days");
        
        if (daysToKeep < 0) {
            LogUtils.warn("Invalid daysToKeep parameter: " + daysToKeep + ". Must be >= 0.");
            return 0;
        }
        
        try {
            // Calculate cutoff date
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            
            // Get screenshot directory
            String directory = getScreenshotDirectory();
            File screenshotDir = new File(directory);
            
            // Get all screenshot files
            File[] screenshotFiles = screenshotDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
            
            if (screenshotFiles == null || screenshotFiles.length == 0) {
                LogUtils.debug("No screenshot files found to clean up");
                return 0;
            }
            
            int deletedCount = 0;
            
            // Check each file's last modified date
            for (File file : screenshotFiles) {
                // Get last modified time
                LocalDateTime fileDate = LocalDateTime.ofInstant(
                    Files.getLastModifiedTime(file.toPath()).toInstant(),
                    java.time.ZoneId.systemDefault()
                );
                
                // If file is older than cutoff date, delete it
                if (fileDate.isBefore(cutoffDate)) {
                    LogUtils.debug("Deleting old screenshot: " + file.getName());
                    if (file.delete()) {
                        deletedCount++;
                    } else {
                        LogUtils.warn("Failed to delete file: " + file.getAbsolutePath());
                    }
                }
            }
            
            LogUtils.info("Deleted " + deletedCount + " old screenshot files");
            return deletedCount;
        } catch (Exception e) {
            LogUtils.error("Error cleaning up old screenshots: " + e.getMessage(), e);
            return 0;
        }
    }
}