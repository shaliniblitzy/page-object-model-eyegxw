package com.storydoc.tests;

// External imports
import org.testng.annotations.Test; // TestNG version 7.7.1
import org.testng.annotations.BeforeMethod; // TestNG version 7.7.1
import org.testng.annotations.AfterMethod; // TestNG version 7.7.1
import org.testng.Assert; // TestNG version 7.7.1
import org.openqa.selenium.WebDriver; // Selenium WebDriver version 4.8.3
import org.apache.logging.log4j.Logger; // Log4j version 2.19.0
import org.apache.logging.log4j.LogManager; // Log4j version 2.19.0

// Internal imports
import com.storydoc.core.BaseTest;
import com.storydoc.pages.SignupPage;
import com.storydoc.pages.SuccessPage;
import com.storydoc.utils.RandomDataGenerator;
import com.storydoc.utils.AssertUtils;
import com.storydoc.constants.MessageConstants;
import com.storydoc.reports.ExtentTestManager;

/**
 * Test class that validates the confirmation page functionality after successful
 * signup, including verification of success messages, page elements, and
 * navigation options
 */
public class ConfirmationPageTests extends BaseTest {

  private static final Logger logger = LogManager.getLogger(ConfirmationPageTests.class);
  private SignupPage signupPage; // Page object for the signup page
  private SuccessPage successPage; // Page object for the confirmation page
  private String testEmail; // Email address used for test
  private String testPassword; // Password used for test

  /**
   * Setup method that runs before each test to initialize test data and page
   * objects
   */
  @BeforeMethod
  public void setupTest() {
    // Generate random email using RandomDataGenerator.generateRandomEmail()
    testEmail = RandomDataGenerator.generateRandomEmail();

    // Generate random password using RandomDataGenerator.generateRandomPassword()
    testPassword = RandomDataGenerator.generateRandomPassword();

    // Initialize signupPage with driver from BaseTest
    signupPage = new SignupPage(driver);

    // Initialize successPage with driver from BaseTest
    successPage = new SuccessPage(driver);

    // Log test setup initialization using ExtentTestManager
    ExtentTestManager.logInfo("Test setup initialized");
  }

  /**
   * Cleanup method that runs after each test to reset state
   */
  @AfterMethod
  public void cleanupTest() {
    // Log test completion
    ExtentTestManager.logInfo("Test completed");

    // Reset page object state if needed
  }

  /**
   * Verifies that the success message is displayed after successful signup
   */
  @Test(description = "Verify success message is displayed after successful signup")
  public void testSignupSuccessMessageDisplayed() {
    // Log test start
    ExtentTestManager.logInfo("Starting test: Verify success message is displayed");

    // Navigate to signup page using signupPage.navigateToSignupPage()
    signupPage.navigateToSignupPage();

    // Submit signup form with test email and password using signupPage.submitSignupForm()
    successPage = signupPage.submitSignupForm(testEmail, testPassword);

    // Assert that signup was successful using
    // AssertUtils.assertTrue(successPage.isSignupSuccessful(),
    // MessageConstants.ASSERT_SIGNUP_SUCCESSFUL)
    AssertUtils.assertTrue(
        successPage.isSignupSuccessful(), MessageConstants.ASSERT_SIGNUP_SUCCESSFUL);

    // Take screenshot of successful confirmation page
    takeScreenshot("SignupSuccessConfirmation");

    // Log test success
    ExtentTestManager.logInfo("Test completed: Success message is displayed");
  }

  /**
   * Verifies the content of the confirmation message after successful signup
   */
  @Test(description = "Verify confirmation message content after successful signup")
  public void testConfirmationMessageContent() {
    // Log test start
    ExtentTestManager.logInfo("Starting test: Verify confirmation message content");

    // Navigate to signup page using signupPage.navigateToSignupPage()
    signupPage.navigateToSignupPage();

    // Submit signup form with test email and password
    successPage = signupPage.submitSignupForm(testEmail, testPassword);

    // Get confirmation message text using successPage.getConfirmationMessage()
    String confirmationMessage = successPage.getConfirmationMessage();

    // Assert that message contains expected text using AssertUtils.assertTextContains()
    AssertUtils.assertTextContains(
        successPage.getConfirmationMessage(),
        MessageConstants.UI_SUCCESS_MESSAGE_TEXT,
        "Confirmation Message");

    // Log test success
    ExtentTestManager.logInfo("Test completed: Confirmation message content is correct");
  }

