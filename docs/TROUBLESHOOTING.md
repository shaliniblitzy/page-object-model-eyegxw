# Storydoc Signup Automation Framework - Troubleshooting Guide

## Table of Contents

1. [Introduction](#introduction)
2. [Common Issues Overview](#common-issues-overview)
3. [WebDriver Issues](#webdriver-issues)
4. [Element Location Problems](#element-location-problems)
5. [Synchronization Issues](#synchronization-issues)
6. [Test Execution Problems](#test-execution-problems)
7. [Configuration Issues](#configuration-issues)
8. [Reporting Issues](#reporting-issues)
9. [Performance Optimization](#performance-optimization)
10. [Debugging Techniques](#debugging-techniques)
11. [Frequently Asked Questions](#frequently-asked-questions)
12. [Getting Additional Help](#getting-additional-help)

## Introduction

Welcome to the troubleshooting guide for the Storydoc Signup Automation Framework. This document is designed to help you diagnose and resolve common issues that may arise when using the framework to automate testing of the Storydoc signup process.

### Purpose of This Guide

This guide provides solutions for common problems encountered during:
- Framework setup and configuration
- Test development and execution
- WebDriver and browser interactions
- Test reporting and analysis

### How to Use This Guide

1. Use the table of contents to navigate to the section relevant to your issue
2. Each section contains problems categorized by symptoms, causes, and solutions
3. Code examples are provided where applicable to illustrate solutions
4. For issues not covered in this guide, refer to the [Getting Additional Help](#getting-additional-help) section

### General Troubleshooting Approach

When encountering issues with the automation framework, follow these steps:

1. **Identify the symptoms**: What error message is shown? At what point does the test fail?
2. **Check the logs**: Review framework logs, browser console logs, and test reports
3. **Reproduce the issue**: Try to reproduce the problem in a controlled environment
4. **Isolate the problem**: Determine if the issue is with the framework, test code, or application
5. **Apply solutions**: Try the relevant solutions from this guide
6. **Document findings**: Document the issue and solution for future reference

## Common Issues Overview

This table provides a quick reference to common issues and where to find solutions in this guide:

| Issue Type | Common Symptoms | Section Reference |
|------------|----------------|-------------------|
| WebDriver initialization failures | `SessionNotCreatedException`, `WebDriverException` | [WebDriver Issues](#webdriver-issues) |
| Element not found | `NoSuchElementException`, test fails to interact with elements | [Element Location Problems](#element-location-problems) |
| Timing issues | `TimeoutException`, intermittent failures | [Synchronization Issues](#synchronization-issues) |
| Test failures | Tests fail inconsistently, incorrect test flow | [Test Execution Problems](#test-execution-problems) |
| Framework setup issues | Configuration not loading, incorrect settings | [Configuration Issues](#configuration-issues) |
| Missing or incorrect reports | Reports not generated, missing screenshots | [Reporting Issues](#reporting-issues) |
| Slow test execution | Tests take too long to run | [Performance Optimization](#performance-optimization) |

## WebDriver Issues

### Driver Binary Not Found

**Problem**: Tests fail to start with error related to driver executable not found.

**Symptoms**:
```
java.lang.IllegalStateException: The driver executable does not exist: /path/to/chromedriver
```

**Causes**:
- WebDriverManager failed to download or locate the driver binary
- Internet connectivity issues
- Incompatible browser and driver versions

**Solutions**:

1. **Use WebDriverManager's setup method explicitly**:
```java
// For Chrome
WebDriverManager.chromedriver().setup();

// For Firefox
WebDriverManager.firefoxdriver().setup();

// For Edge
WebDriverManager.edgedriver().setup();
```

2. **Specify a particular version of the driver**:
```java
WebDriverManager.chromedriver().driverVersion("109.0.5414.74").setup();
```

3. **Manual driver management** (fallback approach):
```java
System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
```

4. **Verify internet connectivity** and proxy settings if WebDriverManager is unable to download drivers.

### Browser Version Incompatibility

**Problem**: WebDriver initialization fails due to browser and driver version mismatch.

**Symptoms**:
```
org.openqa.selenium.SessionNotCreatedException: Message: session not created: This version of ChromeDriver only supports Chrome version XX
```

**Causes**:
- Browser was updated but driver version doesn't match
- Driver version too old or too new for browser

**Solutions**:

1. **Let WebDriverManager handle version matching**:
```java
WebDriverManager.chromedriver().browserVersion("112").setup();
```

2. **Update the browser to match available driver** or vice versa.

3. **Use the framework's configuration to specify browser and driver version**:
```properties
# In config.properties
browser=chrome
browser.version=112
```

### Browser Crashes During Test

**Problem**: Browser terminates unexpectedly during test execution.

**Symptoms**:
- `WebDriverException` with message about lost connection
- Test process hangs indefinitely
- Browser window disappears during test

**Causes**:
- Memory issues or resource constraints
- Browser instability
- Incompatible browser extensions
- Conflicting browser instances

**Solutions**:

1. **Implement robust error handling and recovery**:
```java
public WebDriver getDriver() {
    try {
        if (driver == null || isDriverClosed(driver)) {
            initDriver();
        }
        return driver;
    } catch (WebDriverException e) {
        logger.error("WebDriver crashed, reinitializing", e);
        quitDriver(); // Force cleanup
        initDriver(); // Reinitialize
        return driver;
    }
}

private boolean isDriverClosed(WebDriver driver) {
    try {
        driver.getTitle(); // Simple command to check if driver is responsive
        return false;
    } catch (Exception e) {
        return true;
    }
}
```

2. **Increase memory allocation** for the JVM running the tests.

3. **Use headless browser mode** for more stability in CI/CD environments:
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
driver = new ChromeDriver(options);
```

4. **Disable browser extensions and notifications**:
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--disable-extensions");
options.addArguments("--disable-notifications");
```

### Headless Browser Issues

**Problem**: Tests behave differently or fail when running in headless mode.

**Symptoms**:
- Element interactions fail only in headless mode
- Tests pass locally but fail in CI pipeline
- Unexpected JavaScript errors in headless mode

**Causes**:
- Viewport size differences in headless mode
- Feature differences between normal and headless mode
- Timing differences

**Solutions**:

1. **Set specific window size for headless browser**:
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
options.addArguments("--window-size=1920,1080");
```

2. **Add additional arguments to improve headless mode behavior**:
```java
options.addArguments("--disable-gpu");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");
```

3. **Use slower interaction speeds in headless mode**:
```java
if (ConfigurationManager.getInstance().isHeadless()) {
    waitUtils.setLongerTimeouts();
}
```

### WebDriver Thread Safety Issues

**Problem**: Concurrent tests interfere with each other when running in parallel.

**Symptoms**:
- Random test failures in parallel execution
- Browser actions being applied to the wrong window
- Element interactions targeting the wrong context

**Causes**:
- Sharing WebDriver instance between threads
- Missing ThreadLocal implementation
- Static references causing thread conflicts

**Solutions**:

1. **Ensure WebDriver instances are thread-local**:
```java
public class WebDriverManager {
    private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initDriver();
        }
        return driverThreadLocal.get();
    }
    
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
```

2. **Ensure all page objects and test context data are also thread-safe**:
```java
public class TestBase {
    private static ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    
    @BeforeMethod
    public void setUp() {
        WebDriver driver = WebDriverManager.getDriver();
        ExtentTest test = ExtentManager.createTest(getClass().getSimpleName());
        extentTestThreadLocal.set(test);
    }
    
    protected ExtentTest getTest() {
        return extentTestThreadLocal.get();
    }
}
```

3. **Use TestNG parallel execution with proper threading settings**:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Parallel Suite" parallel="methods" thread-count="3">
  <test name="Signup Tests">
    <classes>
      <class name="com.storydoc.tests.SignupTest"/>
    </classes>
  </test>
</suite>
```

## Element Location Problems

### NoSuchElementException

**Problem**: WebDriver fails to locate elements on the page.

**Symptoms**:
```
org.openqa.selenium.NoSuchElementException: no such element: Unable to locate element: {"method":"css selector","selector":"input[data-testid='email-input']"}
```

**Causes**:
- Element locator is incorrect
- Element is in an iframe or shadow DOM
- Element has not been rendered yet
- Page structure has changed

**Solutions**:

1. **Check and update locators** in the locator repository:
```java
// Original locator
public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-input']");

// Updated locator if the data-testid attribute changed
public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-field']");
// Or use alternative strategy
public static final By EMAIL_FIELD = By.id("email");
```

2. **Handle iframes properly**:
```java
// Switch to iframe before accessing elements
driver.switchTo().frame("iframe-id");
// Interact with elements
driver.findElement(SignupPageLocators.EMAIL_FIELD).sendKeys("test@example.com");
// Switch back to main content
driver.switchTo().defaultContent();
```

3. **Implement proper wait strategies**:
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(SignupPageLocators.EMAIL_FIELD));
element.sendKeys("test@example.com");
```

4. **Use multiple locator strategies as fallback**:
```java
public WebElement findElementSafely(By primaryLocator, By... fallbackLocators) {
    try {
        return waitForElementVisible(primaryLocator);
    } catch (NoSuchElementException | TimeoutException e) {
        for (By fallbackLocator : fallbackLocators) {
            try {
                return waitForElementVisible(fallbackLocator);
            } catch (NoSuchElementException | TimeoutException ex) {
                // Continue to next fallback
            }
        }
        throw new NoSuchElementException("Element not found with primary or fallback locators");
    }
}
```

### StaleElementReferenceException

**Problem**: References to WebElements become invalid after page updates.

**Symptoms**:
```
org.openqa.selenium.StaleElementReferenceException: stale element reference: element is not attached to the page document
```

**Causes**:
- Page reload or navigation
- DOM updates through JavaScript
- AJAX refreshing parts of the page
- Element was removed and recreated

**Solutions**:

1. **Find the element again when needed**:
```java
public void safeClick(By locator) {
    try {
        driver.findElement(locator).click();
    } catch (StaleElementReferenceException e) {
        // Find the element again
        driver.findElement(locator).click();
    }
}
```

2. **Implement a retry mechanism with explicit waits**:
```java
public void clickWithRetry(By locator, int maxRetries) {
    int attempts = 0;
    while (attempts < maxRetries) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            return;
        } catch (StaleElementReferenceException e) {
            attempts++;
            if (attempts == maxRetries) {
                throw e;
            }
        }
    }
}
```

3. **Create a utility method to handle stale elements with refreshing strategy**:
```java
public <T> T withStaleProtection(Supplier<T> action) {
    int maxRetries = 3;
    StaleElementReferenceException lastException = null;
    
    for (int retry = 0; retry < maxRetries; retry++) {
        try {
            return action.get();
        } catch (StaleElementReferenceException e) {
            lastException = e;
            sleep(500); // Short pause before retry
        }
    }
    throw lastException;
}

// Usage example
String buttonText = withStaleProtection(() -> driver.findElement(BUTTON_LOCATOR).getText());
```

### Dynamic Elements and Shadow DOM

**Problem**: Unable to locate elements in dynamic content or Shadow DOM.

**Symptoms**:
- `NoSuchElementException` for elements that are visibly present
- Unable to interact with elements inside custom web components
- Standard locator strategies fail

**Causes**:
- Element is inside Shadow DOM
- Element is generated dynamically with JavaScript
- Element has dynamic attributes or IDs

**Solutions**:

1. **Accessing elements in Shadow DOM**:
```java
// Access shadow root
WebElement hostElement = driver.findElement(By.cssSelector("host-element"));
SearchContext shadowRoot = hostElement.getShadowRoot();

// Find element in shadow DOM
WebElement shadowElement = shadowRoot.findElement(By.cssSelector(".shadow-element"));
```

2. **Use JavaScript to find and interact with dynamic elements**:
```java
public WebElement findDynamicElement(String cssSelector) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    return (WebElement) js.executeScript(
        "return document.querySelector('" + cssSelector + "')");
}
```

3. **Create a specialized locator strategy for dynamic IDs**:
```java
public By dynamicId(String idPrefix) {
    return new By() {
        @Override
        public List<WebElement> findElements(SearchContext context) {
            return ((FindsBy) context).findElements(
                By.xpath("//*[starts-with(@id, '" + idPrefix + "')]"));
        }
    };
}
```

### Incorrect Locator Types

**Problem**: Element locators are technically valid but not optimal for the page structure.

**Symptoms**:
- Tests are flaky or slow
- Element location intermittently fails
- Locators work in one browser but not another

**Causes**:
- Using XPath when CSS selector would be more efficient
- Overly complex selectors
- Brittle locators dependent on page structure

**Solutions**:

1. **Prefer data-test attributes when available**:
```java
// Best practice
By.cssSelector("[data-testid='signup-button']");

// Instead of brittle structural selectors
By.xpath("//div[@class='form-container']/div[2]/button");
```

2. **Use CSS selectors instead of XPath when possible**:
```java
// Prefer this
By.cssSelector("button.signup-btn");

// Instead of this
By.xpath("//button[@class='signup-btn']");
```

3. **Create more robust locators with multiple attributes**:
```java
By.cssSelector("button.btn-primary[type='submit'][name='signup']");
```

4. **Use relative locators for elements that are positioned relative to known elements**:
```java
// Selenium 4 relative locator
WebElement emailField = driver.findElement(By.cssSelector("input[data-testid='email-input']"));
WebElement passwordField = driver.findElement(RelativeLocator.with(By.tagName("input")).below(emailField));
```

## Synchronization Issues

### TimeoutException

**Problem**: Element interactions fail because elements aren't ready within the timeout period.

**Symptoms**:
```
org.openqa.selenium.TimeoutException: Expected condition failed: waiting for visibility of element located by By.cssSelector: input[data-testid='email-input']
```

**Causes**:
- Page or element loads too slowly
- Wait timeout too short
- JavaScript rendering delays
- Network latency

**Solutions**:

1. **Increase wait timeout for specific scenarios**:
```java
public WebElement waitLongerForElement(By locator) {
    WebDriverWait extendedWait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Longer timeout
    return extendedWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}
```

2. **Create configurable timeouts**:
```java
public WebElement waitForElementVisible(By locator) {
    int timeout = ConfigurationManager.getInstance().getTimeout();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}
```

3. **Implement adaptive wait times that adjust based on environment**:
```java
public int getAdaptiveTimeout() {
    // Get base timeout
    int baseTimeout = ConfigurationManager.getInstance().getTimeout();
    
    // Apply multiplier for CI environment (which might be slower)
    if ("ci".equals(ConfigurationManager.getInstance().getEnvironment())) {
        return baseTimeout * 2;
    }
    
    return baseTimeout;
}
```

4. **Wait for page to completely load before proceeding**:
```java
public void waitForPageLoad() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> ((JavascriptExecutor) driver)
        .executeScript("return document.readyState").equals("complete"));
}
```

### AJAX and Dynamic Content

**Problem**: Tests fail to handle asynchronous content that loads after the initial page load.

**Symptoms**:
- Elements are not found even with waits
- Interactions happen before content is fully loaded
- Assertions fail on dynamic content

**Causes**:
- AJAX calls loading content after page load
- Animations delaying element visibility
- Progressive loading of page elements

**Solutions**:

1. **Wait for specific AJAX indicators to disappear**:
```java
public void waitForAjaxToComplete() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Boolean) js.executeScript("return jQuery.active == 0");
    });
}
```

2. **Create a custom wait condition for network activity**:
```java
public void waitForNetworkIdle() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Boolean) js.executeScript(
            "return window.performance.getEntriesByType('resource').filter(r => !r.responseEnd).length == 0");
    });
}
```

3. **Implement custom wait for Angular applications**:
```java
public void waitForAngular() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Boolean) js.executeScript(
            "return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1");
    });
}
```

### Page Transitions

**Problem**: Tests fail during navigation between pages.

**Symptoms**:
- Element interactions happen on wrong page
- Unexpected page state during test execution
- Page loads incomplete before test continues

**Causes**:
- Navigation not complete before test continues
- Redirect chains not fully completed
- SPA routing not settled

**Solutions**:

1. **Verify page URL before proceeding**:
```java
public void waitForUrlToContain(String urlFragment) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(ExpectedConditions.urlContains(urlFragment));
}
```

2. **Check for specific elements that confirm page load**:
```java
public boolean isSignupPageLoaded() {
    try {
        return waitForElementVisible(SignupPageLocators.SIGNUP_FORM) != null;
    } catch (TimeoutException e) {
        return false;
    }
}
```

3. **Implement wait for page title change**:
```java
public void waitForPageTitle(String expectedTitle) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(ExpectedConditions.titleContains(expectedTitle));
}
```

4. **Use proper page object transitions**:
```java
public SuccessPage clickSignUp() {
    click(SignupPageLocators.SIGNUP_BUTTON);
    
    // Wait for page transition to complete
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(ExpectedConditions.urlContains("success"));
    
    // Return new page object
    return new SuccessPage(driver);
}
```

### Implicit vs Explicit Wait Conflicts

**Problem**: Mixing implicit and explicit waits causes unpredictable timing issues.

**Symptoms**:
- Inconsistent wait behavior
- Timeouts longer than expected
- Different behavior across test runs

**Causes**:
- Using both implicit and explicit waits in the same test
- Implicit wait set globally but explicit waits used locally
- Wait strategy inconsistencies

**Solutions**:

1. **Disable implicit waits and use only explicit waits**:
```java
// In WebDriver initialization
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

// Then use explicit waits everywhere
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
```

2. **Create a consistent wait strategy**:
```java
public class WaitStrategy {
    public static WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(
            ConfigurationManager.getInstance().getTimeout()));
    }
    
    public static WebElement waitForElementVisible(WebDriver driver, By locator) {
        return getWait(driver).until(
            ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    public static WebElement waitForElementClickable(WebDriver driver, By locator) {
        return getWait(driver).until(
            ExpectedConditions.elementToBeClickable(locator));
    }
}
```

3. **Document the wait strategy** in framework documentation to ensure consistent usage.

## Test Execution Problems

### Flaky Tests

**Problem**: Tests pass sometimes and fail other times without code changes.

**Symptoms**:
- Inconsistent test results across runs
- Fails in CI but passes locally
- Different results when run individually vs. in suite

**Causes**:
- Timing issues
- Race conditions
- Environmental differences
- Resource contention
- Element locator brittleness

**Solutions**:

1. **Implement retry mechanism for flaky tests**:
```java
public class RetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_RETRY = 2;
    
    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY) {
            count++;
            return true;
        }
        return false;
    }
}

// Usage in test
@Test(retryAnalyzer = RetryAnalyzer.class)
public void flakyTest() {
    // Test implementation
}
```

2. **Create more robust synchronization**:
```java
public void waitForStableElement(By locator) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(driver -> {
        try {
            WebElement element = driver.findElement(locator);
            // Check if element position is stable
            Point location = element.getLocation();
            Thread.sleep(200); // Short wait
            return location.equals(element.getLocation());
        } catch (Exception e) {
            return false;
        }
    });
}
```

3. **Identify and quarantine flaky tests**:
```java
// Mark known flaky tests for investigation
@Test(groups = {"flaky", "quarantine"})
@Ignore("JIRA-1234: Test is flaky due to timing issues")
public void knownFlakyTest() {
    // Test implementation
}
```

4. **Log detailed information for flaky test troubleshooting**:
```java
// Use TestNG listeners to capture details about failures
public class TestListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = ((TestBase) result.getInstance()).getDriver();
        captureScreenshot(driver, result.getName());
        logBrowserConsole(driver);
        logPageSource(driver);
    }
}
```

### Test Data Issues

**Problem**: Tests fail due to data issues rather than application or framework problems.

**Symptoms**:
- Tests fail with validation errors
- Unique constraint violations
- Data-dependent test failures

**Causes**:
- Reusing the same test data across runs
- Static test data that doesn't account for uniqueness
- Data not cleaned up between test runs

**Solutions**:

1. **Generate unique test data for each run**:
```java
public String generateUniqueEmail() {
    return "test" + System.currentTimeMillis() + "@example.com";
}

