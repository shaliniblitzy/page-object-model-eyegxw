package com.storydoc.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.storydoc.constants.TimeoutConstants;
import com.storydoc.config.ConfigurationManager;
import com.storydoc.exceptions.FrameworkException;

import java.time.Duration;
import java.util.function.Function;

/**
 * Utility class that provides centralized wait mechanisms for browser and element synchronization
 * across the Selenium framework. Implements explicit waits, fluent waits, and custom wait conditions
 * to handle timing issues between test execution and application state changes.
 */
public final class WaitUtils {

    private static final Logger logger = LogManager.getLogger(WaitUtils.class);

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private WaitUtils() {
        throw new IllegalStateException("Utility class - do not instantiate");
    }

    /**
     * Waits for an element to be visible on the page
     *
     * @param driver WebDriver instance
     * @param locator Element locator
     * @return The visible WebElement
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator) {
        LogUtils.debug(WaitUtils.class, "Waiting for element to be visible: " + locator);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.ELEMENT_VISIBLE_TIMEOUT_SECONDS));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "Element not visible within timeout: " + locator, e);
            throw new FrameworkException("Element not visible within timeout: " + locator, e);
        }
    }

    /**
     * Waits for an element to be visible on the page with custom timeout
     *
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout in seconds
     * @return The visible WebElement
     */
    public static WebElement waitForElementVisible(WebDriver driver, By locator, long timeoutInSeconds) {
        LogUtils.debug(WaitUtils.class, "Waiting for element to be visible with timeout " + timeoutInSeconds + "s: " + locator);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "Element not visible within timeout " + timeoutInSeconds + "s: " + locator, e);
            throw new FrameworkException("Element not visible within timeout " + timeoutInSeconds + "s: " + locator, e);
        }
    }

    /**
     * Waits for an element to be clickable (visible and enabled)
     *
     * @param driver WebDriver instance
     * @param locator Element locator
     * @return The clickable WebElement
     */
    public static WebElement waitForElementClickable(WebDriver driver, By locator) {
        LogUtils.debug(WaitUtils.class, "Waiting for element to be clickable: " + locator);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.ELEMENT_CLICKABLE_TIMEOUT_SECONDS));
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "Element not clickable within timeout: " + locator, e);
            throw new FrameworkException("Element not clickable within timeout: " + locator, e);
        }
    }

    /**
     * Waits for an element to be clickable with custom timeout
     *
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout in seconds
     * @return The clickable WebElement
     */
    public static WebElement waitForElementClickable(WebDriver driver, By locator, long timeoutInSeconds) {
        LogUtils.debug(WaitUtils.class, "Waiting for element to be clickable with timeout " + timeoutInSeconds + "s: " + locator);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "Element not clickable within timeout " + timeoutInSeconds + "s: " + locator, e);
            throw new FrameworkException("Element not clickable within timeout " + timeoutInSeconds + "s: " + locator, e);
        }
    }

    /**
     * Waits for an element to be present in the DOM
     *
     * @param driver WebDriver instance
     * @param locator Element locator
     * @return The present WebElement
     */
    public static WebElement waitForElementPresent(WebDriver driver, By locator) {
        LogUtils.debug(WaitUtils.class, "Waiting for element to be present in DOM: " + locator);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.ELEMENT_PRESENCE_TIMEOUT_SECONDS));
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "Element not present in DOM within timeout: " + locator, e);
            throw new FrameworkException("Element not present in DOM within timeout: " + locator, e);
        }
    }

    /**
     * Waits for an element to become invisible or not present
     *
     * @param driver WebDriver instance
     * @param locator Element locator
     * @return True if the element is no longer visible
     */
    public static boolean waitForElementInvisible(WebDriver driver, By locator) {
        LogUtils.debug(WaitUtils.class, "Waiting for element to become invisible: " + locator);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.ELEMENT_INVISIBLE_TIMEOUT_SECONDS));
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "Element still visible within timeout: " + locator, e);
            throw new FrameworkException("Element still visible within timeout: " + locator, e);
        }
    }

    /**
     * Waits for the page to complete loading by checking document.readyState
     *
     * @param driver WebDriver instance
     */
    public static void waitForPageLoad(WebDriver driver) {
        LogUtils.debug(WaitUtils.class, "Waiting for page to complete loading");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.PAGE_LOAD_TIMEOUT_SECONDS));
            wait.until(driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete"));
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "Page did not load completely within timeout", e);
            throw new FrameworkException("Page did not load completely within timeout", e);
        }
    }

    /**
     * Waits for jQuery AJAX calls to complete by checking jQuery.active count
     *
     * @param driver WebDriver instance
     */
    public static void waitForJQueryLoad(WebDriver driver) {
        LogUtils.debug(WaitUtils.class, "Waiting for jQuery AJAX calls to complete");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.AJAX_TIMEOUT_SECONDS));
            
            // First check if jQuery is available
            Boolean jQueryDefined = (Boolean) ((JavascriptExecutor) driver).executeScript("return typeof jQuery != 'undefined'");
            
            if (Boolean.TRUE.equals(jQueryDefined)) {
                wait.until(driver1 -> ((Long) ((JavascriptExecutor) driver1).executeScript("return jQuery.active") == 0));
            } else {
                LogUtils.debug(WaitUtils.class, "jQuery is not defined on this page, skipping jQuery wait");
            }
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "jQuery AJAX calls did not complete within timeout", e);
            throw new FrameworkException("jQuery AJAX calls did not complete within timeout", e);
        }
    }

    /**
     * Waits for an element to contain specific text
     *
     * @param driver WebDriver instance
     * @param locator Element locator
     * @param text Expected text
     * @return The WebElement containing the expected text
     */
    public static WebElement waitForElementText(WebDriver driver, By locator, String text) {
        LogUtils.debug(WaitUtils.class, "Waiting for element to contain text '" + text + "': " + locator);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.DEFAULT_TIMEOUT_SECONDS));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            return driver.findElement(locator);
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "Element does not contain expected text '" + text + "' within timeout: " + locator, e);
            throw new FrameworkException("Element does not contain expected text '" + text + "' within timeout: " + locator, e);
        }
    }

    /**
     * Waits for the current URL to contain a specific substring
     *
     * @param driver WebDriver instance
     * @param urlFragment Expected URL fragment
     * @return True if URL contains the fragment
     */
    public static boolean waitForUrlContains(WebDriver driver, String urlFragment) {
        LogUtils.debug(WaitUtils.class, "Waiting for URL to contain '" + urlFragment + "'");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.DEFAULT_TIMEOUT_SECONDS));
            return wait.until(ExpectedConditions.urlContains(urlFragment));
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "URL does not contain '" + urlFragment + "' within timeout", e);
            throw new FrameworkException("URL does not contain '" + urlFragment + "' within timeout", e);
        }
    }

    /**
     * Creates a FluentWait instance with appropriate configuration for handling transient failures
     *
     * @param driver WebDriver instance
     * @return Configured FluentWait instance
     */
    public static FluentWait<WebDriver> fluent(WebDriver driver) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(TimeoutConstants.DEFAULT_TIMEOUT_SECONDS))
                .pollingEvery(Duration.ofMillis(TimeoutConstants.POLLING_INTERVAL_MILLIS))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
    }

    /**
     * Waits using a custom condition defined as a Function
     *
     * @param driver WebDriver instance
     * @param condition Custom wait condition
     * @param message Message for timeout exception
     * @return Result of the condition
     */
    public static boolean waitWithCustomCondition(WebDriver driver, Function<WebDriver, Boolean> condition, String message) {
        LogUtils.debug(WaitUtils.class, "Waiting with custom condition: " + message);
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.DEFAULT_TIMEOUT_SECONDS));
            return wait.until(condition);
        } catch (TimeoutException e) {
            LogUtils.error(WaitUtils.class, "Custom wait condition failed: " + message, e);
            throw new FrameworkException("Custom wait condition failed: " + message, e);
        }
    }

    /**
     * Forces the thread to sleep for a specific duration (use sparingly)
     *
     * @param millis Sleep duration in milliseconds
     */
    public static void sleepInMillis(long millis) {
        LogUtils.warn(WaitUtils.class, "Using explicit sleep for " + millis + "ms. Consider using explicit waits instead.");
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogUtils.error(WaitUtils.class, "Thread sleep interrupted", e);
        }
    }
}