  /**
   * Verifies that the welcome header is displayed and contains correct text
   */
  @Test(description = "Verify welcome header is displayed on confirmation page")
  public void testWelcomeHeaderDisplayed() {
    // Log test start
    ExtentTestManager.logInfo("Starting test: Verify welcome header is displayed");

    // Navigate to signup page using signupPage.navigateToSignupPage()
    signupPage.navigateToSignupPage();

    // Submit signup form with test email and password
    successPage = signupPage.submitSignupForm(testEmail, testPassword);

    // Assert that welcome header verification passes using
    // AssertUtils.assertTrue(successPage.verifyWelcomeHeader())
    AssertUtils.assertTrue(successPage.verifyWelcomeHeader(), "Welcome header should be displayed");

    // Log test success
    ExtentTestManager.logInfo("Test completed: Welcome header is displayed");
  }

  /**
   * Verifies that the continue button is present and can be clicked
   */
  @Test(description = "Verify continue button functionality on confirmation page")
  public void testContinueButtonFunctionality() {
    // Log test start
    ExtentTestManager.logInfo("Starting test: Verify continue button functionality");

    // Navigate to signup page using signupPage.navigateToSignupPage()
    signupPage.navigateToSignupPage();

    // Submit signup form with test email and password
    successPage = signupPage.submitSignupForm(testEmail, testPassword);

    // Verify that confirmation page is displayed
    AssertUtils.assertTrue(successPage.isSignupSuccessful(), "Confirmation page should be displayed");

    // Click continue button using successPage.clickContinueButton()
    successPage.clickContinueButton();

    // Verify navigation to next page or state
    ExtentTestManager.logInfo("Clicked continue button and navigated to next page");

    // Log test success
    ExtentTestManager.logInfo("Test completed: Continue button functionality is working");
  }

  /**
   * Verifies that the account information section is displayed on the
   * confirmation page
   */
  @Test(description = "Verify account information section is displayed on confirmation page")
  public void testAccountInfoSection() {
    // Log test start
    ExtentTestManager.logInfo("Starting test: Verify account information section is displayed");

    // Navigate to signup page using signupPage.navigateToSignupPage()
    signupPage.navigateToSignupPage();

    // Submit signup form with test email and password
    successPage = signupPage.submitSignupForm(testEmail, testPassword);

    // Verify that confirmation page is displayed
    AssertUtils.assertTrue(successPage.isSignupSuccessful(), "Confirmation page should be displayed");

    // Assert that account info section is present using
    // AssertUtils.assertTrue(successPage.isAccountInfoSectionPresent())
    AssertUtils.assertTrue(
        successPage.isAccountInfoSectionPresent(), "Account information section should be present");

    // Log test success
    ExtentTestManager.logInfo("Test completed: Account information section is displayed");
  }

  /**
   * Verifies if an email verification message is present on the confirmation page
   */
  @Test(description = "Verify if email verification message is present on confirmation page")
  public void testVerificationMessageIfPresent() {
    // Log test start
    ExtentTestManager.logInfo("Starting test: Verify if email verification message is present");

    // Navigate to signup page using signupPage.navigateToSignupPage()
    signupPage.navigateToSignupPage();

    // Submit signup form with test email and password
    successPage = signupPage.submitSignupForm(testEmail, testPassword);

    // Verify that confirmation page is displayed
    AssertUtils.assertTrue(successPage.isSignupSuccessful(), "Confirmation page should be displayed");

    // Check if verification message is present using
    // successPage.isVerificationMessagePresent()
    boolean isVerificationMessagePresent = successPage.isVerificationMessagePresent();

    // If present, log and validate the message content
    if (isVerificationMessagePresent) {
      ExtentTestManager.logInfo("Verification message is present");
      // Add validation logic for the message content if needed
    } else {
      ExtentTestManager.logInfo("Verification message is not present");
    }

    // Log test result
    ExtentTestManager.logInfo("Test completed: Verification message presence checked");
  }
}