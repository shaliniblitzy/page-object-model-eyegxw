package com.storydoc.pages;

import com.storydoc.utils.WaitUtils;
import com.storydoc.utils.JavaScriptUtils;
import com.storydoc.utils.LogUtils;
import com.storydoc.constants.TimeoutConstants;
import com.storydoc.exceptions.FrameworkException;
import com.storydoc.config.ConfigurationManager;
import com.storydoc.utils.ScreenshotUtils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.ElementNotInteractableException;
import java.time.Duration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Abstract base class that implements the Page Object Model design pattern for the Storydoc automation framework.
 * Provides common functionality for page interactions, element operations, and synchronization mechanisms
 * that all page object classes inherit.
 */
public abstract class BasePage {
    
    private static final Logger logger = LogManager.getLogger(BasePage.class);
    
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected JavascriptExecutor js;
    
    /**
     * Initializes a new instance of BasePage with the WebDriver instance
     *
     * @param driver WebDriver instance to use for browser interactions
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 
            Duration.ofSeconds(TimeoutConstants.DEFAULT_TIMEOUT_SECONDS));
        this.js = (JavascriptExecutor) driver;
        LogUtils.debug(BasePage.class, "Initialized BasePage with driver: " + driver);
    }
    
    /**
     * Waits for an element to be visible on the page
     *
     * @param locator Element locator
     * @return The visible WebElement
     */
    protected WebElement waitForElementVisible(By locator) {
        LogUtils.debug(BasePage.class, "Waiting for element to be visible: " + locator);
        try {
            return WaitUtils.waitForElementVisible(driver, locator);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to wait for element visibility: " + locator, e);
            throw new FrameworkException("Failed to wait for element visibility: " + locator, e);
        }
    }
    
    /**
     * Waits for an element to be clickable (visible and enabled)
     *
     * @param locator Element locator
     * @return The clickable WebElement
     */
    protected WebElement waitForElementClickable(By locator) {
        LogUtils.debug(BasePage.class, "Waiting for element to be clickable: " + locator);
        try {
            return WaitUtils.waitForElementClickable(driver, locator);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to wait for element to be clickable: " + locator, e);
            throw new FrameworkException("Failed to wait for element to be clickable: " + locator, e);
        }
    }
    
    /**
     * Clicks on an element after waiting for it to be clickable
     *
     * @param locator Element locator
     */
    protected void click(By locator) {
        LogUtils.debug(BasePage.class, "Clicking on element: " + locator);
        try {
            WebElement element = waitForElementClickable(locator);
            element.click();
        } catch (ElementNotInteractableException e) {
            LogUtils.warn(BasePage.class, "Element not interactable with standard click, trying JavaScript click: " + locator);
            try {
                WebElement element = driver.findElement(locator);
                JavaScriptUtils.click(driver, element);
            } catch (Exception ex) {
                LogUtils.error(BasePage.class, "Failed to click element with JavaScript: " + locator, ex);
                throw new FrameworkException("Failed to click element: " + locator, ex);
            }
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to click element: " + locator, e);
            throw new FrameworkException("Failed to click element: " + locator, e);
        }
    }
    
    /**
     * Types text into an input field after waiting for it to be visible
     *
     * @param locator Element locator
     * @param text Text to enter
     */
    protected void type(By locator, String text) {
        LogUtils.debug(BasePage.class, "Typing text into element: " + locator + ", text: " + text);
        try {
            WebElement element = waitForElementVisible(locator);
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to type text into element: " + locator, e);
            throw new FrameworkException("Failed to type text into element: " + locator, e);
        }
    }
    
    /**
     * Selects a checkbox or radio button
     *
     * @param locator Element locator
     */
    protected void select(By locator) {
        LogUtils.debug(BasePage.class, "Selecting element: " + locator);
        try {
            WebElement element = waitForElementClickable(locator);
            if (!element.isSelected()) {
                element.click();
            }
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to select element: " + locator, e);
            throw new FrameworkException("Failed to select element: " + locator, e);
        }
    }
    
    /**
     * Selects an option from a dropdown by visible text
     *
     * @param locator Element locator
     * @param visibleText Text of the option to select
     */
    protected void selectByVisibleText(By locator, String visibleText) {
        LogUtils.debug(BasePage.class, "Selecting dropdown option by visible text: " + locator + ", text: " + visibleText);
        try {
            WebElement element = waitForElementVisible(locator);
            Select select = new Select(element);
            select.selectByVisibleText(visibleText);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to select dropdown option by visible text: " + locator, e);
            throw new FrameworkException("Failed to select dropdown option by visible text: " + locator, e);
        }
    }
    
    /**
     * Selects an option from a dropdown by value attribute
     *
     * @param locator Element locator
     * @param value Value attribute of the option to select
     */
    protected void selectByValue(By locator, String value) {
        LogUtils.debug(BasePage.class, "Selecting dropdown option by value: " + locator + ", value: " + value);
        try {
            WebElement element = waitForElementVisible(locator);
            Select select = new Select(element);
            select.selectByValue(value);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to select dropdown option by value: " + locator, e);
            throw new FrameworkException("Failed to select dropdown option by value: " + locator, e);
        }
    }
    
