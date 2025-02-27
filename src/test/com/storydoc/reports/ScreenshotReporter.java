package com.storydoc.reports;

import com.storydoc.core.WebDriverManager;
import com.storydoc.utils.FileUtils;
import com.storydoc.utils.LogUtils;
import com.storydoc.exceptions.FrameworkException;
import com.storydoc.reports.ExtentTestManager;

import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Utility class responsible for capturing screenshots during test execution, especially on test failures.
 * Manages screenshot file creation, naming, and facilitates attachment to ExtentReports
 * to provide visual evidence of test execution state.
 */
public class ScreenshotReporter {
    
    private static final Logger LOGGER = LogManager.getLogger(ScreenshotReporter.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final String SCREENSHOT_FILE_EXTENSION = ".png";
    
    /**
     * Captures a screenshot of the current browser state
     *
     * @param testName Name of the test for which screenshot is being captured
     * @return Path to the saved screenshot file, or null if capture failed
     */
    public static String captureScreenshot(String testName) {
        LOGGER.debug("Capturing screenshot for test: {}", testName);
        
        try {
            // Get the WebDriver instance from WebDriverManager
            if (WebDriverManager.getDriver() == null) {
                LOGGER.error("WebDriver instance is null, cannot take screenshot");
                return null;
            }
            
            // Check if WebDriver supports screenshots
            if (!(WebDriverManager.getDriver() instanceof TakesScreenshot)) {
                LOGGER.error("WebDriver does not support screenshots");
                return null;
            }
            
            // Generate unique filename with testName and timestamp
            String screenshotFileName = generateScreenshotFileName(testName);
            String screenshotDirectory = FileUtils.getScreenshotsDirectory();
            String screenshotPath = screenshotDirectory + File.separator + screenshotFileName;
            
            // Take screenshot as a file
            File screenshotFile = ((TakesScreenshot) WebDriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            
            // Copy the file to the desired location using our custom FileUtils
            FileUtils.copyFile(screenshotFile, new File(screenshotPath));
            
            LogUtils.info(ScreenshotReporter.class, "Screenshot captured successfully: " + screenshotPath);
            return screenshotPath;
            
        } catch (Exception e) {
            LogUtils.error(ScreenshotReporter.class, "Failed to capture screenshot for test: " + testName, e);
            return null;
        }
    }
    
    /**
     * Captures a screenshot of the current browser state as a byte array
     *
     * @return Screenshot as byte array, or null if capture failed
     */
    public static byte[] captureScreenshotAsBytes() {
        LOGGER.debug("Capturing screenshot as bytes");
        
        try {
            // Check if WebDriver is available
            if (WebDriverManager.getDriver() == null) {
                LOGGER.error("WebDriver instance is null, cannot take screenshot");
                return null;
            }
            
            // Check if WebDriver supports screenshots
            if (!(WebDriverManager.getDriver() instanceof TakesScreenshot)) {
                LOGGER.error("WebDriver does not support screenshots");
                return null;
            }
            
            // Take screenshot as bytes
            return ((TakesScreenshot) WebDriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            LogUtils.error(ScreenshotReporter.class, "Failed to capture screenshot as bytes", e);
            return null;
        }
    }
    
    /**
     * Captures a screenshot of the current browser state as a Base64 encoded string
     *
     * @return Base64 encoded screenshot, or null if capture failed
     */
    public static String captureScreenshotAsBase64() {
        LOGGER.debug("Capturing screenshot as Base64 string");
        
        try {
            // Check if WebDriver is available
            if (WebDriverManager.getDriver() == null) {
                LOGGER.error("WebDriver instance is null, cannot take screenshot");
                return null;
            }
            
            // Check if WebDriver supports screenshots
            if (!(WebDriverManager.getDriver() instanceof TakesScreenshot)) {
                LOGGER.error("WebDriver does not support screenshots");
                return null;
            }
            
            // Take screenshot as Base64 string
            return ((TakesScreenshot) WebDriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            LogUtils.error(ScreenshotReporter.class, "Failed to capture screenshot as Base64 string", e);
            return null;
        }
    }
    
    /**
     * Generates a unique file name for a screenshot based on test name and timestamp
     *
     * @param testName Name of the test
     * @return Generated file name with timestamp and extension
     */
    private static String generateScreenshotFileName(String testName) {
        String cleanTestName = cleanFileName(testName);
        String timestamp = DATE_FORMAT.format(new Date());
        return cleanTestName + "_" + timestamp + SCREENSHOT_FILE_EXTENSION;
    }
    
    /**
     * Removes invalid file characters from a string to create a valid file name
     *
     * @param name String to clean
     * @return Cleaned file name
     */
    private static String cleanFileName(String name) {
        if (name == null || name.isEmpty()) {
            return "screenshot";
        }
        
        // Replace invalid file name characters with underscore
        return name.replaceAll("[^a-zA-Z0-9._-]", "_").trim();
    }
    
    /**
     * Attaches a screenshot to the current test report
     *
     * @param screenshotPath Path to the screenshot file
     * @param title Title for the screenshot in the report
     * @return True if screenshot was attached successfully, false otherwise
     */
    public static boolean attachScreenshotToReport(String screenshotPath, String title) {
        if (screenshotPath == null || screenshotPath.isEmpty()) {
            LogUtils.warn(ScreenshotReporter.class, "Cannot attach screenshot to report: Invalid screenshot path");
            return false;
        }
        
        try {
            ExtentTestManager.logScreenshot(screenshotPath, title);
            LogUtils.info(ScreenshotReporter.class, "Screenshot attached to report: " + title);
            return true;
        } catch (Exception e) {
            LogUtils.error(ScreenshotReporter.class, "Failed to attach screenshot to report", e);
            return false;
        }
    }
    
    /**
     * Captures and attaches a screenshot directly to the report as Base64
     *
     * @param title Title for the screenshot in the report
     * @return True if screenshot was attached successfully, false otherwise
     */
    public static boolean attachScreenshotToReportAsBase64(String title) {
        String base64Screenshot = captureScreenshotAsBase64();
        
        if (base64Screenshot == null) {
            LogUtils.warn(ScreenshotReporter.class, "Cannot attach screenshot to report: Failed to capture Base64 screenshot");
            return false;
        }
        
        try {
            ExtentTestManager.logInfo("Screenshot: " + title);
            return true;
        } catch (Exception e) {
            LogUtils.error(ScreenshotReporter.class, "Failed to attach Base64 screenshot to report", e);
            return false;
        }
    }
}