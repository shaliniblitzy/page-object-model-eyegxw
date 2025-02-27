package com.storydoc.reports;

// Internal imports
import com.storydoc.reports.ExtentManager;
import com.storydoc.utils.LogUtils;
import com.storydoc.exceptions.FrameworkException;

// External imports
import com.aventstack.extentreports.ExtentTest; // v5.0.9
import com.aventstack.extentreports.Status; // v5.0.9
import com.aventstack.extentreports.MediaEntityBuilder; // v5.0.9
import org.apache.logging.log4j.Logger; // v2.19.0
import org.apache.logging.log4j.LogManager; // v2.19.0
import java.util.Map; // JDK 11
import java.util.HashMap; // JDK 11

/**
 * Manages ExtentTest instances for the test automation framework.
 * Provides thread-safe handling of test instances to support parallel test execution,
 * along with utility methods for creating, retrieving, and updating test reports.
 */
public class ExtentTestManager {
    
    private static final Logger LOGGER = LogManager.getLogger(ExtentTestManager.class);
    private static final ThreadLocal<ExtentTest> extentTestMap = new ThreadLocal<>();
    
    /**
     * Creates a new test in the ExtentReports and associates it with the current thread
     *
     * @param testName The name of the test
     * @return The created ExtentTest instance
     */
    public static synchronized ExtentTest startTest(String testName) {
        LOGGER.info("Starting a new test: {}", testName);
        ExtentTest test = ExtentManager.getInstance().getExtentReports().createTest(testName);
        extentTestMap.set(test);
        return test;
    }
    
    /**
     * Creates a new test with description in the ExtentReports and associates it with the current thread
     *
     * @param testName The name of the test
     * @param description The description of the test
     * @return The created ExtentTest instance
     */
    public static synchronized ExtentTest startTest(String testName, String description) {
        LOGGER.info("Starting a new test: {} with description: {}", testName, description);
        ExtentTest test = ExtentManager.getInstance().getExtentReports().createTest(testName, description);
        extentTestMap.set(test);
        return test;
    }
    
    /**
     * Retrieves the ExtentTest instance associated with the current thread
     *
     * @return The current thread's ExtentTest instance
     */
    public static synchronized ExtentTest getTest() {
        ExtentTest test = extentTestMap.get();
        if (test == null) {
            LOGGER.warn("No test instance found for current thread");
        }
        return test;
    }
    
    /**
     * Removes the ExtentTest instance from the current thread after test completion
     */
    public static synchronized void endTest() {
        LOGGER.info("Ending the current test");
        extentTestMap.remove();
    }
    
    /**
     * Logs a passed step in the current test
     *
     * @param details The details of the passed step
     */
    public static void logPass(String details) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.PASS, details);
            LogUtils.info("PASS: " + details);
        }
    }
    
    /**
     * Logs a failed step in the current test
     *
     * @param details The details of the failed step
     */
    public static void logFail(String details) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.FAIL, details);
            LogUtils.error("FAIL: " + details);
        }
    }
    
    /**
     * Logs a failed step with exception details in the current test
     *
     * @param details The details of the failed step
     * @param throwable The exception associated with the failure
     */
    public static void logFail(String details, Throwable throwable) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.FAIL, details);
            test.log(Status.FAIL, throwable);
            LogUtils.error("FAIL: " + details, throwable);
        }
    }
    
    /**
     * Logs a skipped step in the current test
     *
     * @param details The details of the skipped step
     */
    public static void logSkip(String details) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.SKIP, details);
            LogUtils.info("SKIP: " + details);
        }
    }
    
    /**
     * Logs an informational message in the current test
     *
     * @param details The details of the information
     */
    public static void logInfo(String details) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.INFO, details);
            LogUtils.info("INFO: " + details);
        }
    }
    
    /**
     * Logs a warning message in the current test
     *
     * @param details The warning details
     */
    public static void logWarning(String details) {
        ExtentTest test = getTest();
        if (test != null) {
            test.log(Status.WARNING, details);
            LogUtils.info("WARNING: " + details);
        }
    }
    
    /**
     * Attaches a screenshot to the current test report
     *
     * @param screenshotPath The path to the screenshot file
     * @param title The title/caption for the screenshot
     */
    public static void logScreenshot(String screenshotPath, String title) {
        ExtentTest test = getTest();
        if (test != null && screenshotPath != null) {
            try {
                test.info(title, MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                LogUtils.info("Screenshot attached: " + title + " at " + screenshotPath);
            } catch (Exception e) {
                LogUtils.error("Failed to attach screenshot: " + screenshotPath, e);
                test.log(Status.WARNING, "Failed to attach screenshot: " + screenshotPath + ". Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Assigns a category to the current test for better organization in reports
     *
     * @param category The category to assign
     */
    public static void assignCategory(String category) {
        ExtentTest test = getTest();
        if (test != null) {
            test.assignCategory(category);
            LogUtils.info("Assigned category: " + category);
        }
    }
    
    /**
     * Assigns an author to the current test for attribution in reports
     *
     * @param author The author to assign
     */
    public static void assignAuthor(String author) {
        ExtentTest test = getTest();
        if (test != null) {
            test.assignAuthor(author);
            LogUtils.info("Assigned author: " + author);
        }
    }
    
    /**
     * Logs a numbered test step with description in the current test
     *
     * @param stepNumber The step number
     * @param description The step description
     */
    public static void logTestStep(int stepNumber, String description) {
        String step = "Step " + stepNumber + ": " + description;
        logInfo(step);
        LogUtils.info("Executing " + step);
    }
}