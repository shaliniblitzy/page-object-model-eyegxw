# Storydoc Selenium Automation Framework - Usage Guide

## Table of Contents

1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Configuration](#configuration)
4. [Running Tests](#running-tests)
5. [Test Reports](#test-reports)
6. [Creating New Tests](#creating-new-tests)
7. [Troubleshooting](#troubleshooting)
8. [Best Practices](#best-practices)
9. [Advanced Usage](#advanced-usage)
10. [References](#references)

## Introduction

The Storydoc Selenium Automation Framework is a robust testing solution built using the Page Object Model (POM) design pattern to automate testing of the Storydoc signup process. This framework provides a maintainable, scalable approach to UI automation with clear separation of concerns between test logic, page representation, and element identification.

### Key Components

- **Page Objects**: Java classes that model web pages and their behaviors
- **Locator Repository**: Centralized storage of element selectors separate from page objects
- **WebDriver Manager**: Handles browser session initialization and configuration
- **Configuration Manager**: Controls test environment settings and parameters
- **Test Base**: Provides common setup and teardown functionality for all tests
- **Test Utilities**: Helper methods for common operations
- **Reporting System**: Generates detailed HTML reports with screenshots

This framework is designed with maintainability as a primary goal, allowing testers to quickly adapt to UI changes by centralizing element identifiers and providing a consistent structure for test implementation.

## Prerequisites

Before using the Storydoc Selenium Automation Framework, ensure you have the following prerequisites installed and configured:

### Required Software

- **Java Development Kit (JDK) 11+**: Required for framework compilation and execution
- **Maven 3.8+**: Dependency management and build automation tool
- **Supported Browsers**:
  - Google Chrome (latest or latest-1)
  - Mozilla Firefox (latest or latest-1)
  - Microsoft Edge (latest)
- **Integrated Development Environment (IDE)**:
  - IntelliJ IDEA (recommended)
  - Eclipse
  - Visual Studio Code with Java extensions

### Setup Instructions

1. Install JDK 11 or higher and configure JAVA_HOME environment variable
2. Install Maven and configure PATH environment variable
3. Install at least one of the supported browsers
4. Clone the framework repository:
   ```bash
   git clone <repository-url>
   cd storydoc-automation
   ```
5. Install required IDE plugins:
   - TestNG plugin
   - Maven plugin

### Knowledge Prerequisites

Basic familiarity with the following technologies will help you effectively use the framework:

- Java programming language
- Selenium WebDriver API
- TestNG testing framework
- Page Object Model design pattern
- CSS selectors and XPath for element location

Refer to the SETUP.md file in the docs directory for detailed environment setup instructions.

## Configuration

The Storydoc Selenium Automation Framework provides flexible configuration options to adapt to different testing environments and requirements.

### Configuration Files

The framework uses a hierarchical configuration approach with the following files:

- **config.properties**: Base configuration with default settings
- **environment-specific properties**: Override settings for specific environments (dev.properties, staging.properties)
- **local.properties**: Optional local overrides (gitignored)

Configuration files are located in `src/main/resources/config/` directory.

### Key Configuration Parameters

| Parameter | Description | Default Value |
|-----------|-------------|---------------|
| `browser` | Browser to use for test execution | chrome |
| `headless` | Run browser in headless mode | false |
| `base.url` | Base URL for application under test | https://editor-staging.storydoc.com |
| `signup.url` | URL for signup page | https://editor-staging.storydoc.com/sign-up |
| `timeout.seconds` | Default timeout for element wait | 10 |
| `page.load.timeout` | Timeout for page loads | 30 |
| `screenshots.dir` | Directory for screenshots | test-output/screenshots |
| `reports.dir` | Directory for test reports | test-output/reports |

### Browser Configuration

The framework supports the following browsers:

- **Chrome**: Set `browser=chrome` in properties file
- **Firefox**: Set `browser=firefox` in properties file
- **Edge**: Set `browser=edge` in properties file

To enable headless mode (ideal for CI/CD execution), set `headless=true`.

### Environment Selection

Specify the target environment using the `env` parameter:

- **LOCAL**: Development environment on localhost
- **DEV**: Development environment
- **STAGING**: Staging environment (recommended for testing)

Example for selecting environment in properties file:
```properties
env=STAGING
```

### Command-line Configuration Overrides

Any configuration parameter can be overridden via command-line during test execution:

```bash
mvn test -Dbrowser=firefox -Denv=STAGING -Dheadless=true
```

### Configuration Examples

#### Running Tests in Chrome on Staging Environment
```properties
# staging.properties
browser=chrome
headless=false
env=STAGING
base.url=https://editor-staging.storydoc.com
signup.url=https://editor-staging.storydoc.com/sign-up
timeout.seconds=10
page.load.timeout=30
```

#### CI/CD Execution Configuration
```properties
# ci.properties
browser=chrome
headless=true
env=STAGING
timeout.seconds=15
page.load.timeout=45
retry.max=2
```

## Running Tests

This section provides instructions for executing tests using the framework.

### Running All Tests

To run all available tests in the default configuration:

```bash
mvn clean test
```

### Running Specific Tests

To run a specific test class:

```bash
mvn clean test -Dtest=SignupTest
```

To run multiple test classes:

```bash
mvn clean test -Dtest=SignupTest,ErrorHandlingTest
```

To run a specific test method within a class:

```bash
mvn clean test -Dtest=SignupTest#testPositiveSignupFlow
```

### Using TestNG XML Suite Files

For more control over test execution, use TestNG XML suite files:

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

Example testng.xml file:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Storydoc Signup Test Suite">
  <test name="Signup Tests">
    <classes>
      <class name="com.storydoc.tests.SignupTest" />
    </classes>
  </test>
</suite>
```

### Running Tests by Group

Tests can be organized into groups and executed selectively:

```bash
mvn clean test -Dgroups=smoke
```

Common groups in the framework:
- `smoke`: Critical path tests for basic functionality
- `regression`: Complete test suite for thorough verification
- `signup`: All signup-related tests
- `flaky`: Tests known to be occasionally unstable

### Parallel Test Execution

For faster execution, tests can be run in parallel:

```bash
mvn clean test -DsuiteXmlFile=parallel-testng.xml
```

Example parallel-testng.xml:
```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Parallel Test Suite" parallel="classes" thread-count="3">
  <test name="Signup Tests">
    <classes>
      <class name="com.storydoc.tests.SignupTest" />
      <class name="com.storydoc.tests.ErrorHandlingTest" />
    </classes>
  </test>
</suite>
```

### Running Tests with Custom Configuration

Override configuration parameters from command line:

```bash
mvn clean test -Dbrowser=firefox -Denv=STAGING -Dheadless=true
```

### CI/CD Execution

#### Jenkins Pipeline
The framework can be executed in Jenkins using the provided Jenkinsfile:

```bash
# Jenkins will automatically execute the pipeline defined in Jenkinsfile
# Typical execution command within the pipeline:
mvn clean test -Dbrowser=chrome -Dheadless=true -Denv=STAGING -Dgroups=regression
```

#### GitHub Actions
Execute tests in GitHub Actions workflow:

```bash
# Executed by GitHub Actions workflow defined in .github/workflows/selenium-tests.yml
mvn -B clean test -Dbrowser=chrome -Dheadless=true -Denv=STAGING
```

### Troubleshooting Test Execution

If tests are failing to run:

1. Check that all dependencies are installed: `mvn dependency:resolve`
2. Verify browser drivers are being downloaded correctly by WebDriverManager
3. Check for environment-specific issues in the configuration
4. Run with additional logging: `mvn clean test -Dlog4j.configurationFile=log4j2-debug.xml`
5. Check for port conflicts if WebDriver fails to initialize

## Test Reports

The framework generates comprehensive test reports to provide visibility into test execution results.

### Report Locations

After test execution, reports are available at:

- **HTML Reports**: `target/extent-reports/index.html`
- **TestNG XML Reports**: `target/surefire-reports`
- **Screenshots**: `target/extent-reports/screenshots`
- **Logs**: `target/logs`

### Understanding the ExtentReports Dashboard

The main test report is generated using ExtentReports and provides a rich HTML dashboard:

1. **Dashboard Overview**:
   - Test execution summary with pass/fail counts
   - Execution time and environment details
   - Charts showing test result distribution

2. **Test Case Details**:
   - Detailed view of each test case
   - Step-by-step execution log
   - Timestamps and duration for each step
   - Test data used during execution
   - Screenshots for failed steps

3. **Navigation**:
   - Filter tests by status (pass/fail/skip)
   - Search tests by name
   - Category and tag-based filtering

### Analyzing Test Failures

When a test fails, the report provides detailed information to help diagnose the issue:

1. **Error Messages**: Precise error description and exception details
2. **Screenshots**: Automatically captured at the point of failure
3. **Step-by-Step Logs**: Chronological sequence of actions leading to failure
4. **Environment Information**: Browser, version, OS, and other relevant details

Example of a failure analysis workflow:
1. Open the HTML report in a browser
2. Identify failed tests (marked in red)
3. Click on the test to view details
4. Check the error message for initial diagnosis
5. Review the screenshot at the point of failure
6. Examine the step log to understand the sequence of events

### Historical Reports

By default, reports are overwritten on each test run. To preserve historical reports:

1. Configure the report directory with timestamp:
   ```properties
   reports.dir=test-output/reports/run-{timestamp}
   ```

2. Or use the Maven Surefire Reports Plugin to archive reports:
   ```xml
   <plugin>
     <groupId>org.apache.maven.plugins</groupId>
     <artifactId>maven-surefire-plugin</artifactId>
     <configuration>
       <reportsDirectory>${project.build.directory}/surefire-reports/${maven.build.timestamp}</reportsDirectory>
     </configuration>
   </plugin>
   ```

### CI/CD Integration

In CI/CD environments, reports can be automatically published:

#### Jenkins
```groovy
publishHTML(target: [
    reportName: 'Test Report',
    reportDir: 'target/extent-reports',
    reportFiles: 'index.html',
    keepAll: true
])
```

#### GitHub Actions
```yaml
- name: Publish Test Report
  uses: actions/upload-artifact@v3
  if: always()
  with:
    name: test-reports
    path: target/extent-reports/
```

### Report Customization

The appearance and content of reports can be customized:

```java
public void initializeReporting() {
    ExtentSparkReporter htmlReporter = new ExtentSparkReporter("test-output/report.html");
    htmlReporter.config().setTheme(Theme.DARK); // or Theme.STANDARD
    htmlReporter.config().setDocumentTitle("Storydoc Signup Test Report");
    htmlReporter.config().setReportName("Selenium Test Automation Results");
    htmlReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
    
    extent = new ExtentReports();
    extent.attachReporter(htmlReporter);
    extent.setSystemInfo("OS", System.getProperty("os.name"));
    extent.setSystemInfo("Browser", ConfigurationManager.getInstance().getBrowser());
    extent.setSystemInfo("Environment", ConfigurationManager.getInstance().getEnvironment());
    extent.setSystemInfo("Tester", "QA Team");
}
```

## Creating New Tests

This section explains how to create new tests using the framework.

### Test Class Structure

New test classes should extend the TestBase class which provides common setup and teardown functionality:

```java
public class NewFeatureTest extends TestBase {
    
    private SignupPage signupPage;
    
    @BeforeMethod
    public void setupTest() {
        signupPage = new SignupPage(driver);
    }
    
    @Test
    public void testNewFeature() {
        // Test implementation
    }
}
```

### Creating Test Methods

Test methods use TestNG annotations and follow a standard structure:

```java
@Test(groups = {"signup", "regression"})
public void testPositiveSignupFlow() {
    // 1. Arrange - prepare test data
    String email = RandomDataGenerator.generateRandomEmail();
    String password = RandomDataGenerator.generateRandomPassword();
    
    // 2. Act - perform the actions
    signupPage.navigateToSignupPage();
    SuccessPage successPage = signupPage
        .enterEmail(email)
        .enterPassword(password)
        .acceptTerms()
        .clickSignUp();
    
    // 3. Assert - verify the results
    Assert.assertTrue(successPage.isSignupSuccessful(), 
        "Signup was not successful");
    Assert.assertTrue(successPage.getConfirmationMessage().contains("successfully"), 
        "Success message not displayed correctly");
    
    // 4. Log - record important information
    test.info("Successfully verified signup process with email: " + email);
}
```

### Creating New Page Objects

To create a page object for a new page:

1. **Create a Locators Class**:
```java
public class NewPageLocators {
    public static final By PAGE_HEADER = By.cssSelector("h1.header");
    public static final By SUBMIT_BUTTON = By.id("submit-btn");
    public static final By USERNAME_FIELD = By.cssSelector("input[data-testid='username-input']");
    // Add more locators as needed
}
```

2. **Create the Page Object Class**:
```java
public class NewPage extends BasePage {
    
    public NewPage(WebDriver driver) {
        super(driver);
    }
    
    public boolean isPageLoaded() {
        return isElementPresent(NewPageLocators.PAGE_HEADER);
    }
    
    public NewPage enterUsername(String username) {
        type(NewPageLocators.USERNAME_FIELD, username);
        return this;
    }
    
    public SuccessPage clickSubmit() {
        click(NewPageLocators.SUBMIT_BUTTON);
        return new SuccessPage(driver);
    }
}
```

3. **Update Navigation Methods in Other Pages**:
```java
// In existing page that can navigate to the new page
public NewPage navigateToNewPage() {
    click(ExistingPageLocators.NEW_PAGE_LINK);
    return new NewPage(driver);
}
```

### Best Practices for Locator Definition

When defining locators, follow these guidelines:

1. **Prefer Stable Attributes**:
   - Use `id` attributes when available
   - Use `data-testid` or similar test-specific attributes
   - CSS selectors are generally more stable than XPath

2. **Create Descriptive Locator Names**:
   - Use capitalized, underscore-separated names
   - Include the element type in the name
   - Group related elements with prefixes

Example of good locator definitions:
```java
public static final By SIGNUP_FORM = By.cssSelector("form[data-testid='signup-form']");
public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-input']");
public static final By PASSWORD_FIELD = By.cssSelector("input[data-testid='password-input']");
public static final By LOGIN_LINK = By.cssSelector("a[data-testid='login-link']");
```

### Managing Test Data

For test data management, consider these approaches:

1. **Runtime Generation**:
```java
String email = RandomDataGenerator.generateRandomEmail();
String password = RandomDataGenerator.generateRandomPassword();
```

2. **Data Providers for Multiple Test Cases**:
```java
@DataProvider(name = "validCredentials")
public Object[][] getValidCredentials() {
    return new Object[][] {
        {"test1@example.com", "Password1!"},
        {"test2@example.com", "Password2@"}
    };
}

@Test(dataProvider = "validCredentials")
public void testLoginWithDifferentCredentials(String email, String password) {
    // Test implementation using the provided data
}
```

3. **Constants for Static Test Data**:
```java
public class TestConstants {
    public static final String VALID_EMAIL_PATTERN = "test_{0}@example.com";
    public static final String VALID_PASSWORD = "Test@1234";
    public static final String EXPECTED_SUCCESS_MESSAGE = "successfully";
}
```

### Adding Effective Assertions

Write clear assertions that provide useful feedback on failure:

```java
// Good assertion with descriptive message
Assert.assertTrue(successPage.isSignupSuccessful(), 
    "Signup was not successful - success message not displayed");

// Verifying specific element text
String confirmationMessage = successPage.getConfirmationMessage();
Assert.assertTrue(confirmationMessage.contains("successfully"), 
    "Success message did not contain expected text. Actual message: " + confirmationMessage);

// Verifying element state
Assert.assertTrue(signupPage.isTermsCheckboxSelected(),
    "Terms checkbox should be selected after clicking");
```

## Troubleshooting

This section provides guidance on diagnosing and resolving common issues with the framework.

### WebDriver Initialization Issues

| Problem | Possible Causes | Solution |
|---------|----------------|----------|
| `SessionNotCreatedException` | Incompatible browser driver version | Update WebDriverManager version or specify compatible driver version |
| `WebDriverException: cannot find chrome binary` | Chrome not installed or path incorrect | Install Chrome or specify path in system properties |
| `ConnectionRefusedException` | Port in use or browser not responding | Kill existing browser processes and retry |

Example fix for browser binary path:
```java
ChromeOptions options = new ChromeOptions();
options.setBinary("/path/to/chrome");
```

### Element Location Failures

| Problem | Possible Causes | Solution |
|---------|----------------|----------|
| `NoSuchElementException` | Element not present, locator changed | Update locator, increase wait time, check element exists before interaction |
| `StaleElementReferenceException` | Page refreshed after element found | Refetch element before interaction, implement retry mechanism |
| `ElementNotInteractableException` | Element present but not interactable | Wait for element to become interactable, check if hidden by other elements |

Example retry mechanism for stale elements:
```java
public void safeClick(By locator) {
    int attempts = 0;
    while (attempts < 3) {
        try {
            WebElement element = driver.findElement(locator);
            element.click();
            break;
        } catch (StaleElementReferenceException e) {
            attempts++;
            if (attempts == 3) {
                throw e;
            }
        }
    }
}
```

### Timing Issues

| Problem | Possible Causes | Solution |
|---------|----------------|----------|
| `TimeoutException` | Element not appearing within timeout period | Increase wait timeout, check if condition is correct |
| Intermittent failures | Race conditions, app performance variations | Implement explicit waits, add more robust synchronization |
| Tests run too slowly | Excessive waits, browser performance | Optimize wait times, use headless browser for CI/CD |

Example of improved wait strategy:
```java
public WebElement waitForElementVisible(By locator, long timeoutInSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    wait.pollingEvery(Duration.ofMillis(200));
    wait.ignoring(StaleElementReferenceException.class);
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}
```

### Browser-Specific Issues

| Browser | Common Issues | Solutions |
|---------|--------------|-----------|
| Chrome | Headless mode rendering differences | Use `--window-size` argument to set proper dimensions |
| Firefox | Slower element interaction | Increase timeouts specifically for Firefox |
| Edge | WebDriver compatibility | Ensure latest Edge WebDriver is compatible with browser |

Example browser-specific configuration:
```java
private static ChromeOptions getChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    if (ConfigurationManager.getInstance().isHeadless()) {
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
    }
    return options;
}
```

### Framework Configuration Issues

| Problem | Possible Causes | Solution |
|---------|----------------|----------|
| Missing properties | Configuration file not found | Check file path, use fallback values |
| Incorrect environment | Environment parameter not set | Explicitly set environment in properties or command-line |
| Permission issues | File access permissions | Run with appropriate permissions, check user rights |

Example configuration fallback:
```java
public String getProperty(String key, String defaultValue) {
    String value = properties.getProperty(key);
    return (value != null) ? value : defaultValue;
}
```

### Debugging Techniques

When facing difficult issues:

1. **Enable Detailed Logging**:
   ```
   mvn test -Dlog4j.configuration=log4j2-debug.xml
   ```

2. **Use Browser Developer Tools**:
   - Run in non-headless mode
   - Add breakpoints in test code
   - Inspect elements manually to verify locators

3. **Capture Element State**:
   ```java
   // Add to your BasePage class for debugging
   protected void debugElement(By locator) {
       try {
           WebElement element = driver.findElement(locator);
           logger.debug("Element found: " + locator);
           logger.debug("Displayed: " + element.isDisplayed());
           logger.debug("Enabled: " + element.isEnabled());
           logger.debug("Selected: " + element.isSelected());
           logger.debug("Size: " + element.getSize());
           logger.debug("Location: " + element.getLocation());
           logger.debug("Attributes: " + element.getAttribute("outerHTML"));
       } catch (Exception e) {
           logger.debug("Failed to find element: " + locator, e);
       }
   }
   ```

4. **Diagnose JavaScript Issues**:
   ```java
   // Check for JavaScript errors on page
   private List<String> getJsErrors() {
       List<LogEntry> logs = driver.manage().logs().get(LogType.BROWSER).getAll();
       return logs.stream()
           .filter(log -> log.getLevel().equals(Level.SEVERE))
           .map(LogEntry::getMessage)
           .collect(Collectors.toList());
   }
   ```

## Best Practices

Follow these recommended practices to maintain high-quality test automation.

### Test Independence

1. **Self-contained Tests**:
   - Each test should be independent and not rely on other tests
   - Avoid dependencies between test methods
   - Setup and teardown should ensure clean state

2. **Isolated Test Data**:
   - Use unique test data for each test run
   - Generate dynamic data with timestamps to avoid conflicts
   - Clean up test data after execution when possible

3. **Stateless Page Objects**:
   - Page objects should not maintain state between tests
   - Create new page object instances for each test
   - Pass values between pages explicitly, not through class variables

### Effective Page Object Pattern Implementation

1. **Responsibility Separation**:
   - Page objects should only know about page structure and behavior
   - Tests should only know about business processes
   - Locators should be in separate classes or files

2. **Method Design**:
   - Methods should represent user actions or business operations
   - Return appropriate page objects after navigation
   - Use method chaining for sequential operations
   - Include verification methods for conditions on the page

3. **Element Interaction**:
   - Encapsulate all WebDriver interactions in the base page class
   - Provide helper methods for common actions (click, type, select)
   - Handle waits and synchronization at the page object level

Example of well-structured page object:
```java
public class SignupPage extends BasePage {
    
    public SignupPage(WebDriver driver) {
        super(driver);
    }
    
    // Navigation method
    public SignupPage navigateToSignupPage() {
        driver.get(ConfigurationManager.getInstance().getProperty("signup.url"));
        waitForPageToLoad();
        return this;
    }
    
    // Action methods with fluent interface
    public SignupPage enterEmail(String email) {
        type(SignupPageLocators.EMAIL_FIELD, email);
        return this;
    }
    
    public SignupPage enterPassword(String password) {
        type(SignupPageLocators.PASSWORD_FIELD, password);
        return this;
    }
    
    public SignupPage acceptTerms() {
        click(SignupPageLocators.TERMS_CHECKBOX);
        return this;
    }
    
    // Navigation method returning new page object
    public SuccessPage clickSignUp() {
        click(SignupPageLocators.SIGNUP_BUTTON);
        return new SuccessPage(driver);
    }
    
    // Business-level method combining multiple actions
    public SuccessPage submitSignupForm(String email, String password) {
        return enterEmail(email)
               .enterPassword(password)
               .acceptTerms()
               .clickSignUp();
    }
    
    // Verification methods
    public boolean isSignupPageLoaded() {
        return isElementPresent(SignupPageLocators.SIGNUP_FORM);
    }
    
    public boolean isTermsCheckboxSelected() {
        return driver.findElement(SignupPageLocators.TERMS_CHECKBOX).isSelected();
    }
    
    // Helper method specific to this page
    private void waitForPageToLoad() {
        waitForElementVisible(SignupPageLocators.SIGNUP_FORM);
    }
}
```

### Synchronization and Wait Strategies

1. **Explicit Waits Over Implicit**:
   - Avoid implicit waits as they apply to all element operations
   - Use explicit waits with specific conditions and timeouts
   - Implement custom wait conditions for complex scenarios

2. **Wait for Expected Conditions**:
   - Wait for specific element states (visible, clickable, selected)
   - Use appropriate wait condition for the interaction
   - Add custom conditions when standard ones are insufficient

3. **Dynamic Wait Timeouts**:
   - Use configuration-driven timeout values
   - Adjust timeouts based on operation complexity
   - Consider shorter timeouts for negative tests (expecting failure)

Example of effective wait implementation:
```java
// In BasePage class
protected WebElement waitForElementVisible(By locator) {
    int timeout = ConfigurationManager.getInstance().getTimeout();
    return waitForElementVisible(locator, timeout);
}

protected WebElement waitForElementVisible(By locator, long timeoutInSeconds) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}

protected WebElement waitForElementClickable(By locator) {
    int timeout = ConfigurationManager.getInstance().getTimeout();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.elementToBeClickable(locator));
}

protected boolean waitForElementInvisible(By locator) {
    int timeout = ConfigurationManager.getInstance().getTimeout();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
}

// Custom wait condition example
protected void waitForPageLoad() {
    new WebDriverWait(driver, Duration.ofSeconds(
        ConfigurationManager.getInstance().getPageLoadTimeout()))
        .until(driver -> ((JavascriptExecutor) driver)
            .executeScript("return document.readyState").equals("complete"));
}
```

### Maintainable Locator Strategies

1. **Prefer Stable Attributes**:
   - Use IDs when available (most stable)
   - Use data-testid or other test-specific attributes
   - Use multiple attributes to create robust selectors

2. **Minimize XPath Usage**:
   - Prefer CSS selectors over XPath when possible
   - Avoid complex XPath expressions
   - Never use absolute XPath (starting with /html)

3. **Locator Organization**:
   - Group locators by functionality or UI component
   - Use clear naming conventions
   - Document the purpose of complex locators

Example of robust locator strategies:
```java
// Good locators
public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-input']");
public static final By EMAIL_FIELD_ALTERNATIVE = By.cssSelector("input[type='email'][name='email']");

// Fallback strategy using XPath when necessary
public static final By TERMS_CHECKBOX = By.xpath("//label[contains(text(), 'Terms')]/preceding-sibling::input[@type='checkbox']");

// Avoid using brittle locators like these:
// By.cssSelector(".form > div:nth-child(2) > input");
// By.xpath("/html/body/div/form/div[2]/input");
```

### CI/CD Integration Best Practices

1. **Pipeline Configuration**:
   - Run tests in headless mode for CI/CD
   - Use appropriate timeouts for CI environments
   - Configure retry mechanism for flaky tests

2. **Result Handling**:
   - Generate machine-readable reports (XML) for CI integration
   - Store screenshots and logs as artifacts
   - Implement email notifications for failures

3. **Resource Management**:
   - Clean up browser processes after execution
   - Limit parallel execution based on available resources
   - Use lightweight browser options for CI/CD

Example GitHub Actions workflow configuration:
```yaml
name: Selenium Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        cache: maven
    
    - name: Build and test
      run: mvn -B test -Dbrowser=chrome -Dheadless=true -Denv=STAGING -Dtimeout.seconds=15
    
    - name: Publish test report
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: test-reports
        path: |
          target/extent-reports/
          target/surefire-reports/
```

## Advanced Usage

This section covers more advanced capabilities and customizations available in the framework.

### Parallel Test Execution

Configure TestNG to run tests in parallel for faster execution:

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Parallel Test Suite" parallel="classes" thread-count="3">
  <test name="Signup Tests">
    <classes>
      <class name="com.storydoc.tests.SignupTest" />
      <class name="com.storydoc.tests.ErrorHandlingTest" />
    </classes>
  </test>
</suite>
```

Key considerations for parallel execution:
- Use ThreadLocal for WebDriver: `ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();`
- Make sure all page objects and utilities are thread-safe
- Configure appropriate thread count based on machine resources
- Use isolated test data to avoid conflicts between threads

### Custom TestNG Listeners

Extend functionality with custom TestNG listeners:

```java
public class CustomTestListener implements ITestListener {
    
    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test started: " + result.getName());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        // Log test success, update external systems, etc.
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        // Custom failure handling, create tickets, etc.
    }
    
    // Implement other methods as needed
}
```

Register the listener in testng.xml:
```xml
<suite name="Test Suite">
  <listeners>
    <listener class-name="com.storydoc.listeners.CustomTestListener" />
  </listeners>
  <test name="Signup Tests">
    <!-- test classes -->
  </test>
</suite>
```

### Extending the Framework for New Features

To extend the framework for additional functionality:

1. **Add New Utility Classes**:
```java
public class ApiUtils {
    private static final RestAssured restAssured = new RestAssured();
    
    public static Response sendGetRequest(String endpoint, Map<String, String> headers) {
        // Implementation
    }
    
    public static Response sendPostRequest(String endpoint, Object body, Map<String, String> headers) {
        // Implementation
    }
}
```

2. **Create Component-Based Page Objects**:
```java
// Component class for a reusable UI element
public class NavigationBar {
    private WebDriver driver;
    
    public NavigationBar(WebDriver driver) {
        this.driver = driver;
    }
    
    public void clickHomeLink() {
        driver.findElement(By.id("home-link")).click();
    }
    
    public void clickProfileLink() {
        driver.findElement(By.id("profile-link")).click();
    }
}

// Page object using the component
public class DashboardPage extends BasePage {
    private NavigationBar navigationBar;
    
    public DashboardPage(WebDriver driver) {
        super(driver);
        this.navigationBar = new NavigationBar(driver);
    }
    
    public NavigationBar getNavigationBar() {
        return navigationBar;
    }
}
```

3. **Add Custom Annotations**:
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestInfo {
    String feature() default "";
    String[] tags() default {};
    Priority priority() default Priority.MEDIUM;
}

// Usage
@Test
@TestInfo(feature = "Signup", tags = {"smoke", "regression"}, priority = Priority.HIGH)
public void testSignupFlow() {
    // Test implementation
}
```

### Custom Reporting Enhancements

Enhance reports with additional information:

```java
public void customizeReporting() {
    // Create ExtentReports instance
    ExtentReports extent = new ExtentReports();
    
    // Configure HTML reporter
    ExtentSparkReporter htmlReporter = new ExtentSparkReporter("test-output/custom-report.html");
    htmlReporter.config().setTheme(Theme.DARK);
    htmlReporter.config().setDocumentTitle("Custom Storydoc Test Report");
    htmlReporter.config().enableTimeline(true);
    
    // Attach reporter to main ExtentReports instance
    extent.attachReporter(htmlReporter);
    
    // Add custom system info
    extent.setSystemInfo("Environment", ConfigurationManager.getInstance().getEnvironment());
    extent.setSystemInfo("Build", getBuildNumber());
    extent.setSystemInfo("Tester", System.getProperty("user.name"));
    
    // Add environment-specific coloring
    if (ConfigurationManager.getInstance().getEnvironment().equals("STAGING")) {
        htmlReporter.config().setCss(".badge-primary { background-color: #f0ad4e !important; }");
    }
}
```

### External Tool Integration

Integrate with external testing tools:

1. **BrowserStack/Sauce Labs Integration**:
```java
public WebDriver initializeRemoteDriver() {
    String username = System.getenv("BROWSERSTACK_USERNAME");
    String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
    String browserStackUrl = "https://" + username + ":" + accessKey + "@hub-cloud.browserstack.com/wd/hub";
    
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability("browser", "Chrome");
    capabilities.setCapability("browser_version", "latest");
    capabilities.setCapability("os", "Windows");
    capabilities.setCapability("os_version", "10");
    capabilities.setCapability("name", "Storydoc Signup Test");
    
    return new RemoteWebDriver(new URL(browserStackUrl), capabilities);
}
```

2. **JIRA Integration for Defect Creation**:
```java
public void createJiraTicket(ITestResult result, String screenshotPath) {
    // Implementation using JIRA REST API
    JiraClient jiraClient = new JiraClient(
        ConfigurationManager.getInstance().getProperty("jira.url"),
        ConfigurationManager.getInstance().getProperty("jira.username"),
        ConfigurationManager.getInstance().getProperty("jira.apiToken")
    );
    
    Issue issue = jiraClient.createIssue(
        "STORYDOC", // Project key
        "Bug",      // Issue type
        "Test Failure: " + result.getName(),
        "Test failed with error: " + result.getThrowable().getMessage() + 
        "\n\nExecution details: " + getTestExecutionDetails(result)
    );
    
    jiraClient.addAttachment(issue.getId(), new File(screenshotPath));
}
```

### Performance Optimization

Optimize framework performance for large test suites:

1. **Headless Browser Execution**:
```java
private static ChromeOptions getOptimizedChromeOptions() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--disable-gpu");
    options.addArguments("--window-size=1920,1080");
    options.addArguments("--disable-extensions");
    options.addArguments("--no-sandbox");
    options.addArguments("--disable-dev-shm-usage");
    options.setPageLoadStrategy(PageLoadStrategy.EAGER);
    return options;
}
```

2. **Resource Cleanup**:
```java
@AfterMethod
public void optimizedTearDown() {
    if (driver != null) {
        // Clear browser data before closing
        try {
            driver.manage().deleteAllCookies();
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
            ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
        } catch (Exception e) {
            // Log but don't fail the test for cleanup issues
            logger.warn("Failed to clean browser data: " + e.getMessage());
        }
        driver.quit();
    }
}
```

3. **Optimized Wait Strategies**:
```java
public WebElement optimizedWait(By locator) {
    // Start with a quick check to avoid unnecessary waiting
    try {
        WebElement element = driver.findElement(locator);
        if (element.isDisplayed() && element.isEnabled()) {
            return element;
        }
    } catch (NoSuchElementException | StaleElementReferenceException e) {
        // Element not immediately available, continue to wait
    }
    
    // Use explicit wait with exponential backoff
    FluentWait<WebDriver> wait = new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(ConfigurationManager.getInstance().getTimeout()))
        .pollingEvery(Duration.ofMillis(200))
        .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
    
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}
```

## References

### Framework Documentation

- [SETUP.md](./SETUP.md) - Detailed setup instructions
- [ARCHITECTURE.md](./ARCHITECTURE.md) - Framework architecture overview
- [CONTRIBUTING.md](./CONTRIBUTING.md) - Guidelines for contributing to the framework
- [CHANGELOG.md](./CHANGELOG.md) - History of changes and versions

### External Documentation

- [Selenium WebDriver](https://www.selenium.dev/documentation/en/webdriver/) - Official Selenium WebDriver documentation
- [TestNG](https://testng.org/doc/) - TestNG documentation
- [Java Documentation](https://docs.oracle.com/en/java/) - Java language documentation
- [Maven](https://maven.apache.org/guides/) - Maven build tool documentation

### Useful Resources

- [Selenium Best Practices](https://www.selenium.dev/documentation/en/guidelines_and_recommendations/) - Official Selenium best practices
- [Page Object Model Pattern](https://martinfowler.com/bliki/PageObject.html) - Martin Fowler's description of the pattern
- [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) - WebDriverManager documentation
- [ExtentReports](https://www.extentreports.com/docs/versions/5/java/index.html) - ExtentReports documentation

### Community Support

- [Selenium Forums](https://groups.google.com/g/selenium-users) - Selenium Users Group
- [Stack Overflow](https://stackoverflow.com/questions/tagged/selenium) - Selenium-tagged questions
- [GitHub Repository](https://github.com/yourusername/storydoc-automation) - Framework GitHub repository for issues and contributions