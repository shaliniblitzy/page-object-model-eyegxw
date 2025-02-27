package com.storydoc.reports;

// Internal imports
import com.storydoc.reports.ReportConfiguration;
import com.storydoc.utils.FileUtils;
import com.storydoc.config.ConfigurationManager;
import com.storydoc.exceptions.FrameworkException;
import com.storydoc.constants.TimeoutConstants;

// External imports
import com.aventstack.extentreports.ExtentReports; // v5.0.9
import com.aventstack.extentreports.ExtentTest; // v5.0.9
import com.aventstack.extentreports.reporter.ExtentSparkReporter; // v5.0.9
import org.apache.logging.log4j.Logger; // v2.19.0
import org.apache.logging.log4j.LogManager; // v2.19.0
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Singleton class responsible for managing the ExtentReports instance for test reporting.
 * Handles initialization, configuration, and access to the reporting engine throughout the test framework.
 */
public class ExtentManager {
    
    private static final Logger LOGGER = LogManager.getLogger(ExtentManager.class);
    private static ExtentManager instance;
    private ExtentReports extentReports;
    
    /**
     * Private constructor that initializes the ExtentReports instance
     */
    private ExtentManager() {
        LOGGER.info("Initializing ExtentManager");
        createReportInstance();
    }
    
    /**
     * Gets the singleton instance of the ExtentManager
     *
     * @return The singleton instance of ExtentManager
     */
    public static synchronized ExtentManager getInstance() {
        if (instance == null) {
            synchronized (ExtentManager.class) {
                if (instance == null) {
                    instance = new ExtentManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Creates and initializes a new ExtentReports instance
     */
    private void createReportInstance() {
        try {
            // Initialize a new ExtentReports instance
            extentReports = new ExtentReports();
            
            // Get the report file path
            String reportFilePath = ReportConfiguration.getReportFilePath();
            
            // Create necessary directories for the report
            FileUtils.createDirectory(FileUtils.getReportsDirectory());
            
            // Create and configure the reporter
            ExtentSparkReporter sparkReporter = ReportConfiguration.createSparkReporter(reportFilePath);
            
            // Attach reporter to the ExtentReports instance
            extentReports.attachReporter(sparkReporter);
            
            // Set system information in the report
            Map<String, String> systemInfo = ReportConfiguration.getSystemInfo();
            for (Map.Entry<String, String> entry : systemInfo.entrySet()) {
                extentReports.setSystemInfo(entry.getKey(), entry.getValue());
            }
            
            LOGGER.info("Successfully created ExtentReports instance with reporter at: {}", reportFilePath);
        } catch (Exception e) {
            String errorMessage = "Failed to initialize ExtentReports";
            LOGGER.error(errorMessage, e);
            throw new FrameworkException(errorMessage, e);
        }
    }
    
    /**
     * Gets the ExtentReports instance for creating and managing tests
     *
     * @return The ExtentReports instance
     */
    public synchronized ExtentReports getExtentReports() {
        return extentReports;
    }
    
    /**
     * Creates a new test node in the report
     *
     * @param testName The name of the test
     * @return The created test instance
     */
    public ExtentTest createTest(String testName) {
        LOGGER.debug("Creating test: {}", testName);
        return extentReports.createTest(testName);
    }
    
    /**
     * Creates a new test node in the report with a description
     *
     * @param testName The name of the test
     * @param description The description of the test
     * @return The created test instance
     */
    public ExtentTest createTest(String testName, String description) {
        LOGGER.debug("Creating test: {} with description: {}", testName, description);
        return extentReports.createTest(testName, description);
    }
    
    /**
     * Writes all test information to the report file and finalizes the report
     */
    public synchronized void flush() {
        LOGGER.info("Flushing ExtentReports to write results to disk");
        extentReports.flush();
        LOGGER.info("ExtentReports flushed successfully");
    }
    
    /**
     * Resets the ExtentReports instance and creates a new one
     */
    public synchronized void reset() {
        LOGGER.info("Resetting ExtentReports instance");
        
        // Flush any existing reports to ensure nothing is lost
        if (extentReports != null) {
            flush();
        }
        
        // Set extentReports to null and create a new instance
        extentReports = null;
        createReportInstance();
        
        LOGGER.info("ExtentReports instance reset successfully");
    }
}