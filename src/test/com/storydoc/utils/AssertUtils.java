package com.storydoc.utils;

import com.storydoc.constants.MessageConstants;
import com.storydoc.exceptions.FrameworkException;
import com.storydoc.utils.LogUtils;
import com.storydoc.utils.ScreenshotUtils;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * Utility class providing enhanced assertion methods for Selenium WebDriver tests
 * with detailed logging, screenshot capture on failures, and consistent error handling throughout the test framework.
 */
public final class AssertUtils {

    private static final Logger LOGGER = LogManager.getLogger(AssertUtils.class);

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private AssertUtils() {
        throw new IllegalStateException("Utility class - cannot be instantiated");
    }

    /**
     * Asserts that two values are equal with enhanced logging and screenshot on failure
     *
     * @param actual   Actual value
     * @param expected Expected value
     * @param message  Message to display on failure
     */
    public static void assertEqual(Object actual, Object expected, String message) {
        try {
            Assert.assertEquals(actual, expected, message);
            LogUtils.info("Assertion passed: " + message);
        } catch (AssertionError e) {
            LogUtils.error("Assertion failed: " + message + ". Expected: " + expected + ", Actual: " + actual);
            ScreenshotUtils.captureScreenshot("assertEqual_failure");
            throw e;
        }
    }

    /**
     * Asserts that a condition is true with enhanced logging and screenshot on failure
     *
     * @param condition Condition to verify
     * @param message   Message to display on failure
     */
    public static void assertTrue(boolean condition, String message) {
        try {
            Assert.assertTrue(condition, message);
            LogUtils.info("Assertion passed: " + message);
        } catch (AssertionError e) {
            LogUtils.error("Assertion failed: " + message + ". Expected true but was false");
            ScreenshotUtils.captureScreenshot("assertTrue_failure");
            throw e;
        }
    }

    /**
     * Asserts that a condition is false with enhanced logging and screenshot on failure
     *
     * @param condition Condition to verify
     * @param message   Message to display on failure
     */
    public static void assertFalse(boolean condition, String message) {
        try {
            Assert.assertFalse(condition, message);
            LogUtils.info("Assertion passed: " + message);
        } catch (AssertionError e) {
            LogUtils.error("Assertion failed: " + message + ". Expected false but was true");
            ScreenshotUtils.captureScreenshot("assertFalse_failure");
            throw e;
        }
    }

    /**
     * Asserts that an element is present in the DOM with detailed error reporting
     *
     * @param element     WebElement to verify
     * @param elementName Name of the element for reporting
     */
    public static void assertElementPresent(WebElement element, String elementName) {
        try {
            Assert.assertNotNull(element, "Element '" + elementName + "' should be present");
            LogUtils.info("Element is present: " + elementName);
        } catch (NoSuchElementException | AssertionError e) {
            LogUtils.error("Element is not present: " + elementName);
            ScreenshotUtils.captureScreenshot("element_not_present_" + elementName);
            throw new FrameworkException("Element is not present: " + elementName, e);
        }
    }

    /**
     * Asserts that an element is visible on the page with detailed error reporting
     *
     * @param element     WebElement to verify
     * @param elementName Name of the element for reporting
     */
    public static void assertElementVisible(WebElement element, String elementName) {
        try {
            Assert.assertTrue(element.isDisplayed(),
                    String.format(MessageConstants.ASSERT_ELEMENT_VISIBLE, elementName));
            LogUtils.info("Element is visible: " + elementName);
        } catch (NoSuchElementException | StaleElementReferenceException | AssertionError e) {
            LogUtils.error("Element is not visible: " + elementName);
            ScreenshotUtils.captureScreenshot("element_not_visible_" + elementName);
            throw new FrameworkException("Element is not visible: " + elementName, e);
        }
    }

    /**
     * Asserts that an element is clickable (enabled) with detailed error reporting
     *
     * @param element     WebElement to verify
     * @param elementName Name of the element for reporting
     */
    public static void assertElementClickable(WebElement element, String elementName) {
        try {
            Assert.assertTrue(element.isEnabled(),
                    String.format(MessageConstants.ASSERT_ELEMENT_CLICKABLE, elementName));
            LogUtils.info("Element is clickable: " + elementName);
        } catch (NoSuchElementException | StaleElementReferenceException | AssertionError e) {
            LogUtils.error("Element is not clickable: " + elementName);
            ScreenshotUtils.captureScreenshot("element_not_clickable_" + elementName);
            throw new FrameworkException("Element is not clickable: " + elementName, e);
        }
    }