public String generateUniquePassword() {
    return "Test@" + System.currentTimeMillis();
}
```

2. **Implement proper test data cleanup**:
```java
@AfterMethod
public void cleanupTestData() {
    // If you have API access, clean up test accounts
    if (testEmailUsed != null) {
        apiClient.deleteUserAccount(testEmailUsed);
        testEmailUsed = null;
    }
}
```

3. **Use test-specific data namespaces**:
```java
public String getTestSpecificEmail(Method testMethod) {
    return "test." + testMethod.getName() + "." + System.currentTimeMillis() + "@example.com";
}
```

4. **Data-driven tests with TestNG DataProvider**:
```java
@DataProvider(name = "validCredentials")
public Object[][] validCredentials() {
    return new Object[][] {
        { generateUniqueEmail(), "Test@123", "standard user" },
        { generateUniqueEmail(), "Test@456", "premium user" }
    };
}

@Test(dataProvider = "validCredentials")
public void testSignupWithVariousCredentials(String email, String password, String userType) {
    // Test implementation using the provided data
}
```

### Environment-Specific Issues

**Problem**: Tests pass in one environment but fail in another.

**Symptoms**:
- Tests pass locally but fail in CI/CD
- Tests pass in development but fail in staging
- Different behavior across operating systems

**Causes**:
- Browser version differences
- OS-specific rendering variations
- Environment configuration differences
- Network latency and performance variations

**Solutions**:

1. **Implement environment-aware configuration**:
```java
public class ConfigurationManager {
    public int getTimeout() {
        String env = System.getProperty("test.environment", "local");
        switch (env) {
            case "ci":
                return 30; // Longer timeouts for CI
            case "staging":
                return 20; // Medium timeouts for staging
            default:
                return 10; // Default for local
        }
    }
}
```

2. **Create browser-specific test logic when needed**:
```java
public void handleBrowserSpecificFlow() {
    String browser = ConfigurationManager.getInstance().getBrowser();
    if ("firefox".equalsIgnoreCase(browser)) {
        // Firefox-specific logic
        // ...
    } else {
        // Default logic for other browsers
        // ...
    }
}
```

3. **Standardize test environments**:
```java
// Use Docker containers for consistent environments
// Example docker-compose.yml excerpt
services:
  chrome:
    image: selenium/standalone-chrome:latest
    ports:
      - "4444:4444"
```

4. **Log detailed environment information with test reports**:
```java
public void addEnvironmentInfoToReport() {
    ExtentTest test = getTest();
    test.assignCategory(ConfigurationManager.getInstance().getEnvironment());
    test.assignDevice(ConfigurationManager.getInstance().getBrowser());
    test.info("OS: " + System.getProperty("os.name"));
    test.info("Browser: " + ConfigurationManager.getInstance().getBrowser());
    test.info("Environment: " + ConfigurationManager.getInstance().getEnvironment());
}
```

### TestNG Configuration Problems

**Problem**: Tests are not running as expected due to TestNG configuration issues.

**Symptoms**:
- Tests not executed in the expected order
- Test dependencies not respected
- Incorrect grouping or filtering of tests

**Causes**:
- Improper TestNG XML configuration
- Misunderstood test dependencies
- Group definitions incorrect

**Solutions**:

1. **Fix test dependencies**:
```java
// Define test dependencies correctly
@Test(groups = {"signup"})
public void testNavigateToSignupPage() {
    // Test implementation
}

@Test(groups = {"signup"}, dependsOnMethods = {"testNavigateToSignupPage"})
public void testEnterCredentials() {
    // Test implementation
}
```

2. **Configure TestNG XML properly**:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Signup Test Suite">
  <test name="Signup Tests">
    <groups>
      <run>
        <include name="signup"/>
        <exclude name="flaky"/>
      </run>
    </groups>
    <classes>
      <class name="com.storydoc.tests.SignupTest"/>
    </classes>
  </test>
</suite>
```

3. **Use test listeners to debug test execution flow**:
```java
public class DebugListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Starting test: " + result.getName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test passed: " + result.getName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getName());
    }
}
```

4. **Ensure proper setup and teardown methods**:
```java
@BeforeSuite
public void suiteSetup() {
    // Suite-level setup
}

@BeforeClass
public void classSetup() {
    // Class-level setup
}

@BeforeMethod
public void methodSetup() {
    // Method-level setup
}

@AfterMethod
public void methodTeardown() {
    // Method-level cleanup
}

@AfterClass
public void classTeardown() {
    // Class-level cleanup
}

@AfterSuite
public void suiteTeardown() {
    // Suite-level cleanup
}
```

## Configuration Issues

### Properties File Loading

**Problem**: Configuration properties not loading correctly.

**Symptoms**:
- `NullPointerException` when accessing configuration values
- Default values used instead of configured ones
- Configuration file not found errors

**Causes**:
- Incorrect file path
- Classpath issues
- File format problems
- Character encoding issues

**Solutions**:

1. **Verify file location and path**:
```java
public Properties loadProperties() {
    Properties props = new Properties();
    String configFilePath = "config.properties";
    
    // Try loading from classpath
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFilePath)) {
        if (input == null) {
            System.err.println("Unable to find " + configFilePath + " in classpath");
            // Try loading from file system as fallback
            try (FileInputStream fileInput = new FileInputStream(configFilePath)) {
                props.load(fileInput);
            }
        } else {
            props.load(input);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    return props;
}
```

2. **Implement proper error handling for missing properties**:
```java
public String getProperty(String key, String defaultValue) {
    String value = properties.getProperty(key);
    if (value == null) {
        logger.warn("Property '{}' not found, using default: {}", key, defaultValue);
        return defaultValue;
    }
    return value;
}
```

3. **Support environment-specific property files**:
```java
public Properties loadEnvironmentProperties() {
    Properties props = new Properties();
    String env = System.getProperty("test.environment", "local");
    String configFile = env + ".properties";
    
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
        if (input != null) {
            props.load(input);
        } else {
            logger.warn("Environment config file {} not found", configFile);
            // Load default properties
            try (InputStream defaultInput = getClass().getClassLoader().getResourceAsStream("default.properties")) {
                if (defaultInput != null) {
                    props.load(defaultInput);
                }
            }
        }
    } catch (IOException e) {
        logger.error("Error loading properties", e);
    }
    
    return props;
}
```

4. **Add clear logging about configuration loading**:
```java
public void loadConfiguration() {
    logger.info("Loading configuration for environment: {}", getEnvironment());
    // Configuration loading logic
    logLoadedConfiguration();
}

private void logLoadedConfiguration() {
    logger.info("Configuration loaded: Browser={}, BaseURL={}, Timeout={}s",
        getBrowser(), getBaseUrl(), getTimeout());
}
```

### Environment Variables Issues

**Problem**: Environment variables not properly set or retrieved.

**Symptoms**:
- Security credentials not available
- Environment-specific settings not applied
- Default values used unexpectedly

**Causes**:
- Environment variables not set in execution environment
- Permission issues accessing environment variables
- Case sensitivity problems

**Solutions**:

1. **Get environment variables with fallback values**:
```java
public String getEnvironmentVariable(String name, String defaultValue) {
    String value = System.getenv(name);
    if (value == null || value.isEmpty()) {
        logger.warn("Environment variable {} not set, using default", name);
        return defaultValue;
    }
    return value;
}
```

2. **Load sensitive information from environment variables**:
```java
public String getTestPassword() {
    String password = System.getenv("TEST_PASSWORD");
    if (password == null || password.isEmpty()) {
        // Use a default password only for local development
        if ("local".equals(getEnvironment())) {
            return "TestPassword123";
        } else {
            throw new ConfigurationException("TEST_PASSWORD environment variable must be set");
        }
    }
    return password;
}
```

3. **Document required environment variables**:
```java
// In your framework documentation or README:
/**
 * Required Environment Variables:
 * - TEST_EMAIL: Email prefix to use for test accounts
 * - TEST_PASSWORD: Password to use for test accounts
 * - TEST_ENVIRONMENT: Environment to test against (local, dev, staging)
 */
```

4. **Check for environment variables at startup**:
```java
public void validateEnvironmentSetup() {
    List<String> requiredVars = Arrays.asList("TEST_EMAIL", "TEST_PASSWORD", "TEST_ENVIRONMENT");
    List<String> missing = new ArrayList<>();
    
    for (String var : requiredVars) {
        if (System.getenv(var) == null) {
            missing.add(var);
        }
    }
    
    if (!missing.isEmpty()) {
        logger.error("Missing required environment variables: {}", missing);
        throw new ConfigurationException("Missing required environment variables: " + missing);
    }
}
```

### Cross-Platform Issues

**Problem**: Tests behave differently on different operating systems.

**Symptoms**:
- Path-related issues on different OSes
- Line ending differences causing file parsing problems
- Font rendering and element positioning differences

**Causes**:
- OS-specific browser behavior
- Path separator differences (/ vs \)
- Different browser defaults

**Solutions**:

1. **Use platform-independent file paths**:
```java
public String getScreenshotsDirectory() {
    return "test-output" + File.separator + "screenshots";
}
```

2. **Handle OS-specific browser options**:
```java
public ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win")) {
        // Windows-specific options
        options.addArguments("--disable-gpu");
    } else if (os.contains("mac")) {
        // Mac-specific options
        options.addArguments("--kiosk");
    } else if (os.contains("linux")) {
        // Linux-specific options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
    }
    
    return options;
}
```

3. **Normalize line endings when reading files**:
```java
public List<String> readLines(String filePath) throws IOException {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
    }
    return lines;
}
```

4. **Log platform information for debugging**:
```java
public void logPlatformInfo() {
    logger.info("OS: {}", System.getProperty("os.name"));
    logger.info("OS Version: {}", System.getProperty("os.version"));
    logger.info("OS Architecture: {}", System.getProperty("os.arch"));
    logger.info("Java Version: {}", System.getProperty("java.version"));
}
```

### Browser Profile Issues

**Problem**: Browser profiles or settings affecting test behavior.

**Symptoms**:
- Unexpected popups or notifications
- Extensions interfering with tests
- Caching affecting test repeatability
- Cookie persistence between tests

**Causes**:
- Browser profiles being reused
- Notifications and permission prompts
- Extensions loaded automatically
- Browser defaults vary by environment

**Solutions**:

1. **Use clean browser profiles for tests**:
```java
public ChromeOptions getCleanChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    
    // Use a clean, dedicated user data directory
    options.addArguments("--user-data-dir=/tmp/chrome-data-" + System.currentTimeMillis());
    
    // Disable extensions, notifications, etc.
    options.addArguments("--disable-extensions");
    options.addArguments("--disable-notifications");
    options.addArguments("--disable-popup-blocking");
    
    return options;
}
```

2. **Clear cookies and storage between tests**:
```java
@AfterMethod
public void cleanBrowserState() {
    // Clear cookies
    driver.manage().deleteAllCookies();
    
    // Clear local storage
    ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
    
    // Clear session storage
    ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
}
```

3. **Block unwanted browser requests**:
```java
public void blockUnwantedRequests() {
    // Using Chrome DevTools Protocol with Selenium 4
    DevTools devTools = ((ChromeDriver) driver).getDevTools();
    devTools.createSession();
    
    // Block analytics, tracking, etc.
    devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    devTools.send(Network.setBlockedURLs(List.of(
        "*/analytics.js", "*/gtm.js", "*/tracking.js"
    )));
}
```

4. **Set default browser preferences**:
```java
public ChromeOptions getChromePrefOptions() {
    ChromeOptions options = new ChromeOptions();
    
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("profile.default_content_setting_values.notifications", 2); // Block notifications
    prefs.put("credentials_enable_service", false); // Disable password manager
    prefs.put("profile.password_manager_enabled", false); // Disable password manager
    prefs.put("autofill.profile_enabled", false); // Disable autofill
    
    options.setExperimentalOption("prefs", prefs);
    return options;
}
```

## Reporting Issues

### Missing Screenshots

**Problem**: Screenshots not captured or not attached to reports on test failure.

**Symptoms**:
- No screenshots in test reports
- Broken screenshot links
- Screenshot files missing

**Causes**:
- Screenshot directory not created
- Permission issues writing files
- Incorrect file paths
- Screenshots not properly attached to reports

**Solutions**:

1. **Ensure screenshot directory exists**:
```java
public String captureScreenshot(String testName) {
    String directory = "test-output/screenshots";
    File screenshotDir = new File(directory);
    if (!screenshotDir.exists()) {
        screenshotDir.mkdirs();
    }
    
    String filename = testName + "_" + System.currentTimeMillis() + ".png";
    String filePath = directory + File.separator + filename;
    
    try {
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Files.copy(screenshotFile.toPath(), Paths.get(filePath));
        return filePath;
    } catch (IOException e) {
        logger.error("Failed to capture screenshot", e);
        return null;
    }
}
```

2. **Properly attach screenshots to ExtentReports**:
```java
public class TestListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        TestBase testInstance = (TestBase) result.getInstance();
        WebDriver driver = testInstance.getDriver();
        ExtentTest test = testInstance.getTest();
        
        if (driver != null) {
            String screenshotPath = captureScreenshot(driver, result.getName());
            try {
                // Use absolute path for proper attachment
                test.fail("Test failed, see screenshot below:",
                    MediaEntityBuilder.createScreenCaptureFromPath(
                        new File(screenshotPath).getAbsolutePath()).build());
            } catch (IOException e) {
                test.fail("Test failed but screenshot could not be attached: " + e.getMessage());
            }
        }
    }
    
    private String captureScreenshot(WebDriver driver, String testName) {
        // Screenshot capture implementation
    }
}
```

3. **Handle headless browser screenshots**:
```java
public String captureHeadlessScreenshot(String testName) {
    String directory = "test-output/screenshots";
    File screenshotDir = new File(directory);
    if (!screenshotDir.exists()) {
        screenshotDir.mkdirs();
    }
    
    String filename = testName + "_" + System.currentTimeMillis() + ".png";
    String filePath = directory + File.separator + filename;
    
    // For headless Chrome, set window size before capturing
    Dimension originalSize = driver.manage().window().getSize();
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    try {
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Files.copy(screenshotFile.toPath(), Paths.get(filePath));
        // Restore original size
        driver.manage().window().setSize(originalSize);
        return filePath;
    } catch (IOException e) {
        logger.error("Failed to capture screenshot", e);
        return null;
    }
}
```

4. **Add detailed logging for screenshot operations**:
```java
public String captureScreenshotWithLogging(String testName) {
    logger.info("Capturing screenshot for test: {}", testName);
    String directory = "test-output/screenshots";
    logger.debug("Screenshot directory: {}", directory);
    
    // Create directory
    File screenshotDir = new File(directory);
    if (!screenshotDir.exists()) {
        boolean created = screenshotDir.mkdirs();
        logger.debug("Created screenshot directory: {}", created);
    }
    
    // Rest of screenshot capture logic
    // ...
    
    logger.info("Screenshot captured: {}", filePath);
    return filePath;
}
```

### Report Generation Failures

**Problem**: Test reports not generated or incomplete.

**Symptoms**:
- Missing or empty report files
- Incomplete test information in reports
- Report generation errors

**Causes**:
- ExtentReports not properly configured
- Report not flushed at end of execution
- Report directory permission issues
- Concurrent test execution conflicts

**Solutions**:

1. **Properly set up ExtentReports**:
```java
public class ExtentManager {
    private static ExtentReports extent;
    
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }
    
    private static void createInstance() {
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("test-output/extent-report.html");
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle("Storydoc Signup Test Report");
        htmlReporter.config().setReportName("Selenium Test Automation Results");
        
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Browser", ConfigurationManager.getInstance().getBrowser());
        extent.setSystemInfo("Environment", ConfigurationManager.getInstance().getEnvironment());
    }
    
    public static synchronized ExtentTest createTest(String testName) {
        return getInstance().createTest(testName);
    }
}
```

2. **Ensure report is flushed at the end of test execution**:
```java
public class TestListener implements ITestListener {
    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getInstance().flush();
    }
}
```

3. **Handle report directory creation**:
```java
public static void setupReportDirectory() {
    File reportDir = new File("test-output");
    if (!reportDir.exists()) {
        boolean created = reportDir.mkdirs();
        if (!created) {
            throw new RuntimeException("Failed to create report directory: test-output");
        }
    }
}
```

4. **Implement Thread-safe reporting for parallel execution**:
```java
public class ExtentTestManager {
    private static Map<Integer, ExtentTest> extentTestMap = new ConcurrentHashMap<>();
    
    public static synchronized ExtentTest getTest() {
        return extentTestMap.get((int) Thread.currentThread().getId());
    }
    
    public static synchronized ExtentTest startTest(String testName) {
        ExtentTest test = ExtentManager.getInstance().createTest(testName);
        extentTestMap.put((int) Thread.currentThread().getId(), test);
        return test;
    }
}
```

### Logging Configuration

**Problem**: Inadequate or excessive logging affecting troubleshooting.

**Symptoms**:
- Missing important log information
- Log files too large with unnecessary details
- Log files not created or inaccessible

**Causes**:
- Incorrect Log4j configuration
- Log level set inappropriately
- Log file path issues
- Missing appenders

**Solutions**:

1. **Configure proper Log4j2 XML configuration**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        <RollingFile name="File" fileName="logs/test.log" filePattern="logs/test-%d{yyyy-MM-dd}-%i.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="10 MB"/>
                <TimeBasedTriggeringPolicy interval="1" modulate="true"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
        <Logger name="com.storydoc" level="debug" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Logger>
    </Loggers>
</Configuration>
```

2. **Create a log helper class**:
```java
public class LogHelper {
    private static final Logger logger = LogManager.getLogger(LogHelper.class);
    
    public static void logTestStart(String testName) {
        logger.info("===============================================");
        logger.info("Starting test: {}", testName);
        logger.info("===============================================");
    }
    
    public static void logTestEnd(String testName, String status) {
        logger.info("===============================================");
        logger.info("Test completed: {} - Status: {}", testName, status);
        logger.info("===============================================");
    }
    
    public static void logStep(String stepDescription) {
        logger.info("STEP: {}", stepDescription);
    }
    
