package com.storydoc.locators;

import org.openqa.selenium.By; // version 4.8.x

/**
 * Locator repository class containing element identifiers for the success page
 * shown after successful signup.
 * 
 * This class follows the Locator Repository pattern for improved maintainability
 * by centralizing all element locators for the success page in a dedicated file.
 */
public class SuccessPageLocators {
    
    /**
     * Locator for the success message element displayed after successful signup
     * Uses CSS selector with data-testid attribute for reliable identification
     */
    public static final By SUCCESS_MESSAGE = By.cssSelector("[data-testid='signup-success-message']");
    
    /**
     * Locator for the welcome header on the success page
     * Uses CSS selector with data-testid attribute for reliable identification
     */
    public static final By WELCOME_HEADER = By.cssSelector("h1[data-testid='welcome-header']");
    
    /**
     * Locator for the continue button that navigates to the dashboard
     * Uses CSS selector with data-testid attribute for reliable identification
     */
    public static final By CONTINUE_BUTTON = By.cssSelector("button[data-testid='continue-button']");
}