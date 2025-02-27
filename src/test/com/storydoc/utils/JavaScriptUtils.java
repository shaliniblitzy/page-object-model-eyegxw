package com.storydoc.utils;

import org.openqa.selenium.WebDriver; // v4.8.3
import org.openqa.selenium.JavascriptExecutor; // v4.8.3
import org.openqa.selenium.WebElement; // v4.8.3
import org.openqa.selenium.By; // v4.8.3
import org.openqa.selenium.TimeoutException; // v4.8.3
import org.apache.logging.log4j.Logger; // v2.19.0
import org.apache.logging.log4j.LogManager; // v2.19.0

import com.storydoc.utils.LogUtils;
import com.storydoc.exceptions.FrameworkException;
import com.storydoc.constants.TimeoutConstants;

/**
 * Utility class that provides methods for JavaScript operations in Selenium tests.
 * This class includes methods for executing JavaScript, scrolling elements into view,
 * highlighting elements, and performing other JavaScript-related operations.
 */
public class JavaScriptUtils {

    private static final Logger logger = LogManager.getLogger(JavaScriptUtils.class);

    /**
     * Executes JavaScript code in the browser.
     *
     * @param driver The WebDriver instance
     * @param script The JavaScript code to execute
     * @param args Arguments to pass to the JavaScript code
     * @return Result of JavaScript execution
     * @throws FrameworkException if JavaScript execution fails
     */
    public static Object executeScript(WebDriver driver, String script, Object... args) {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            LogUtils.debug(JavaScriptUtils.class, "Executing JavaScript: " + script);
            return jsExecutor.executeScript(script, args);
        } catch (Exception e) {
            LogUtils.error(JavaScriptUtils.class, "Failed to execute JavaScript: " + script, e);
            throw new FrameworkException("Failed to execute JavaScript: " + script, e);
        }
    }

    /**
     * Executes asynchronous JavaScript code in the browser.
     *
     * @param driver The WebDriver instance
     * @param script The JavaScript code to execute
     * @param args Arguments to pass to the JavaScript code
     * @return Result of asynchronous JavaScript execution
     * @throws FrameworkException if JavaScript execution fails
     */
    public static Object executeAsyncScript(WebDriver driver, String script, Object... args) {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            LogUtils.debug(JavaScriptUtils.class, "Executing asynchronous JavaScript: " + script);
            return jsExecutor.executeAsyncScript(script, args);
        } catch (Exception e) {
            LogUtils.error(JavaScriptUtils.class, "Failed to execute asynchronous JavaScript: " + script, e);
            throw new FrameworkException("Failed to execute asynchronous JavaScript: " + script, e);
        }
    }

    /**
     * Scrolls an element into view using JavaScript.
     *
     * @param driver The WebDriver instance
     * @param element The element to scroll into view
     * @param alignToTop Whether to align the element to the top of the viewport
     */
    public static void scrollIntoView(WebDriver driver, WebElement element, boolean alignToTop) {
        LogUtils.debug(JavaScriptUtils.class, "Scrolling element into view, alignToTop: " + alignToTop);
        executeScript(driver, "arguments[0].scrollIntoView(arguments[1]);", element, alignToTop);
    }

    /**
     * Scrolls an element into view with default alignment (top).
     *
     * @param driver The WebDriver instance
     * @param element The element to scroll into view
     */
    public static void scrollIntoView(WebDriver driver, WebElement element) {
        scrollIntoView(driver, element, true);
    }

    /**
     * Scrolls to the bottom of the page.
     *
     * @param driver The WebDriver instance
     */
    public static void scrollToBottom(WebDriver driver) {
        LogUtils.debug(JavaScriptUtils.class, "Scrolling to bottom of page");
        executeScript(driver, "window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Scrolls to the top of the page.
     *
     * @param driver The WebDriver instance
     */
    public static void scrollToTop(WebDriver driver) {
        LogUtils.debug(JavaScriptUtils.class, "Scrolling to top of page");
        executeScript(driver, "window.scrollTo(0, 0);");
    }

    /**
     * Clicks an element using JavaScript.
     *
     * @param driver The WebDriver instance
     * @param element The element to click
     */
    public static void click(WebDriver driver, WebElement element) {
        LogUtils.debug(JavaScriptUtils.class, "Clicking element using JavaScript");
        executeScript(driver, "arguments[0].click();", element);
    }

    /**
     * Highlights an element by changing its background color.
     *
     * @param driver The WebDriver instance
     * @param element The element to highlight
     * @param color The highlight color (CSS color value)
     */
    public static void highlight(WebDriver driver, WebElement element, String color) {
        String originalStyle = (String) executeScript(driver, "return arguments[0].getAttribute('style');", element);
        executeScript(driver, "arguments[0].setAttribute('style', 'background-color: " + color + " !important; border: 2px solid " + color + " !important;');", element);
        
        try {
            Thread.sleep(300); // Brief pause to see the highlight
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Restore original style
        executeScript(driver, "arguments[0].setAttribute('style', arguments[1]);", element, originalStyle != null ? originalStyle : "");
    }

    /**
     * Highlights an element with default color (yellow).
     *
     * @param driver The WebDriver instance
     * @param element The element to highlight
     */
    public static void highlight(WebDriver driver, WebElement element) {
        highlight(driver, element, "#FFFF00");
    }

    /**
     * Gets browser information using JavaScript.
     *
     * @param driver The WebDriver instance
     * @return Browser information
     */
    public static String getBrowserInfo(WebDriver driver) {
        return (String) executeScript(driver, "return navigator.userAgent;");
    }

    /**
     * Gets the document.readyState status.
     *
     * @param driver The WebDriver instance
     * @return Page load status (complete, interactive, loading)
     */
    public static String getPageLoadStatus(WebDriver driver) {
        return (String) executeScript(driver, "return document.readyState;");
    }

    /**
     * Waits for page to fully load using document.readyState.
     *
     * @param driver The WebDriver instance
     * @param timeoutInSeconds Maximum time to wait in seconds
     * @return True if page loaded within timeout, false otherwise
     */
    public static boolean waitForPageLoad(WebDriver driver, long timeoutInSeconds) {
        LogUtils.debug(JavaScriptUtils.class, "Waiting for page to load, timeout: " + timeoutInSeconds + " seconds");
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (timeoutInSeconds * 1000);
        
        while (System.currentTimeMillis() < endTime) {
            String readyState = getPageLoadStatus(driver);
            if (readyState.equals("complete")) {
                LogUtils.debug(JavaScriptUtils.class, "Page loaded successfully");
                return true;
            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        LogUtils.warn(JavaScriptUtils.class, "Page load timeout reached after " + timeoutInSeconds + " seconds");
        return false;
    }

    /**
     * Waits for page to fully load with default timeout.
     *
     * @param driver The WebDriver instance
     * @return True if page loaded within timeout, false otherwise
     */
    public static boolean waitForPageLoad(WebDriver driver) {
        return waitForPageLoad(driver, TimeoutConstants.DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * Accepts or dismisses JavaScript alert.
     *
     * @param driver The WebDriver instance
     * @param accept True to accept, false to dismiss
     * @return Alert text or null if no alert present
     */
    public static String handleAlert(WebDriver driver, boolean accept) {
        Boolean isAlertPresent = (Boolean) executeScript(driver, "return (typeof window.alert !== 'undefined' && window.alert !== null);");
        
        if (Boolean.TRUE.equals(isAlertPresent)) {
            String alertText = (String) executeScript(driver, "return window.alert.text;");
            if (accept) {
                executeScript(driver, "window.alert.accept();");
                LogUtils.debug(JavaScriptUtils.class, "Alert accepted: " + alertText);
            } else {
                executeScript(driver, "window.alert.dismiss();");
                LogUtils.debug(JavaScriptUtils.class, "Alert dismissed: " + alertText);
            }
            return alertText;
        }
        
        LogUtils.debug(JavaScriptUtils.class, "No alert present to handle");
        return null;
    }

    /**
     * Checks if element is visible in the viewport.
     *
     * @param driver The WebDriver instance
     * @param element The element to check
     * @return True if element is in viewport, false otherwise
     */
    public static boolean isElementInViewport(WebDriver driver, WebElement element) {
        String script = 
            "var rect = arguments[0].getBoundingClientRect();" +
            "return (" +
            "    rect.top >= 0 &&" +
            "    rect.left >= 0 &&" +
            "    rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&" +
            "    rect.right <= (window.innerWidth || document.documentElement.clientWidth)" +
            ");";
        
        return (Boolean) executeScript(driver, script, element);
    }

    /**
     * Gets element text using JavaScript (works for hidden elements).
     *
     * @param driver The WebDriver instance
     * @param element The element to get text from
     * @return Element text content
     */
    public static String getElementText(WebDriver driver, WebElement element) {
        return (String) executeScript(driver, "return arguments[0].textContent;", element);
    }

    /**
     * Sets element value using JavaScript.
     *
     * @param driver The WebDriver instance
     * @param element The element to set value for
     * @param value The value to set
     */
    public static void setElementValue(WebDriver driver, WebElement element, String value) {
        executeScript(driver, "arguments[0].value = arguments[1];", element, value);
    }
}