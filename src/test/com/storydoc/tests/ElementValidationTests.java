package com.storydoc.tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.storydoc.core.BaseTest;
import com.storydoc.pages.SignupPage;
import com.storydoc.locators.SignupPageLocators;
import com.storydoc.constants.TimeoutConstants;
import com.storydoc.utils.RandomDataGenerator;

/**
 * Test class that validates the presence, visibility, attributes, and behavior of UI elements
 * on the Storydoc signup page following the Page Object Model pattern.
 */
public class ElementValidationTests extends BaseTest {

    private SignupPage signupPage;

    /**
     * Sets up the test environment before each test method
     */
    @BeforeMethod
    public void setUp() {
        signupPage = new SignupPage(driver);
        signupPage.navigateToSignupPage();
        Assert.assertTrue(signupPage.isSignupPageLoaded(), "Signup page failed to load");
        logStep("Signup page loaded successfully for element validation");
    }

    /**
     * Cleans up the test environment after each test method
     */
    @AfterMethod
    public void tearDown() {
        logStep("Element validation test completed");
    }

    /**
     * Validates that the signup form and all its required elements are visible
     */
    @Test(groups = {"element-validation", "smoke"})
    public void testSignupFormVisibility() {
        logStep("Validating signup form element visibility");
        
        // Verify email field visibility
        WebElement emailField = driver.findElement(SignupPageLocators.EMAIL_FIELD);
        Assert.assertTrue(emailField.isDisplayed(), "Email field should be visible");
        
        // Verify password field visibility
        WebElement passwordField = driver.findElement(SignupPageLocators.PASSWORD_FIELD);
        Assert.assertTrue(passwordField.isDisplayed(), "Password field should be visible");
        
        // Verify terms checkbox visibility
        WebElement termsCheckbox = driver.findElement(SignupPageLocators.TERMS_CHECKBOX);
        Assert.assertTrue(termsCheckbox.isDisplayed(), "Terms checkbox should be visible");
        
        // Verify signup button visibility
        WebElement signupButton = driver.findElement(SignupPageLocators.SIGNUP_BUTTON);
        Assert.assertTrue(signupButton.isDisplayed(), "Signup button should be visible");
        
        takeScreenshot("signup-form-visibility");
    }

    /**
     * Validates that all required elements on the signup page are enabled
     */
    @Test(groups = {"element-validation"})
    public void testSignupElementsEnabled() {
        logStep("Validating that all signup elements are enabled");
        
        // Verify email field is enabled
        WebElement emailField = driver.findElement(SignupPageLocators.EMAIL_FIELD);
        Assert.assertTrue(emailField.isEnabled(), "Email field should be enabled");
        
        // Verify password field is enabled
        WebElement passwordField = driver.findElement(SignupPageLocators.PASSWORD_FIELD);
        Assert.assertTrue(passwordField.isEnabled(), "Password field should be enabled");
        
        // Verify terms checkbox is enabled
        WebElement termsCheckbox = driver.findElement(SignupPageLocators.TERMS_CHECKBOX);
        Assert.assertTrue(termsCheckbox.isEnabled(), "Terms checkbox should be enabled");
        
        // Verify signup button is enabled
        WebElement signupButton = driver.findElement(SignupPageLocators.SIGNUP_BUTTON);
        Assert.assertTrue(signupButton.isEnabled(), "Signup button should be enabled");
    }

    /**
     * Validates the attributes of the email field
     */
    @Test(groups = {"element-validation"})
    public void testEmailFieldAttributes() {
        logStep("Validating email field attributes");
        
        WebElement emailField = driver.findElement(SignupPageLocators.EMAIL_FIELD);
        
        // Validate type attribute
        String type = emailField.getAttribute("type");
        Assert.assertEquals(type, "email", "Email field should have type 'email'");
        
        // Validate required attribute if present
        boolean isRequired = Boolean.parseBoolean(emailField.getAttribute("required")) || 
                             "true".equalsIgnoreCase(emailField.getAttribute("required")) ||
                             emailField.getAttribute("required") != null;
        Assert.assertTrue(isRequired, "Email field should be required");
        
        // Validate placeholder if present
        String placeholder = emailField.getAttribute("placeholder");
        if (placeholder != null && !placeholder.isEmpty()) {
            Assert.assertFalse(placeholder.isEmpty(), "Email placeholder should not be empty if present");
            logStep("Email field placeholder: " + placeholder);
        }
        
        // Validate name attribute if present
        String name = emailField.getAttribute("name");
        if (name != null && !name.isEmpty()) {
            Assert.assertFalse(name.isEmpty(), "Email field name attribute should not be empty if present");
        }
        
        // Validate autocomplete attribute
        String autocomplete = emailField.getAttribute("autocomplete");
        if (autocomplete != null) {
            logStep("Email field autocomplete: " + autocomplete);
        }
    }

