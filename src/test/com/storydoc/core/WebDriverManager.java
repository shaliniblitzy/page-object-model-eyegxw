package com.storydoc.core;

import com.storydoc.config.BrowserType;
import com.storydoc.config.ConfigurationManager;
import com.storydoc.core.DriverFactory;
import com.storydoc.exceptions.FrameworkException;
import com.storydoc.constants.TimeoutConstants;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.OutputType;
import java.lang.ThreadLocal;
import java.time.Duration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Singleton class responsible for managing WebDriver instances throughout the test execution lifecycle.
 * Provides thread-safe access to browser instances, handles initialization and cleanup, 
 * and ensures consistent WebDriver configuration across tests.
 * Supports parallel test execution through ThreadLocal implementation.
 */
public class WebDriverManager {
    
    private static final Logger LOGGER = LogManager.getLogger(WebDriverManager.class);
    private static ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();
    
    /**
     * Gets the WebDriver instance for the current thread, initializing it if necessary.
     *
     * @return Current thread's WebDriver instance
     */
    public static WebDriver getDriver() {
        LOGGER.debug("Attempting to get WebDriver instance");
        
        if (DRIVER_THREAD_LOCAL.get() == null) {
            LOGGER.debug("WebDriver instance not found, initializing new instance");
            initDriver();
        }
        
        return DRIVER_THREAD_LOCAL.get();
    }
    
    /**
     * Initializes a new WebDriver instance for the current thread.
     *
     * @return Newly created WebDriver instance
     */
    public static WebDriver initDriver() {
        LOGGER.info("Initializing WebDriver instance");
        
        WebDriver driver = DriverFactory.createDriver();
        DRIVER_THREAD_LOCAL.set(driver);
        configureDriver(driver);
        
        return driver;
    }
    
    /**
     * Closes the WebDriver instance for the current thread and cleans up resources.
     */
    public static void quitDriver() {
        LOGGER.info("Quitting WebDriver instance");
        
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        
        if (driver != null) {
            try {
                driver.quit();
            } catch (WebDriverException e) {
                LOGGER.warn("Exception occurred while quitting WebDriver", e);
            } finally {
                DRIVER_THREAD_LOCAL.remove();
            }
        }
        
        LOGGER.debug("WebDriver instance has been quit and removed from ThreadLocal");
    }
    
    /**
     * Configures standard WebDriver settings for the browser instance.
     *
     * @param driver WebDriver to configure
     */
    private static void configureDriver(WebDriver driver) {
        // Configure timeouts
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(
                ConfigurationManager.getInstance().getPageLoadTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(
                TimeoutConstants.SCRIPT_TIMEOUT_SECONDS));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(
                TimeoutConstants.IMPLICITLY_WAIT_TIMEOUT_SECONDS));
        
        // Maximize browser window if configured
        if (ConfigurationManager.getInstance().shouldMaximizeBrowser()) {
            driver.manage().window().maximize();
        }
        
        LOGGER.debug("WebDriver configuration complete");
    }
    
    /**
     * Captures a screenshot of the current browser state.
     *
     * @param fileName Name of the file to save the screenshot as
     * @return Screenshot as byte array or null if capture failed
     */
    public static byte[] takeScreenshot(String fileName) {
        LOGGER.debug("Taking screenshot: {}", fileName);
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        
        if (driver instanceof TakesScreenshot) {
            try {
                return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            } catch (Exception e) {
                LOGGER.error("Failed to take screenshot", e);
            }
        }
        
        return null;
    }
    
    /**
     * Executes JavaScript in the browser context.
     *
     * @param script JavaScript to execute
     * @param args Arguments to pass to the script
     * @return Result of JavaScript execution or null if failed
     */
    public static Object executeJavaScript(String script, Object... args) {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        
        if (driver instanceof JavascriptExecutor) {
            try {
                return ((JavascriptExecutor) driver).executeScript(script, args);
            } catch (Exception e) {
                LOGGER.error("Failed to execute JavaScript: {}", script, e);
            }
        }
        
        return null;
    }
    
    /**
     * Gets the current WebDriver instance without initialization if it doesn't exist.
     *
     * @return Current WebDriver instance or null if not initialized
     */
    public static WebDriver getCurrentDriver() {
        return DRIVER_THREAD_LOCAL.get();
    }
    
    /**
     * Quits the current WebDriver instance and initializes a new one.
     *
     * @return Newly created WebDriver instance
     */
    public static WebDriver resetDriver() {
        quitDriver();
        return initDriver();
    }
    
    /**
     * Checks if the current WebDriver session has been quit or is invalid.
     *
     * @return True if driver session is invalid or quit, false otherwise
     */
    public static boolean hasDriverQuit() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        
        if (driver == null) {
            return true;
        }
        
        try {
            // Attempt a simple operation to check if the session is still valid
            driver.getTitle();
            return false;
        } catch (WebDriverException e) {
            LOGGER.debug("WebDriver session is no longer valid", e);
            return true;
        }
    }
}