    public static void logError(String message, Throwable error) {
        logger.error("ERROR: {}", message, error);
    }
}
```

3. **Log browser console messages**:
```java
public void logBrowserConsole() {
    LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
    for (LogEntry entry : logs) {
        logger.info("Browser console [{}]: {}", 
            new Date(entry.getTimestamp()), entry.getMessage());
    }
}
```

4. **Create a directory for logs**:
```java
public void setupLogDirectory() {
    File logDir = new File("logs");
    if (!logDir.exists()) {
        boolean created = logDir.mkdirs();
        if (!created) {
            System.err.println("Failed to create log directory: logs");
        }
    }
}
```

### Report Customization

**Problem**: Default reports don't provide enough information or aren't formatted properly.

**Symptoms**:
- Reports lack important test details
- Reports not visually organized for readability
- Missing test environment information

**Causes**:
- Default report settings not customized
- Report template issues
- Missing test metadata

**Solutions**:

1. **Customize ExtentReports configuration**:
```java
public void configureExtentReports() {
    ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/custom-report.html");
    
    // Apply custom styling
    sparkReporter.config().setTheme(Theme.DARK);
    sparkReporter.config().setCss(".test-name { font-weight: bold; }");
    sparkReporter.config().setJs("document.getElementsByClassName('logo')[0].style.display='none';");
    
    // Set document information
    sparkReporter.config().setDocumentTitle("Storydoc Signup Test Report");
    sparkReporter.config().setReportName("Automated Test Results");
    sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
    
    ExtentReports extent = new ExtentReports();
    extent.attachReporter(sparkReporter);
    
    // Add system information
    extent.setSystemInfo("Application", "Storydoc Signup");
    extent.setSystemInfo("Environment", ConfigurationManager.getInstance().getEnvironment());
    extent.setSystemInfo("Browser", ConfigurationManager.getInstance().getBrowser());
    extent.setSystemInfo("OS", System.getProperty("os.name"));
    extent.setSystemInfo("Java Version", System.getProperty("java.version"));
}
```

2. **Add detailed test information to reports**:
```java
public void logTestDetails(ExtentTest test, Method method) {
    // Add annotations as test metadata
    if (method.isAnnotationPresent(Test.class)) {
        Test testAnnotation = method.getAnnotation(Test.class);
        test.info("Test Description: " + 
            (testAnnotation.description().isEmpty() ? "N/A" : testAnnotation.description()));
        
        if (testAnnotation.groups().length > 0) {
            test.assignCategory(testAnnotation.groups());
        }
    }
    
    // Log test data
    test.info("Test Data: Email - " + testData.getEmail());
    
    // Add author information
    if (method.isAnnotationPresent(Author.class)) {
        Author authorAnnotation = method.getAnnotation(Author.class);
        test.assignAuthor(authorAnnotation.name());
    } else {
        test.assignAuthor("QA Team");
    }
}
```

3. **Create a custom TestNG HTML reporter**:
```java
public class CustomHTMLReporter extends HTMLReporter {
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        super.generateReport(xmlSuites, suites, outputDirectory);
        
        // Add custom post-processing for TestNG HTML reports
        try {
            File htmlReport = new File(outputDirectory + File.separator + "index.html");
            if (htmlReport.exists()) {
                String content = Files.readString(htmlReport.toPath());
                
                // Add custom styling
                content = content.replace("</head>",
                    "<style>.test-name{font-weight:bold;color:#2196F3;}</style></head>");
                
                // Add custom header
                content = content.replace("<body>",
                    "<body><div style='background:#263238;color:white;padding:10px;'>" +
                    "<h2>Storydoc Signup Test Results</h2>" +
                    "<p>Environment: " + ConfigurationManager.getInstance().getEnvironment() + "</p>" +
                    "</div>");
                
                Files.writeString(htmlReport.toPath(), content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

4. **Add visualization of test execution trend** (if you're storing historical data):
```java
public void addTestTrendChart() {
    // Using ExtentReports Spark reporter
    ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/custom-report.html");
    
    // Enable dashboard with charts
    sparkReporter.config().setReportName("Storydoc Test Results");
    sparkReporter.viewConfigurer().viewOrder()
        .as(new ViewName[] {
            ViewName.DASHBOARD,
            ViewName.TEST,
            ViewName.CATEGORY,
            ViewName.DEVICE,
            ViewName.AUTHOR
        }).apply();
    
    ExtentReports extent = new ExtentReports();
    extent.attachReporter(sparkReporter);
    
    // The rest of your ExtentReports configuration
}
```

## Performance Optimization

### Slow Test Execution

**Problem**: Tests run too slowly, increasing total execution time.

**Symptoms**:
- Long test execution times
- CI/CD pipeline timeouts
- Tests taking longer than manual execution

**Causes**:
- Excessive waits or timeouts
- Inefficient element location strategies
- Browser performance issues
- Test code inefficiencies

**Solutions**:

1. **Optimize wait strategies**:
```java
public void optimizeWaitStrategy() {
    // Reduce default timeout for most operations
    int defaultTimeout = ConfigurationManager.getInstance().getTimeout();
    
    // Use shorter timeouts for frequent operations
    int shortTimeout = Math.max(2, defaultTimeout / 3);
    
    // Only use longer timeouts for known slow operations
    int longTimeout = defaultTimeout * 2;
    
    // Create different wait objects for different scenarios
    WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(shortTimeout));
    WebDriverWait defaultWait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeout));
    WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(longTimeout));
    
    // Use appropriate wait for the operation
    shortWait.until(ExpectedConditions.elementToBeClickable(locator)); // For quick UI responses
    defaultWait.until(ExpectedConditions.visibilityOfElementLocated(locator)); // Standard operations
    longWait.until(ExpectedConditions.urlContains("confirmation")); // Known slow page transitions
}
```

2. **Use optimized locator strategies**:
```java
public void useOptimizedLocators() {
    // Optimize locator strategies for performance
    
    // GOOD: Specific, direct locators
    By fastLocator = By.id("email-input"); // Fastest
    By goodLocator = By.cssSelector("input[data-testid='email-input']"); // Good
    
    // BAD: Avoid these for performance-critical elements
    By slowLocator = By.xpath("//div[@class='form']//input[@type='email']"); // Slower
    By verySlowLocator = By.xpath("//*[contains(@class, 'input') and contains(@placeholder, 'Email')]"); // Very slow
}
```

3. **Implement browser performance optimizations**:
```java
public ChromeOptions getPerformanceOptimizedOptions() {
    ChromeOptions options = new ChromeOptions();
    
    // Disable unnecessary features
    options.addArguments("--disable-extensions");
    options.addArguments("--disable-infobars");
    options.addArguments("--disable-notifications");
    options.addArguments("--disable-popup-blocking");
    
    // Performance optimizations
    options.addArguments("--disable-gpu"); // Reduces GPU usage
    options.addArguments("--disable-dev-shm-usage"); // Uses /tmp instead of /dev/shm
    
    // Reduce memory usage
    options.addArguments("--js-flags=--expose-gc"); // Enable garbage collection control
    options.addArguments("--aggressive-cache-discard"); // Discard cache more aggressively
    
    return options;
}
```

4. **Reduce unnecessary actions and validations**:
```java
public void optimizeTestFlow() {
    // Only perform verification on critical elements
    // Instead of checking every element on a page:
    
    // INEFFICIENT:
    Assert.assertTrue(isElementPresent(locator1));
    Assert.assertTrue(isElementPresent(locator2));
    Assert.assertTrue(isElementPresent(locator3));
    // ... many more checks
    
    // EFFICIENT:
    // Check only key elements that confirm the page state
    Assert.assertTrue(isElementPresent(keyElementLocator));
    
    // Use this test method annotation to set timeouts
    @Test(timeOut = 30000) // Fail the test if it runs longer than 30 seconds
    public void performanceEfficientTest() {
        // Test implementation
    }
}
```

### Memory Management

**Problem**: Tests consume excessive memory, causing performance issues or crashes.

**Symptoms**:
- OutOfMemoryError exceptions
- Browser crashes during test execution
- Slow test execution as test suite progresses
- System becoming unresponsive during test execution

**Causes**:
- Memory leaks in test code
- Browser instances not properly closed
- Excessive screenshot or logging
- Too many browser instances in parallel

**Solutions**:

1. **Properly close browser resources**:
```java
public void closeAllResources() {
    try {
        if (driver != null) {
            driver.quit(); // Better than just driver.close()
            driver = null;
        }
    } catch (Exception e) {
        logger.error("Error closing WebDriver", e);
    }
}
```

2. **Implement proper teardown in TestNG**:
```java
@AfterMethod(alwaysRun = true) // Make sure this always runs, even after failures
public void tearDown() {
    closeAllResources();
    
    // Suggest garbage collection
    System.gc();
}

@AfterClass(alwaysRun = true)
public void classTearDown() {
    // Clean up any class-level resources
    // ...
    
    // Suggest garbage collection
    System.gc();
}
```

3. **Limit parallel execution based on system resources**:
```xml
<!-- In testng.xml -->
<suite name="Memory-Optimized Suite" parallel="classes" thread-count="3">
    <!-- Limit thread count based on available memory -->
    <test name="Signup Tests">
        <classes>
            <class name="com.storydoc.tests.SignupTest"/>
        </classes>
    </test>
</suite>
```

4. **Monitor memory usage during test execution**:
```java
public void logMemoryUsage(String checkpoint) {
    Runtime runtime = Runtime.getRuntime();
    
    // Calculate used memory
    long usedMemory = runtime.totalMemory() - runtime.freeMemory();
    long usedMemoryMB = usedMemory / (1024 * 1024);
    
    logger.info("Memory usage at {}: {} MB", checkpoint, usedMemoryMB);
    
    // Trigger garbage collection if memory usage is high
    if (usedMemoryMB > 500) { // Threshold of 500MB
        logger.warn("Memory usage high, suggesting garbage collection");
        System.gc();
    }
}
```

### Resource Cleanup

**Problem**: Resources not properly released, causing resource leaks.

**Symptoms**:
- Browser processes remaining after tests
- File handles not released
- Driver sessions not properly closed
- System slowing down over time during testing

**Causes**:
- Missing or incomplete teardown methods
- Exception during cleanup
- Inherited resources not being released

**Solutions**:

1. **Implement robust WebDriver cleanup**:
```java
public static void quitDriver() {
    WebDriver driver = driverThreadLocal.get();
    if (driver != null) {
        try {
            // Close all windows
            Set<String> windowHandles = driver.getWindowHandles();
            for (String handle : windowHandles) {
                driver.switchTo().window(handle);
                driver.close();
            }
        } catch (Exception e) {
            logger.warn("Error closing browser windows", e);
        } finally {
            try {
                driver.quit();
            } catch (Exception e) {
                logger.error("Error quitting WebDriver", e);
            }
            driverThreadLocal.remove();
        }
    }
}
```

2. **Clean up screenshot and log files**:
```java
public void cleanupOldFiles() {
    // Clean up old screenshots (older than 7 days)
    try {
        Path screenshotDir = Paths.get("test-output/screenshots");
        if (Files.exists(screenshotDir)) {
            Files.list(screenshotDir)
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toInstant()
                            .isBefore(Instant.now().minus(7, ChronoUnit.DAYS));
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        logger.warn("Could not delete old file: {}", path, e);
                    }
                });
        }
    } catch (IOException e) {
        logger.error("Error cleaning up old files", e);
    }
}
```

3. **Use try-with-resources for file operations**:
```java
public void saveTestData(String testName, String data) {
    // Ensure resources are closed automatically
    try (FileWriter writer = new FileWriter("test-output/data/" + testName + ".txt")) {
        writer.write(data);
    } catch (IOException e) {
        logger.error("Error saving test data", e);
    }
}
```

4. **Implement a shutdown hook for emergency cleanup**:
```java
public void registerCleanupHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        logger.info("Shutdown hook executing, cleaning up resources");
        try {
            // Force cleanup of all WebDriver instances
            cleanupAllWebDrivers();
            // Other cleanup tasks
        } catch (Exception e) {
            logger.error("Error during emergency cleanup", e);
        }
    }));
}

private void cleanupAllWebDrivers() {
    // Implementation to force-quit any remaining WebDriver instances
}
```

### Parallel Execution Optimization

**Problem**: Parallel test execution is inefficient or causing failures.

**Symptoms**:
- Tests failing only during parallel execution
- Resource contention
- Tests running slower in parallel than expected
- Unpredictable test results

**Causes**:
- Resource sharing issues
- Thread safety problems
- Test dependencies not properly configured
- System resource limitations

**Solutions**:

1. **Configure TestNG for optimal parallel execution**:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Optimized Parallel Suite" parallel="classes" thread-count="4" data-provider-thread-count="2">
    <!-- Separate tests by groups to avoid contention -->
    <test name="Signup Tests" parallel="methods" thread-count="2">
        <groups>
            <run>
                <include name="signup"/>
            </run>
        </groups>
        <classes>
            <class name="com.storydoc.tests.SignupTest"/>
        </classes>
    </test>
    
    <!-- Other tests in separate test tag -->
    <test name="Other Tests" parallel="methods" thread-count="2">
        <groups>
            <run>
                <include name="other"/>
            </run>
        </groups>
        <classes>
            <class name="com.storydoc.tests.OtherTest"/>
        </classes>
    </test>
</suite>
```

2. **Make tests data independent**:
```java
@Test(groups = {"signup"})
public void testSignupWithUniqueData() {
    // Generate unique test data for each test run
    String uniqueEmail = "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    String uniquePassword = "Test@" + System.currentTimeMillis();
    
    // Use unique data in the test
    signupPage.enterEmail(uniqueEmail)
              .enterPassword(uniquePassword)
              .acceptTerms()
              .clickSignUp();
              
    // Test assertions
}
```

3. **Use ThreadLocal for all shared resources**:
```java
public class TestContext {
    private static ThreadLocal<Map<String, Object>> testContexts = new ThreadLocal<>();
    
    public static void set(String key, Object value) {
        getContext().put(key, value);
    }
    
    public static Object get(String key) {
        return getContext().get(key);
    }
    
    private static Map<String, Object> getContext() {
        Map<String, Object> context = testContexts.get();
        if (context == null) {
            context = new HashMap<>();
            testContexts.set(context);
        }
        return context;
    }
    
    public static void reset() {
        testContexts.remove();
    }
}
```

4. **Implement adaptive thread count based on system resources**:
```java
public int calculateOptimalThreadCount() {
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    
    // Calculate available memory per thread (in MB)
    long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
    
    // Assume each browser instance needs ~400MB memory
    int memoryBasedThreads = (int) (maxMemory / 400);
    
    // Use the lower of CPU-based or memory-based thread count
    int optimalThreads = Math.min(availableProcessors, memoryBasedThreads);
    
    // Cap to a reasonable maximum
    return Math.min(optimalThreads, 8);
}
```

## Debugging Techniques

### Effective Logging

**Problem**: Insufficient information for debugging test failures.

**Symptoms**:
- Test failures with unclear causes
- Difficulty reproducing issues
- Missing context for failures

**Causes**:
- Inadequate logging
- Generic error messages
- Too high log level

**Solutions**:

1. **Implement hierarchical logging**:
```java
public class TestLogger {
    private final Logger logger;
    
    public TestLogger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }
    
    public void startTest(String testName) {
        logger.info("========== STARTING TEST: {} ==========", testName);
    }
    
    public void endTest(String testName, String result) {
        logger.info("========== TEST COMPLETED: {} - RESULT: {} ==========", testName, result);
    }
    
    public void startStep(String stepDescription) {
        logger.info("STEP: {}", stepDescription);
    }
    
    public void debug(String message) {
        logger.debug(message);
    }
    
    public void info(String message) {
        logger.info(message);
    }
    
    public void warn(String message) {
        logger.warn(message);
    }
    
    public void error(String message, Throwable e) {
        logger.error(message, e);
    }
}
```

2. **Log detailed element interaction information**:
```java
public WebElement click(By locator) {
    logger.debug("Attempting to click element: {}", locator);
    try {
        WebElement element = waitForElementClickable(locator);
        logger.debug("Element found and clickable: {}", getElementDetails(element));
        element.click();
        logger.debug("Successfully clicked element");
        return element;
    } catch (Exception e) {
        logger.error("Failed to click element: {}", locator, e);
        throw e;
    }
}

private String getElementDetails(WebElement element) {
    try {
        String tagName = element.getTagName();
        String id = element.getAttribute("id");
        String classes = element.getAttribute("class");
        String text = element.getText();
        
        return String.format("Tag: %s, ID: %s, Class: %s, Text: %s", 
                            tagName, id, classes, text);
    } catch (Exception e) {
        return "Could not get element details";
    }
}
```

3. **Add context to test execution**:
```java
public class TestContextLogger {
    private static ThreadLocal<Map<String, Object>> contextMap = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(TestContextLogger.class);
    
    public static void setContext(String key, Object value) {
        getContextMap().put(key, value);
    }
    
    public static Object getContext(String key) {
        return getContextMap().get(key);
    }
    
    public static void clearContext() {
        getContextMap().clear();
    }
    
    public static void logContext(String message) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(" - Context: {");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : getContextMap().entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        
        sb.append("}");
        logger.info(sb.toString());
    }
    
    private static Map<String, Object> getContextMap() {
        Map<String, Object> map = contextMap.get();
        if (map == null) {
            map = new HashMap<>();
            contextMap.set(map);
        }
        return map;
    }
}
```

4. **Create custom TestNG listeners for detailed test logging**:
```java
public class LoggingTestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(LoggingTestListener.class);
    
    @Override
    public void onTestStart(ITestResult result) {
        logger.info("========== TEST STARTED: {} ==========", result.getName());
        logTestParameters(result);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("TEST PASSED: {} - Duration: {}ms", 
            result.getName(), result.getEndMillis() - result.getStartMillis());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("TEST FAILED: {} - Duration: {}ms", 
            result.getName(), result.getEndMillis() - result.getStartMillis());
        logger.error("Failure Cause: ", result.getThrowable());
    }
    
    private void logTestParameters(ITestResult result) {
        Object[] params = result.getParameters();
        if (params != null && params.length > 0) {
            StringBuilder sb = new StringBuilder("Test Parameters: ");
            for (int i = 0; i < params.length; i++) {
                sb.append("Param").append(i).append("=[").append(params[i]).append("] ");
            }
            logger.info(sb.toString());
        }
    }
}
```

### Browser Developer Tools Integration

**Problem**: Limited visibility into browser state during test execution.

**Symptoms**:
- Unable to debug JavaScript errors
- No insight into network requests
- DOM structure unclear during test

**Causes**:
- No integration with browser developer tools
- Missing browser logs

**Solutions**:

1. **Capture browser console logs**:
```java
public void logBrowserConsole() {
    LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
    for (LogEntry entry : logs) {
        String logLevel;
        switch (entry.getLevel().toString()) {
            case "SEVERE":
                logLevel = "ERROR";
                break;
            case "WARNING":
                logLevel = "WARN";
                break;
            default:
                logLevel = "INFO";
        }
        
        logger.info("Browser Console [{}] [{}]: {}", 
            logLevel, new Date(entry.getTimestamp()), entry.getMessage());
    }
}
```

2. **Use Chrome DevTools Protocol (Selenium 4)**:
```java
public void monitorNetworkTraffic() {
    DevTools devTools = ((ChromeDriver) driver).getDevTools();
    devTools.createSession();
    
    // Listen for network events
    devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    
    // Log requests
    devTools.addListener(Network.requestWillBeSent(), request -> {
        logger.debug("Network Request: {} {}", 
            request.getRequest().getMethod(), 
            request.getRequest().getUrl());
    });
    
    // Log responses
    devTools.addListener(Network.responseReceived(), response -> {
        logger.debug("Network Response: {} {} - Status: {}", 
            response.getResponse().getUrl(),
            response.getResponse().getStatus(),
            response.getResponse().getStatusText());
        
        if (response.getResponse().getStatus() >= 400) {
            logger.warn("HTTP Error: {} {}", 
                response.getResponse().getStatus(),
                response.getResponse().getUrl());
        }
    });
}
```

3. **Capture DOM snapshots**:
```java
public String captureDOMSnapshot() {
    try {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript("return document.documentElement.outerHTML");
    } catch (Exception e) {
        logger.error("Failed to capture DOM snapshot", e);
        return null;
    }
}

public void saveDOMSnapshot(String testName) {
    String dom = captureDOMSnapshot();
    if (dom != null) {
        try {
            Files.writeString(Paths.get("test-output/dom-snapshots/" + 
                testName + "_" + System.currentTimeMillis() + ".html"), dom);
        } catch (IOException e) {
            logger.error("Failed to save DOM snapshot", e);
        }
    }
}
```

4. **Execute JavaScript for debugging**:
```java
public void debugWithJavaScript() {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    
    // Check for JavaScript errors
    List<Object> jsErrors = (List<Object>) js.executeScript(
        "return window.jsErrors || []");
    
    for (Object error : jsErrors) {
        logger.warn("JavaScript Error: {}", error);
    }
    
    // Log page readiness
    boolean isReady = (Boolean) js.executeScript(
        "return document.readyState === 'complete'");
    
    logger.info("Page ready state: {}", isReady ? "Complete" : "Loading");
    
    // Check specific element state
    WebElement element = driver.findElement(By.id("email"));
    boolean isVisible = (Boolean) js.executeScript(
        "var elem = arguments[0]; " +
        "return !!(elem.offsetWidth || elem.offsetHeight || " +
        "elem.getClientRects().length);", 
        element);
    
    logger.info("Element visibility: {}", isVisible);
}
```

### Remote Debugging

**Problem**: Difficult to debug tests running in CI/CD environments.

**Symptoms**:
- Tests fail in CI but work locally
- Limited visibility into remote test execution
- Difficulty reproducing environment-specific issues

**Causes**:
- Execution environment differences
- Limited access to CI/CD server logs
- Containerized execution

**Solutions**:

1. **Enable VNC for visual debugging in containers**:
```yaml
# In docker-compose.yml
services:
  selenium-chrome:
    image: selenium/standalone-chrome-debug:latest  # -debug image includes VNC
    ports:
      - "4444:4444"  # WebDriver port
      - "5900:5900"  # VNC port
    volumes:
      - /dev/shm:/dev/shm  # Shared memory for better performance
```

2. **Generate detailed execution reports for remote debugging**:
```java
public void enhanceRemoteDebugging(ExtentTest test) {
    // Add system information
    test.info(MarkupHelper.createCodeBlock(
        "OS: " + System.getProperty("os.name") + "\n" +
        "Java: " + System.getProperty("java.version") + "\n" +
        "Browser: " + ((RemoteWebDriver) driver).getCapabilities().getBrowserName() + " " +
        ((RemoteWebDriver) driver).getCapabilities().getBrowserVersion() + "\n" +
        "TimeZone: " + TimeZone.getDefault().getID()
    ));
    
    // Add screenshots at key points
    test.info("Page state", MediaEntityBuilder.createScreenCaptureFromBase64String(
        getBase64Screenshot()).build());
    
    // Add page source
    test.info("Page Source", MarkupHelper.createCodeBlock(driver.getPageSource(), "html"));
}

private String getBase64Screenshot() {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
}
```

3. **Implement detailed environment reporting**:
```java
public class EnvironmentLogger {
    private static final Logger logger = LogManager.getLogger(EnvironmentLogger.class);
    
    public static void logEnvironmentDetails() {
        logger.info("=== Environment Details ===");
        
        // System properties
        logger.info("OS: {} v{} ({})", 
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch"));
        
        logger.info("Java: {} ({})", 
            System.getProperty("java.version"),
            System.getProperty("java.vendor"));
        
        // Memory
        Runtime rt = Runtime.getRuntime();
        logger.info("Memory: Total={} MB, Free={} MB, Max={} MB",
            rt.totalMemory() / (1024 * 1024),
            rt.freeMemory() / (1024 * 1024),
            rt.maxMemory() / (1024 * 1024));
        
        // Environment variables (only log non-sensitive ones)
        logger.info("Environment Variables:");
        System.getenv().forEach((key, value) -> {
            if (!key.toLowerCase().contains("key") && 
                !key.toLowerCase().contains("token") && 
                !key.toLowerCase().contains("password") && 
                !key.toLowerCase().contains("secret")) {
                logger.info("  {}={}", key, value);
            }
        });
        
        // Log test configuration
        logger.info("Test Configuration:");
        logger.info("  Browser: {}", ConfigurationManager.getInstance().getBrowser());
        logger.info("  Environment: {}", ConfigurationManager.getInstance().getEnvironment());
        logger.info("  Timeout: {}s", ConfigurationManager.getInstance().getTimeout());
    }
}
```

4. **Create a remote execution debug helper**:
```java
public class RemoteDebugHelper {
    private final WebDriver driver;
    private final Logger logger = LogManager.getLogger(RemoteDebugHelper.class);
    
    public RemoteDebugHelper(WebDriver driver) {
        this.driver = driver;
    }
    
    public void collectDebugBundle(String testName, Throwable error) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String debugDir = "test-output/debug/" + testName + "_" + timestamp;
        
        try {
            Files.createDirectories(Paths.get(debugDir));
            
            // Save screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), Paths.get(debugDir + "/screenshot.png"));
            
            // Save page source
            Files.writeString(Paths.get(debugDir + "/page_source.html"), driver.getPageSource());
            
            // Save browser logs
            StringBuilder logs = new StringBuilder();
            for (LogEntry entry : driver.manage().logs().get(LogType.BROWSER)) {
                logs.append(entry.getLevel()).append(" - ")
                    .append(new Date(entry.getTimestamp())).append(": ")
                    .append(entry.getMessage()).append("\n");
            }
            Files.writeString(Paths.get(debugDir + "/browser_logs.txt"), logs.toString());
            
            // Save error details
            if (error != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                error.printStackTrace(pw);
                Files.writeString(Paths.get(debugDir + "/error.txt"), sw.toString());
            }
            
            // Save capabilities and environment info
            Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
            Files.writeString(Paths.get(debugDir + "/capabilities.json"), 
                new JSONObject(caps.asMap()).toString(2));
            
            logger.info("Debug bundle created at: {}", debugDir);
        } catch (IOException e) {
            logger.error("Failed to create debug bundle", e);
        }
    }
}
```

### Debugging WebDriver Issues

**Problem**: Specific WebDriver problems that are difficult to diagnose.

**Symptoms**:
- StaleElementReferenceException
- NoSuchElementException
- ElementNotInteractableException
- UnexpectedAlertPresentException

**Causes**:
- Element state changes
- DOM structure changes
- Element visibility/interactability issues
- Unexpected dialogs

**Solutions**:

1. **Implement driver health check**:
```java
public boolean isDriverHealthy() {
    try {
        // Try a simple command to check driver responsiveness
        driver.getCurrentUrl();
        return true;
    } catch (WebDriverException e) {
        logger.warn("WebDriver health check failed: {}", e.getMessage());
        return false;
    }
}

public WebDriver getHealthyDriver() {
    if (!isDriverHealthy()) {
        logger.info("Reinitializing unhealthy WebDriver");
        quitDriver();
        initDriver();
    }
    return driver;
}
```

2. **Create debug helper methods for element issues**:
```java
public void debugElement(WebElement element, String elementName) {
    try {
        logger.debug("Element '{}' properties:", elementName);
        logger.debug("  Tag: {}", element.getTagName());
        logger.debug("  Text: {}", element.getText());
        logger.debug("  Displayed: {}", element.isDisplayed());
        logger.debug("  Enabled: {}", element.isEnabled());
        logger.debug("  Selected: {}", element.isSelected());
        logger.debug("  Location: {}", element.getLocation());
        logger.debug("  Size: {}", element.getSize());
        
        // Get computed CSS properties
        logger.debug("  CSS properties:");
        logCssProperty(element, "visibility");
        logCssProperty(element, "display");
        logCssProperty(element, "opacity");
        logCssProperty(element, "position");
        logCssProperty(element, "z-index");
    } catch (StaleElementReferenceException e) {
        logger.debug("Element '{}' is stale", elementName);
    } catch (Exception e) {
        logger.debug("Error debugging element '{}': {}", elementName, e.getMessage());
    }
}

private void logCssProperty(WebElement element, String propertyName) {
    try {
        String value = element.getCssValue(propertyName);
        logger.debug("    {}: {}", propertyName, value);
    } catch (Exception e) {
        logger.debug("    {} (error): {}", propertyName, e.getMessage());
    }
}
```

3. **Implement alert handling utilities**:
```java
public boolean isAlertPresent() {
    try {
        driver.switchTo().alert();
        return true;
    } catch (NoAlertPresentException e) {
        return false;
    }
}

public void handleAlert(boolean accept) {
    try {
        Alert alert = driver.switchTo().alert();
        logger.info("Alert detected: {}", alert.getText());
        
        if (accept) {
            alert.accept();
            logger.info("Alert accepted");
        } else {
            alert.dismiss();
            logger.info("Alert dismissed");
        }
    } catch (NoAlertPresentException e) {
        logger.debug("No alert present to handle");
    }
}

public void safeAction(Runnable action) {
    try {
        action.run();
    } catch (UnexpectedAlertPresentException e) {
        logger.warn("Unexpected alert encountered: {}", e.getMessage());
        handleAlert(true); // Accept the alert
        // Retry the action
        action.run();
    }
}
```

4. **Create a robust element finder for debugging**:
```java
public WebElement findElementSafely(By locator, int timeoutInSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    
    try {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    } catch (TimeoutException e) {
        // Debug why element wasn't found
        logger.error("Element not found: {}", locator);
        
        // Try alternative approaches
        try {
            // Try with JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement element = null;
            
            if (locator instanceof By.ByCssSelector) {
                String css = locator.toString().replace("By.cssSelector: ", "");
                element = (WebElement) js.executeScript(
                    "return document.querySelector(arguments[0]);", css);
            } else if (locator instanceof By.ById) {
                String id = locator.toString().replace("By.id: ", "");
                element = (WebElement) js.executeScript(
                    "return document.getElementById(arguments[0]);", id);
            }
            
            if (element != null) {
                logger.info("Found element using JavaScript: {}", locator);
                return element;
            }
        } catch (Exception jsEx) {
            logger.debug("JavaScript element search failed: {}", jsEx.getMessage());
        }
        
        // Log DOM snapshot for debugging
        logger.debug("DOM snapshot: {}", driver.getPageSource().substring(0, 
            Math.min(10000, driver.getPageSource().length())));
        
        throw new NoSuchElementException("Element not found: " + locator);
    }
}
```

## Frequently Asked Questions

### General Framework Questions

#### Q: How do I add a new browser to the framework?

**A**: To add a new browser (e.g., Edge):

1. Add the browser option in the configuration properties file:
```properties
# config.properties
browser=edge
```

2. Update the WebDriverManager class:
```java
public static void initDriver() {
    String browser = ConfigurationManager.getInstance().getBrowser().toLowerCase();
    
    switch (browser) {
        case "chrome":
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(getChromeOptions());
            break;
        case "firefox":
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver(getFirefoxOptions());
            break;
        case "edge":
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver(getEdgeOptions());
            break;
        default:
            throw new IllegalArgumentException("Browser " + browser + " not supported");
    }
    
    driver.manage().window().maximize();
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
}

private static EdgeOptions getEdgeOptions() {
    EdgeOptions options = new EdgeOptions();
    // Configure Edge-specific options
    return options;
}
```

3. Add any browser-specific logic to your test code

#### Q: How can I run tests in headless mode?

**A**: Configure headless mode in the browser options:

```java
public static ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    
    if (ConfigurationManager.getInstance().isHeadless()) {
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
    }
    
    return options;
}
```

And in your config.properties:
```properties
headless=true
```

#### Q: How do I add custom capabilities to WebDriver?

**A**: Extend the browser options with desired capabilities:

```java
public static ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    
    // Add arguments
    options.addArguments("--disable-extensions");
    options.addArguments("--start-maximized");
    
