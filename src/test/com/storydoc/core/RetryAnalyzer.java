package com.storydoc.core;

import com.storydoc.constants.TimeoutConstants;
import com.storydoc.utils.LogUtils;
import org.apache.logging.log4j.Logger; // v2.19.0
import org.apache.logging.log4j.LogManager; // v2.19.0
import org.testng.IRetryAnalyzer; // v7.7.1
import org.testng.ITestResult; // v7.7.1

/**
 * Implements TestNG's IRetryAnalyzer interface to provide automatic retry 
 * functionality for failed tests. This helps handle flaky tests and improves
 * test reliability by retrying failed tests a configurable number of times.
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    
    private static final Logger LOGGER = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    
    /**
     * Determines whether a failed test should be retried based on the maximum retry
     * attempts configuration.
     *
     * @param result The result of the test method execution
     * @return true if the test should be retried, false otherwise
     */
    @Override
    public boolean retry(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        
        if (retryCount < TimeoutConstants.MAX_RETRY_ATTEMPTS) {
            retryCount++;
            LogUtils.info("Retrying test: " + testName + " - Retry attempt: " + retryCount);
            
            try {
                // Wait before retrying
                Thread.sleep(TimeoutConstants.RETRY_INTERVAL_MILLIS);
            } catch (InterruptedException e) {
                LogUtils.warn("Thread interrupted during retry wait period: " + e.getMessage());
                Thread.currentThread().interrupt(); // Restore interrupted state
            }
            
            return true; // Retry the test
        }
        
        LogUtils.warn("Maximum retry attempts reached for test: " + testName);
        return false; // Do not retry the test
    }
    
    /**
     * Returns the current retry count for tracking purposes.
     *
     * @return The current retry count
     */
    public int getRetryCount() {
        return retryCount;
    }
    
    /**
     * Resets the retry count to zero, useful for ensuring a clean state
     * between test methods.
     */
    public void resetRetryCount() {
        retryCount = 0;
        LogUtils.info("Retry count has been reset");
    }
}