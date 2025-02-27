package com.storydoc.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.Config.Key;
import org.aeonbits.owner.Config.DefaultValue;
import org.aeonbits.owner.Config.ConverterClass;

/**
 * Interface that defines configuration properties for the Selenium automation framework using the Owner library.
 * Provides type-safe access to configuration values such as browser settings, URLs, timeouts, and reporting options.
 */
@Sources({
    "classpath:config.properties",                  // Default configuration
    "classpath:${environment}.properties",          // Environment-specific configuration
    "system:properties",                            // System properties override
    "system:env"                                    // Environment variables have highest precedence
})
public interface ConfigProperties extends Config {
    
    /**
     * Gets the configured browser type for test execution
     * 
     * @return The browser type to use for testing (CHROME, FIREFOX, EDGE)
     */
    @Key("browser")
    @DefaultValue("CHROME")
    @ConverterClass(BrowserTypeConverter.class)
    BrowserType browser();
    
    /**
     * Determines if the browser should run in headless mode
     * 
     * @return True if headless mode is enabled, false otherwise
     */
    @Key("headless")
    @DefaultValue("false")
    boolean headless();
    
    /**
     * Determines if the browser window should be maximized on startup
     * 
     * @return True if browser should be maximized, false otherwise
     */
    @Key("maximize.browser")
    @DefaultValue("true")
    boolean maximizeBrowser();
    
    /**
     * Gets the configured environment type for test execution
     * 
     * @return The environment type (LOCAL, DEV, STAGING)
     */
    @Key("environment")
    @DefaultValue("STAGING")
    @ConverterClass(EnvironmentTypeConverter.class)
    EnvironmentType environment();
    
    /**
     * Gets the base URL of the application under test
     * 
     * @return The base URL of the application
     */
    @Key("base.url")
    @DefaultValue("https://editor-staging.storydoc.com")
    String baseUrl();
    
    /**
     * Gets the signup URL of the application under test
     * 
     * @return The signup URL of the application
     */
    @Key("signup.url")
    @DefaultValue("https://editor-staging.storydoc.com/sign-up")
    String signupUrl();
    
    /**
     * Gets the default timeout in seconds for element wait operations
     * 
     * @return The timeout value in seconds
     */
    @Key("timeout.seconds")
    @DefaultValue("10")
    int timeoutSeconds();
    
    /**
     * Gets the timeout in seconds for page load operations
     * 
     * @return The page load timeout value in seconds
     */
    @Key("page.load.timeout")
    @DefaultValue("30")
    int pageLoadTimeoutSeconds();
    
    /**
     * Gets the implicit wait time in milliseconds
     * 
     * @return The implicit wait time in milliseconds
     */
    @Key("implicit.wait.millis")
    @DefaultValue("0")
    int implicitWaitMillis();
    
    /**
     * Gets the polling interval in milliseconds for fluent waits
     * 
     * @return The polling interval in milliseconds
     */
    @Key("polling.interval.millis")
    @DefaultValue("500")
    int pollingIntervalMillis();
    
    /**
     * Gets the directory path for storing screenshots
     * 
     * @return The screenshots directory path
     */
    @Key("screenshot.path")
    @DefaultValue("./test-output/screenshots")
    String screenshotPath();
    
    /**
     * Determines if screenshots should be taken on test failures
     * 
     * @return True if screenshots should be taken on failures
     */
    @Key("screenshots.on.failure")
    @DefaultValue("true")
    boolean screenshotsOnFailure();
    
    /**
     * Gets the directory path for storing test reports
     * 
     * @return The reports directory path
     */
    @Key("report.path")
    @DefaultValue("./test-output/reports")
    String reportPath();
    
    /**
     * Gets the title for test reports
     * 
     * @return The report title
     */
    @Key("report.title")
    @DefaultValue("Storydoc Signup Test Report")
    String reportTitle();
    
    /**
     * Gets the name for test reports
     * 
     * @return The report name
     */
    @Key("report.name")
    @DefaultValue("Selenium Test Automation Results")
    String reportName();
    
    /**
     * Gets the number of times to retry failed tests
     * 
     * @return The retry count
     */
    @Key("retry.count")
    @DefaultValue("0")
    int retryCount();
    
    /**
     * Determines if tests should be executed in parallel
     * 
     * @return True if parallel execution is enabled
     */
    @Key("parallel.execution")
    @DefaultValue("false")
    boolean parallelExecution();
    
    /**
     * Gets the number of threads for parallel test execution
     * 
     * @return The thread count
     */
    @Key("thread.count")
    @DefaultValue("1")
    int threadCount();
    
    /**
     * Gets the timeout in seconds for element visibility wait operations
     * 
     * @return The element visibility timeout in seconds
     */
    @Key("element.visible.timeout")
    @DefaultValue("10")
    int elementVisibleTimeout();
    
    /**
     * Gets the timeout in seconds for element clickability wait operations
     * 
     * @return The element clickability timeout in seconds
     */
    @Key("element.clickable.timeout")
    @DefaultValue("10")
    int elementClickableTimeout();
    
    /**
     * Gets the timeout in seconds for element presence wait operations
     * 
     * @return The element presence timeout in seconds
     */
    @Key("element.presence.timeout")
    @DefaultValue("10")
    int elementPresenceTimeout();
}