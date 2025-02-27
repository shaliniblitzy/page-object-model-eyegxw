package com.storydoc.config;

import org.aeonbits.owner.ConfigFactory;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.storydoc.constants.TimeoutConstants;
import com.storydoc.exceptions.ConfigurationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton class responsible for loading and managing test configuration settings.
 * Provides centralized access to browser options, environment settings, URLs, timeouts,
 * and other configuration parameters required for test execution.
 */
public class ConfigurationManager {
    
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private final ConfigProperties properties;
    
    /**
     * Private constructor that initializes the configuration properties.
     * Loads the appropriate property file based on environment setting.
     */
    private ConfigurationManager() {
        LOGGER.info("Initializing Configuration Manager");
        
        try {
            properties = ConfigFactory.create(ConfigProperties.class);
            LOGGER.info("Configuration loaded successfully for environment: {}", properties.environment());
        } catch (Exception e) {
            LOGGER.error("Failed to load configuration", e);
            throw new ConfigurationException("Failed to initialize configuration", e);
        }
    }
    
    /**
     * Gets the singleton instance of the ConfigurationManager, creating it if it doesn't exist.
     *
     * @return The singleton instance of ConfigurationManager
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Gets the ConfigProperties interface for accessing typed configuration values.
     *
     * @return The configuration properties interface
     */
    public ConfigProperties getProperties() {
        return properties;
    }
    
    /**
     * Gets the configured browser type for test execution.
     *
     * @return The browser type (CHROME, FIREFOX, EDGE)
     */
    public BrowserType getBrowser() {
        return properties.browser();
    }
    
    /**
     * Gets the configured environment type for test execution.
     *
     * @return The environment type (LOCAL, DEV, STAGING)
     */
    public EnvironmentType getEnvironment() {
        return properties.environment();
    }
    
    /**
     * Gets the base URL of the application under test.
     *
     * @return The base URL of the application
     */
    public String getBaseUrl() {
        return properties.baseUrl();
    }
    
    /**
     * Gets the signup URL of the application under test.
     *
     * @return The signup URL of the application
     */
    public String getSignupUrl() {
        return properties.signupUrl();
    }
    
    /**
     * Gets the default timeout in seconds for element wait operations.
     *
     * @return The timeout value in seconds
     */
    public int getTimeout() {
        return properties.timeoutSeconds();
    }
    
    /**
     * Gets the timeout in seconds for page load operations.
     *
     * @return The page load timeout value in seconds
     */
    public int getPageLoadTimeout() {
        return properties.pageLoadTimeoutSeconds();
    }
    
    /**
     * Determines if the browser should run in headless mode.
     *
     * @return True if headless mode is enabled, false otherwise
     */
    public boolean isHeadless() {
        return properties.headless();
    }
    
    /**
     * Determines if the browser window should be maximized on startup.
     *
     * @return True if browser should be maximized, false otherwise
     */
    public boolean shouldMaximizeBrowser() {
        return properties.maximizeBrowser();
    }
    
    /**
     * Gets the implicit wait time in milliseconds.
     *
     * @return The implicit wait time in milliseconds
     */
    public int getImplicitWaitMillis() {
        return properties.implicitWaitMillis();
    }
    
    /**
     * Gets the polling interval in milliseconds for fluent waits.
     *
     * @return The polling interval in milliseconds
     */
    public int getPollingIntervalMillis() {
        return properties.pollingIntervalMillis();
    }
    
    /**
     * Gets the directory path for storing screenshots.
     *
     * @return The screenshots directory path
     */
    public String getScreenshotPath() {
        return properties.screenshotPath();
    }
    
    /**
     * Determines if screenshots should be taken on test failures.
     *
     * @return True if screenshots should be taken on failures
     */
    public boolean isScreenshotsOnFailure() {
        return properties.screenshotsOnFailure();
    }
    
    /**
     * Gets the directory path for storing test reports.
     *
     * @return The reports directory path
     */
    public String getReportPath() {
        return properties.reportPath();
    }
    
    /**
     * Gets the title for test reports.
     *
     * @return The report title
     */
    public String getReportTitle() {
        return properties.reportTitle();
    }
    
    /**
     * Gets the name for test reports.
     *
     * @return The report name
     */
    public String getReportName() {
        return properties.reportName();
    }
    
    /**
     * Gets the number of times to retry failed tests.
     *
     * @return The retry count
     */
    public int getRetryCount() {
        return properties.retryCount();
    }
    
    /**
     * Determines if tests should be executed in parallel.
     *
     * @return True if parallel execution is enabled
     */
    public boolean isParallelExecution() {
        return properties.parallelExecution();
    }
    
    /**
     * Gets the number of threads for parallel test execution.
     *
     * @return The thread count
     */
    public int getThreadCount() {
        return properties.threadCount();
    }
    
    /**
     * Gets the timeout in seconds for element visibility wait operations.
     *
     * @return The element visibility timeout in seconds
     */
    public int getElementVisibleTimeout() {
        return properties.elementVisibleTimeout();
    }
    
    /**
     * Gets the timeout in seconds for element clickability wait operations.
     *
     * @return The element clickability timeout in seconds
     */
    public int getElementClickableTimeout() {
        return properties.elementClickableTimeout();
    }
    
    /**
     * Gets the timeout in seconds for element presence wait operations.
     *
     * @return The element presence timeout in seconds
     */
    public int getElementPresenceTimeout() {
        return properties.elementPresenceTimeout();
    }
    
    /**
     * Gets a specific property value by its key.
     * Provides access to properties not covered by specific getter methods.
     *
     * @param key The property key
     * @return The property value, or null if not found
     */
    public String getProperty(String key) {
        LOGGER.debug("Retrieving property for key: {}", key);
        
        try {
            // Check for specific getters based on key
            switch (key) {
                case "browser":
                    return getBrowser().toString();
                case "environment":
                    return getEnvironment().toString();
                case "base.url":
                    return getBaseUrl();
                case "signup.url":
                    return getSignupUrl();
                case "timeout.seconds":
                    return String.valueOf(getTimeout());
                case "page.load.timeout":
                    return String.valueOf(getPageLoadTimeout());
                case "headless":
                    return String.valueOf(isHeadless());
                case "maximize.browser":
                    return String.valueOf(shouldMaximizeBrowser());
                case "implicit.wait.millis":
                    return String.valueOf(getImplicitWaitMillis());
                case "polling.interval.millis":
                    return String.valueOf(getPollingIntervalMillis());
                case "screenshot.path":
                    return getScreenshotPath();
                case "screenshots.on.failure":
                    return String.valueOf(isScreenshotsOnFailure());
                case "report.path":
                    return getReportPath();
                case "report.title":
                    return getReportTitle();
                case "report.name":
                    return getReportName();
                case "retry.count":
                    return String.valueOf(getRetryCount());
                case "parallel.execution":
                    return String.valueOf(isParallelExecution());
                case "thread.count":
                    return String.valueOf(getThreadCount());
                case "element.visible.timeout":
                    return String.valueOf(getElementVisibleTimeout());
                case "element.clickable.timeout":
                    return String.valueOf(getElementClickableTimeout());
                case "element.presence.timeout":
                    return String.valueOf(getElementPresenceTimeout());
                default:
                    // Try to get property from system properties
                    String value = System.getProperty(key);
                    if (value == null) {
                        LOGGER.warn("No specific getter for property key: {}", key);
                    }
                    return value;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to get property for key: {}", key, e);
            return null;
        }
    }
}