    // Add preferences
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("profile.default_content_setting_values.notifications", 2);
    options.setExperimentalOption("prefs", prefs);
    
    // Add capabilities
    options.setCapability("acceptInsecureCerts", true);
    
    return options;
}
```

#### Q: How do I handle dynamic IDs in locators?

**A**: Use a more stable locator strategy:

```java
// Instead of fixed IDs which may change:
public static final By EMAIL_FIELD = By.id("email-field-12345");

// Use data attributes if available:
public static final By EMAIL_FIELD = By.cssSelector("[data-testid='email-input']");

// Use XPath with partial matching if necessary:
public static final By EMAIL_FIELD = By.xpath("//input[contains(@id, 'email-field')]");

// Use XPath with multiple attributes:
public static final By EMAIL_FIELD = By.xpath("//input[@type='email' and @placeholder='Email']");
```

### Test Execution Questions

#### Q: How do I run a specific test or test group?

**A**: Use TestNG XML files or command line parameters:

With XML file:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Specific Test Suite">
  <test name="Specific Test">
    <classes>
      <class name="com.storydoc.tests.SignupTest">
        <methods>
          <include name="testPositiveSignupFlow"/>
        </methods>
      </class>
    </classes>
  </test>
</suite>
```

With Maven command:
```bash
mvn test -Dgroups=signup
# or for specific test
mvn test -Dtest=SignupTest#testPositiveSignupFlow
```

#### Q: How can I pass parameters to my tests?

**A**: Use TestNG parameters, data providers, or system properties:

With TestNG parameters:
```java
@Parameters({"browser", "environment"})
@BeforeTest
public void setUp(String browser, String environment) {
    // Setup using parameters
}
```

With Maven:
```bash
mvn test -Dbrowser=chrome -Denvironment=staging
```

#### Q: How do I handle flaky tests?

**A**: Implement retry logic for flaky tests:

```java
// Create a retry analyzer
public class RetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_RETRY = 2;
    
    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY) {
            count++;
            return true;
        }
        return false;
    }
}

// Apply to specific tests
@Test(retryAnalyzer = RetryAnalyzer.class)
public void flakyTest() {
    // Test implementation
}

// Or apply to all tests with a listener
public class RetryListener implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, 
                          Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
```

#### Q: How do I handle timeouts appropriately?

**A**: Use a tiered timeout strategy:

```java
// Short timeout for quick operations
public WebElement waitForElementClickable(By locator) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    return wait.until(ExpectedConditions.elementToBeClickable(locator));
}

// Standard timeout for most operations
public WebElement waitForElementVisible(By locator) {
    WebDriverWait wait = new WebDriverWait(driver, 
        Duration.ofSeconds(ConfigurationManager.getInstance().getTimeout()));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}

// Long timeout for slow operations
public void waitForPageLoad() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> ((JavascriptExecutor) driver)
        .executeScript("return document.readyState").equals("complete"));
}
```

### Framework Architecture Questions

#### Q: How do I extend the framework with new page objects?

**A**: Follow these steps:

1. Create a locator file for the new page:
```java
public class NewPageLocators {
    public static final By PAGE_HEADER = By.cssSelector("h1.page-header");
    public static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");
    // Add more locators as needed
}
```

2. Create the page object class:
```java
public class NewPage extends BasePage {
    public NewPage(WebDriver driver) {
        super(driver);
    }
    
    public boolean isPageLoaded() {
        return isElementPresent(NewPageLocators.PAGE_HEADER);
    }
    
    public void clickSubmit() {
        click(NewPageLocators.SUBMIT_BUTTON);
    }
    
    // Add more page-specific methods
}
```

3. Update existing page objects for navigation:
```java
public class ExistingPage extends BasePage {
    // Existing methods...
    
    public NewPage navigateToNewPage() {
        click(ExistingPageLocators.NEW_PAGE_LINK);
        return new NewPage(driver);
    }
}
```

#### Q: How can I make the framework more maintainable?

**A**: Apply these best practices:

1. Keep locators centralized in separate classes
2. Use fluent interfaces for readability
3. Implement proper encapsulation in page objects
4. Write comprehensive JavaDoc comments
5. Create utility classes for common operations
6. Implement consistent naming conventions
7. Use appropriate design patterns (Factory, Builder, etc.)
8. Minimize duplication through inheritance or composition

#### Q: How can I implement data-driven testing?

**A**: Use TestNG DataProviders:

```java
@DataProvider(name = "signupTestData")
public Object[][] signupTestData() {
    return new Object[][] {
        {"test1@example.com", "Password123", true, "Standard user"},
        {"test2@example.com", "Password456", true, "Premium user"}
    };
}

@Test(dataProvider = "signupTestData")
public void testSignupWithMultipleUsers(String email, String password, 
                                       boolean acceptTerms, String userType) {
    logger.info("Testing signup with user type: {}", userType);
    
    SuccessPage successPage = signupPage
        .enterEmail(email)
        .enterPassword(password);
        
    if (acceptTerms) {
        successPage = successPage.acceptTerms().clickSignUp();
    } else {
        // Test negative case without accepting terms
    }
    
    Assert.assertTrue(successPage.isSignupSuccessful());
}
```

#### Q: How do I handle test dependencies properly?

**A**: Use TestNG dependencies and proper page object transitions:

```java
@Test(groups = {"signup", "smoke"})
public void testNavigateToSignupPage() {
    signupPage.navigateToSignupPage();
    Assert.assertTrue(signupPage.isSignupPageLoaded());
}

@Test(groups = {"signup"}, dependsOnMethods = {"testNavigateToSignupPage"})
public void testEnterCredentials() {
    signupPage.enterEmail("test@example.com")
              .enterPassword("Test@123");
    Assert.assertTrue(signupPage.areCredentialsEntered());
}

@Test(groups = {"signup"}, dependsOnMethods = {"testEnterCredentials"})
public void testSubmitForm() {
    SuccessPage successPage = signupPage.acceptTerms().clickSignUp();
    Assert.assertTrue(successPage.isSignupSuccessful());
}
```

## Getting Additional Help

### Internal Resources

If this guide doesn't address your specific issue, consider these internal resources:

1. **Framework Documentation**: Refer to the comprehensive framework documentation in the `docs` directory
2. **Examples Directory**: Check the `examples` directory for sample test implementations
3. **Codebase**: Explore the source code, especially utility classes and helpers
4. **Team Knowledge Base**: Search the internal knowledge base for similar issues

### External Resources

These external resources can provide additional help:

