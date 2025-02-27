package com.storydoc.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.storydoc.locators.SuccessPageLocators;
import com.storydoc.constants.MessageConstants;
import com.storydoc.utils.LogUtils;
import com.storydoc.utils.WaitUtils;

/**
 * Page Object class that represents the success/confirmation page displayed after a successful signup on the Storydoc platform.
 * This class provides methods to verify successful account creation and interact with confirmation page elements.
 */
public class SuccessPage extends BasePage {
    
    private static final Logger logger = LogManager.getLogger(SuccessPage.class);
    
    /**
     * Initializes a new SuccessPage instance with the provided WebDriver
     *
     * @param driver WebDriver instance for browser interaction
     */
    public SuccessPage(WebDriver driver) {
        super(driver);
        LogUtils.info(SuccessPage.class, "SuccessPage initialized");
    }
    
    /**
     * Verifies if the signup was successful by checking for the presence of the success message
     *
     * @return True if success message is present, false otherwise
     */
    public boolean isSignupSuccessful() {
        LogUtils.info(SuccessPage.class, "Verifying successful signup");
        boolean isSuccessful = isElementPresent(SuccessPageLocators.SUCCESS_MESSAGE);
        LogUtils.info(SuccessPage.class, "Signup successful: " + isSuccessful);
        return isSuccessful;
    }
    
    /**
     * Gets the text of the success message displayed after signup
     *
     * @return Text content of the success message
     */
    public String getConfirmationMessage() {
        LogUtils.info(SuccessPage.class, "Getting confirmation message text");
        String message = getText(SuccessPageLocators.SUCCESS_MESSAGE);
        LogUtils.debug(SuccessPage.class, "Confirmation message: " + message);
        return message;
    }
    
    /**
     * Waits for the success message to appear after form submission
     *
     * @return True if message appears within timeout, false otherwise
     */
    public boolean waitForSuccessMessage() {
        LogUtils.info(SuccessPage.class, "Waiting for success message to appear");
        try {
            waitForElementVisible(SuccessPageLocators.SUCCESS_MESSAGE);
            LogUtils.info(SuccessPage.class, "Success message appeared");
            return true;
        } catch (TimeoutException e) {
            LogUtils.error(SuccessPage.class, "Success message did not appear within timeout", e);
            return false;
        }
    }
    
    /**
     * Verifies the welcome header contains expected text
     *
     * @return True if header is present and contains expected text
     */
    public boolean verifyWelcomeHeader() {
        LogUtils.info(SuccessPage.class, "Verifying welcome header");
        if (isElementPresent(SuccessPageLocators.WELCOME_HEADER)) {
            String headerText = getText(SuccessPageLocators.WELCOME_HEADER);
            boolean isValid = headerText.contains(MessageConstants.UI_WELCOME_HEADER_TEXT);
            LogUtils.info(SuccessPage.class, "Welcome header verification: " + isValid);
            return isValid;
        }
        LogUtils.info(SuccessPage.class, "Welcome header not found");
        return false;
    }
    
    /**
     * Clicks the continue button to proceed to the next page after successful signup
     */
    public void clickContinueButton() {
        LogUtils.info(SuccessPage.class, "Clicking continue button");
        click(SuccessPageLocators.CONTINUE_BUTTON);
        WaitUtils.waitForPageLoad(driver);
        LogUtils.info(SuccessPage.class, "Navigated to next page after clicking continue");
    }
    
    /**
     * Checks if an email verification message is present on the confirmation page
     *
     * @return True if verification message is present, false otherwise
     */
    public boolean isVerificationMessagePresent() {
        LogUtils.info(SuccessPage.class, "Checking for verification message");
        // Since we don't have a specific locator for the verification message,
        // we'll search for text content that might indicate verification is required
        boolean isPresent = isElementPresent(By.xpath("//*[contains(text(), 'verify') or contains(text(), 'verification') or contains(text(), 'confirm your email')]"));
        LogUtils.info(SuccessPage.class, "Verification message present: " + isPresent);
        return isPresent;
    }
    
    /**
     * Checks if the account information section is present on the success page
     *
     * @return True if account info section is present, false otherwise
     */
    public boolean isAccountInfoSectionPresent() {
        LogUtils.info(SuccessPage.class, "Checking for account information section");
        // Since we don't have a specific locator for the account info section,
        // we'll search for common elements or text that might indicate account info is displayed
        boolean isPresent = isElementPresent(By.xpath("//*[contains(text(), 'account') or contains(text(), 'profile') or contains(text(), 'user info')]"));
        LogUtils.info(SuccessPage.class, "Account information section present: " + isPresent);
        return isPresent;
    }
}