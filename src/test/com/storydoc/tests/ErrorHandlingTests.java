package com.storydoc.tests;

// External imports
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.lang.reflect.Method;

// Internal imports
import com.storydoc.core.BaseTest;
import com.storydoc.pages.SignupPage;
import com.storydoc.pages.SuccessPage;
import com.storydoc.utils.RandomDataGenerator;
import com.storydoc.utils.WaitUtils;
import com.storydoc.locators.SignupPageLocators;
import com.storydoc.constants.TimeoutConstants;
import com.storydoc.core.RetryAnalyzer;
import com.storydoc.core.WebDriverManager;

/**
 * Test class for validating error handling scenarios in the Storydoc signup process.
 * Tests various error conditions and verifies that the framework properly handles
 * exceptions, timeouts, and error states while providing appropriate feedback to users.
 */
public class ErrorHandlingTests extends BaseTest {
    
    private static final Logger LOGGER = LogManager.getLogger(ErrorHandlingTests.class);
    private SignupPage signupPage;
    private SuccessPage successPage;
    private WebDriver driver;
    
    @BeforeMethod
    public void setUp(Method method) {
        super.setUp(method);
        driver = WebDriverManager.getDriver();
        signupPage = new SignupPage(driver);
        signupPage.navigateToSignupPage();
        Assert.assertTrue(signupPage.isSignupPageLoaded(), "Signup page should be loaded");
        logStep("Test setup completed");
    }
    
    @Test(description = "Test error handling for invalid email format")
    public void testInvalidEmailFormat() {
        logStep("Starting test: testInvalidEmailFormat");
        
        signupPage.enterEmail("invalid-email");
        signupPage.enterPassword(RandomDataGenerator.generateRandomPassword());
        signupPage.acceptTerms();
        signupPage.clickSignUp();
        
        Assert.assertTrue(signupPage.hasEmailError(), "Email error should be displayed");
        String errorMessage = signupPage.getEmailErrorMessage();
        Assert.assertTrue(errorMessage.toLowerCase().contains("valid") && 
                          errorMessage.toLowerCase().contains("email"), 
                         "Error message should indicate invalid email format");
        
        logStep("Test completed: testInvalidEmailFormat");
    }
    
    @Test(description = "Test error handling for empty email field")
    public void testEmptyEmail() {
        logStep("Starting test: testEmptyEmail");
        
        signupPage.enterEmail("");
        signupPage.enterPassword(RandomDataGenerator.generateRandomPassword());
        signupPage.acceptTerms();
        signupPage.clickSignUp();
        
        Assert.assertTrue(signupPage.hasEmailError(), "Email error should be displayed");
        String errorMessage = signupPage.getEmailErrorMessage();
        Assert.assertTrue(errorMessage.toLowerCase().contains("required") || 
                          errorMessage.toLowerCase().contains("empty"),
                         "Error message should indicate email is required");
        
        logStep("Test completed: testEmptyEmail");
    }
    
    @Test(description = "Test error handling for empty password field")
    public void testEmptyPassword() {
        logStep("Starting test: testEmptyPassword");
        
        signupPage.enterEmail(RandomDataGenerator.generateRandomEmail());
        signupPage.enterPassword("");
        signupPage.acceptTerms();
        signupPage.clickSignUp();
        
        Assert.assertTrue(signupPage.hasPasswordError(), "Password error should be displayed");
        String errorMessage = signupPage.getPasswordErrorMessage();
        Assert.assertTrue(errorMessage.toLowerCase().contains("required") || 
                          errorMessage.toLowerCase().contains("empty"),
                         "Error message should indicate password is required");
        
        logStep("Test completed: testEmptyPassword");
    }
    
    @Test(description = "Test error handling for weak password")
    public void testWeakPassword() {
        logStep("Starting test: testWeakPassword");
        
        signupPage.enterEmail(RandomDataGenerator.generateRandomEmail());
        signupPage.enterPassword("weak");
        signupPage.acceptTerms();
        signupPage.clickSignUp();
        
        Assert.assertTrue(signupPage.hasPasswordError(), "Password error should be displayed");
        String errorMessage = signupPage.getPasswordErrorMessage();
        Assert.assertTrue(errorMessage.toLowerCase().contains("weak") || 
                          errorMessage.toLowerCase().contains("strong") || 
                          errorMessage.toLowerCase().contains("length") || 
                          errorMessage.toLowerCase().contains("character"),
                         "Error message should indicate password is weak or doesn't meet complexity requirements");
        
        logStep("Test completed: testWeakPassword");
    }
    
    @Test(description = "Test error handling for terms not accepted")
    public void testTermsNotAccepted() {
        logStep("Starting test: testTermsNotAccepted");
        
        signupPage.enterEmail(RandomDataGenerator.generateRandomEmail());
        signupPage.enterPassword(RandomDataGenerator.generateRandomPassword());
        // Deliberately skip accepting terms
        signupPage.clickSignUp();
        
        // Find terms error message - this might need to be added to SignupPage class
        // or we can directly check for a specific element here
        boolean hasTermsError = driver.findElements(By.xpath("//*[contains(text(), 'terms') and contains(text(), 'accept') or contains(text(), 'check')]")).size() > 0;
        Assert.assertTrue(hasTermsError, "Terms acceptance error should be displayed");
        
        logStep("Test completed: testTermsNotAccepted");
    }
    