1. **Selenium Documentation**: [Selenium Documentation](https://www.selenium.dev/documentation/)
2. **TestNG Documentation**: [TestNG Documentation](https://testng.org/doc/)
3. **WebDriverManager Documentation**: [WebDriverManager GitHub](https://github.com/bonigarcia/webdrivermanager)
4. **Stack Overflow**: Search for similar issues on [Stack Overflow](https://stackoverflow.com/questions/tagged/selenium)

### Reporting Issues

If you encounter a framework issue not covered in this guide:

1. **Create a Detailed Bug Report**:
   - Include steps to reproduce
   - Provide environment details (OS, browser version, Java version)
   - Attach relevant logs, screenshots, and stack traces
   - Specify which version of the framework you're using

2. **Follow These Channels**:
   - Submit issues to the internal issue tracker
   - For urgent issues, contact the QA Lead or Automation Engineer
   - Add the issue to the weekly automation meeting agenda

### Required Information for Support

When seeking help, always provide:

1. **Test Execution Details**:
   - Test class and method name
   - Environment used (local, CI, staging)
   - Browser and version
   - Execution time and frequency of issue

2. **Evidence**:
   - ExtentReports output
   - Log files (framework logs, browser console logs)
   - Screenshots at point of failure
   - Video recording if available

3. **Framework Information**:
   - Framework version
   - Any custom modifications
   - Configuration settings
   - Recent changes that might have impacted the test

This comprehensive information will help the team diagnose and resolve your issue more quickly.
```

# docs/TROUBLESHOOTING.md
```markdown
# Storydoc Signup Automation Framework - Troubleshooting Guide

## Table of Contents

1. [Introduction](#introduction)
2. [Common Issues Overview](#common-issues-overview)
3. [WebDriver Issues](#webdriver-issues)
4. [Element Location Problems](#element-location-problems)
5. [Synchronization Issues](#synchronization-issues)
6. [Test Execution Problems](#test-execution-problems)
7. [Configuration Issues](#configuration-issues)
8. [Reporting Issues](#reporting-issues)
9. [Performance Optimization](#performance-optimization)
10. [Debugging Techniques](#debugging-techniques)
11. [Frequently Asked Questions](#frequently-asked-questions)
12. [Getting Additional Help](#getting-additional-help)

## Introduction

Welcome to the troubleshooting guide for the Storydoc Signup Automation Framework. This document is designed to help you diagnose and resolve common issues that may arise when using the framework to automate testing of the Storydoc signup process.

### Purpose of This Guide

This guide provides solutions for common problems encountered during:
- Framework setup and configuration
- Test development and execution
- WebDriver and browser interactions
- Test reporting and analysis

### How to Use This Guide

1. Use the table of contents to navigate to the section relevant to your issue
2. Each section contains problems categorized by symptoms, causes, and solutions
3. Code examples are provided where applicable to illustrate solutions
4. For issues not covered in this guide, refer to the [Getting Additional Help](#getting-additional-help) section

### General Troubleshooting Approach

When encountering issues with the automation framework, follow these steps:

1. **Identify the symptoms**: What error message is shown? At what point does the test fail?
2. **Check the logs**: Review framework logs, browser console logs, and test reports
3. **Reproduce the issue**: Try to reproduce the problem in a controlled environment
4. **Isolate the problem**: Determine if the issue is with the framework, test code, or application
5. **Apply solutions**: Try the relevant solutions from this guide
6. **Document findings**: Document the issue and solution for future reference

## Common Issues Overview

This table provides a quick reference to common issues and where to find solutions in this guide:

| Issue Type | Common Symptoms | Section Reference |
|------------|----------------|-------------------|
| WebDriver initialization failures | `SessionNotCreatedException`, `WebDriverException` | [WebDriver Issues](#webdriver-issues) |
| Element not found | `NoSuchElementException`, test fails to interact with elements | [Element Location Problems](#element-location-problems) |
| Timing issues | `TimeoutException`, intermittent failures | [Synchronization Issues](#synchronization-issues) |
| Test failures | Tests fail inconsistently, incorrect test flow | [Test Execution Problems](#test-execution-problems) |
| Framework setup issues | Configuration not loading, incorrect settings | [Configuration Issues](#configuration-issues) |
| Missing or incorrect reports | Reports not generated, missing screenshots | [Reporting Issues](#reporting-issues) |
| Slow test execution | Tests take too long to run | [Performance Optimization](#performance-optimization) |

## WebDriver Issues

### Driver Binary Not Found

**Problem**: Tests fail to start with error related to driver executable not found.

**Symptoms**:
```
java.lang.IllegalStateException: The driver executable does not exist: /path/to/chromedriver
```

**Causes**:
- WebDriverManager failed to download or locate the driver binary
- Internet connectivity issues
- Incompatible browser and driver versions

**Solutions**:

1. **Use WebDriverManager's setup method explicitly**:
```java
// For Chrome
WebDriverManager.chromedriver().setup();

// For Firefox
WebDriverManager.firefoxdriver().setup();

// For Edge
WebDriverManager.edgedriver().setup();
```

2. **Specify a particular version of the driver**:
```java
WebDriverManager.chromedriver().driverVersion("109.0.5414.74").setup();
```

3. **Manual driver management** (fallback approach):
```java
System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
```

4. **Verify internet connectivity** and proxy settings if WebDriverManager is unable to download drivers.

### Browser Version Incompatibility

**Problem**: WebDriver initialization fails due to browser and driver version mismatch.

**Symptoms**:
```
org.openqa.selenium.SessionNotCreatedException: Message: session not created: This version of ChromeDriver only supports Chrome version XX
```

**Causes**:
- Browser was updated but driver version doesn't match
- Driver version too old or too new for browser

**Solutions**:

1. **Let WebDriverManager handle version matching**:
```java
WebDriverManager.chromedriver().browserVersion("112").setup();
```

2. **Update the browser to match available driver** or vice versa.

3. **Use the framework's configuration to specify browser and driver version**:
```properties
# In config.properties
browser=chrome
browser.version=112
```

### Browser Crashes During Test

**Problem**: Browser terminates unexpectedly during test execution.

**Symptoms**:
- `WebDriverException` with message about lost connection
- Test process hangs indefinitely
- Browser window disappears during test

**Causes**:
- Memory issues or resource constraints
- Browser instability
- Incompatible browser extensions
- Conflicting browser instances

**Solutions**:

1. **Implement robust error handling and recovery**:
```java
public WebDriver getDriver() {
    try {
        if (driver == null || isDriverClosed(driver)) {
            initDriver();
        }
        return driver;
    } catch (WebDriverException e) {
        logger.error("WebDriver crashed, reinitializing", e);
        quitDriver(); // Force cleanup
        initDriver(); // Reinitialize
        return driver;
    }
}

private boolean isDriverClosed(WebDriver driver) {
    try {
        driver.getTitle(); // Simple command to check if driver is responsive
        return false;
    } catch (Exception e) {
        return true;
    }
}
```

2. **Increase memory allocation** for the JVM running the tests.

3. **Use headless browser mode** for more stability in CI/CD environments:
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
driver = new ChromeDriver(options);
```

4. **Disable browser extensions and notifications**:
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--disable-extensions");
options.addArguments("--disable-notifications");
```

### Headless Browser Issues

**Problem**: Tests behave differently or fail when running in headless mode.

**Symptoms**:
- Element interactions fail only in headless mode
- Tests pass locally but fail in CI pipeline
- Unexpected JavaScript errors in headless mode

**Causes**:
- Viewport size differences in headless mode
- Feature differences between normal and headless mode
- Timing differences

**Solutions**:

1. **Set specific window size for headless browser**:
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--headless");
options.addArguments("--window-size=1920,1080");
```

2. **Add additional arguments to improve headless mode behavior**:
```java
options.addArguments("--disable-gpu");
options.addArguments("--no-sandbox");
options.addArguments("--disable-dev-shm-usage");
```

3. **Use slower interaction speeds in headless mode**:
```java
if (ConfigurationManager.getInstance().isHeadless()) {
    waitUtils.setLongerTimeouts();
}
```

### WebDriver Thread Safety Issues

**Problem**: Concurrent tests interfere with each other when running in parallel.

**Symptoms**:
- Random test failures in parallel execution
- Browser actions being applied to the wrong window
- Element interactions targeting the wrong context

**Causes**:
- Sharing WebDriver instance between threads
- Missing ThreadLocal implementation
- Static references causing thread conflicts

**Solutions**:

1. **Ensure WebDriver instances are thread-local**:
```java
public class WebDriverManager {
    private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    
    public static WebDriver getDriver() {
        if (driverThreadLocal.get() == null) {
            initDriver();
        }
        return driverThreadLocal.get();
    }
    
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
        }
    }
}
```

2. **Ensure all page objects and test context data are also thread-safe**:
```java
public class TestBase {
    private static ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    
    @BeforeMethod
    public void setUp() {
        WebDriver driver = WebDriverManager.getDriver();
        ExtentTest test = ExtentManager.createTest(getClass().getSimpleName());
        extentTestThreadLocal.set(test);
    }
    
    protected ExtentTest getTest() {
        return extentTestThreadLocal.get();
    }
}
```

3. **Use TestNG parallel execution with proper threading settings**:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Parallel Suite" parallel="methods" thread-count="3">
  <test name="Signup Tests">
    <classes>
      <class name="com.storydoc.tests.SignupTest"/>
    </classes>
  </test>
</suite>
```

## Element Location Problems

### NoSuchElementException

**Problem**: WebDriver fails to locate elements on the page.

**Symptoms**:
```
org.openqa.selenium.NoSuchElementException: no such element: Unable to locate element: {"method":"css selector","selector":"input[data-testid='email-input']"}
```

**Causes**:
- Element locator is incorrect
- Element is in an iframe or shadow DOM
- Element has not been rendered yet
- Page structure has changed

**Solutions**:

1. **Check and update locators** in the locator repository:
```java
// Original locator
public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-input']");

// Updated locator if the data-testid attribute changed
public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-field']");
// Or use alternative strategy
public static final By EMAIL_FIELD = By.id("email");
```

2. **Handle iframes properly**:
```java
// Switch to iframe before accessing elements
driver.switchTo().frame("iframe-id");
// Interact with elements
driver.findElement(SignupPageLocators.EMAIL_FIELD).sendKeys("test@example.com");
// Switch back to main content
driver.switchTo().defaultContent();
```

3. **Implement proper wait strategies**:
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(SignupPageLocators.EMAIL_FIELD));
element.sendKeys("test@example.com");
```

4. **Use multiple locator strategies as fallback**:
```java
public WebElement findElementSafely(By primaryLocator, By... fallbackLocators) {
    try {
        return waitForElementVisible(primaryLocator);
    } catch (NoSuchElementException | TimeoutException e) {
        for (By fallbackLocator : fallbackLocators) {
            try {
                return waitForElementVisible(fallbackLocator);
            } catch (NoSuchElementException | TimeoutException ex) {
                // Continue to next fallback
            }
        }
        throw new NoSuchElementException("Element not found with primary or fallback locators");
    }
}
```

### StaleElementReferenceException

**Problem**: References to WebElements become invalid after page updates.

**Symptoms**:
```
org.openqa.selenium.StaleElementReferenceException: stale element reference: element is not attached to the page document
```

**Causes**:
- Page reload or navigation
- DOM updates through JavaScript
- AJAX refreshing parts of the page
- Element was removed and recreated

**Solutions**:

1. **Find the element again when needed**:
```java
public void safeClick(By locator) {
    try {
        driver.findElement(locator).click();
    } catch (StaleElementReferenceException e) {
        // Find the element again
        driver.findElement(locator).click();
    }
}
```

2. **Implement a retry mechanism with explicit waits**:
```java
public void clickWithRetry(By locator, int maxRetries) {
    int attempts = 0;
    while (attempts < maxRetries) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
            return;
        } catch (StaleElementReferenceException e) {
            attempts++;
            if (attempts == maxRetries) {
                throw e;
            }
        }
    }
}
```

3. **Create a utility method to handle stale elements with refreshing strategy**:
```java
public <T> T withStaleProtection(Supplier<T> action) {
    int maxRetries = 3;
    StaleElementReferenceException lastException = null;
    
    for (int retry = 0; retry < maxRetries; retry++) {
        try {
            return action.get();
        } catch (StaleElementReferenceException e) {
            lastException = e;
            sleep(500); // Short pause before retry
        }
    }
    throw lastException;
}

// Usage example
String buttonText = withStaleProtection(() -> driver.findElement(BUTTON_LOCATOR).getText());
```

### Dynamic Elements and Shadow DOM

**Problem**: Unable to locate elements in dynamic content or Shadow DOM.

**Symptoms**:
- `NoSuchElementException` for elements that are visibly present
- Unable to interact with elements inside custom web components
- Standard locator strategies fail

**Causes**:
- Element is inside Shadow DOM
- Element is generated dynamically with JavaScript
- Element has dynamic attributes or IDs

**Solutions**:

1. **Accessing elements in Shadow DOM**:
```java
// Access shadow root
WebElement hostElement = driver.findElement(By.cssSelector("host-element"));
SearchContext shadowRoot = hostElement.getShadowRoot();

// Find element in shadow DOM
WebElement shadowElement = shadowRoot.findElement(By.cssSelector(".shadow-element"));
```

2. **Use JavaScript to find and interact with dynamic elements**:
```java
public WebElement findDynamicElement(String cssSelector) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    return (WebElement) js.executeScript(
        "return document.querySelector('" + cssSelector + "')");
}
```

3. **Create a specialized locator strategy for dynamic IDs**:
```java
public By dynamicId(String idPrefix) {
    return new By() {
        @Override
        public List<WebElement> findElements(SearchContext context) {
            return ((FindsBy) context).findElements(
                By.xpath("//*[starts-with(@id, '" + idPrefix + "')]"));
        }
    };
}
```

### Incorrect Locator Types

**Problem**: Element locators are technically valid but not optimal for the page structure.

**Symptoms**:
- Tests are flaky or slow
- Element location intermittently fails
- Locators work in one browser but not another

**Causes**:
- Using XPath when CSS selector would be more efficient
- Overly complex selectors
- Brittle locators dependent on page structure

**Solutions**:

1. **Prefer data-test attributes when available**:
```java
// Best practice
By.cssSelector("[data-testid='signup-button']");

// Instead of brittle structural selectors
By.xpath("//div[@class='form-container']/div[2]/button");
```

2. **Use CSS selectors instead of XPath when possible**:
```java
// Prefer this
By.cssSelector("button.signup-btn");

// Instead of this
By.xpath("//button[@class='signup-btn']");
```

3. **Create more robust locators with multiple attributes**:
```java
By.cssSelector("button.btn-primary[type='submit'][name='signup']");
```

4. **Use relative locators for elements that are positioned relative to known elements**:
```java
// Selenium 4 relative locator
WebElement emailField = driver.findElement(By.cssSelector("input[data-testid='email-input']"));
WebElement passwordField = driver.findElement(RelativeLocator.with(By.tagName("input")).below(emailField));
```

## Synchronization Issues

### TimeoutException

**Problem**: Element interactions fail because elements aren't ready within the timeout period.

**Symptoms**:
```
org.openqa.selenium.TimeoutException: Expected condition failed: waiting for visibility of element located by By.cssSelector: input[data-testid='email-input']
```

**Causes**:
- Page or element loads too slowly
- Wait timeout too short
- JavaScript rendering delays
- Network latency

**Solutions**:

1. **Increase wait timeout for specific scenarios**:
```java
public WebElement waitLongerForElement(By locator) {
    WebDriverWait extendedWait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Longer timeout
    return extendedWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}
```

2. **Create configurable timeouts**:
```java
public WebElement waitForElementVisible(By locator) {
    int timeout = ConfigurationManager.getInstance().getTimeout();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}
```

3. **Implement adaptive wait times that adjust based on environment**:
```java
public int getAdaptiveTimeout() {
    // Get base timeout
    int baseTimeout = ConfigurationManager.getInstance().getTimeout();
    
    // Apply multiplier for CI environment (which might be slower)
    if ("ci".equals(ConfigurationManager.getInstance().getEnvironment())) {
        return baseTimeout * 2;
    }
    
    return baseTimeout;
}
```

4. **Wait for page to completely load before proceeding**:
```java
public void waitForPageLoad() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> ((JavascriptExecutor) driver)
        .executeScript("return document.readyState").equals("complete"));
}
```

### AJAX and Dynamic Content

**Problem**: Tests fail to handle asynchronous content that loads after the initial page load.

**Symptoms**:
- Elements are not found even with waits
- Interactions happen before content is fully loaded
- Assertions fail on dynamic content

**Causes**:
- AJAX calls loading content after page load
- Animations delaying element visibility
- Progressive loading of page elements

**Solutions**:

1. **Wait for specific AJAX indicators to disappear**:
```java
public void waitForAjaxToComplete() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Boolean) js.executeScript("return jQuery.active == 0");
    });
}
```

2. **Create a custom wait condition for network activity**:
```java
public void waitForNetworkIdle() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Boolean) js.executeScript(
            "return window.performance.getEntriesByType('resource').filter(r => !r.responseEnd).length == 0");
    });
}
```

3. **Implement custom wait for Angular applications**:
```java
public void waitForAngular() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Boolean) js.executeScript(
            "return window.getAllAngularTestabilities().findIndex(x=>!x.isStable()) === -1");
    });
}
```

### Page Transitions

**Problem**: Tests fail during navigation between pages.

**Symptoms**:
- Element interactions happen on wrong page
- Unexpected page state during test execution
- Page loads incomplete before test continues

**Causes**:
- Navigation not complete before test continues
- Redirect chains not fully completed
- SPA routing not settled

**Solutions**:

1. **Verify page URL before proceeding**:
```java
public void waitForUrlToContain(String urlFragment) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(ExpectedConditions.urlContains(urlFragment));
}
```

2. **Check for specific elements that confirm page load**:
```java
public boolean isSignupPageLoaded() {
    try {
        return waitForElementVisible(SignupPageLocators.SIGNUP_FORM) != null;
    } catch (TimeoutException e) {
        return false;
    }
}
```

3. **Implement wait for page title change**:
```java
public void waitForPageTitle(String expectedTitle) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(ExpectedConditions.titleContains(expectedTitle));
}
```

4. **Use proper page object transitions**:
```java
public SuccessPage clickSignUp() {
    click(SignupPageLocators.SIGNUP_BUTTON);
    
    // Wait for page transition to complete
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(ExpectedConditions.urlContains("success"));
    
    // Return new page object
    return new SuccessPage(driver);
}
```

### Implicit vs Explicit Wait Conflicts

**Problem**: Mixing implicit and explicit waits causes unpredictable timing issues.

**Symptoms**:
- Inconsistent wait behavior
- Timeouts longer than expected
- Different behavior across test runs

**Causes**:
- Using both implicit and explicit waits in the same test
- Implicit wait set globally but explicit waits used locally
- Wait strategy inconsistencies

**Solutions**:

1. **Disable implicit waits and use only explicit waits**:
```java
// In WebDriver initialization
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

// Then use explicit waits everywhere
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
```

2. **Create a consistent wait strategy**:
```java
public class WaitStrategy {
    public static WebDriverWait getWait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(
            ConfigurationManager.getInstance().getTimeout()));
    }
    
    public static WebElement waitForElementVisible(WebDriver driver, By locator) {
        return getWait(driver).until(
            ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    public static WebElement waitForElementClickable(WebDriver driver, By locator) {
        return getWait(driver).until(
            ExpectedConditions.elementToBeClickable(locator));
    }
}
```

3. **Document the wait strategy** in framework documentation to ensure consistent usage.

## Test Execution Problems

### Flaky Tests

**Problem**: Tests pass sometimes and fail other times without code changes.

**Symptoms**:
- Inconsistent test results across runs
- Fails in CI but passes locally
- Different results when run individually vs. in suite

**Causes**:
- Timing issues
- Race conditions
- Environmental differences
- Resource contention
- Element locator brittleness

**Solutions**:

1. **Implement retry mechanism for flaky tests**:
```java
public class RetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_RETRY = 2;
    
    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY) {
            count++;
            return true;
        }
        return false;
    }
}

// Usage in test
@Test(retryAnalyzer = RetryAnalyzer.class)
public void flakyTest() {
    // Test implementation
}
```

2. **Create more robust synchronization**:
```java
public void waitForStableElement(By locator) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(driver -> {
        try {
            WebElement element = driver.findElement(locator);
            // Check if element position is stable
            Point location = element.getLocation();
            Thread.sleep(200); // Short wait
            return location.equals(element.getLocation());
        } catch (Exception e) {
            return false;
        }
    });
}
```

3. **Identify and quarantine flaky tests**:
```java
// Mark known flaky tests for investigation
@Test(groups = {"flaky", "quarantine"})
@Ignore("JIRA-1234: Test is flaky due to timing issues")
public void knownFlakyTest() {
    // Test implementation
}
```

4. **Log detailed information for flaky test troubleshooting**:
```java
// Use TestNG listeners to capture details about failures
public class TestListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = ((TestBase) result.getInstance()).getDriver();
        captureScreenshot(driver, result.getName());
        logBrowserConsole(driver);
        logPageSource(driver);
    }
}
```

### Test Data Issues

**Problem**: Tests fail due to data issues rather than application or framework problems.

**Symptoms**:
- Tests fail with validation errors
- Unique constraint violations
- Data-dependent test failures

**Causes**:
- Reusing the same test data across runs
- Static test data that doesn't account for uniqueness
- Data not cleaned up between test runs

**Solutions**:

1. **Generate unique test data for each run**:
```java
public String generateUniqueEmail() {
    return "test" + System.currentTimeMillis() + "@example.com";
}

