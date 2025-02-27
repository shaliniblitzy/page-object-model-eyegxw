package com.storydoc.reports;

import com.aventstack.extentreports.ExtentReports; // v5.0.9
import com.aventstack.extentreports.reporter.ExtentSparkReporter; // v5.0.9
import com.aventstack.extentreports.reporter.Reporter; // v5.0.9
import com.aventstack.extentreports.reporter.configuration.Theme; // v5.0.9
import com.storydoc.config.ConfigurationManager;
import com.storydoc.constants.TimeoutConstants;
import com.storydoc.utils.FileUtils;
import org.apache.logging.log4j.LogManager; // v2.19.0
import org.apache.logging.log4j.Logger; // v2.19.0

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the configuration settings for test reports using ExtentReports library.
 * Provides a centralized way to configure report appearance, paths, and system information.
 */
public class ReportConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(ReportConfiguration.class);
    private static final String DEFAULT_REPORT_PATH = "test-output/reports";
    private static final String DEFAULT_SCREENSHOT_PATH = "test-output/screenshots";
    private static final String DEFAULT_REPORT_NAME = "Storydoc Selenium Test Report";
    private static final String DEFAULT_DOCUMENT_TITLE = "Storydoc Signup Test Results";
    private static final String DEFAULT_TIMESTAMP_FORMAT = "EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'";
    private static final Theme DEFAULT_THEME = Theme.STANDARD;
    private static final String EXTENT_CONFIG_FILE = "extent-config.xml";

    /**
     * Creates and configures an ExtentSparkReporter instance with appropriate settings
     *
     * @param reportFilePath Path where the report file should be saved
     * @return Configured HTML reporter for ExtentReports
     */
    public static ExtentSparkReporter createSparkReporter(String reportFilePath) {
        // Create a new ExtentSparkReporter with the specified report file path
        ExtentSparkReporter reporter = new ExtentSparkReporter(reportFilePath);
        
        // Set the report theme using getTheme()
        reporter.config().setTheme(getTheme());
        
        // Set document title using getDocumentTitle()
        reporter.config().setDocumentTitle(getDocumentTitle());
        
        // Set report name using getReportName()
        reporter.config().setReportName(getReportName());
        
        // Set timestamp format using getTimestampFormat()
        reporter.config().setTimeStampFormat(getTimestampFormat());
        
        // Apply any XML configuration if available
        loadExtentXMLConfig(reporter);
        
        // Log the reporter configuration details
        LOGGER.info("Configured ExtentSparkReporter with file path: {}", reportFilePath);
        LOGGER.debug("Report configuration: Theme={}, Title={}, Name={}", 
                    getTheme(), getDocumentTitle(), getReportName());
        
        // Return the configured reporter
        return reporter;
    }

    /**
     * Generates a complete file path for the report with timestamp
     *
     * @return The complete file path for the report
     */
    public static String getReportFilePath() {
        // Get the reports directory from configuration or use default
        String reportDir = ConfigurationManager.getInstance().getProperty("report.path");
        if (reportDir == null || reportDir.trim().isEmpty()) {
            reportDir = DEFAULT_REPORT_PATH;
        }
        
        // Ensure the directory exists by calling FileUtils.createDirectory()
        FileUtils.createDirectory(reportDir);
        
        // Generate a filename with timestamp for uniqueness
        String fileName = generateTimestampedFileName("TestReport", "html");
        
        // Combine directory path and filename
        String filePath = reportDir + File.separator + fileName;
        
        // Return the complete file path
        LOGGER.debug("Generated report file path: {}", filePath);
        return filePath;
    }

    /**
     * Generates the base directory path for storing screenshots
     *
     * @return The directory path for storing screenshots
     */
    public static String getScreenshotFilePath() {
        // Get the screenshots directory from configuration or use default
        String screenshotDir = ConfigurationManager.getInstance().getProperty("screenshot.path");
        if (screenshotDir == null || screenshotDir.trim().isEmpty()) {
            screenshotDir = DEFAULT_SCREENSHOT_PATH;
        }
        
        // Ensure the directory exists by calling FileUtils.createDirectory()
        FileUtils.createDirectory(screenshotDir);
        
        // Return the screenshots directory path
        LOGGER.debug("Using screenshot directory: {}", screenshotDir);
        return screenshotDir;
    }

    /**
     * Creates a map of system information to be displayed in the report
     *
     * @return Map of system information key-value pairs
     */
    public static Map<String, String> getSystemInfo() {
        // Create a new HashMap to store system information
        Map<String, String> systemInfo = new HashMap<>();
        
        // Add operating system information
        systemInfo.put("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        // Add Java version information
        systemInfo.put("Java Version", System.getProperty("java.version"));
        
        // Add browser information from ConfigurationManager
        systemInfo.put("Browser", ConfigurationManager.getInstance().getBrowser().toString());
        
        // Add environment information from ConfigurationManager
        systemInfo.put("Environment", ConfigurationManager.getInstance().getEnvironment().toString());
        
        // Add any additional custom information from configuration
        String testRunId = ConfigurationManager.getInstance().getProperty("test.run.id");
        if (testRunId != null && !testRunId.isEmpty()) {
            systemInfo.put("Test Run ID", testRunId);
        }
        
        // Return the populated map
        LOGGER.debug("Created system info map with {} entries", systemInfo.size());
        return systemInfo;
    }

    /**
     * Gets the configured theme for the report
     *
     * @return The theme to be used for the report
     */
    public static Theme getTheme() {
        // Try to get theme name from configuration
        String themeName = ConfigurationManager.getInstance().getProperty("report.theme");
        
        // If found, convert string to Theme enum value
        if (themeName != null && !themeName.trim().isEmpty()) {
            try {
                return Theme.valueOf(themeName.toUpperCase());
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Invalid theme name: {}. Using default theme: {}", themeName, DEFAULT_THEME);
            }
        }
        
        // If not found or invalid, use DEFAULT_THEME
        LOGGER.debug("Using report theme: {}", DEFAULT_THEME);
        
        // Return the theme
        return DEFAULT_THEME;
    }

    /**
     * Gets the configured document title for the report
     *
     * @return The document title to be used for the report
     */
    public static String getDocumentTitle() {
        // Try to get document title from configuration
        String title = ConfigurationManager.getInstance().getProperty("report.title");
        
        // If not found, use DEFAULT_DOCUMENT_TITLE
        if (title == null || title.trim().isEmpty()) {
            title = DEFAULT_DOCUMENT_TITLE;
        }
        
        // Return the document title
        LOGGER.debug("Using report document title: {}", title);
        return title;
    }

    /**
     * Gets the configured report name
     *
     * @return The report name to be used
     */
    public static String getReportName() {
        // Try to get report name from configuration
        String name = ConfigurationManager.getInstance().getProperty("report.name");
        
        // If not found, use DEFAULT_REPORT_NAME
        if (name == null || name.trim().isEmpty()) {
            name = DEFAULT_REPORT_NAME;
        }
        
        // Return the report name
        LOGGER.debug("Using report name: {}", name);
        return name;
    }

    /**
     * Gets the configured timestamp format for the report
     *
     * @return The timestamp format pattern
     */
    public static String getTimestampFormat() {
        // Try to get timestamp format from configuration
        String format = ConfigurationManager.getInstance().getProperty("report.timestamp.format");
        
        // If not found, use DEFAULT_TIMESTAMP_FORMAT
        if (format == null || format.trim().isEmpty()) {
            format = DEFAULT_TIMESTAMP_FORMAT;
        }
        
        // Return the timestamp format
        LOGGER.debug("Using timestamp format: {}", format);
        return format;
    }

    /**
     * Generates a filename with a timestamp to ensure uniqueness
     *
     * @param prefix    Prefix for the filename
     * @param extension File extension without dot
     * @return Generated filename with timestamp
     */
    public static String generateTimestampedFileName(String prefix, String extension) {
        // Create a SimpleDateFormat with pattern for file-friendly timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        
        // Get current date and format as string
        String timestamp = dateFormat.format(new Date());
        
        // Combine prefix, timestamp, and extension
        String fileName = prefix + "_" + timestamp + "." + extension;
        
        // Return the generated filename
        LOGGER.debug("Generated timestamped filename: {}", fileName);
        return fileName;
    }

    /**
     * Ensures that the report and screenshot directories exist
     */
    public static void ensureReportDirectoryExists() {
        // Get report directory path
        String reportDir = ConfigurationManager.getInstance().getProperty("report.path");
        if (reportDir == null || reportDir.trim().isEmpty()) {
            reportDir = DEFAULT_REPORT_PATH;
        }
        
        // Create directory if it doesn't exist
        boolean reportDirCreated = FileUtils.createDirectory(reportDir);
        
        // Get screenshot directory path
        String screenshotDir = ConfigurationManager.getInstance().getProperty("screenshot.path");
        if (screenshotDir == null || screenshotDir.trim().isEmpty()) {
            screenshotDir = DEFAULT_SCREENSHOT_PATH;
        }
        
        // Create directory if it doesn't exist
        boolean screenshotDirCreated = FileUtils.createDirectory(screenshotDir);
        
        // Log directory creation status
        LOGGER.info("Report directory creation status: {}, path: {}", reportDirCreated, reportDir);
        LOGGER.info("Screenshot directory creation status: {}, path: {}", screenshotDirCreated, screenshotDir);
    }

    /**
     * Loads the ExtentReports XML configuration file if it exists
     *
     * @param reporter The reporter to configure
     * @return True if configuration was loaded, false otherwise
     */
    private static boolean loadExtentXMLConfig(ExtentSparkReporter reporter) {
        try {
            // Check if extent-config.xml exists in the classpath
            URL configUrl = Thread.currentThread().getContextClassLoader().getResource(EXTENT_CONFIG_FILE);
            if (configUrl != null) {
                File configFile = new File(configUrl.getFile());
                
                // If found, load XML configuration to the reporter
                if (configFile.exists()) {
                    reporter.loadXMLConfig(configFile);
                    
                    // Log successful configuration load
                    LOGGER.info("Loaded XML configuration from: {}", configFile.getAbsolutePath());
                    
                    // Return true if loaded
                    return true;
                }
            }
            LOGGER.debug("XML configuration file not found: {}", EXTENT_CONFIG_FILE);
        } catch (Exception e) {
            // Catch and log any exceptions
            LOGGER.warn("Failed to load XML configuration. Using default settings.", e);
        }
        
        // Return false if not loaded
        return false;
    }
}