    @Test(description = "Test timeout handling", retryAnalyzer = RetryAnalyzer.class)
    public void testTimeoutHandling() {
        logStep("Starting test: testTimeoutHandling");
        
        try {
            // Try to find an element that doesn't exist with a short timeout
            WaitUtils.waitForElementVisible(driver, By.id("non-existent-element"), 1);
            Assert.fail("TimeoutException should have been thrown");
        } catch (TimeoutException e) {
            logStep("Successfully caught TimeoutException as expected");
            // This test is successful if we catch the timeout exception
        }
        
        // Verify we can continue test execution after handling the timeout
        Assert.assertTrue(signupPage.isSignupPageLoaded(), "Signup page should still be loaded after timeout");
        
        logStep("Test completed: testTimeoutHandling");
    }
    
    @Test(description = "Test stale element handling", retryAnalyzer = RetryAnalyzer.class)
    public void testStaleElementHandling() {
        logStep("Starting test: testStaleElementHandling");
        
        // Get reference to an element
        WebElement emailField = driver.findElement(SignupPageLocators.EMAIL_FIELD);
        
        // Force page refresh to make element stale
        driver.navigate().refresh();
        WaitUtils.waitForPageLoad(driver);
        
        try {
            // Try to interact with the stale element
            emailField.sendKeys("test@example.com");
            Assert.fail("StaleElementReferenceException should have been thrown");
        } catch (StaleElementReferenceException e) {
            logStep("Successfully caught StaleElementReferenceException as expected");
        }
        
        // Verify we can recover by finding the element again
        try {
            signupPage.enterEmail("recovered@example.com");
            logStep("Successfully recovered from stale element by finding it again");
        } catch (Exception e) {
            Assert.fail("Failed to recover from stale element: " + e.getMessage());
        }
        
        logStep("Test completed: testStaleElementHandling");
    }
    
    @Test(description = "Test element not interactable handling", retryAnalyzer = RetryAnalyzer.class)
    public void testElementNotInteractableHandling() {
        logStep("Starting test: testElementNotInteractableHandling");
        
        WebElement signupButton = driver.findElement(SignupPageLocators.SIGNUP_BUTTON);
        
        // Create a scenario where element exists but is not interactable
        // For test purposes, we'll use JavaScript to make an element temporarily hidden
        try {
            // Make the button hidden using JavaScript
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'none';", signupButton);
            
            // Try to click the hidden element
            signupButton.click();
            Assert.fail("ElementNotInteractableException should have been thrown");
        } catch (ElementNotInteractableException e) {
            logStep("Successfully caught ElementNotInteractableException as expected");
        }
        
        // Restore the button visibility and verify we can interact with it
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.display = 'block';", signupButton);
        WaitUtils.sleepInMillis(TimeoutConstants.BRIEF_PAUSE_MILLIS); // Brief pause to ensure styles are applied
        
        // Verify the element is now interactable again
        try {
            signupButton.click();
            logStep("Successfully recovered by making element interactable again");
        } catch (Exception e) {
            Assert.fail("Failed to interact with element after recovery: " + e.getMessage());
        }
        
        logStep("Test completed: testElementNotInteractableHandling");
    }
    
    @Test(description = "Test handling of multiple form errors")
    public void testMultipleErrorHandling() {
        logStep("Starting test: testMultipleErrorHandling");
        
        // Enter invalid data in multiple fields to trigger multiple errors
        signupPage.enterEmail("invalid-email");
        signupPage.enterPassword("weak");
        // Skip accepting terms
        signupPage.clickSignUp();
        
        // Verify multiple errors are displayed
        Assert.assertTrue(signupPage.hasEmailError(), "Email error should be displayed");
        Assert.assertTrue(signupPage.hasPasswordError(), "Password error should be displayed");
        
        // Check for terms error as well - this might need a new method in SignupPage
        boolean hasTermsError = driver.findElements(By.xpath("//*[contains(text(), 'terms') and contains(text(), 'accept') or contains(text(), 'check')]")).size() > 0;
        Assert.assertTrue(hasTermsError, "Terms acceptance error should be displayed");
        
        // Verify appropriate error messages
        String emailError = signupPage.getEmailErrorMessage();
        String passwordError = signupPage.getPasswordErrorMessage();
        
        Assert.assertTrue(emailError.toLowerCase().contains("valid") && 
                          emailError.toLowerCase().contains("email"), 
                         "Email error message should indicate invalid format");
        
        Assert.assertTrue(passwordError.toLowerCase().contains("weak") || 
                          passwordError.toLowerCase().contains("strong") || 
                          passwordError.toLowerCase().contains("length") || 
                          passwordError.toLowerCase().contains("character"),
                         "Password error message should indicate strength issues");
        
        logStep("Test completed: testMultipleErrorHandling");
    }
    
    @Test(description = "Test recovery from form errors")
    public void testRecoveryFromErrors() {
        logStep("Starting test: testRecoveryFromErrors");
        
        // First, submit the form with invalid data
        signupPage.enterEmail("invalid-email");
        signupPage.enterPassword("weak");
        signupPage.clickSignUp();
        
        // Verify errors are displayed
        Assert.assertTrue(signupPage.hasEmailError(), "Email error should be displayed");
        Assert.assertTrue(signupPage.hasPasswordError(), "Password error should be displayed");
        
        // Now correct all errors and resubmit
        String validEmail = RandomDataGenerator.generateRandomEmail();
        String validPassword = RandomDataGenerator.generateRandomPassword();
        
        signupPage.enterEmail(validEmail);
        signupPage.enterPassword(validPassword);
        signupPage.acceptTerms();
        
        // Attempt to submit the form again
        successPage = signupPage.clickSignUp();
        
        // Verify successful signup
        Assert.assertTrue(successPage.isSignupSuccessful(), "Signup should succeed after correcting errors");
        
        logStep("Test completed: testRecoveryFromErrors");
    }
}