public String generateUniquePassword() {
    return "Test@" + System.currentTimeMillis();
}
```

2. **Implement proper test data cleanup**:
```java
@AfterMethod
public void cleanupTestData() {
    // If you have API access, clean up test accounts
    if (testEmailUsed != null) {
        apiClient.deleteUserAccount(testEmailUsed);
        testEmailUsed = null;
    }
}
```

3. **Use test-specific data namespaces**:
```java
public String getTestSpecificEmail(Method testMethod) {
    return "test." + testMethod.getName() + "." + System.currentTimeMillis() + "@example.com";
}
```

4. **Data-driven tests with TestNG DataProvider**:
```java
@DataProvider(name = "validCredentials")
public Object[][] validCredentials() {
    return new Object[][] {
        { generateUniqueEmail(), "Test@123", "standard user" },
        { generateUniqueEmail(), "Test@456", "premium user" }
    };
}

@Test(dataProvider = "validCredentials")
public void testSignupWithVariousCredentials(String email, String password, String userType) {
    // Test implementation using the provided data
}
```

### Environment-Specific Issues

**Problem**: Tests pass in one environment but fail in another.

**Symptoms**:
- Tests pass locally but fail in CI/CD
- Tests pass in development but fail in staging
- Different behavior across operating systems

**Causes**:
- Browser version differences
- OS-specific rendering variations
- Environment configuration differences
- Network latency and performance variations

**Solutions**:

1. **Implement environment-aware configuration**:
```java
public class ConfigurationManager {
    public int getTimeout() {
        String env = System.getProperty("test.environment", "local");
        switch (env) {
            case "ci":
                return 30; // Longer timeouts for CI
            case "staging":
                return 20; // Medium timeouts for staging
            default:
                return 10; // Default for local
        }
    }
}
```

2. **Create browser-specific test logic when needed**:
```java
public void handleBrowserSpecificFlow() {
    String browser = ConfigurationManager.getInstance().getBrowser();
    if ("firefox".equalsIgnoreCase(browser)) {
        // Firefox-specific logic
        // ...
    } else {
        // Default logic for other browsers
        // ...
    }
}
```

3. **Standardize test environments**:
```java
// Use Docker containers for consistent environments
// Example docker-compose.yml excerpt
services:
  chrome:
    image: selenium/standalone-chrome:latest
    ports:
      - "4444:4444"
    volumes:
      - /dev/shm:/dev/shm  # Shared memory for better performance
```

4. **Log detailed environment information with test reports**:
```java
public void addEnvironmentInfoToReport() {
    ExtentTest test = getTest();
    test.assignCategory(ConfigurationManager.getInstance().getEnvironment());
    test.assignDevice(ConfigurationManager.getInstance().getBrowser());
    test.info("OS: " + System.getProperty("os.name"));
    test.info("Browser: " + ConfigurationManager.getInstance().getBrowser());
    test.info("Environment: " + ConfigurationManager.getInstance().getEnvironment());
}
```

### TestNG Configuration Problems

**Problem**: Tests are not running as expected due to TestNG configuration issues.

**Symptoms**:
- Tests not executed in the expected order
- Test dependencies not respected
- Incorrect grouping or filtering of tests

**Causes**:
- Improper TestNG XML configuration
- Misunderstood test dependencies
- Group definitions incorrect

**Solutions**:

1. **Fix test dependencies**:
```java
// Define test dependencies correctly
@Test(groups = {"signup"})
public void testNavigateToSignupPage() {
    // Test implementation
}

@Test(groups = {"signup"}, dependsOnMethods = {"testNavigateToSignupPage"})
public void testEnterCredentials() {
    // Test implementation
}
```

2. **Configure TestNG XML properly**:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Signup Test Suite">
  <test name="Signup Tests">
    <groups>
      <run>
        <include name="signup"/>
        <exclude name="flaky"/>
      </run>
    </groups>
    <classes>
      <class name="com.storydoc.tests.SignupTest"/>
    </classes>
  </test>
</suite>
```

3. **Use test listeners to debug test execution flow**:
```java
public class DebugListener implements ITestListener {
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Starting test: " + result.getName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("Test passed: " + result.getName());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("Test failed: " + result.getName());
    }
}
```

4. **Ensure proper setup and teardown methods**:
```java
@BeforeSuite
public void suiteSetup() {
    // Suite-level setup
}

@BeforeClass
public void classSetup() {
    // Class-level setup
}

@BeforeMethod
public void methodSetup() {
    // Method-level setup
}

@AfterMethod
public void methodTeardown() {
    // Method-level cleanup
}

@AfterClass
public void classTeardown() {
    // Class-level cleanup
}

@AfterSuite
public void suiteTeardown() {
    // Suite-level cleanup
}
```

## Configuration Issues

### Properties File Loading

**Problem**: Configuration properties not loading correctly.

**Symptoms**:
- `NullPointerException` when accessing configuration values
- Default values used instead of configured ones
- Configuration file not found errors

**Causes**:
- Incorrect file path
- Classpath issues
- File format problems
- Character encoding issues

**Solutions**:

1. **Verify file location and path**:
```java
public Properties loadProperties() {
    Properties props = new Properties();
    String configFilePath = "config.properties";
    
    // Try loading from classpath
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFilePath)) {
        if (input == null) {
            System.err.println("Unable to find " + configFilePath + " in classpath");
            // Try loading from file system as fallback
            try (FileInputStream fileInput = new FileInputStream(configFilePath)) {
                props.load(fileInput);
            }
        } else {
            props.load(input);
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    return props;
}
```

2. **Implement proper error handling for missing properties**:
```java
public String getProperty(String key, String defaultValue) {
    String value = properties.getProperty(key);
    if (value == null) {
        logger.warn("Property '{}' not found, using default: {}", key, defaultValue);
        return defaultValue;
    }
    return value;
}
```

3. **Support environment-specific property files**:
```java
public Properties loadEnvironmentProperties() {
    Properties props = new Properties();
    String env = System.getProperty("test.environment", "local");
    String configFile = env + ".properties";
    
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
        if (input != null) {
            props.load(input);
        } else {
            logger.warn("Environment config file {} not found", configFile);
            // Load default properties
            try (InputStream defaultInput = getClass().getClassLoader().getResourceAsStream("default.properties")) {
                if (defaultInput != null) {
                    props.load(defaultInput);
                }
            }
        }
    } catch (IOException e) {
        logger.error("Error loading properties", e);
    }
    
    return props;
}
```

4. **Add clear logging about configuration loading**:
```java
public void loadConfiguration() {
    logger.info("Loading configuration for environment: {}", getEnvironment());
    // Configuration loading logic
    logLoadedConfiguration();
}

private void logLoadedConfiguration() {
    logger.info("Configuration loaded: Browser={}, BaseURL={}, Timeout={}s",
        getBrowser(), getBaseUrl(), getTimeout());
}
```

### Environment Variables Issues

**Problem**: Environment variables not properly set or retrieved.

**Symptoms**:
- Security credentials not available
- Environment-specific settings not applied
- Default values used unexpectedly

**Causes**:
- Environment variables not set in execution environment
- Permission issues accessing environment variables
- Case sensitivity problems

**Solutions**:

1. **Get environment variables with fallback values**:
```java
public String getEnvironmentVariable(String name, String defaultValue) {
    String value = System.getenv(name);
    if (value == null || value.isEmpty()) {
        logger.warn("Environment variable {} not set, using default", name);
        return defaultValue;
    }
    return value;
}
```

2. **Load sensitive information from environment variables**:
```java
public String getTestPassword() {
    String password = System.getenv("TEST_PASSWORD");
    if (password == null || password.isEmpty()) {
        // Use a default password only for local development
        if ("local".equals(getEnvironment())) {
            return "TestPassword123";
        } else {
            throw new ConfigurationException("TEST_PASSWORD environment variable must be set");
        }
    }
    return password;
}
```

3. **Document required environment variables**:
```java
// In your framework documentation or README:
/**
 * Required Environment Variables:
 * - TEST_EMAIL: Email prefix to use for test accounts
 * - TEST_PASSWORD: Password to use for test accounts
 * - TEST_ENVIRONMENT: Environment to test against (local, dev, staging)
 */
```

4. **Check for environment variables at startup**:
```java
public void validateEnvironmentSetup() {
    List<String> requiredVars = Arrays.asList("TEST_EMAIL", "TEST_PASSWORD", "TEST_ENVIRONMENT");
    List<String> missing = new ArrayList<>();
    
    for (String var : requiredVars) {
        if (System.getenv(var) == null) {
            missing.add(var);
        }
    }
    
    if (!missing.isEmpty()) {
        logger.error("Missing required environment variables: {}", missing);
        throw new ConfigurationException("Missing required environment variables: " + missing);
    }
}
```

### Cross-Platform Issues

**Problem**: Tests behave differently on different operating systems.

**Symptoms**:
- Path-related issues on different OSes
- Line ending differences causing file parsing problems
- Font rendering and element positioning differences

**Causes**:
- OS-specific browser behavior
- Path separator differences (/ vs \)
- Different browser defaults

**Solutions**:

1. **Use platform-independent file paths**:
```java
public String getScreenshotsDirectory() {
    return "test-output" + File.separator + "screenshots";
}
```

2. **Handle OS-specific browser options**:
```java
public ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win")) {
        // Windows-specific options
        options.addArguments("--disable-gpu");
    } else if (os.contains("mac")) {
        // Mac-specific options
        options.addArguments("--kiosk");
    } else if (os.contains("linux")) {
        // Linux-specific options
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
    }
    
    return options;
}
```

3. **Normalize line endings when reading files**:
```java
public List<String> readLines(String filePath) throws IOException {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
    }
    return lines;
}
```

4. **Log platform information for debugging**:
```java
public void logPlatformInfo() {
    logger.info("OS: {}", System.getProperty("os.name"));
    logger.info("OS Version: {}", System.getProperty("os.version"));
    logger.info("OS Architecture: {}", System.getProperty("os.arch"));
    logger.info("Java Version: {}", System.getProperty("java.version"));
}
```

### Browser Profile Issues

**Problem**: Browser profiles or settings affecting test behavior.

**Symptoms**:
- Unexpected popups or notifications
- Extensions interfering with tests
- Caching affecting test repeatability
- Cookie persistence between tests

**Causes**:
- Browser profiles being reused
- Notifications and permission prompts
- Extensions loaded automatically
- Browser defaults vary by environment

**Solutions**:

1. **Use clean browser profiles for tests**:
```java
public ChromeOptions getCleanChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    
    // Use a clean, dedicated user data directory
    options.addArguments("--user-data-dir=/tmp/chrome-data-" + System.currentTimeMillis());
    
    // Disable extensions, notifications, etc.
    options.addArguments("--disable-extensions");
    options.addArguments("--disable-notifications");
    options.addArguments("--disable-popup-blocking");
    
    return options;
}
```

2. **Clear cookies and storage between tests**:
```java
@AfterMethod
public void cleanBrowserState() {
    // Clear cookies
    driver.manage().deleteAllCookies();
    
    // Clear local storage
    ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
    
    // Clear session storage
    ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
}
```

3. **Block unwanted browser requests**:
```java
public void blockUnwantedRequests() {
    // Using Chrome DevTools Protocol with Selenium 4
    DevTools devTools = ((ChromeDriver) driver).getDevTools();
    devTools.createSession();
    
    // Block analytics, tracking, etc.
    devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    devTools.send(Network.setBlockedURLs(List.of(
        "*/analytics.js", "*/gtm.js", "*/tracking.js"
    )));
}
```

4. **Set default browser preferences**:
```java
public ChromeOptions getChromePrefOptions() {
    ChromeOptions options = new ChromeOptions();
    
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("profile.default_content_setting_values.notifications", 2); // Block notifications
    prefs.put("credentials_enable_service", false); // Disable password manager
    prefs.put("profile.password_manager_enabled", false); // Disable password manager
    prefs.put("autofill.profile_enabled", false); // Disable autofill
    
    options.setExperimentalOption("prefs", prefs);
    return options;
}
```

## Reporting Issues

### Missing Screenshots

**Problem**: Screenshots not captured or not attached to reports on test failure.

**Symptoms**:
- No screenshots in test reports
- Broken screenshot links
- Screenshot files missing

**Causes**:
- Screenshot directory not created
- Permission issues writing files
- Incorrect file paths
- Screenshots not properly attached to reports

**Solutions**:

1. **Ensure screenshot directory exists**:
```java
public String captureScreenshot(String testName) {
    String directory = "test-output/screenshots";
    File screenshotDir = new File(directory);
    if (!screenshotDir.exists()) {
        screenshotDir.mkdirs();
    }
    
    String filename = testName + "_" + System.currentTimeMillis() + ".png";
    String filePath = directory + File.separator + filename;
    
    try {
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Files.copy(screenshotFile.toPath(), Paths.get(filePath));
        return filePath;
    } catch (IOException e) {
        logger.error("Failed to capture screenshot", e);
        return null;
    }
}
```

2. **Properly attach screenshots to ExtentReports**:
```java
public class TestListener implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {
        TestBase testInstance = (TestBase) result.getInstance();
        WebDriver driver = testInstance.getDriver();
        ExtentTest test = testInstance.getTest();
        
        if (driver != null) {
            String screenshotPath = captureScreenshot(driver, result.getName());
            try {
                // Use absolute path for proper attachment
                test.fail("Test failed, see screenshot below:",
                    MediaEntityBuilder.createScreenCaptureFromPath(
                        new File(screenshotPath).getAbsolutePath()).build());
            } catch (IOException e) {
                test.fail("Test failed but screenshot could not be attached: " + e.getMessage());
            }
        }
    }
    
    private String captureScreenshot(WebDriver driver, String testName) {
        // Screenshot capture implementation
    }
}
```

3. **Handle headless browser screenshots**:
```java
public String captureHeadlessScreenshot(String testName) {
    String directory = "test-output/screenshots";
    File screenshotDir = new File(directory);
    if (!screenshotDir.exists()) {
        screenshotDir.mkdirs();
    }
    
    String filename = testName + "_" + System.currentTimeMillis() + ".png";
    String filePath = directory + File.separator + filename;
    
    // For headless Chrome, set window size before capturing
    Dimension originalSize = driver.manage().window().getSize();
    driver.manage().window().setSize(new Dimension(1920, 1080));
    
    try {
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        Files.copy(screenshotFile.toPath(), Paths.get(filePath));
        // Restore original size
        driver.manage().window().setSize(originalSize);
        return filePath;
    } catch (IOException e) {
        logger.error("Failed to capture screenshot", e);
        return null;
    }
}
```

4. **Add detailed logging for screenshot operations**:
```java
public String captureScreenshotWithLogging(String testName) {
    logger.info("Capturing screenshot for test: {}", testName);
    String directory = "test-output/screenshots";
    logger.debug("Screenshot directory: {}", directory);
    
    // Create directory
    File screenshotDir = new File(directory);
    if (!screenshotDir.exists()) {
        boolean created = screenshotDir.mkdirs();
        logger.debug("Created screenshot directory: {}", created);
    }
    
    // Rest of screenshot capture logic
    // ...
    
    logger.info("Screenshot captured: {}", filePath);
    return filePath;
}
```

### Report Generation Failures

**Problem**: Test reports not generated or incomplete.

**Symptoms**:
- Missing or empty report files
- Incomplete test information in reports
- Report generation errors

**Causes**:
- ExtentReports not properly configured
- Report not flushed at end of execution
- Report directory permission issues
- Concurrent test execution conflicts

**Solutions**:

1. **Properly set up ExtentReports**:
```java
public class ExtentManager {
    private static ExtentReports extent;
    
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            createInstance();
        }
        return extent;
    }
    
    private static void createInstance() {
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("test-output/extent-report.html");
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setDocumentTitle("Storydoc Signup Test Report");
        htmlReporter.config().setReportName("Selenium Test Automation Results");
        
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Browser", ConfigurationManager.getInstance().getBrowser());
        extent.setSystemInfo("Environment", ConfigurationManager.getInstance().getEnvironment());
    }
    
    public static synchronized ExtentTest createTest(String testName) {
        return getInstance().createTest(testName);
    }
}
```

2. **Ensure report is flushed at the end of test execution**:
```java
public class TestListener implements ITestListener {
    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getInstance().flush();
    }
}
```

3. **Handle report directory creation**:
```java
public static void setupReportDirectory() {
    File reportDir = new File("test-output");
    if (!reportDir.exists()) {
        boolean created = reportDir.mkdirs();
        if (!created) {
            throw new RuntimeException("Failed to create report directory: test-output");
        }
    }
}
```

4. **Implement Thread-safe reporting for parallel execution**:
```java
public class ExtentTestManager {
    private static Map<Integer, ExtentTest> extentTestMap = new ConcurrentHashMap<>();
    
    public static synchronized ExtentTest getTest() {
        return extentTestMap.get((int) Thread.currentThread().getId());
    }
    
    public static synchronized ExtentTest startTest(String testName) {
        ExtentTest test = ExtentManager.getInstance().createTest(testName);
        extentTestMap.put((int) Thread.currentThread().getId(), test);
        return test;
    }
}
```

### Logging Configuration

**Problem**: Inadequate or excessive logging affecting troubleshooting.

**Symptoms**:
- Missing important log information
- Log files too large with unnecessary details
- Log files not created or inaccessible

**Causes**:
- Incorrect Log4j configuration
- Log level set inappropriately
- Log file path issues
- Missing appenders

**Solutions**:

1. **Configure proper Log4j2 XML configuration**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        <RollingFile name="File" fileName="logs/test.log" filePattern="logs/test-%d{yyyy-MM-dd}-%i.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="10 MB"/>
                <TimeBasedTriggeringPolicy interval="1" modulate="true"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
        <Logger name="com.storydoc" level="debug" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Logger>
    </Loggers>
</Configuration>
```

2. **Create a log helper class**:
```java
public class LogHelper {
    private static final Logger logger = LogManager.getLogger(LogHelper.class);
    
    public static void logTestStart(String testName) {
        logger.info("===============================================");
        logger.info("Starting test: {}", testName);
        logger.info("===============================================");
    }
    
    public static void logTestEnd(String testName, String status) {
        logger.info("===============================================");
        logger.info("Test completed: {} - Status: {}", testName, status);
        logger.info("===============================================");
    }
    
    public static void logStep(String stepDescription) {
        logger.info("STEP: {}", stepDescription);
    }
    