    /**
     * Validates the attributes of the password field
     */
    @Test(groups = {"element-validation"})
    public void testPasswordFieldAttributes() {
        logStep("Validating password field attributes");
        
        WebElement passwordField = driver.findElement(SignupPageLocators.PASSWORD_FIELD);
        
        // Validate type attribute
        String type = passwordField.getAttribute("type");
        Assert.assertEquals(type, "password", "Password field should have type 'password'");
        
        // Validate required attribute if present
        boolean isRequired = Boolean.parseBoolean(passwordField.getAttribute("required")) || 
                             "true".equalsIgnoreCase(passwordField.getAttribute("required")) ||
                             passwordField.getAttribute("required") != null;
        Assert.assertTrue(isRequired, "Password field should be required");
        
        // Validate placeholder if present
        String placeholder = passwordField.getAttribute("placeholder");
        if (placeholder != null && !placeholder.isEmpty()) {
            Assert.assertFalse(placeholder.isEmpty(), "Password placeholder should not be empty if present");
            logStep("Password field placeholder: " + placeholder);
        }
        
        // Validate minlength attribute if present
        String minLength = passwordField.getAttribute("minlength");
        if (minLength != null && !minLength.isEmpty()) {
            int minLengthValue = Integer.parseInt(minLength);
            Assert.assertTrue(minLengthValue >= 8, "Password field should have minlength of at least 8");
        }
        
        // Validate autocomplete attribute
        String autocomplete = passwordField.getAttribute("autocomplete");
        if (autocomplete != null) {
            logStep("Password field autocomplete: " + autocomplete);
        }
    }

    /**
     * Validates the attributes of the terms checkbox
     */
    @Test(groups = {"element-validation"})
    public void testTermsCheckboxAttributes() {
        logStep("Validating terms checkbox attributes");
        
        WebElement termsCheckbox = driver.findElement(SignupPageLocators.TERMS_CHECKBOX);
        
        // Validate type attribute
        String type = termsCheckbox.getAttribute("type");
        Assert.assertEquals(type, "checkbox", "Terms field should have type 'checkbox'");
        
        // Validate initial state (should be unchecked)
        Assert.assertFalse(termsCheckbox.isSelected(), "Terms checkbox should be unchecked initially");
        
        // Validate required attribute if present
        boolean isRequired = Boolean.parseBoolean(termsCheckbox.getAttribute("required")) || 
                             "true".equalsIgnoreCase(termsCheckbox.getAttribute("required")) ||
                             termsCheckbox.getAttribute("required") != null;
        
        if (isRequired) {
            logStep("Terms checkbox is required");
        }
        
        // Validate that we can check the box
        termsCheckbox.click();
        Assert.assertTrue(termsCheckbox.isSelected(), "Terms checkbox should be checked after clicking");
        
        // Validate that we can uncheck the box
        termsCheckbox.click();
        Assert.assertFalse(termsCheckbox.isSelected(), "Terms checkbox should be unchecked after clicking again");
    }

    /**
     * Validates the attributes of the signup button
     */
    @Test(groups = {"element-validation"})
    public void testSignupButtonAttributes() {
        logStep("Validating signup button attributes");
        
        WebElement signupButton = driver.findElement(SignupPageLocators.SIGNUP_BUTTON);
        
        // Validate type attribute
        String type = signupButton.getAttribute("type");
        Assert.assertTrue(type == null || type.equals("submit") || type.equals("button"), 
                         "Signup button should have type 'submit' or 'button'");
        
        // Validate text content
        String buttonText = signupButton.getText();
        Assert.assertFalse(buttonText.isEmpty(), "Signup button should have text");
        logStep("Signup button text: " + buttonText);
        
        // Validate disabled attribute
        boolean isDisabled = "true".equals(signupButton.getAttribute("disabled"));
        boolean isEnabled = signupButton.isEnabled();
        logStep("Signup button enabled state: " + isEnabled + ", disabled attribute: " + isDisabled);
        
        // Validate button classes if present
        String classes = signupButton.getAttribute("class");
        if (classes != null && !classes.isEmpty()) {
            logStep("Signup button classes: " + classes);
        }
    }

    /**
     * Validates the email field behavior with different inputs
     */
    @Test(groups = {"element-validation"})
    public void testEmailFieldValidation() {
        logStep("Validating email field validation behavior");
        
        // Test with invalid email format
        signupPage.enterEmail("invalid-email");
        
        // Click elsewhere to trigger validation
        driver.findElement(SignupPageLocators.PASSWORD_FIELD).click();
        
        // Check for error message
        Assert.assertTrue(signupPage.hasEmailError(), "Email error should be displayed for invalid email");
        
        String errorMessage = signupPage.getEmailErrorMessage();
        Assert.assertFalse(errorMessage.isEmpty(), "Email error message should not be empty");
        logStep("Email error message for invalid format: " + errorMessage);
        takeScreenshot("email-error-invalid-format");
        
        // Test with empty email
        signupPage.enterEmail("");
        
        // Click elsewhere to trigger validation
        driver.findElement(SignupPageLocators.PASSWORD_FIELD).click();
        
        // Check for error message
        if (signupPage.hasEmailError()) {
            errorMessage = signupPage.getEmailErrorMessage();
            logStep("Email error message for empty field: " + errorMessage);
        }
        
        // Test with valid email
        String validEmail = RandomDataGenerator.generateRandomEmail();
        logStep("Testing with valid email: " + validEmail);
        signupPage.enterEmail(validEmail);
        
        // Click elsewhere to trigger validation
        driver.findElement(SignupPageLocators.PASSWORD_FIELD).click();
        
        // Error should disappear
        Assert.assertFalse(signupPage.hasEmailError(), "Email error should not be displayed for valid email");
    }

