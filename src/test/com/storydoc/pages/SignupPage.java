package com.storydoc.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.storydoc.pages.BasePage;
import com.storydoc.locators.SignupPageLocators;
import com.storydoc.pages.SuccessPage;
import com.storydoc.models.UserAccount;
import com.storydoc.config.ConfigurationManager;
import com.storydoc.utils.LogUtils;
import com.storydoc.utils.WaitUtils;

/**
 * Page Object class representing the Storydoc signup page. Provides methods to interact with the signup form elements and validate form state.
 */
public class SignupPage extends BasePage {
    
    private static final Logger logger = LogManager.getLogger(SignupPage.class);
    
    /**
     * Initializes a new SignupPage instance with the provided WebDriver
     *
     * @param driver WebDriver instance for browser interaction
     */
    public SignupPage(WebDriver driver) {
        super(driver);
        LogUtils.info(SignupPage.class, "SignupPage object created");
    }
    
    /**
     * Navigates to the Storydoc signup page using the URL from ConfigurationManager
     *
     * @return Returns this page object for method chaining
     */
    public SignupPage navigateToSignupPage() {
        String signupUrl = ConfigurationManager.getInstance().getSignupUrl();
        LogUtils.info(SignupPage.class, "Navigating to signup page: " + signupUrl);
        navigateTo(signupUrl);
        WaitUtils.waitForPageLoad(driver);
        LogUtils.info(SignupPage.class, "Successfully navigated to signup page");
        return this;
    }
    
    /**
     * Verifies if the signup page is loaded by checking for the presence of the signup form
     *
     * @return True if the signup form is present, false otherwise
     */
    public boolean isSignupPageLoaded() {
        boolean isLoaded = isElementPresent(SignupPageLocators.SIGNUP_FORM);
        LogUtils.info(SignupPage.class, "Signup page loaded: " + isLoaded);
        return isLoaded;
    }
    
    /**
     * Enters the provided email address in the email field
     *
     * @param email The email address to enter
     * @return Returns this page object for method chaining
     */
    public SignupPage enterEmail(String email) {
        LogUtils.info(SignupPage.class, "Entering email: " + email);
        type(SignupPageLocators.EMAIL_FIELD, email);
        return this;
    }
    
    /**
     * Enters the provided password in the password field
     *
     * @param password The password to enter
     * @return Returns this page object for method chaining
     */
    public SignupPage enterPassword(String password) {
        LogUtils.info(SignupPage.class, "Entering password");
        type(SignupPageLocators.PASSWORD_FIELD, password);
        return this;
    }
    
    /**
     * Clicks the terms and conditions checkbox to accept them
     *
     * @return Returns this page object for method chaining
     */
    public SignupPage acceptTerms() {
        LogUtils.info(SignupPage.class, "Accepting terms and conditions");
        click(SignupPageLocators.TERMS_CHECKBOX);
        return this;
    }
    
    /**
     * Clicks the signup button to submit the form and navigate to the success page
     *
     * @return Returns a new SuccessPage object representing the success page after signup
     */
    public SuccessPage clickSignUp() {
        LogUtils.info(SignupPage.class, "Clicking signup button");
        click(SignupPageLocators.SIGNUP_BUTTON);
        WaitUtils.waitForPageLoad(driver);
        return new SuccessPage(driver);
    }
    
    /**
     * Fills in all form fields and submits the signup form in one operation
     *
     * @param email The email address to enter
     * @param password The password to enter
     * @return Returns a new SuccessPage object representing the success page after signup
     */
    public SuccessPage submitSignupForm(String email, String password) {
        LogUtils.info(SignupPage.class, "Submitting complete signup form with email: " + email);
        return enterEmail(email)
               .enterPassword(password)
               .acceptTerms()
               .clickSignUp();
    }
    
    /**
     * Submits the signup form using user account data from a UserAccount model object
     *
     * @param userAccount UserAccount object containing signup data
     * @return Returns a new SuccessPage object representing the success page after signup
     */
    public SuccessPage submitSignupForm(UserAccount userAccount) {
        LogUtils.info(SignupPage.class, "Submitting signup form with UserAccount data: " + userAccount.getEmail());
        enterEmail(userAccount.getEmail());
        enterPassword(userAccount.getPassword());
        
        if (userAccount.isTermsAccepted()) {
            acceptTerms();
        }
        
        return clickSignUp();
    }
    
    /**
     * Gets the error message text displayed for the email field
     *
     * @return The error message text, or empty string if no error is displayed
     */
    public String getEmailErrorMessage() {
        if (isElementVisible(SignupPageLocators.EMAIL_ERROR)) {
            String errorMsg = getText(SignupPageLocators.EMAIL_ERROR);
            LogUtils.debug(SignupPage.class, "Email error message: " + errorMsg);
            return errorMsg;
        }
        LogUtils.debug(SignupPage.class, "No email error message displayed");
        return "";
    }
    
    /**
     * Gets the error message text displayed for the password field
     *
     * @return The error message text, or empty string if no error is displayed
     */
    public String getPasswordErrorMessage() {
        if (isElementVisible(SignupPageLocators.PASSWORD_ERROR)) {
            String errorMsg = getText(SignupPageLocators.PASSWORD_ERROR);
            LogUtils.debug(SignupPage.class, "Password error message: " + errorMsg);
            return errorMsg;
        }
        LogUtils.debug(SignupPage.class, "No password error message displayed");
        return "";
    }
    
    /**
     * Checks if an error message is displayed for the email field
     *
     * @return True if an error message is displayed, false otherwise
     */
    public boolean hasEmailError() {
        boolean hasError = isElementVisible(SignupPageLocators.EMAIL_ERROR);
        LogUtils.debug(SignupPage.class, "Has email error: " + hasError);
        return hasError;
    }
    
    /**
     * Checks if an error message is displayed for the password field
     *
     * @return True if an error message is displayed, false otherwise
     */
    public boolean hasPasswordError() {
        boolean hasError = isElementVisible(SignupPageLocators.PASSWORD_ERROR);
        LogUtils.debug(SignupPage.class, "Has password error: " + hasError);
        return hasError;
    }
    
    /**
     * Gets the text of the page heading element
     *
     * @return The text content of the page heading
     */
    public String getPageHeading() {
        String heading = getText(SignupPageLocators.PAGE_HEADING);
        LogUtils.debug(SignupPage.class, "Page heading: " + heading);
        return heading;
    }
}