    public static void logError(String message, Throwable error) {
        logger.error("ERROR: {}", message, error);
    }
}
```

3. **Log browser console messages**:
```java
public void logBrowserConsole() {
    LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
    for (LogEntry entry : logs) {
        logger.info("Browser console [{}]: {}", 
            new Date(entry.getTimestamp()), entry.getMessage());
    }
}
```

4. **Create a directory for logs**:
```java
public void setupLogDirectory() {
    File logDir = new File("logs");
    if (!logDir.exists()) {
        boolean created = logDir.mkdirs();
        if (!created) {
            System.err.println("Failed to create log directory: logs");
        }
    }
}
```

### Report Customization

**Problem**: Default reports don't provide enough information or aren't formatted properly.

**Symptoms**:
- Reports lack important test details
- Reports not visually organized for readability
- Missing test environment information

**Causes**:
- Default report settings not customized
- Report template issues
- Missing test metadata

**Solutions**:

1. **Customize ExtentReports configuration**:
```java
public void configureExtentReports() {
    ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/custom-report.html");
    
    // Apply custom styling
    sparkReporter.config().setTheme(Theme.DARK);
    sparkReporter.config().setCss(".test-name { font-weight: bold; }");
    sparkReporter.config().setJs("document.getElementsByClassName('logo')[0].style.display='none';");
    
    // Set document information
    sparkReporter.config().setDocumentTitle("Storydoc Signup Test Report");
    sparkReporter.config().setReportName("Automated Test Results");
    sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
    
    ExtentReports extent = new ExtentReports();
    extent.attachReporter(sparkReporter);
    
    // Add system information
    extent.setSystemInfo("Application", "Storydoc Signup");
    extent.setSystemInfo("Environment", ConfigurationManager.getInstance().getEnvironment());
    extent.setSystemInfo("Browser", ConfigurationManager.getInstance().getBrowser());
    extent.setSystemInfo("OS", System.getProperty("os.name"));
    extent.setSystemInfo("Java Version", System.getProperty("java.version"));
}
```

2. **Add detailed test information to reports**:
```java
public void logTestDetails(ExtentTest test, Method method) {
    // Add annotations as test metadata
    if (method.isAnnotationPresent(Test.class)) {
        Test testAnnotation = method.getAnnotation(Test.class);
        test.info("Test Description: " + 
            (testAnnotation.description().isEmpty() ? "N/A" : testAnnotation.description()));
        
        if (testAnnotation.groups().length > 0) {
            test.assignCategory(testAnnotation.groups());
        }
    }
    
    // Log test data
    test.info("Test Data: Email - " + testData.getEmail());
    
    // Add author information
    if (method.isAnnotationPresent(Author.class)) {
        Author authorAnnotation = method.getAnnotation(Author.class);
        test.assignAuthor(authorAnnotation.name());
    } else {
        test.assignAuthor("QA Team");
    }
}
```

3. **Create a custom TestNG HTML reporter**:
```java
public class CustomHTMLReporter extends HTMLReporter {
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        super.generateReport(xmlSuites, suites, outputDirectory);
        
        // Add custom post-processing for TestNG HTML reports
        try {
            File htmlReport = new File(outputDirectory + File.separator + "index.html");
            if (htmlReport.exists()) {
                String content = Files.readString(htmlReport.toPath());
                
                // Add custom styling
                content = content.replace("</head>",
                    "<style>.test-name{font-weight:bold;color:#2196F3;}</style></head>");
                
                // Add custom header
                content = content.replace("<body>",
                    "<body><div style='background:#263238;color:white;padding:10px;'>" +
                    "<h2>Storydoc Signup Test Results</h2>" +
                    "<p>Environment: " + ConfigurationManager.getInstance().getEnvironment() + "</p>" +
                    "</div>");
                
                Files.writeString(htmlReport.toPath(), content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

4. **Add visualization of test execution trend** (if you're storing historical data):
```java
public void addTestTrendChart() {
    // Using ExtentReports Spark reporter
    ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/custom-report.html");
    
    // Enable dashboard with charts
    sparkReporter.config().setReportName("Storydoc Test Results");
    sparkReporter.viewConfigurer().viewOrder()
        .as(new ViewName[] {
            ViewName.DASHBOARD,
            ViewName.TEST,
            ViewName.CATEGORY,
            ViewName.DEVICE,
            ViewName.AUTHOR
        }).apply();
    
    ExtentReports extent = new ExtentReports();
    extent.attachReporter(sparkReporter);
    
    // The rest of your ExtentReports configuration
}
```

## Performance Optimization

### Slow Test Execution

**Problem**: Tests run too slowly, increasing total execution time.

**Symptoms**:
- Long test execution times
- CI/CD pipeline timeouts
- Tests taking longer than manual execution

**Causes**:
- Excessive waits or timeouts
- Inefficient element location strategies
- Browser performance issues
- Test code inefficiencies

**Solutions**:

1. **Optimize wait strategies**:
```java
public void optimizeWaitStrategy() {
    // Reduce default timeout for most operations
    int defaultTimeout = ConfigurationManager.getInstance().getTimeout();
    
    // Use shorter timeouts for frequent operations
    int shortTimeout = Math.max(2, defaultTimeout / 3);
    
    // Only use longer timeouts for known slow operations
    int longTimeout = defaultTimeout * 2;
    
    // Create different wait objects for different scenarios
    WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(shortTimeout));
    WebDriverWait defaultWait = new WebDriverWait(driver, Duration.ofSeconds(defaultTimeout));
    WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(longTimeout));
    
    // Use appropriate wait for the operation
    shortWait.until(ExpectedConditions.elementToBeClickable(locator)); // For quick UI responses
    defaultWait.until(ExpectedConditions.visibilityOfElementLocated(locator)); // Standard operations
    longWait.until(ExpectedConditions.urlContains("confirmation")); // Known slow page transitions
}
```

2. **Use optimized locator strategies**:
```java
public void useOptimizedLocators() {
    // Optimize locator strategies for performance
    
    // GOOD: Specific, direct locators
    By fastLocator = By.id("email-input"); // Fastest
    By goodLocator = By.cssSelector("input[data-testid='email-input']"); // Good
    
    // BAD: Avoid these for performance-critical elements
    By slowLocator = By.xpath("//div[@class='form']//input[@type='email']"); // Slower
    By verySlowLocator = By.xpath("//*[contains(@class, 'input') and contains(@placeholder, 'Email')]"); // Very slow
}
```

3. **Implement browser performance optimizations**:
```java
public ChromeOptions getPerformanceOptimizedOptions() {
    ChromeOptions options = new ChromeOptions();
    
    // Disable unnecessary features
    options.addArguments("--disable-extensions");
    options.addArguments("--disable-infobars");
    options.addArguments("--disable-notifications");
    options.addArguments("--disable-popup-blocking");
    
    // Performance optimizations
    options.addArguments("--disable-gpu"); // Reduces GPU usage
    options.addArguments("--disable-dev-shm-usage"); // Uses /tmp instead of /dev/shm
    
    // Reduce memory usage
    options.addArguments("--js-flags=--expose-gc"); // Enable garbage collection control
    options.addArguments("--aggressive-cache-discard"); // Discard cache more aggressively
    
    return options;
}
```

4. **Reduce unnecessary actions and validations**:
```java
public void optimizeTestFlow() {
    // Only perform verification on critical elements
    // Instead of checking every element on a page:
    
    // INEFFICIENT:
    Assert.assertTrue(isElementPresent(locator1));
    Assert.assertTrue(isElementPresent(locator2));
    Assert.assertTrue(isElementPresent(locator3));
    // ... many more checks
    
    // EFFICIENT:
    // Check only key elements that confirm the page state
    Assert.assertTrue(isElementPresent(keyElementLocator));
    
    // Use this test method annotation to set timeouts
    @Test(timeOut = 30000) // Fail the test if it runs longer than 30 seconds
    public void performanceEfficientTest() {
        // Test implementation
    }
}
```

### Memory Management

**Problem**: Tests consume excessive memory, causing performance issues or crashes.

**Symptoms**:
- OutOfMemoryError exceptions
- Browser crashes during test execution
- Slow test execution as test suite progresses
- System becoming unresponsive during test execution

**Causes**:
- Memory leaks in test code
- Browser instances not properly closed
- Excessive screenshot or logging
- Too many browser instances in parallel

**Solutions**:

1. **Properly close browser resources**:
```java
public void closeAllResources() {
    try {
        if (driver != null) {
            driver.quit(); // Better than just driver.close()
            driver = null;
        }
    } catch (Exception e) {
        logger.error("Error closing WebDriver", e);
    }
}
```

2. **Implement proper teardown in TestNG**:
```java
@AfterMethod(alwaysRun = true) // Make sure this always runs, even after failures
public void tearDown() {
    closeAllResources();
    
    // Suggest garbage collection
    System.gc();
}

@AfterClass(alwaysRun = true)
public void classTearDown() {
    // Clean up any class-level resources
    // ...
    
    // Suggest garbage collection
    System.gc();
}
```

3. **Limit parallel execution based on system resources**:
```xml
<!-- In testng.xml -->
<suite name="Memory-Optimized Suite" parallel="classes" thread-count="3">
    <!-- Limit thread count based on available memory -->
    <test name="Signup Tests">
        <classes>
            <class name="com.storydoc.tests.SignupTest"/>
        </classes>
    </test>
</suite>
```

4. **Monitor memory usage during test execution**:
```java
public void logMemoryUsage(String checkpoint) {
    Runtime runtime = Runtime.getRuntime();
    
    // Calculate used memory
    long usedMemory = runtime.totalMemory() - runtime.freeMemory();
    long usedMemoryMB = usedMemory / (1024 * 1024);
    
    logger.info("Memory usage at {}: {} MB", checkpoint, usedMemoryMB);
    
    // Trigger garbage collection if memory usage is high
    if (usedMemoryMB > 500) { // Threshold of 500MB
        logger.warn("Memory usage high, suggesting garbage collection");
        System.gc();
    }
}
```

### Resource Cleanup

**Problem**: Resources not properly released, causing resource leaks.

**Symptoms**:
- Browser processes remaining after tests
- File handles not released
- Driver sessions not properly closed
- System slowing down over time during testing

**Causes**:
- Missing or incomplete teardown methods
- Exception during cleanup
- Inherited resources not being released

**Solutions**:

1. **Implement robust WebDriver cleanup**:
```java
public static void quitDriver() {
    WebDriver driver = driverThreadLocal.get();
    if (driver != null) {
        try {
            // Close all windows
            Set<String> windowHandles = driver.getWindowHandles();
            for (String handle : windowHandles) {
                driver.switchTo().window(handle);
                driver.close();
            }
        } catch (Exception e) {
            logger.warn("Error closing browser windows", e);
        } finally {
            try {
                driver.quit();
            } catch (Exception e) {
                logger.error("Error quitting WebDriver", e);
            }
            driverThreadLocal.remove();
        }
    }
}
```

2. **Clean up screenshot and log files**:
```java
public void cleanupOldFiles() {
    // Clean up old screenshots (older than 7 days)
    try {
        Path screenshotDir = Paths.get("test-output/screenshots");
        if (Files.exists(screenshotDir)) {
            Files.list(screenshotDir)
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toInstant()
                            .isBefore(Instant.now().minus(7, ChronoUnit.DAYS));
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        logger.warn("Could not delete old file: {}", path, e);
                    }
                });
        }
    } catch (IOException e) {
        logger.error("Error cleaning up old files", e);
    }
}
```

3. **Use try-with-resources for file operations**:
```java
public void saveTestData(String testName, String data) {
    // Ensure resources are closed automatically
    try (FileWriter writer = new FileWriter("test-output/data/" + testName + ".txt")) {
        writer.write(data);
    } catch (IOException e) {
        logger.error("Error saving test data", e);
    }
}
```

4. **Implement a shutdown hook for emergency cleanup**:
```java
public void registerCleanupHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        logger.info("Shutdown hook executing, cleaning up resources");
        try {
            // Force cleanup of all WebDriver instances
            cleanupAllWebDrivers();
            // Other cleanup tasks
        } catch (Exception e) {
            logger.error("Error during emergency cleanup", e);
        }
    }));
}

private void cleanupAllWebDrivers() {
    // Implementation to force-quit any remaining WebDriver instances
}
```

### Parallel Execution Optimization

**Problem**: Parallel test execution is inefficient or causing failures.

**Symptoms**:
- Tests failing only during parallel execution
- Resource contention
- Tests running slower in parallel than expected
- Unpredictable test results

**Causes**:
- Resource sharing issues
- Thread safety problems
- Test dependencies not properly configured
- System resource limitations

**Solutions**:

1. **Configure TestNG for optimal parallel execution**:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Optimized Parallel Suite" parallel="classes" thread-count="4" data-provider-thread-count="2">
    <!-- Separate tests by groups to avoid contention -->
    <test name="Signup Tests" parallel="methods" thread-count="2">
        <groups>
            <run>
                <include name="signup"/>
            </run>
        </groups>
        <classes>
            <class name="com.storydoc.tests.SignupTest"/>
        </classes>
    </test>
    
    <!-- Other tests in separate test tag -->
    <test name="Other Tests" parallel="methods" thread-count="2">
        <groups>
            <run>
                <include name="other"/>
            </run>
        </groups>
        <classes>
            <class name="com.storydoc.tests.OtherTest"/>
        </classes>
    </test>
</suite>
```

2. **Make tests data independent**:
```java
@Test(groups = {"signup"})
public void testSignupWithUniqueData() {
    // Generate unique test data for each test run
    String uniqueEmail = "test" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    String uniquePassword = "Test@" + System.currentTimeMillis();
    
    // Use unique data in the test
    signupPage.enterEmail(uniqueEmail)
              .enterPassword(uniquePassword)
              .acceptTerms()
              .clickSignUp();
              
    // Test assertions
}
```

3. **Use ThreadLocal for all shared resources**:
```java
public class TestContext {
    private static ThreadLocal<Map<String, Object>> testContexts = new ThreadLocal<>();
    
    public static void set(String key, Object value) {
        getContext().put(key, value);
    }
    
    public static Object get(String key) {
        return getContext().get(key);
    }
    
    private static Map<String, Object> getContext() {
        Map<String, Object> context = testContexts.get();
        if (context == null) {
            context = new HashMap<>();
            testContexts.set(context);
        }
        return context;
    }
    
    public static void reset() {
        testContexts.remove();
    }
}
```

4. **Implement adaptive thread count based on system resources**:
```java
public int calculateOptimalThreadCount() {
    int availableProcessors = Runtime.getRuntime().availableProcessors();
    
    // Calculate available memory per thread (in MB)
    long maxMemory = Runtime.getRuntime().maxMemory() / (1024 * 1024);
    
    // Assume each browser instance needs ~400MB memory
    int memoryBasedThreads = (int) (maxMemory / 400);
    
    // Use the lower of CPU-based or memory-based thread count
    int optimalThreads = Math.min(availableProcessors, memoryBasedThreads);
    
    // Cap to a reasonable maximum
    return Math.min(optimalThreads, 8);
}
```

## Debugging Techniques

### Effective Logging

**Problem**: Insufficient information for debugging test failures.

**Symptoms**:
- Test failures with unclear causes
- Difficulty reproducing issues
- Missing context for failures

**Causes**:
- Inadequate logging
- Generic error messages
- Too high log level

**Solutions**:

1. **Implement hierarchical logging**:
```java
public class TestLogger {
    private final Logger logger;
    
    public TestLogger(Class<?> clazz) {
        this.logger = LogManager.getLogger(clazz);
    }
    
    public void startTest(String testName) {
        logger.info("========== STARTING TEST: {} ==========", testName);
    }
    
    public void endTest(String testName, String result) {
        logger.info("========== TEST COMPLETED: {} - RESULT: {} ==========", testName, result);
    }
    
    public void startStep(String stepDescription) {
        logger.info("STEP: {}", stepDescription);
    }
    
    public void debug(String message) {
        logger.debug(message);
    }
    
    public void info(String message) {
        logger.info(message);
    }
    
    public void warn(String message) {
        logger.warn(message);
    }
    
    public void error(String message, Throwable e) {
        logger.error(message, e);
    }
}
```

2. **Log detailed element interaction information**:
```java
public WebElement click(By locator) {
    logger.debug("Attempting to click element: {}", locator);
    try {
        WebElement element = waitForElementClickable(locator);
        logger.debug("Element found and clickable: {}", getElementDetails(element));
        element.click();
        logger.debug("Successfully clicked element");
        return element;
    } catch (Exception e) {
        logger.error("Failed to click element: {}", locator, e);
        throw e;
    }
}

private String getElementDetails(WebElement element) {
    try {
        String tagName = element.getTagName();
        String id = element.getAttribute("id");
        String classes = element.getAttribute("class");
        String text = element.getText();
        
        return String.format("Tag: %s, ID: %s, Class: %s, Text: %s", 
                            tagName, id, classes, text);
    } catch (Exception e) {
        return "Could not get element details";
    }
}
```

3. **Add context to test execution**:
```java
public class TestContextLogger {
    private static ThreadLocal<Map<String, Object>> contextMap = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(TestContextLogger.class);
    
    public static void setContext(String key, Object value) {
        getContextMap().put(key, value);
    }
    
    public static Object getContext(String key) {
        return getContextMap().get(key);
    }
    
    public static void clearContext() {
        getContextMap().clear();
    }
    
    public static void logContext(String message) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(" - Context: {");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : getContextMap().entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        
        sb.append("}");
        logger.info(sb.toString());
    }
    
    private static Map<String, Object> getContextMap() {
        Map<String, Object> map = contextMap.get();
        if (map == null) {
            map = new HashMap<>();
            contextMap.set(map);
        }
        return map;
    }
}
```

4. **Create custom TestNG listeners for detailed test logging**:
```java
public class LoggingTestListener implements ITestListener {
    private static final Logger logger = LogManager.getLogger(LoggingTestListener.class);
    
    @Override
    public void onTestStart(ITestResult result) {
        logger.info("========== TEST STARTED: {} ==========", result.getName());
        logTestParameters(result);
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("TEST PASSED: {} - Duration: {}ms", 
            result.getName(), result.getEndMillis() - result.getStartMillis());
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("TEST FAILED: {} - Duration: {}ms", 
            result.getName(), result.getEndMillis() - result.getStartMillis());
        logger.error("Failure Cause: ", result.getThrowable());
    }
    
    private void logTestParameters(ITestResult result) {
        Object[] params = result.getParameters();
        if (params != null && params.length > 0) {
            StringBuilder sb = new StringBuilder("Test Parameters: ");
            for (int i = 0; i < params.length; i++) {
                sb.append("Param").append(i).append("=[").append(params[i]).append("] ");
            }
            logger.info(sb.toString());
        }
    }
}
```

### Browser Developer Tools Integration

**Problem**: Limited visibility into browser state during test execution.

**Symptoms**:
- Unable to debug JavaScript errors
- No insight into network requests
- DOM structure unclear during test

**Causes**:
- No integration with browser developer tools
- Missing browser logs

**Solutions**:

1. **Capture browser console logs**:
```java
public void logBrowserConsole() {
    LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
    for (LogEntry entry : logs) {
        String logLevel;
        switch (entry.getLevel().toString()) {
            case "SEVERE":
                logLevel = "ERROR";
                break;
            case "WARNING":
                logLevel = "WARN";
                break;
            default:
                logLevel = "INFO";
        }
        
        logger.info("Browser Console [{}] [{}]: {}", 
            logLevel, new Date(entry.getTimestamp()), entry.getMessage());
    }
}
```

2. **Use Chrome DevTools Protocol (Selenium 4)**:
```java
public void monitorNetworkTraffic() {
    DevTools devTools = ((ChromeDriver) driver).getDevTools();
    devTools.createSession();
    
    // Listen for network events
    devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
    
    // Log requests
    devTools.addListener(Network.requestWillBeSent(), request -> {
        logger.debug("Network Request: {} {}", 
            request.getRequest().getMethod(), 
            request.getRequest().getUrl());
    });
    
    // Log responses
    devTools.addListener(Network.responseReceived(), response -> {
        logger.debug("Network Response: {} {} - Status: {}", 
            response.getResponse().getUrl(),
            response.getResponse().getStatus(),
            response.getResponse().getStatusText());
        
        if (response.getResponse().getStatus() >= 400) {
            logger.warn("HTTP Error: {} {}", 
                response.getResponse().getStatus(),
                response.getResponse().getUrl());
        }
    });
}
```

3. **Capture DOM snapshots**:
```java
public String captureDOMSnapshot() {
    try {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript("return document.documentElement.outerHTML");
    } catch (Exception e) {
        logger.error("Failed to capture DOM snapshot", e);
        return null;
    }
}

public void saveDOMSnapshot(String testName) {
    String dom = captureDOMSnapshot();
    if (dom != null) {
        try {
            Files.writeString(Paths.get("test-output/dom-snapshots/" + 
                testName + "_" + System.currentTimeMillis() + ".html"), dom);
        } catch (IOException e) {
            logger.error("Failed to save DOM snapshot", e);
        }
    }
}
```

4. **Execute JavaScript for debugging**:
```java
public void debugWithJavaScript() {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    
    // Check for JavaScript errors
    List<Object> jsErrors = (List<Object>) js.executeScript(
        "return window.jsErrors || []");
    
    for (Object error : jsErrors) {
        logger.warn("JavaScript Error: {}", error);
    }
    
    // Log page readiness
    boolean isReady = (Boolean) js.executeScript(
        "return document.readyState === 'complete'");
    
    logger.info("Page ready state: {}", isReady ? "Complete" : "Loading");
    
    // Check specific element state
    WebElement element = driver.findElement(By.id("email"));
    boolean isVisible = (Boolean) js.executeScript(
        "var elem = arguments[0]; " +
        "return !!(elem.offsetWidth || elem.offsetHeight || " +
        "elem.getClientRects().length);", 
        element);
    
    logger.info("Element visibility: {}", isVisible);
}
```

### Remote Debugging

**Problem**: Difficult to debug tests running in CI/CD environments.

**Symptoms**:
- Tests fail in CI but work locally
- Limited visibility into remote test execution
- Difficulty reproducing environment-specific issues

**Causes**:
- Execution environment differences
- Limited access to CI/CD server logs
- Containerized execution

**Solutions**:

1. **Enable VNC for visual debugging in containers**:
```yaml
# In docker-compose.yml
services:
  selenium-chrome:
    image: selenium/standalone-chrome-debug:latest  # -debug image includes VNC
    ports:
      - "4444:4444"  # WebDriver port
      - "5900:5900"  # VNC port
    volumes:
      - /dev/shm:/dev/shm  # Shared memory for better performance
```

2. **Generate detailed execution reports for remote debugging**:
```java
public void enhanceRemoteDebugging(ExtentTest test) {
    // Add system information
    test.info(MarkupHelper.createCodeBlock(
        "OS: " + System.getProperty("os.name") + "\n" +
        "Java: " + System.getProperty("java.version") + "\n" +
        "Browser: " + ((RemoteWebDriver) driver).getCapabilities().getBrowserName() + " " +
        ((RemoteWebDriver) driver).getCapabilities().getBrowserVersion() + "\n" +
        "TimeZone: " + TimeZone.getDefault().getID()
    ));
    
    // Add screenshots at key points
    test.info("Page state", MediaEntityBuilder.createScreenCaptureFromBase64String(
        getBase64Screenshot()).build());
    
    // Add page source
    test.info("Page Source", MarkupHelper.createCodeBlock(driver.getPageSource(), "html"));
}

private String getBase64Screenshot() {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
}
```

3. **Implement detailed environment reporting**:
```java
public class EnvironmentLogger {
    private static final Logger logger = LogManager.getLogger(EnvironmentLogger.class);
    
    public static void logEnvironmentDetails() {
        logger.info("=== Environment Details ===");
        
        // System properties
        logger.info("OS: {} v{} ({})", 
            System.getProperty("os.name"),
            System.getProperty("os.version"),
            System.getProperty("os.arch"));
        
        logger.info("Java: {} ({})", 
            System.getProperty("java.version"),
            System.getProperty("java.vendor"));
        
        // Memory
        Runtime rt = Runtime.getRuntime();
        logger.info("Memory: Total={} MB, Free={} MB, Max={} MB",
            rt.totalMemory() / (1024 * 1024),
            rt.freeMemory() / (1024 * 1024),
            rt.maxMemory() / (1024 * 1024));
        
        // Environment variables (only log non-sensitive ones)
        logger.info("Environment Variables:");
        System.getenv().forEach((key, value) -> {
            if (!key.toLowerCase().contains("key") && 
                !key.toLowerCase().contains("token") && 
                !key.toLowerCase().contains("password") && 
                !key.toLowerCase().contains("secret")) {
                logger.info("  {}={}", key, value);
            }
        });
        
        // Log test configuration
        logger.info("Test Configuration:");
        logger.info("  Browser: {}", ConfigurationManager.getInstance().getBrowser());
        logger.info("  Environment: {}", ConfigurationManager.getInstance().getEnvironment());
        logger.info("  Timeout: {}s", ConfigurationManager.getInstance().getTimeout());
    }
}
```

4. **Create a remote execution debug helper**:
```java
public class RemoteDebugHelper {
    private final WebDriver driver;
    private final Logger logger = LogManager.getLogger(RemoteDebugHelper.class);
    
    public RemoteDebugHelper(WebDriver driver) {
        this.driver = driver;
    }
    
    public void collectDebugBundle(String testName, Throwable error) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String debugDir = "test-output/debug/" + testName + "_" + timestamp;
        
        try {
            Files.createDirectories(Paths.get(debugDir));
            
            // Save screenshot
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), Paths.get(debugDir + "/screenshot.png"));
            
            // Save page source
            Files.writeString(Paths.get(debugDir + "/page_source.html"), driver.getPageSource());
            
            // Save browser logs
            StringBuilder logs = new StringBuilder();
            for (LogEntry entry : driver.manage().logs().get(LogType.BROWSER)) {
                logs.append(entry.getLevel()).append(" - ")
                    .append(new Date(entry.getTimestamp())).append(": ")
                    .append(entry.getMessage()).append("\n");
            }
            Files.writeString(Paths.get(debugDir + "/browser_logs.txt"), logs.toString());
            
            // Save error details
            if (error != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                error.printStackTrace(pw);
                Files.writeString(Paths.get(debugDir + "/error.txt"), sw.toString());
            }
            
            // Save capabilities and environment info
            Capabilities caps = ((RemoteWebDriver) driver).getCapabilities();
            Files.writeString(Paths.get(debugDir + "/capabilities.json"), 
                new JSONObject(caps.asMap()).toString(2));
            
            logger.info("Debug bundle created at: {}", debugDir);
        } catch (IOException e) {
            logger.error("Failed to create debug bundle", e);
        }
    }
}
```

### Debugging WebDriver Issues

**Problem**: Specific WebDriver problems that are difficult to diagnose.

**Symptoms**:
- StaleElementReferenceException
- NoSuchElementException
- ElementNotInteractableException
- UnexpectedAlertPresentException

**Causes**:
- Element state changes
- DOM structure changes
- Element visibility/interactability issues
- Unexpected dialogs

**Solutions**:

1. **Implement driver health check**:
```java
public boolean isDriverHealthy() {
    try {
        // Try a simple command to check driver responsiveness
        driver.getCurrentUrl();
        return true;
    } catch (WebDriverException e) {
        logger.warn("WebDriver health check failed: {}", e.getMessage());
        return false;
    }
}

public WebDriver getHealthyDriver() {
    if (!isDriverHealthy()) {
        logger.info("Reinitializing unhealthy WebDriver");
        quitDriver();
        initDriver();
    }
    return driver;
}
```

2. **Create debug helper methods for element issues**:
```java
public void debugElement(WebElement element, String elementName) {
    try {
        logger.debug("Element '{}' properties:", elementName);
        logger.debug("  Tag: {}", element.getTagName());
        logger.debug("  Text: {}", element.getText());
        logger.debug("  Displayed: {}", element.isDisplayed());
        logger.debug("  Enabled: {}", element.isEnabled());
        logger.debug("  Selected: {}", element.isSelected());
        logger.debug("  Location: {}", element.getLocation());
        logger.debug("  Size: {}", element.getSize());
        
        // Get computed CSS properties
        logger.debug("  CSS properties:");
        logCssProperty(element, "visibility");
        logCssProperty(element, "display");
        logCssProperty(element, "opacity");
        logCssProperty(element, "position");
        logCssProperty(element, "z-index");
    } catch (StaleElementReferenceException e) {
        logger.debug("Element '{}' is stale", elementName);
    } catch (Exception e) {
        logger.debug("Error debugging element '{}': {}", elementName, e.getMessage());
    }
}

private void logCssProperty(WebElement element, String propertyName) {
    try {
        String value = element.getCssValue(propertyName);
        logger.debug("    {}: {}", propertyName, value);
    } catch (Exception e) {
        logger.debug("    {} (error): {}", propertyName, e.getMessage());
    }
}
```

3. **Implement alert handling utilities**:
```java
public boolean isAlertPresent() {
    try {
        driver.switchTo().alert();
        return true;
    } catch (NoAlertPresentException e) {
        return false;
    }
}

public void handleAlert(boolean accept) {
    try {
        Alert alert = driver.switchTo().alert();
        logger.info("Alert detected: {}", alert.getText());
        
        if (accept) {
            alert.accept();
            logger.info("Alert accepted");
        } else {
            alert.dismiss();
            logger.info("Alert dismissed");
        }
    } catch (NoAlertPresentException e) {
        logger.debug("No alert present to handle");
    }
}

public void safeAction(Runnable action) {
    try {
        action.run();
    } catch (UnexpectedAlertPresentException e) {
        logger.warn("Unexpected alert encountered: {}", e.getMessage());
        handleAlert(true); // Accept the alert
        // Retry the action
        action.run();
    }
}
```

4. **Create a robust element finder for debugging**:
```java
public WebElement findElementSafely(By locator, int timeoutInSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    
    try {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    } catch (TimeoutException e) {
        // Debug why element wasn't found
        logger.error("Element not found: {}", locator);
        
        // Try alternative approaches
        try {
            // Try with JavaScript
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement element = null;
            
            if (locator instanceof By.ByCssSelector) {
                String css = locator.toString().replace("By.cssSelector: ", "");
                element = (WebElement) js.executeScript(
                    "return document.querySelector(arguments[0]);", css);
            } else if (locator instanceof By.ById) {
                String id = locator.toString().replace("By.id: ", "");
                element = (WebElement) js.executeScript(
                    "return document.getElementById(arguments[0]);", id);
            }
            
            if (element != null) {
                logger.info("Found element using JavaScript: {}", locator);
                return element;
            }
        } catch (Exception jsEx) {
            logger.debug("JavaScript element search failed: {}", jsEx.getMessage());
        }
        
        // Log DOM snapshot for debugging
        logger.debug("DOM snapshot: {}", driver.getPageSource().substring(0, 
            Math.min(10000, driver.getPageSource().length())));
        
        throw new NoSuchElementException("Element not found: " + locator);
    }
}
```

## Frequently Asked Questions

### General Framework Questions

#### Q: How do I add a new browser to the framework?

**A**: To add a new browser (e.g., Edge):

1. Add the browser option in the configuration properties file:
```properties
# config.properties
browser=edge
```

2. Update the WebDriverManager class:
```java
public static void initDriver() {
    String browser = ConfigurationManager.getInstance().getBrowser().toLowerCase();
    
    switch (browser) {
        case "chrome":
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(getChromeOptions());
            break;
        case "firefox":
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver(getFirefoxOptions());
            break;
        case "edge":
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver(getEdgeOptions());
            break;
        default:
            throw new IllegalArgumentException("Browser " + browser + " not supported");
    }
    
    driver.manage().window().maximize();
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
}

private static EdgeOptions getEdgeOptions() {
    EdgeOptions options = new EdgeOptions();
    // Configure Edge-specific options
    return options;
}
```

3. Add any browser-specific logic to your test code

#### Q: How can I run tests in headless mode?

**A**: Configure headless mode in the browser options:

```java
public static ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    
    if (ConfigurationManager.getInstance().isHeadless()) {
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
    }
    
