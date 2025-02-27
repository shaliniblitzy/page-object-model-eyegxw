package com.storydoc.locators;

import org.openqa.selenium.By; // Selenium WebDriver 4.8.x

/**
 * Static class providing common locator constants used across multiple pages 
 * in the Storydoc application. This class centralizes element identifiers that
 * appear consistently throughout the application's signup flow and other pages.
 * 
 * These locators represent UI elements like headers, footers, common buttons,
 * and notifications that are reused across multiple screens.
 */
public class CommonLocators {
    
    // Header Elements
    public static final By HEADER = By.cssSelector("header[data-testid='app-header']");
    public static final By LOGO = By.cssSelector("img[data-testid='storydoc-logo']");
    public static final By NAVIGATION_MENU = By.cssSelector("nav[data-testid='main-navigation']");
    
    // Footer Elements
    public static final By FOOTER = By.cssSelector("footer[data-testid='app-footer']");
    
    // Common UI Components
    public static final By LOADING_SPINNER = By.cssSelector("div[data-testid='loading-spinner']");
    public static final By ERROR_NOTIFICATION = By.cssSelector("div[data-testid='error-notification']");
    public static final By SUCCESS_NOTIFICATION = By.cssSelector("div[data-testid='success-notification']");
    
    // Modal Elements
    public static final By MODAL_CONTAINER = By.cssSelector("div[data-testid='modal-container']");
    public static final By MODAL_CLOSE_BUTTON = By.cssSelector("button[data-testid='modal-close-button']");
    
    // Common Form Elements
    public static final By FORM_ERROR_MESSAGE = By.cssSelector("div[data-testid='form-error']");
    public static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");
    public static final By CANCEL_BUTTON = By.cssSelector("button[data-testid='cancel-button']");
    
    // Common Navigation Links
    public static final By SIGN_IN_LINK = By.cssSelector("a[data-testid='sign-in-link']");
    public static final By SIGN_UP_LINK = By.cssSelector("a[data-testid='sign-up-link']");
    
    /**
     * Private constructor to prevent instantiation as this is a utility class
     * with static members only.
     * 
     * @throws IllegalStateException if instantiation is attempted
     */
    private CommonLocators() {
        throw new IllegalStateException("Utility class - do not instantiate");
    }
}