    /**
     * Validates the password field behavior with different inputs
     */
    @Test(groups = {"element-validation"})
    public void testPasswordFieldValidation() {
        logStep("Validating password field validation behavior");
        
        // Test with short password
        signupPage.enterPassword("short");
        
        // Click elsewhere to trigger validation
        driver.findElement(SignupPageLocators.EMAIL_FIELD).click();
        
        // Check for error message
        Assert.assertTrue(signupPage.hasPasswordError(), "Password error should be displayed for short password");
        
        String errorMessage = signupPage.getPasswordErrorMessage();
        Assert.assertFalse(errorMessage.isEmpty(), "Password error message should not be empty");
        logStep("Password error message for short password: " + errorMessage);
        takeScreenshot("password-error-too-short");
        
        // Test with empty password
        signupPage.enterPassword("");
        
        // Click elsewhere to trigger validation
        driver.findElement(SignupPageLocators.EMAIL_FIELD).click();
        
        // Check for error message
        if (signupPage.hasPasswordError()) {
            errorMessage = signupPage.getPasswordErrorMessage();
            logStep("Password error message for empty field: " + errorMessage);
        }
        
        // Test with valid password
        String validPassword = RandomDataGenerator.generateRandomPassword();
        logStep("Testing with valid password format");
        signupPage.enterPassword(validPassword);
        
        // Click elsewhere to trigger validation
        driver.findElement(SignupPageLocators.EMAIL_FIELD).click();
        
        // Error should disappear
        Assert.assertFalse(signupPage.hasPasswordError(), "Password error should not be displayed for valid password");
    }

    /**
     * Validates interactions between form fields including focus and tab navigation
     */
    @Test(groups = {"element-validation"})
    public void testFormFieldInteractions() {
        logStep("Validating form field interactions");
        
        // Focus on email field
        WebElement emailField = driver.findElement(SignupPageLocators.EMAIL_FIELD);
        emailField.click();
        
        // Verify it has focus
        WebElement focusedElement = driver.switchTo().activeElement();
        Assert.assertEquals(focusedElement.getAttribute("data-testid"), emailField.getAttribute("data-testid"), 
                           "Email field should have focus");
        
        // Tab to password field
        focusedElement.sendKeys("\t");
        focusedElement = driver.switchTo().activeElement();
        WebElement passwordField = driver.findElement(SignupPageLocators.PASSWORD_FIELD);
        Assert.assertEquals(focusedElement.getAttribute("data-testid"), passwordField.getAttribute("data-testid"), 
                           "Password field should have focus after tab from email");
        
        // Tab to terms checkbox
        focusedElement.sendKeys("\t");
        focusedElement = driver.switchTo().activeElement();
        WebElement termsCheckbox = driver.findElement(SignupPageLocators.TERMS_CHECKBOX);
        Assert.assertEquals(focusedElement.getAttribute("data-testid"), termsCheckbox.getAttribute("data-testid"), 
                           "Terms checkbox should have focus after tab from password");
        
        // Tab to signup button
        focusedElement.sendKeys("\t");
        focusedElement = driver.switchTo().activeElement();
        WebElement signupButton = driver.findElement(SignupPageLocators.SIGNUP_BUTTON);
        Assert.assertEquals(focusedElement.getAttribute("data-testid"), signupButton.getAttribute("data-testid"), 
                           "Signup button should have focus after tab from terms checkbox");
    }

    /**
     * Validates form behavior when attempting to submit without filling in required fields
     */
    @Test(groups = {"element-validation"})
    public void testFormSubmitWithoutFillingFields() {
        logStep("Validating form submission without filling in required fields");
        
        // Try to submit form without filling any fields
        WebElement signupButton = driver.findElement(SignupPageLocators.SIGNUP_BUTTON);
        signupButton.click();
        
        // Verify validation errors appear
        boolean hasEmailError = signupPage.hasEmailError();
        boolean hasPasswordError = signupPage.hasPasswordError();
        
        Assert.assertTrue(hasEmailError || hasPasswordError, 
                         "Validation errors should appear when submitting empty form");
        
        // Get the error messages if they exist
        if (hasEmailError) {
            String emailError = signupPage.getEmailErrorMessage();
            Assert.assertFalse(emailError.isEmpty(), "Email error message should not be empty");
            logStep("Email error message on empty submission: " + emailError);
        }
        
        if (hasPasswordError) {
            String passwordError = signupPage.getPasswordErrorMessage();
            Assert.assertFalse(passwordError.isEmpty(), "Password error message should not be empty");
            logStep("Password error message on empty submission: " + passwordError);
        }
        
        takeScreenshot("form-submit-without-fields");
    }
}