    /**
     * Asserts that element text matches expected value with detailed error reporting
     *
     * @param element     WebElement to verify
     * @param expected    Expected text
     * @param elementName Name of the element for reporting
     */
    public static void assertTextEquals(WebElement element, String expected, String elementName) {
        try {
            String actualText = element.getText();
            Assert.assertEquals(actualText, expected, "Text in " + elementName + " should match expected value");
            LogUtils.info("Text matches for element '" + elementName + "': " + expected);
        } catch (NoSuchElementException | StaleElementReferenceException | AssertionError e) {
            String actualText = "";
            try {
                actualText = element.getText();
            } catch (Exception ex) {
                // Ignore if we can't get the text
            }
            LogUtils.error("Text assertion failed for element '" + elementName + 
                    "'. Expected: " + expected + ", Actual: " + actualText);
            ScreenshotUtils.captureScreenshot("text_mismatch_" + elementName);
            throw new FrameworkException("Text assertion failed for element: " + elementName, e);
        }
    }

    /**
     * Asserts that element text contains expected substring with detailed error reporting
     *
     * @param element          WebElement to verify
     * @param expectedSubstring Expected substring
     * @param elementName      Name of the element for reporting
     */
    public static void assertTextContains(WebElement element, String expectedSubstring, String elementName) {
        try {
            String actualText = element.getText();
            Assert.assertTrue(actualText.contains(expectedSubstring),
                    "Text in " + elementName + " should contain: " + expectedSubstring);
            LogUtils.info("Text in element '" + elementName + "' contains: " + expectedSubstring);
        } catch (NoSuchElementException | StaleElementReferenceException | AssertionError e) {
            String actualText = "";
            try {
                actualText = element.getText();
            } catch (Exception ex) {
                // Ignore if we can't get the text
            }
            LogUtils.error("Text in element '" + elementName + "' does not contain expected substring: " 
                    + expectedSubstring + ". Actual text: " + actualText);
            ScreenshotUtils.captureScreenshot("text_substring_" + elementName);
            throw new FrameworkException("Text does not contain expected substring in element: " + elementName, e);
        }
    }

    /**
     * Asserts that success message is displayed after an operation with detailed error reporting
     *
     * @param messageElement WebElement containing the success message
     * @param operation      Name of the operation that should have succeeded
     */
    public static void assertSuccessMessage(WebElement messageElement, String operation) {
        try {
            Assert.assertTrue(messageElement.isDisplayed(), "Success message should be displayed for " + operation);
            String messageText = messageElement.getText();
            Assert.assertTrue(messageText.contains("success") || messageText.contains("successful") 
                    || messageText.contains("completed"),
                    MessageConstants.ASSERT_SIGNUP_SUCCESSFUL);
            LogUtils.info("Success message verified for operation: " + operation + ". Message: " + messageText);
        } catch (NoSuchElementException | StaleElementReferenceException | AssertionError e) {
            LogUtils.error("Failed to verify success message for operation: " + operation);
            ScreenshotUtils.captureScreenshot("success_message_" + operation);
            throw new FrameworkException("Failed to verify success message for operation: " + operation, e);
        }
    }

    /**
     * Asserts form validation error is displayed for a field with detailed error reporting
     *
     * @param errorElement WebElement containing the error message
     * @param fieldName    Name of the field with validation error
     */
    public static void assertFormValidationError(WebElement errorElement, String fieldName) {
        try {
            Assert.assertTrue(errorElement.isDisplayed(), "Validation error should be displayed for " + fieldName);
            LogUtils.info("Validation error verified for field: " + fieldName + 
                    ". Error message: " + errorElement.getText());
        } catch (NoSuchElementException | StaleElementReferenceException | AssertionError e) {
            LogUtils.error("Failed to verify validation error for field: " + fieldName);
            ScreenshotUtils.captureScreenshot("validation_error_" + fieldName);
            throw new FrameworkException("Failed to verify validation error for field: " + fieldName, e);
        }
    }

    /**
     * Asserts that the current URL contains expected text
     *
     * @param driver       WebDriver instance
     * @param expectedText Text that should be present in the URL
     */
    public static void assertUrlContains(WebDriver driver, String expectedText) {
        try {
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains(expectedText),
                    "Current URL should contain: " + expectedText + ", but was: " + currentUrl);
            LogUtils.info("URL contains expected text: " + expectedText + ". Current URL: " + currentUrl);
        } catch (AssertionError e) {
            LogUtils.error("URL does not contain expected text: " + expectedText 
                    + ". Current URL: " + driver.getCurrentUrl());
            ScreenshotUtils.captureScreenshot("url_assertion");
            throw e;
        }
    }
}