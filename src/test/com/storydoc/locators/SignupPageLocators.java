package com.storydoc.locators;

import org.openqa.selenium.By; // Selenium WebDriver 4.8.x

/**
 * Class containing static constants defining locator strategies for elements on the Storydoc signup page.
 * Used by the SignupPage class to interact with UI elements.
 * 
 * This class implements the locator repository pattern to improve maintainability
 * by centralizing all element identifiers in one place, making it easier to update
 * when UI changes occur.
 */
public class SignupPageLocators {
    
    /**
     * Locator for the main signup form
     */
    public static final By SIGNUP_FORM = By.cssSelector("form[data-testid='signup-form']");
    
    /**
     * Locator for the email input field
     */
    public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-input']");
    
    /**
     * Locator for the password input field
     */
    public static final By PASSWORD_FIELD = By.cssSelector("input[data-testid='password-input']");
    
    /**
     * Locator for the terms and conditions checkbox
     */
    public static final By TERMS_CHECKBOX = By.cssSelector("input[data-testid='terms-checkbox']");
    
    /**
     * Locator for the signup submit button
     */
    public static final By SIGNUP_BUTTON = By.cssSelector("button[data-testid='signup-button']");
    
    /**
     * Locator for the email validation error message
     */
    public static final By EMAIL_ERROR = By.cssSelector("[data-testid='email-error']");
    
    /**
     * Locator for the password validation error message
     */
    public static final By PASSWORD_ERROR = By.cssSelector("[data-testid='password-error']");
    
    /**
     * Locator for the sign in link for existing users
     */
    public static final By SIGN_IN_LINK = By.cssSelector("a[data-testid='signin-link']");
    
    /**
     * Locator for the main page heading
     */
    public static final By PAGE_HEADING = By.cssSelector("h1[data-testid='signup-heading']");
    
    /**
     * Locator for the form container div
     */
    public static final By FORM_CONTAINER = By.cssSelector("div[data-testid='signup-container']");
    
    /**
     * Private constructor to prevent instantiation as this is a utility class with static members only
     */
    private SignupPageLocators() {
        throw new IllegalStateException("Utility class - cannot be instantiated");
    }
}