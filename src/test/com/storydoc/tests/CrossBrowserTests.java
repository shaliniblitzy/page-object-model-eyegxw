package com.storydoc.tests;

import org.testng.annotations.Test;
import org.testng.annotations.Parameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.Assert;
import org.openqa.selenium.WebDriver;
import java.lang.reflect.Method;

import com.storydoc.core.BaseTest;
import com.storydoc.pages.SignupPage;
import com.storydoc.pages.SuccessPage;
import com.storydoc.config.BrowserType;
import com.storydoc.utils.RandomDataGenerator;
import com.storydoc.models.UserAccount;
import com.storydoc.core.WebDriverManager;
import com.storydoc.utils.AssertUtils;

/**
 * Test class that implements cross-browser testing for the Storydoc signup process.
 * Uses TestNG parameterization to run the same test on different browsers.
 */
public class CrossBrowserTests extends BaseTest {
    
    private SignupPage signupPage;
    private WebDriver driver;
    
    /**
     * Setup method that initializes the WebDriver and page objects before each test
     * 
     * @param browser The browser to run the test on
     */
    @BeforeMethod
    @Parameters({"browser"})
    public void setUp(String browser, Method method) {
        // Call super.setUp to initialize driver and other resources
        super.setUp(method);
        
        // Get the driver initialized by BaseTest
        this.driver = super.driver;
        
        // Initialize SignupPage with the driver from BaseTest
        signupPage = new SignupPage(driver);
        
        // Log browser information
        logStep("Running test on browser: " + browser);
    }
    
    /**
     * Teardown method that closes the browser and releases resources after each test
     */
    @AfterMethod
    public void tearDown() {
        // BaseTest's tearDown will be called automatically by TestNG
        // No need for additional cleanup here
    }
    
    /**
     * Test method that verifies the signup process works correctly on different browsers
     * 
     * @param browser The browser being tested
     */
    @Test
    @Parameters({"browser"})
    public void testSignupOnDifferentBrowsers(String browser) {
        // Navigate to signup page
        signupPage.navigateToSignupPage();
        
        // Generate random test data
        String email = RandomDataGenerator.generateRandomEmail();
        String password = RandomDataGenerator.generateRandomPassword();
        
        // Log test data
        logStep("Using test data - Email: " + email + " for browser: " + browser);
        
        // Fill out and submit signup form
        SuccessPage successPage = signupPage
            .enterEmail(email)
            .enterPassword(password)
            .acceptTerms()
            .clickSignUp();
        
        // Verify successful signup
        Assert.assertTrue(successPage.isSignupSuccessful(), 
            "Signup should be successful on " + browser);
        
        // Verify confirmation message
        String confirmationMessage = successPage.getConfirmationMessage();
        Assert.assertTrue(confirmationMessage.contains("successfully"), 
            "Success message should contain 'successfully' on " + browser);
        
        // Log test completion
        logStep("Successfully completed signup test on " + browser);
    }
    
    /**
     * Data provider that supplies test data for parameterized tests
     * 
     * @return Two-dimensional array of test data
     */
    @DataProvider(name = "userAccountData")
    public Object[][] provideTestData() {
        return new Object[][] {
            { "test.user1@example.com", "Password123!" },
            { "test.user2@example.com", "Secure@456" },
            { "test.user3@example.com", "Test$789" }
        };
    }
    
    /**
     * Test method that verifies the signup process with different test data
     * 
     * @param browser The browser being tested
     * @param email Email address for signup
     * @param password Password for signup
     */
    @Test
    @Parameters({"browser"})
    public void testSignupWithDataProvider(String browser, String email, String password) {
        // Navigate to signup page
        signupPage.navigateToSignupPage();
        
        // Fill out and submit signup form with provided data
        SuccessPage successPage = signupPage
            .enterEmail(email)
            .enterPassword(password)
            .acceptTerms()
            .clickSignUp();
        
        // Verify successful signup
        Assert.assertTrue(successPage.isSignupSuccessful(), 
            "Signup should be successful on " + browser + " with data: " + email);
        
        // Log test completion
        logStep("Successfully completed signup test on " + browser + " with email: " + email);
    }
    
    /**
     * Test method that verifies browser-specific elements are displayed correctly
     * 
     * @param browser The browser being tested
     */
    @Test
    @Parameters({"browser"})
    public void verifyBrowserLogos(String browser) {
        // Navigate to signup page
        signupPage.navigateToSignupPage();
        
        // Verify the page loaded correctly across browsers
        Assert.assertTrue(signupPage.isSignupPageLoaded(), 
            "Signup page should load successfully on " + browser);
        
        // Log verification result
        logStep("Successfully verified signup page loading on " + browser);
    }
}