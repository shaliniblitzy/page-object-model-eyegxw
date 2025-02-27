package com.storydoc.constants;

/**
 * A utility class containing all the message constants used throughout the test framework
 * for consistency and maintainability.
 */
public class MessageConstants {

    // Logging Messages
    public static final String LOG_NAVIGATING_TO_SIGNUP = "Navigating to Storydoc signup page";
    public static final String LOG_ENTERING_EMAIL = "Entering email: {0}";
    public static final String LOG_ENTERING_PASSWORD = "Entering password";
    public static final String LOG_ACCEPTING_TERMS = "Accepting terms and conditions";
    public static final String LOG_CLICKING_SIGNUP = "Clicking signup button";
    public static final String LOG_VERIFYING_SUCCESS = "Verifying successful signup";
    public static final String LOG_TEST_STARTING = "Starting test: {0}";
    public static final String LOG_TEST_COMPLETED = "Test completed successfully: {0}";
    public static final String LOG_TEST_FAILED = "Test failed: {0} with exception: {1}";
    
    // Assertion Messages
    public static final String ASSERT_PAGE_LOADED = "Page {0} should be loaded";
    public static final String ASSERT_EMAIL_FIELD_PRESENT = "Email field should be present on signup page";
    public static final String ASSERT_PASSWORD_FIELD_PRESENT = "Password field should be present on signup page";
    public static final String ASSERT_TERMS_CHECKBOX_PRESENT = "Terms checkbox should be present on signup page";
    public static final String ASSERT_SIGNUP_BUTTON_PRESENT = "Signup button should be present on signup page";
    public static final String ASSERT_SIGNUP_SUCCESSFUL = "Signup should be successful";
    public static final String ASSERT_EMAIL_ERROR_DISPLAYED = "Email error message should be displayed";
    public static final String ASSERT_PASSWORD_ERROR_DISPLAYED = "Password error message should be displayed";
    public static final String ASSERT_ELEMENT_VISIBLE = "Element should be visible: {0}";
    public static final String ASSERT_ELEMENT_CLICKABLE = "Element should be clickable: {0}";
    
    // UI Text Constants
    public static final String UI_SUCCESS_MESSAGE_TEXT = "Your account has been created successfully";
    public static final String UI_WELCOME_HEADER_TEXT = "Welcome to Storydoc";
    public static final String UI_EMAIL_ERROR_INVALID_FORMAT = "Please enter a valid email address";
    public static final String UI_EMAIL_ERROR_REQUIRED = "Email is required";
    public static final String UI_PASSWORD_ERROR_TOO_SHORT = "Password must be at least 8 characters long";
    public static final String UI_PASSWORD_ERROR_REQUIRED = "Password is required";
    public static final String UI_TERMS_REQUIRED_MESSAGE = "You must accept the terms and conditions";
    
    // Error Messages
    public static final String ERROR_TIMEOUT_WAITING_FOR_ELEMENT = "Timeout waiting for element: {0}";
    public static final String ERROR_ELEMENT_NOT_FOUND = "Element not found: {0}";
    public static final String ERROR_UNEXPECTED_PAGE_STATE = "Unexpected page state. Expected: {0}, Actual: {1}";
    public static final String ERROR_STALE_ELEMENT = "Stale element reference: {0}";
    public static final String ERROR_BROWSER_NOT_RESPONDING = "Browser not responding";
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     * 
     * @throws IllegalStateException if an attempt is made to instantiate this class
     */
    private MessageConstants() {
        throw new IllegalStateException("Utility class - should not be instantiated");
    }
}