    return options;
}
```

And in your config.properties:
```properties
headless=true
```

#### Q: How do I add custom capabilities to WebDriver?

**A**: Extend the browser options with desired capabilities:

```java
public static ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    
    // Add arguments
    options.addArguments("--disable-extensions");
    options.addArguments("--start-maximized");
    
    // Add preferences
    Map<String, Object> prefs = new HashMap<>();
    prefs.put("profile.default_content_setting_values.notifications", 2);
    options.setExperimentalOption("prefs", prefs);
    
    // Add capabilities
    options.setCapability("acceptInsecureCerts", true);
    
    return options;
}
```

#### Q: How do I handle dynamic IDs in locators?

**A**: Use a more stable locator strategy:

```java
// Instead of fixed IDs which may change:
public static final By EMAIL_FIELD = By.id("email-field-12345");

// Use data attributes if available:
public static final By EMAIL_FIELD = By.cssSelector("[data-testid='email-input']");

// Use XPath with partial matching if necessary:
public static final By EMAIL_FIELD = By.xpath("//input[contains(@id, 'email-field')]");

// Use XPath with multiple attributes:
public static final By EMAIL_FIELD = By.xpath("//input[@type='email' and @placeholder='Email']");
```

### Test Execution Questions

#### Q: How do I run a specific test or test group?

**A**: Use TestNG XML files or command line parameters:

With XML file:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Specific Test Suite">
  <test name="Specific Test">
    <classes>
      <class name="com.storydoc.tests.SignupTest">
        <methods>
          <include name="testPositiveSignupFlow"/>
        </methods>
      </class>
    </classes>
  </test>
</suite>
```

With Maven command:
```bash
mvn test -Dgroups=signup
# or for specific test
mvn test -Dtest=SignupTest#testPositiveSignupFlow
```

#### Q: How can I pass parameters to my tests?

**A**: Use TestNG parameters, data providers, or system properties:

With TestNG parameters:
```java
@Parameters({"browser", "environment"})
@BeforeTest
public void setUp(String browser, String environment) {
    // Setup using parameters
}
```

With Maven:
```bash
mvn test -Dbrowser=chrome -Denvironment=staging
```

#### Q: How do I handle flaky tests?

**A**: Implement retry logic for flaky tests:

```java
// Create a retry analyzer
public class RetryAnalyzer implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_RETRY = 2;
    
    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY) {
            count++;
            return true;
        }
        return false;
    }
}

// Apply to specific tests
@Test(retryAnalyzer = RetryAnalyzer.class)
public void flakyTest() {
    // Test implementation
}

// Or apply to all tests with a listener
public class RetryListener implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, 
                          Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
```

#### Q: How do I handle timeouts appropriately?

**A**: Use a tiered timeout strategy:

```java
// Short timeout for quick operations
public WebElement waitForElementClickable(By locator) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    return wait.until(ExpectedConditions.elementToBeClickable(locator));
}

// Standard timeout for most operations
public WebElement waitForElementVisible(By locator) {
    WebDriverWait wait = new WebDriverWait(driver, 
        Duration.ofSeconds(ConfigurationManager.getInstance().getTimeout()));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}

// Long timeout for slow operations
public void waitForPageLoad() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(driver -> ((JavascriptExecutor) driver)
        .executeScript("return document.readyState").equals("complete"));
}
```

### Framework Architecture Questions

#### Q: How do I extend the framework with new page objects?

**A**: Follow these steps:

1. Create a locator file for the new page:
```java
public class NewPageLocators {
    public static final By PAGE_HEADER = By.cssSelector("h1.page-header");
    public static final By SUBMIT_BUTTON = By.cssSelector("button[type='submit']");
    // Add more locators as needed
}
```

2. Create the page object class:
```java
public class NewPage extends BasePage {
    public NewPage(WebDriver driver) {
        super(driver);
    }
    
    public boolean isPageLoaded() {
        return isElementPresent(NewPageLocators.PAGE_HEADER);
    }
    
    public void clickSubmit() {
        click(NewPageLocators.SUBMIT_BUTTON);
    }
    
    // Add more page-specific methods
}
```

3. Update existing page objects for navigation:
```java
public class ExistingPage extends BasePage {
    // Existing methods...
    
    public NewPage navigateToNewPage() {
        click(ExistingPageLocators.NEW_PAGE_LINK);
        return new NewPage(driver);
    }
}
```

#### Q: How can I make the framework more maintainable?

**A**: Apply these best practices:

1. Keep locators centralized in separate classes
2. Use fluent interfaces for readability
3. Implement proper encapsulation in page objects
4. Write comprehensive JavaDoc comments
5. Create utility classes for common operations
6. Implement consistent naming conventions
7. Use appropriate design patterns (Factory, Builder, etc.)
8. Minimize duplication through inheritance or composition

#### Q: How can I implement data-driven testing?

**A**: Use TestNG DataProviders:

```java
@DataProvider(name = "signupTestData")
public Object[][] signupTestData() {
    return new Object[][] {
        {"test1@example.com", "Password123", true, "Standard user"},
        {"test2@example.com", "Password456", true, "Premium user"}
    };
}

@Test(dataProvider = "signupTestData")
public void testSignupWithMultipleUsers(String email, String password, 
                                       boolean acceptTerms, String userType) {
    logger.info("Testing signup with user type: {}", userType);
    
    SuccessPage successPage = signupPage
        .enterEmail(email)
        .enterPassword(password);
        
    if (acceptTerms) {
        successPage = successPage.acceptTerms().clickSignUp();
    } else {
        // Test negative case without accepting terms
    }
    
    Assert.assertTrue(successPage.isSignupSuccessful());
}
```

#### Q: How do I handle test dependencies properly?

**A**: Use TestNG dependencies and proper page object transitions:

```java
@Test(groups = {"signup", "smoke"})
public void testNavigateToSignupPage() {
    signupPage.navigateToSignupPage();
    Assert.assertTrue(signupPage.isSignupPageLoaded());
}

@Test(groups = {"signup"}, dependsOnMethods = {"testNavigateToSignupPage"})
public void testEnterCredentials() {
    signupPage.enterEmail("test@example.com")
              .enterPassword("Test@123");
    Assert.assertTrue(signupPage.areCredentialsEntered());
}

@Test(groups = {"signup"}, dependsOnMethods = {"testEnterCredentials"})
public void testSubmitForm() {
    SuccessPage successPage = signupPage.acceptTerms().clickSignUp();
    Assert.assertTrue(successPage.isSignupSuccessful());
}
```

## Getting Additional Help

### Internal Resources

If this guide doesn't address your specific issue, consider these internal resources:

1. **Framework Documentation**: Refer to the comprehensive framework documentation in the `docs` directory
2. **Examples Directory**: Check the `examples` directory for sample test implementations
3. **Codebase**: Explore the source code, especially utility classes and helpers
4. **Team Knowledge Base**: Search the internal knowledge base for similar issues

### External Resources

These external resources can provide additional help:

1. **Selenium Documentation**: [Selenium Documentation](https://www.selenium.dev/documentation/)
2. **TestNG Documentation**: [TestNG Documentation](https://testng.org/doc/)
3. **WebDriverManager Documentation**: [WebDriverManager GitHub](https://github.com/bonigarcia/webdrivermanager)
4. **Stack Overflow**: Search for similar issues on [Stack Overflow](https://stackoverflow.com/questions/tagged/selenium)

### Reporting Issues

If you encounter a framework issue not covered in this guide:

1. **Create a Detailed Bug Report**:
   - Include steps to reproduce
   - Provide environment details (OS, browser version, Java version)
   - Attach relevant logs, screenshots, and stack traces
   - Specify which version of the framework you're using

2. **Follow These Channels**:
   - Submit issues to the internal issue tracker
   - For urgent issues, contact the QA Lead or Automation Engineer
   - Add the issue to the weekly automation meeting agenda

### Required Information for Support

When seeking help, always provide:

1. **Test Execution Details**:
   - Test class and method name
   - Environment used (local, CI, staging)
   - Browser and version
   - Execution time and frequency of issue

2. **Evidence**:
   - ExtentReports output
   - Log files (framework logs, browser console logs)
   - Screenshots at point of failure
   - Video recording if available

3. **Framework Information**:
   - Framework version
   - Any custom modifications
   - Configuration settings
   - Recent changes that might have impacted the test

This comprehensive information will help the team diagnose and resolve your issue more quickly.