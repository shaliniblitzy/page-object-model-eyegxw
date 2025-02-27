package com.storydoc.tests;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.lang.reflect.Method;

import com.storydoc.core.BaseTest;
import com.storydoc.pages.SignupPage;
import com.storydoc.utils.RandomDataGenerator;
import com.storydoc.constants.MessageConstants;

/**
 * Test class that validates form input fields and error messages on the Storydoc signup page.
 * This class contains test cases for various validation scenarios including empty fields,
 * invalid input formats, and verification of appropriate error messages.
 */
public class FormValidationTests extends BaseTest {
    
    private SignupPage signupPage;
    private static final Logger logger = LogManager.getLogger(FormValidationTests.class);
    
    /**
     * Setup method executed before each test method
     */
    @BeforeMethod
    public void setUp(Method method) {
        super.setUp(method);
        signupPage = new SignupPage(driver);
        signupPage.navigateToSignupPage();
        logStep("Test setup complete - navigated to signup page");
    }
    
    /**
     * Test validation for empty email field
     */
    @Test(groups = {"validation", "regression"})
    public void testEmptyEmailValidation() {
        logStep("Beginning empty email validation test");
        
        // Enter valid password but leave email empty
        signupPage.enterPassword(RandomDataGenerator.generateRandomPassword());
        
        // Click sign up button
        signupPage.clickSignUp();
        
        // Verify email error is displayed
        Assert.assertTrue(signupPage.hasEmailError(), "Email error should be displayed");
        
        // Verify error message matches expected
        Assert.assertEquals(signupPage.getEmailErrorMessage(), MessageConstants.UI_EMAIL_ERROR_REQUIRED, 
            "Error message for empty email field does not match expected");
        
        logStep("Empty email validation test completed successfully");
    }
    
    /**
     * Test validation for invalid email format
     */
    @Test(groups = {"validation", "regression"})
    public void testInvalidEmailFormatValidation() {
        logStep("Beginning invalid email format validation test");
        
        // Enter invalid email format
        signupPage.enterEmail("invalidemail");
        
        // Enter valid password
        signupPage.enterPassword(RandomDataGenerator.generateRandomPassword());
        
        // Click sign up button
        signupPage.clickSignUp();
        
        // Verify email error is displayed
        Assert.assertTrue(signupPage.hasEmailError(), "Email format error should be displayed");
        
        // Verify error message matches expected
        Assert.assertEquals(signupPage.getEmailErrorMessage(), MessageConstants.UI_EMAIL_ERROR_INVALID_FORMAT,
            "Error message for invalid email format does not match expected");
        
        logStep("Invalid email format validation test completed successfully");
    }
    
    /**
     * Test validation for empty password field
     */
    @Test(groups = {"validation", "regression"})
    public void testEmptyPasswordValidation() {
        logStep("Beginning empty password validation test");
        
        // Enter valid email but leave password empty
        signupPage.enterEmail(RandomDataGenerator.generateRandomEmail());
        
        // Click sign up button
        signupPage.clickSignUp();
        
        // Verify password error is displayed
        Assert.assertTrue(signupPage.hasPasswordError(), "Password error should be displayed");
        
        // Verify error message matches expected
        Assert.assertEquals(signupPage.getPasswordErrorMessage(), MessageConstants.UI_PASSWORD_ERROR_REQUIRED,
            "Error message for empty password field does not match expected");
        
        logStep("Empty password validation test completed successfully");
    }
    
    /**
     * Test validation for password length requirements
     */
    @Test(groups = {"validation", "regression"})
    public void testPasswordLengthValidation() {
        logStep("Beginning password length validation test");
        
        // Enter valid email
        signupPage.enterEmail(RandomDataGenerator.generateRandomEmail());
        
        // Enter password that is too short
        signupPage.enterPassword(RandomDataGenerator.generateRandomString(4));
        
        // Click sign up button
        signupPage.clickSignUp();
        
        // Verify password error is displayed
        Assert.assertTrue(signupPage.hasPasswordError(), "Password length error should be displayed");
        
        // Verify error message matches expected
        Assert.assertEquals(signupPage.getPasswordErrorMessage(), MessageConstants.UI_PASSWORD_ERROR_TOO_SHORT,
            "Error message for password too short does not match expected");
        
        logStep("Password length validation test completed successfully");
    }
    
    /**
     * Test validation for password complexity requirements
     */
    @Test(groups = {"validation", "regression"})
    public void testPasswordComplexityValidation() {
        logStep("Beginning password complexity validation test");
        
        // Enter valid email
        signupPage.enterEmail(RandomDataGenerator.generateRandomEmail());
        
        // Enter password without required complexity
        signupPage.enterPassword("password12345");
        
        // Click sign up button
        signupPage.clickSignUp();
        
        // Verify password error is displayed
        Assert.assertTrue(signupPage.hasPasswordError(), "Password complexity error should be displayed");
        
        // Verify error message contains complexity requirements
        String errorMessage = signupPage.getPasswordErrorMessage();
        Assert.assertTrue(errorMessage.contains("special") || 
                         errorMessage.contains("uppercase") ||
                         errorMessage.contains("character") ||
                         errorMessage.contains("requirements"),
            "Error message for password complexity should mention requirements");
        
        logStep("Password complexity validation test completed successfully");
    }
    
    /**
     * Test validation for unchecked terms checkbox
     */
    @Test(groups = {"validation", "regression"})
    public void testTermsCheckboxValidation() {
        logStep("Beginning terms checkbox validation test");
        
        // Enter valid email
        signupPage.enterEmail(RandomDataGenerator.generateRandomEmail());
        
        // Enter valid password
        signupPage.enterPassword(RandomDataGenerator.generateRandomPassword());
        
        // Do not check terms checkbox
        
        // Click sign up button
        signupPage.clickSignUp();
        
        // Verify terms error message is displayed
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains(MessageConstants.UI_TERMS_REQUIRED_MESSAGE),
            "Error message for terms not accepted should be visible");
        
        logStep("Terms checkbox validation test completed successfully");
    }
    
    /**
     * Test multiple validation errors appearing simultaneously
     */
    @Test(groups = {"validation", "regression"})
    public void testMultipleValidationErrors() {
        logStep("Beginning multiple validation errors test");
        
        // Leave all fields empty
        
        // Click sign up button
        signupPage.clickSignUp();
        
        // Verify email error is displayed
        Assert.assertTrue(signupPage.hasEmailError(), "Email error should be displayed");
        
        // Verify password error is displayed
        Assert.assertTrue(signupPage.hasPasswordError(), "Password error should be displayed");
        
        // Verify terms error message is displayed
        String pageSource = driver.getPageSource();
        Assert.assertTrue(pageSource.contains(MessageConstants.UI_TERMS_REQUIRED_MESSAGE),
            "Error message for terms not accepted should be visible");
        
        // Verify all error messages match expected constants
        Assert.assertEquals(signupPage.getEmailErrorMessage(), MessageConstants.UI_EMAIL_ERROR_REQUIRED,
            "Error message for empty email field does not match expected");
        Assert.assertEquals(signupPage.getPasswordErrorMessage(), MessageConstants.UI_PASSWORD_ERROR_REQUIRED,
            "Error message for empty password field does not match expected");
        
        logStep("Multiple validation errors test completed successfully");
    }
}