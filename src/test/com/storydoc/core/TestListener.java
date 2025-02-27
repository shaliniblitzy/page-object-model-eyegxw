package com.storydoc.core;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ITestContext;
import java.lang.reflect.Method;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.ExtentTest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.storydoc.reports.ExtentManager;
import com.storydoc.reports.ExtentTestManager;
import com.storydoc.reports.ScreenshotReporter;
import com.storydoc.utils.LogUtils;
import com.storydoc.core.WebDriverManager;
import com.storydoc.constants.MessageConstants;
import com.storydoc.constants.TimeoutConstants;
import com.storydoc.exceptions.FrameworkException;
import com.storydoc.core.RetryAnalyzer;

/**
 * TestNG listener implementation responsible for handling test lifecycle events,
 * integrating with the ExtentReports reporting system, capturing screenshots on failures,
 * and logging test execution details. This component ensures proper test execution
 * monitoring and reporting.
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestMethodName(result);
        LogUtils.info(MessageConstants.LOG_TEST_STARTING.replace("{0}", testName));
        
        ExtentTestManager.startTest(testName);
        ExtentTestManager.logInfo("Test started");
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestMethodName(result);
        LogUtils.info(MessageConstants.LOG_TEST_COMPLETED.replace("{0}", testName));
        
        ExtentTestManager.logPass("Test passed successfully");
        ExtentTestManager.endTest();
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestMethodName(result);
        String errorMessage = formatErrorMessage(result.getThrowable());
        LogUtils.error(MessageConstants.LOG_TEST_FAILED
            .replace("{0}", testName)
            .replace("{1}", errorMessage));
        
        // Take screenshot if WebDriver is available
        try {
            if (WebDriverManager.getDriver() != null) {
                String screenshotPath = ScreenshotReporter.captureScreenshot(testName);
                if (screenshotPath != null) {
                    ScreenshotReporter.attachScreenshotToReport(screenshotPath, "Failure Screenshot");
                }
            }
        } catch (Exception e) {
            LogUtils.error("Failed to capture screenshot: " + e.getMessage(), e);
        }
        
        ExtentTestManager.logFail("Test failed: " + errorMessage);
        ExtentTestManager.getTest().log(Status.FAIL, result.getThrowable());
        ExtentTestManager.endTest();
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestMethodName(result);
        LogUtils.info("Test skipped: " + testName);
        
        // Check if test is being retried
        if (isRetrying(result)) {
            LogUtils.info("Test is being retried: " + testName);
        } else {
            ExtentTestManager.logSkip("Test skipped");
            ExtentTestManager.endTest();
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        String testName = getTestMethodName(result);
        LogUtils.info("Test failed but within success percentage: " + testName);
        
        ExtentTestManager.logWarning("Test failed but within success percentage");
        ExtentTestManager.endTest();
    }
    
    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getSuite().getName();
        LogUtils.info("Test suite started: " + suiteName);
        
        // Ensure ExtentReports is initialized
        ExtentManager.getInstance();
    }
    
    @Override
    public void onFinish(ITestContext context) {
        String suiteName = context.getSuite().getName();
        LogUtils.info("Test suite finished: " + suiteName);
        
        // Generate report
        ExtentManager.getInstance().flush();
        
        // Log test execution summary
        int passedTests = context.getPassedTests().size();
        int failedTests = context.getFailedTests().size();
        int skippedTests = context.getSkippedTests().size();
        
        LogUtils.info("Test execution summary - Passed: " + passedTests + 
                      ", Failed: " + failedTests + 
                      ", Skipped: " + skippedTests);
    }
    
    /**
     * Extracts the test method name from the test result for reporting
     *
     * @param result The test result
     * @return The name of the test method
     */
    private String getTestMethodName(ITestResult result) {
        return result.getMethod().getMethodName();
    }
    
    /**
     * Formats the exception message for better readability in reports
     *
     * @param throwable The exception to format
     * @return A formatted error message
     */
    private String formatErrorMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown error (no exception details available)";
        }
        
        String errorMessage = throwable.getMessage();
        if (errorMessage == null || errorMessage.isEmpty()) {
            errorMessage = throwable.getClass().getName();
        }
        
        // Add first few lines of stack trace for better context
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            StringBuilder formattedError = new StringBuilder(errorMessage);
            formattedError.append(" [");
            formattedError.append(throwable.getClass().getSimpleName());
            formattedError.append(" at ");
            formattedError.append(stackTrace[0].toString());
            formattedError.append("]");
            return formattedError.toString();
        }
        
        return errorMessage;
    }
    
    /**
     * Checks if the test is being retried using RetryAnalyzer
     *
     * @param result The test result
     * @return true if test is being retried, false otherwise
     */
    private boolean isRetrying(ITestResult result) {
        RetryAnalyzer retryAnalyzer = (RetryAnalyzer) result.getMethod().getRetryAnalyzer(result);
        if (retryAnalyzer != null) {
            return retryAnalyzer.getRetryCount() > 0;
        }
        return false;
    }
}