    /**
     * Selects an option from a dropdown by index
     *
     * @param locator Element locator
     * @param index Index of the option to select
     */
    protected void selectByIndex(By locator, int index) {
        LogUtils.debug(BasePage.class, "Selecting dropdown option by index: " + locator + ", index: " + index);
        try {
            WebElement element = waitForElementVisible(locator);
            Select select = new Select(element);
            select.selectByIndex(index);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to select dropdown option by index: " + locator, e);
            throw new FrameworkException("Failed to select dropdown option by index: " + locator, e);
        }
    }
    
    /**
     * Gets text from an element after waiting for it to be visible
     *
     * @param locator Element locator
     * @return Text content of the element
     */
    protected String getText(By locator) {
        LogUtils.debug(BasePage.class, "Getting text from element: " + locator);
        try {
            WebElement element = waitForElementVisible(locator);
            return element.getText();
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to get text from element: " + locator, e);
            throw new FrameworkException("Failed to get text from element: " + locator, e);
        }
    }
    
    /**
     * Gets the value of an attribute from an element
     *
     * @param locator Element locator
     * @param attributeName Name of the attribute to get
     * @return Value of the specified attribute
     */
    protected String getAttributeValue(By locator, String attributeName) {
        LogUtils.debug(BasePage.class, "Getting attribute value from element: " + locator + ", attribute: " + attributeName);
        try {
            WebElement element = waitForElementVisible(locator);
            return element.getAttribute(attributeName);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to get attribute value from element: " + locator, e);
            throw new FrameworkException("Failed to get attribute value from element: " + locator, e);
        }
    }
    
    /**
     * Checks if an element is present in the DOM
     *
     * @param locator Element locator
     * @return True if element is present, false otherwise
     */
    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            LogUtils.debug(BasePage.class, "Element is present: " + locator);
            return true;
        } catch (NoSuchElementException e) {
            LogUtils.debug(BasePage.class, "Element is not present: " + locator);
            return false;
        }
    }
    
    /**
     * Checks if an element is visible on the page
     *
     * @param locator Element locator
     * @return True if element is visible, false otherwise
     */
    protected boolean isElementVisible(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            boolean isVisible = element.isDisplayed();
            LogUtils.debug(BasePage.class, "Element visibility: " + locator + " - " + isVisible);
            return isVisible;
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            LogUtils.debug(BasePage.class, "Element is not visible (not found or stale): " + locator);
            return false;
        }
    }
    
    /**
     * Checks if an element is enabled for interaction
     *
     * @param locator Element locator
     * @return True if element is enabled, false otherwise
     */
    protected boolean isElementEnabled(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            boolean isEnabled = element.isEnabled();
            LogUtils.debug(BasePage.class, "Element enabled state: " + locator + " - " + isEnabled);
            return isEnabled;
        } catch (NoSuchElementException e) {
            LogUtils.debug(BasePage.class, "Element is not enabled (not found): " + locator);
            return false;
        }
    }
    
    /**
     * Checks if a checkbox or radio button is selected
     *
     * @param locator Element locator
     * @return True if element is selected, false otherwise
     */
    protected boolean isElementSelected(By locator) {
        try {
            WebElement element = driver.findElement(locator);
            boolean isSelected = element.isSelected();
            LogUtils.debug(BasePage.class, "Element selection state: " + locator + " - " + isSelected);
            return isSelected;
        } catch (NoSuchElementException e) {
            LogUtils.debug(BasePage.class, "Element is not selected (not found): " + locator);
            return false;
        }
    }
    
    /**
     * Navigates to a specified URL
     *
     * @param url URL to navigate to
     */
    protected void navigateTo(String url) {
        LogUtils.debug(BasePage.class, "Navigating to URL: " + url);
        try {
            driver.get(url);
            WaitUtils.waitForPageLoad(driver);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to navigate to URL: " + url, e);
            throw new FrameworkException("Failed to navigate to URL: " + url, e);
        }
    }
    
    /**
     * Refreshes the current page
     */
    protected void refreshPage() {
        LogUtils.debug(BasePage.class, "Refreshing current page");
        try {
            driver.navigate().refresh();
            WaitUtils.waitForPageLoad(driver);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to refresh page", e);
            throw new FrameworkException("Failed to refresh page", e);
        }
    }
    
    /**
     * Gets the current URL of the browser
     *
     * @return Current URL
     */
    protected String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        LogUtils.debug(BasePage.class, "Current URL: " + url);
        return url;
    }
    
    /**
     * Gets the title of the current page
     *
     * @return Page title
     */
    protected String getTitle() {
        String title = driver.getTitle();
        LogUtils.debug(BasePage.class, "Current page title: " + title);
        return title;
    }
    
    /**
     * Takes a screenshot of the current page
     *
     * @param screenshotName Name for the screenshot file
     * @return Path to saved screenshot
     */
    protected String takeScreenshot(String screenshotName) {
        LogUtils.debug(BasePage.class, "Taking screenshot: " + screenshotName);
        try {
            return ScreenshotUtils.captureScreenshot(screenshotName);
        } catch (Exception e) {
            LogUtils.error(BasePage.class, "Failed to take screenshot: " + screenshotName, e);
            return null;
        }
    }
}