package com.storydoc.tests;

// External imports
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.Assert;
import org.apache.logging.log4j.Logger; // v2.19.0
import org.apache.logging.log4j.LogManager; // v2.19.0

// Internal imports
import com.storydoc.core.BaseTest;
import com.storydoc.pages.SignupPage;
import com.storydoc.pages.SuccessPage;
import com.storydoc.models.UserAccount;
import com.storydoc.core.RetryAnalyzer;
import com.storydoc.utils.RandomDataGenerator;
import com.storydoc.utils.AssertUtils;

/**
 * Test class containing automated test cases for the Storydoc signup process using 
 * Selenium WebDriver and Page Object Model pattern. Validates the positive signup 
 * flow with different test data and verifies successful account creation.
 */
public class SignupTests extends BaseTest {
    
    private static final Logger logger = LogManager.getLogger(SignupTests.class);
    private SignupPage signupPage;
    private SuccessPage successPage;
    
    /**
     * Setup method that runs before each test method. Initializes the page objects.
     */
    @BeforeMethod
    public void setUp() {
        signupPage = new SignupPage(driver);
        logger.info("SignupTests setup complete, initialized page objects");
    }
    
    /**
     * Cleanup method that runs after each test method. Performs any necessary cleanup.
     */
    @AfterMethod
    public void tearDown() {
        logger.info("SignupTests teardown complete");
        successPage = null;
        signupPage = null;
    }
    
    /**
     * Tests the basic positive signup flow with valid credentials.
     */
    @Test(description = "Verify successful signup with valid credentials", 
          priority = 1, 
          retryAnalyzer = RetryAnalyzer.class)
    public void testPositiveSignupFlow() {
        logger.info("Starting positive signup flow test");
        
        // Generate random test data for email and password
        String email = RandomDataGenerator.generateRandomEmail();
        String password = RandomDataGenerator.generateRandomPassword();
        
        // Navigate to signup page
        signupPage.navigateToSignupPage();
        
        // Log the step
        logStep("Entering email: " + email + " and password");
        
        // Enter credentials and accept terms
        signupPage.enterEmail(email);
        signupPage.enterPassword(password);
        signupPage.acceptTerms();
        
        // Log the submission step
        logStep("Submitting signup form");
        
        // Submit form and get success page
        successPage = signupPage.clickSignUp();
        
        // Verify success
        AssertUtils.assertTrue(successPage.isSignupSuccessful(), 
            "Signup should be successful");
        
        // Verify success message contains expected text
        String confirmationMessage = successPage.getConfirmationMessage();
        AssertUtils.assertTrue(confirmationMessage.contains("successfully"), 
            "Success message should contain 'successfully' but was: " + confirmationMessage);
        
        // Take screenshot of successful signup
        takeScreenshot("Successful_Signup_Completed");
        
        logger.info("Positive signup flow test passed successfully");
    }
    
    /**
     * Tests the signup flow with various test data provided through a data provider.
     * 
     * @param userAccount User account data for testing
     */
    @Test(description = "Verify signup with different test data", 
          priority = 2, 
          dataProvider = "signupTestData", 
          retryAnalyzer = RetryAnalyzer.class)
    public void testSignupWithDifferentDataSets(UserAccount userAccount) {
        logger.info("Testing signup with data: " + userAccount.getEmail());
        
        // Navigate to signup page
        signupPage.navigateToSignupPage();
        
        // Log the step
        logStep("Submitting signup form with test data: " + userAccount.getEmail());
        
        // Submit form using account data
        successPage = signupPage.submitSignupForm(userAccount);
        
        // Wait for success message to appear
        successPage.waitForSuccessMessage();
        
        // Verify success
        AssertUtils.assertTrue(successPage.isSignupSuccessful(), 
            "Signup should be successful with test data: " + userAccount.getEmail());
        
        // Verify success message contains expected text
        String confirmationMessage = successPage.getConfirmationMessage();
        AssertUtils.assertTrue(confirmationMessage.contains("successfully"), 
            "Success message should contain 'successfully' but was: " + confirmationMessage);
        
        // Take screenshot of successful signup
        takeScreenshot("Successful_Signup_TestData_" + userAccount.getEmail().replaceAll("[^a-zA-Z0-9]", "_"));
        
        logger.info("Data-driven signup test passed successfully for: " + userAccount.getEmail());
    }
    
    /**
     * Data provider method that supplies test data for signup tests.
     * 
     * @return Array of UserAccount objects with different test data
     */
    @DataProvider(name = "signupTestData")
    public Object[][] signupTestData() {
        // Create array to hold test data
        return new Object[][] {
            { UserAccount.createValidAccount() },
            { UserAccount.createCustomAccount("test_custom1@example.com", "Password@123") },
            { UserAccount.createCustomAccount("test_custom2@example.com", "StrongP@ssw0rd") }
        };
    }
}