package com.storydoc.reports;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.ExtentTest;

import com.storydoc.reports.ExtentTestManager;
import com.storydoc.utils.LogUtils;
import com.storydoc.reports.ScreenshotReporter;

/**
 * Utility class that bridges Log4j logging and ExtentReports reporting 
 * to provide unified logging capabilities across the framework.
 * This class ensures consistent logging in both the console/log files via Log4j
 * and in the HTML test reports via ExtentReports.
 */
public final class TestLogger {
    
    private static final Logger LOGGER = LogManager.getLogger(TestLogger.class);
    
    /**
     * Private constructor to prevent instantiation of utility class
     */
    private TestLogger() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }
    
    /**
     * Logs an informational message to both Log4j and ExtentReports
     * 
     * @param message The message to log
     */
    public static void info(String message) {
        LogUtils.info(message);
        ExtentTestManager.logInfo(message);
    }
    
    /**
     * Logs a debug message to both Log4j and ExtentReports
     * 
     * @param message The message to log
     */
    public static void debug(String message) {
        LogUtils.debug(message);
        // Only add to ExtentReports if test exists
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.debug(message);
        }
    }
    
    /**
     * Logs a warning message to both Log4j and ExtentReports
     * 
     * @param message The message to log
     */
    public static void warn(String message) {
        LogUtils.warn(message);
        ExtentTestManager.logWarning(message);
    }
    
    /**
     * Logs an error message to both Log4j and ExtentReports
     * 
     * @param message The message to log
     */
    public static void error(String message) {
        LogUtils.error(message);
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.log(Status.ERROR, message);
        }
    }
    
    /**
     * Logs an error message with exception details to both Log4j and ExtentReports
     * 
     * @param message The message to log
     * @param t The exception to log
     */
    public static void error(String message, Throwable t) {
        LogUtils.error(message, t);
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.log(Status.ERROR, message);
            test.log(Status.ERROR, t);
        }
    }
    
    /**
     * Logs a successful test step message to both Log4j and ExtentReports
     * 
     * @param message The message to log
     */
    public static void pass(String message) {
        LogUtils.info("PASS: " + message);
        ExtentTestManager.logPass(message);
    }
    
    /**
     * Logs a failed test step message to both Log4j and ExtentReports
     * 
     * @param message The message to log
     */
    public static void fail(String message) {
        LogUtils.error("FAIL: " + message);
        ExtentTestManager.logFail(message);
    }
    
    /**
     * Logs a failed test step message with exception details to both Log4j and ExtentReports
     * 
     * @param message The message to log
     * @param t The exception to log
     */
    public static void fail(String message, Throwable t) {
        LogUtils.error("FAIL: " + message, t);
        ExtentTestManager.logFail(message, t);
        
        // Capture screenshot on failure
        String screenshotPath = ScreenshotReporter.captureScreenshot("Failure_" + System.currentTimeMillis());
        if (screenshotPath != null) {
            ExtentTestManager.logScreenshot(screenshotPath, "Failure Screenshot");
        }
    }
    
    /**
     * Logs a skipped test step message to both Log4j and ExtentReports
     * 
     * @param message The message to log
     */
    public static void skip(String message) {
        LogUtils.warn("SKIP: " + message);
        ExtentTestManager.logSkip(message);
    }
    
    /**
     * Logs code snippets or structured data with syntax highlighting in ExtentReports
     * 
     * @param message The message describing the code
     * @param code The code snippet to log
     */
    public static void logCode(String message, String code) {
        LogUtils.info(message + "\n" + code);
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.info(message);
            test.info(MarkupHelper.createCodeBlock(code));
        }
    }
    
    /**
     * Logs JSON data with pretty formatting in ExtentReports
     * 
     * @param message The message describing the JSON data
     * @param json The JSON string to log
     */
    public static void logJson(String message, String json) {
        LogUtils.info(message + "\n" + json);
        ExtentTest test = ExtentTestManager.getTest();
        if (test != null) {
            test.info(message);
            test.info(MarkupHelper.createCodeBlock(json, "json"));
        }
    }
    
    /**
     * Logs a test step with title and description in ExtentReports
     * 
     * @param stepNumber The step number
     * @param description The step description
     */
    public static void logStep(int stepNumber, String description) {
        String stepInfo = "Step " + stepNumber + ": " + description;
        LogUtils.info(stepInfo);
        ExtentTestManager.logTestStep(stepNumber, description);
    }
    
    /**
     * Captures and logs a screenshot to the test report
     * 
     * @param message The message describing the screenshot
     */
    public static void logScreenshot(String message) {
        LogUtils.info("Capturing screenshot: " + message);
        String screenshotPath = ScreenshotReporter.captureScreenshot("Screenshot_" + System.currentTimeMillis());
        if (screenshotPath != null) {
            ExtentTestManager.logScreenshot(screenshotPath, message);
        }
    }
}