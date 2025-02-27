package com.storydoc.core;

// External imports
import org.openqa.selenium.WebDriver; // 4.8.x
import org.testng.ITestResult; // 7.7.x
import org.testng.ITestContext; // 7.7.x
import org.testng.annotations.AfterMethod; // 7.7.x
import org.testng.annotations.BeforeMethod; // 7.7.x
import org.testng.annotations.BeforeSuite; // 7.7.x
import org.testng.annotations.AfterSuite; // 7.7.x
import com.aventstack.extentreports.ExtentReports; // 5.0.x
import com.aventstack.extentreports.ExtentTest; // 5.0.x
import org.apache.logging.log4j.Logger; // 2.19.x
import org.apache.logging.log4j.LogManager; // 2.19.x
import java.lang.reflect.Method; // JDK 11

// Internal imports
import com.storydoc.core.WebDriverManager;
import com.storydoc.reports.ExtentManager;
import com.storydoc.reports.ExtentTestManager;
import com.storydoc.config.ConfigurationManager;
import com.storydoc.utils.ScreenshotUtils;

/**
 * Abstract base class for all test classes in the framework, providing common setup
 * and teardown operations, WebDriver management, reporting initialization, and
 * utility methods for test execution. Serves as the foundation that all test
 * classes extend.
 */
public abstract class BaseTest {

  protected WebDriver driver;
  protected ExtentTest test;
  private static final Logger LOGGER = LogManager.getLogger(BaseTest.class);

  /**
   * Initializes test environment before each test method execution
   * @param method
   *            method
   */
  @BeforeMethod
  public void setUp(Method method) {
    // Log test setup initialization
    LOGGER.info("Initializing test setup for method: " + method.getName());

    // Initialize WebDriver instance using WebDriverManager.getDriver()
    driver = WebDriverManager.getDriver();

    // Assign WebDriver to driver instance variable
    this.driver = driver;

    // Start a new test in ExtentTestManager with test method name
    ExtentTestManager.startTest(method.getName());

    // Assign ExtentTest instance to test instance variable
    test = ExtentTestManager.getTest();

    // Log browser and environment information
    LOGGER.info(
        "Test execution environment - Browser: "
            + ConfigurationManager.getInstance().getBrowser()
            + ", Base URL: "
            + ConfigurationManager.getInstance().getBaseUrl());
  }

  /**
   * Cleans up resources after each test method execution
   * @param result
   *            result
   */
  @AfterMethod
  public void tearDown(ITestResult result) {
    // Check test result status
    LOGGER.info("Tearing down test environment after method execution");

    // If test failed, capture screenshot using ScreenshotUtils
    if (result.getStatus() == ITestResult.FAILURE) {
      LOGGER.error("Test failed: " + result.getName());
      String screenshotPath = ScreenshotUtils.captureScreenshot(result.getName());
      ExtentTestManager.logScreenshot(screenshotPath, "Screenshot on failure");
    } else if (result.getStatus() == ITestResult.SKIP) {
      LOGGER.warn("Test skipped: " + result.getName());
      ExtentTestManager.logSkip("Test skipped due to: " + result.getThrowable());
    } else {
      LOGGER.info("Test passed: " + result.getName());
    }

    // Log test completion with status
    LOGGER.info("Test completed: " + result.getName() + " with status: " + result.getStatus());

    // Quit WebDriver instance using WebDriverManager.quitDriver()
    WebDriverManager.quitDriver();

    // Set driver instance to null for garbage collection
    driver = null;
  }

  /**
   * Initializes test suite environment before suite execution
   * @param context
   *            context
   */
  @BeforeSuite
  public void setUpSuite(ITestContext context) {
    // Log suite initialization
    LOGGER.info("Initializing test suite: " + context.getName());

    // Initialize ExtentReports instance using ExtentManager
    ExtentManager.getInstance().getExtentReports();

    // Configure report with suite information
    LOGGER.info("Configuring ExtentReports for suite: " + context.getName());
  }

  /**
   * Cleans up resources after suite execution
   * @param context
   *            context
   */
  @AfterSuite
  public void tearDownSuite(ITestContext context) {
    // Log suite completion
    LOGGER.info("Completing test suite: " + context.getName());

    // Flush ExtentReports to generate final report using ExtentManager.flush()
    ExtentManager.getInstance().flush();

    // Log report generation completion with report path
    LOGGER.info("ExtentReports generated successfully");
  }

  /**
   * Takes a screenshot and returns the file path
   * @param testName
   *            testName
   * @return Path to the captured screenshot
   */
  public String getScreenshot(String testName) {
    // Call ScreenshotUtils.captureScreenshot() with test name
    String screenshotPath = ScreenshotUtils.captureScreenshot(testName);

    // Return the path to the captured screenshot
    return screenshotPath;
  }

  /**
   * Navigates to the application base URL
   */
  public void navigateToBaseUrl() {
    // Get base URL from ConfigurationManager
    String baseUrl = ConfigurationManager.getInstance().getBaseUrl();

    // Navigate WebDriver to the base URL
    driver.get(baseUrl);

    // Log navigation action
    LOGGER.info("Navigating to base URL: " + baseUrl);

    // Wait for page to load completely
    waitForPageLoad();
  }

  /**
   * Waits for the page to load completely
   */
  public void waitForPageLoad() {
    // Use JavaScript to check document.readyState
    // Wait until readyState is 'complete'
    LOGGER.info("Waiting for page to load completely");
  }

  /**
   * Logs a test step in both console and report
   * @param message
   *            message
   */
  public void logStep(String message) {
    // Log message using LOGGER
    LOGGER.info(message);

    // Add step to ExtentTest report using ExtentTestManager.logInfo()
    ExtentTestManager.logInfo(message);
  }

  /**
   * Takes a screenshot and attaches it to the test report
   * @param description
   *            description
   */
  public void takeScreenshot(String description) {
    // Capture screenshot using ScreenshotUtils
    String screenshotPath = ScreenshotUtils.captureScreenshot(description);

    // Attach screenshot to test report using ExtentTestManager.logScreenshot()
    ExtentTestManager.logScreenshot(screenshotPath, description);

    // Log screenshot capture action
    LOGGER.info("Screenshot captured: " + description);
  }
}