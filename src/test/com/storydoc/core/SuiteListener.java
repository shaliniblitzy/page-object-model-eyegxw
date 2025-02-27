package com.storydoc.core;

import org.testng.ISuiteListener;
import org.testng.ISuite;
import org.testng.ITestNGMethod;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.storydoc.core.WebDriverManager;
import com.storydoc.config.ConfigurationManager;
import com.storydoc.reports.ExtentManager;
import com.storydoc.utils.LogUtils;

/**
 * TestNG listener implementation that handles test suite lifecycle events to provide setup 
 * and teardown operations at the suite level. Responsible for initializing global resources,
 * configuring reporting, and ensuring proper cleanup after suite execution.
 */
public class SuiteListener implements ISuiteListener {
    
    private static final Logger LOGGER = LogManager.getLogger(SuiteListener.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Called when a test suite starts execution
     * 
     * @param suite The test suite that is starting
     */
    @Override
    public void onStart(ISuite suite) {
        // Log test suite start event with suite name and timestamp
        Date startTime = new Date();
        suite.setAttribute("startTime", startTime);
        
        LOGGER.info("========== SUITE STARTED: {} at {} ==========", suite.getName(), DATE_FORMAT.format(startTime));
        
        // Initialize ExtentReports via ExtentManager.getInstance()
        ExtentManager.getInstance();
        
        // Add system information to reports about environment, browser, and JVM
        ExtentManager.getInstance().getExtentReports().setSystemInfo("Environment", ConfigurationManager.getInstance().getEnvironment().toString());
        ExtentManager.getInstance().getExtentReports().setSystemInfo("Browser", ConfigurationManager.getInstance().getBrowser().toString());
        ExtentManager.getInstance().getExtentReports().setSystemInfo("JVM", System.getProperty("java.version"));
        ExtentManager.getInstance().getExtentReports().setSystemInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        
        // Log the total number of test methods in the suite
        int testCount = 0;
        for (ITestNGMethod method : suite.getAllMethods()) {
            testCount++;
        }
        LOGGER.info("Total test methods in suite: {}", testCount);
        
        // Log configuration details like browser type and environment
        logSuiteConfiguration();
    }
    
    /**
     * Called when a test suite finishes execution
     * 
     * @param suite The test suite that has finished
     */
    @Override
    public void onFinish(ISuite suite) {
        Date endTime = new Date();
        
        // Log test suite finish event with suite name and timestamp
        LOGGER.info("========== SUITE FINISHED: {} at {} ==========", 
            suite.getName(), DATE_FORMAT.format(endTime));
        
        // Calculate and log suite execution duration
        String executionTime = calculateSuiteExecutionTime(suite);
        LOGGER.info("Suite execution time: {}", executionTime);
        
        // Generate execution summary (passed, failed, skipped tests)
        String summary = getSummary(suite);
        LOGGER.info("Test execution summary: {}", summary);
        
        // Flush ExtentReports to generate final report
        ExtentManager.getInstance().flush();
        
        // Ensure all WebDriver instances are quit
        WebDriverManager.quitDriver();
        
        // Log report location for easy access
        LOGGER.info("Test report generated successfully. Report location: {}/index.html", 
            ConfigurationManager.getInstance().getReportPath());
    }
    
    /**
     * Calculates the execution time of the test suite
     * 
     * @param suite The test suite
     * @return Formatted suite execution time
     */
    private String calculateSuiteExecutionTime(ISuite suite) {
        Date startTime = (Date) suite.getAttribute("startTime");
        Date endTime = new Date();
        
        if (startTime == null) {
            return "Unknown (start time not recorded)";
        }
        
        long durationInMillis = endTime.getTime() - startTime.getTime();
        
        // Format the duration into a readable string
        long seconds = durationInMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        seconds = seconds % 60;
        minutes = minutes % 60;
        
        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, durationInMillis % 1000);
    }
    
    /**
     * Generates a summary of test results for the suite
     * 
     * @param suite The test suite
     * @return Summary of test execution results
     */
    private String getSummary(ISuite suite) {
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        
        // Get test results from suite
        if (suite.getResults() != null) {
            suite.getResults().values().forEach(suiteResult -> {
                // For each suite result, count passed/failed/skipped tests
                suiteResult.getTestContext().getPassedTests().getAllResults().forEach(result -> passed++);
                suiteResult.getTestContext().getFailedTests().getAllResults().forEach(result -> failed++);
                suiteResult.getTestContext().getSkippedTests().getAllResults().forEach(result -> skipped++);
            });
        }
        
        int total = passed + failed + skipped;
        float successRate = (total > 0) ? ((float) passed / total) * 100 : 0;
        
        return String.format("Total: %d | Passed: %d | Failed: %d | Skipped: %d | Success Rate: %.2f%%", 
            total, passed, failed, skipped, successRate);
    }
    
    /**
     * Logs the configuration details for the test suite
     */
    private void logSuiteConfiguration() {
        LOGGER.info("Test Configuration:");
        LOGGER.info("  Browser: {}", ConfigurationManager.getInstance().getBrowser());
        LOGGER.info("  Environment: {}", ConfigurationManager.getInstance().getEnvironment());
        LOGGER.info("  Base URL: {}", ConfigurationManager.getInstance().getBaseUrl());
        LOGGER.info("  Signup URL: {}", ConfigurationManager.getInstance().getSignupUrl());
        LOGGER.info("  Headless Mode: {}", ConfigurationManager.getInstance().isHeadless());
        LOGGER.info("  Timeout: {} seconds", ConfigurationManager.getInstance().getTimeout());
        LOGGER.info("  Page Load Timeout: {} seconds", ConfigurationManager.getInstance().getPageLoadTimeout());
        LOGGER.info("  Screenshots on Failure: {}", ConfigurationManager.getInstance().isScreenshotsOnFailure());
    }
}