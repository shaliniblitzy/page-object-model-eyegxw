│   E2E Tests   │
                    │   (TestNG)    │
                    └───────────────┘
            ┌───────────────────────────────┐
            │      Integration Tests        │
            │  (Page Object Interactions)   │
            └───────────────────────────────┘
┌───────────────────────────────────────────────────┐
│                   Unit Tests                      │
│  (Framework Components, Utilities, Base Classes)  │
└───────────────────────────────────────────────────┘
```

### Different Test Levels

1. **Unit Testing**:
   - Focuses on individual classes and methods in isolation
   - Utilizes mocking to isolate dependencies
   - Validates framework components function as expected
   - Examples: BasePage, WebDriverManager, ConfigurationManager

2. **Integration Testing**:
   - Validates interactions between components
   - Tests page object transitions and WebDriver integration
   - Ensures configuration and utility classes work together properly
   - Examples: Page navigation flows, WebDriver-Page Object interaction

3. **End-to-End Testing**:
   - Tests the complete signup flow from start to finish
   - Validates business functionality works as expected
   - Runs against actual browsers and staging environment
   - Examples: Complete signup process with valid credentials

### Testing Principles

1. **Test Independence**: Each test is designed to run independently of others, with proper setup and teardown.

2. **Explicit Testing**: Tests clearly state their intentions and expectations through descriptive names and assertions.

3. **Single Responsibility**: Each test focuses on validating a single aspect of functionality.

4. **DRY (Don't Repeat Yourself)**: Common test logic is abstracted into reusable methods and utilities.

5. **Fast Feedback**: Tests prioritize execution speed while maintaining reliability.

6. **Self-Verification**: Tests include robust assertions that clearly identify when a failure occurs.

7. **Controlled Test Data**: Tests generate or manage their own test data to ensure consistent execution.

## 3. Test Levels

### 3.1 Unit Testing

#### Approach for Framework Components

The unit testing strategy focuses on testing individual components of the automation framework in isolation, using mocking to simulate interactions with dependencies.

**Key Components Under Unit Testing:**

1. **Base Classes:**
   - BasePage
   - TestBase
   - WebDriverManager
   - ConfigurationManager

2. **Utility Classes:**
   - WaitUtils
   - RandomDataGenerator
   - FileUtils
   - LogUtils
   - ScreenshotUtils
   - JavaScriptUtils
   - AssertUtils

3. **Model Classes:**
   - UserAccount

#### Mocking Strategy

1. **WebDriver Mocking:**
   Since WebDriver is a key external dependency, we use Mockito to create mocks that simulate browser interactions without launching actual browsers:

   ```java
   @Test
   public void testElementClick() {
       // Mock WebDriver and WebElement
       WebDriver mockDriver = mock(WebDriver.class);
       WebElement mockElement = mock(WebElement.class);
       
       // Setup mock behavior
       when(mockDriver.findElement(any(By.class))).thenReturn(mockElement);
       
       // Test BasePage click method
       BasePage basePage = new ConcreteBasePage(mockDriver);
       basePage.click(By.id("testElement"));
       
       // Verify the element was located and clicked
       verify(mockDriver).findElement(any(By.class));
       verify(mockElement).click();
   }
   ```

2. **Configuration Mocking:**
   Tests for components that rely on configuration values use mocked ConfigurationManager:

   ```java
   @Test
   public void testWebDriverManagerInitialization() {
       // Mock ConfigurationManager
       ConfigurationManager mockConfig = mock(ConfigurationManager.class);
       when(mockConfig.getBrowser()).thenReturn(BrowserType.CHROME);
       when(mockConfig.isHeadless()).thenReturn(true);
       
       // Test with mocked configuration
       WebDriverManager.initDriver();
       
       // Assertions...
   }
   ```

3. **File System Mocking:**
   For utilities that interact with the file system, we mock file operations:

   ```java
   @Test
   public void testScreenshotCapture() {
       // Mock file system operations
       File mockFile = mock(File.class);
       when(mockFile.exists()).thenReturn(true);
       
       // Test with mocked file operations
       // ...
   }
   ```

#### Code Coverage Requirements

| Component Category | Minimum Coverage Target | Priority Focus Areas |
|-------------------|------------------------|---------------------|
| Core Framework    | 85%                    | Exception handling, synchronization |
| Utility Classes   | 90%                    | Parameter validation, error scenarios |
| Page Objects      | 75%                    | Element interaction methods |
| Locator Classes   | 50%                    | N/A (mostly constants) |

#### Unit Test Organization

Unit tests are organized to mirror the structure of the main code:

```
src/test/java/
  ├── unit/
  │   ├── core/
  │   │   ├── BasePageTest.java
  │   │   ├── WebDriverManagerTest.java
  │   │   └── ConfigurationManagerTest.java
  │   ├── utils/
  │   │   ├── WaitUtilsTest.java
  │   │   ├── RandomDataGeneratorTest.java
  │   │   └── JavaScriptUtilsTest.java
  │   ├── pages/
  │   │   ├── SignupPageTest.java
  │   │   └── SuccessPageTest.java
  │   └── models/
  │       └── UserAccountTest.java
```

#### Example Unit Test

```java
@Test
public void testRandomDataGenerator_generateRandomEmail_returnsValidFormat() {
    // Act: Generate a random email
    String email = RandomDataGenerator.generateRandomEmail();
    
    // Assert: Verify email follows expected pattern
    Assert.assertTrue(email.matches("^test_\\d+@example\\.com$"), 
        "Email should match expected pattern");
}
```

### 3.2 Integration Testing

Integration testing focuses on how framework components interact with each other and with external dependencies like WebDriver.

#### Component Interaction Testing

1. **Page Object Interaction Tests:**
   Validate that page objects correctly interact with their locators and the WebDriver:

   ```java
   @Test
   public void testSignupPageInteractsWithLocators() {
       // Use real WebDriver but controlled test environment
       WebDriver driver = WebDriverManager.getDriver();
       
       // Create page object
       SignupPage signupPage = new SignupPage(driver);
       
       // Verify page object can find elements using locators
       signupPage.navigateToSignupPage();
       Assert.assertTrue(signupPage.isSignupPageLoaded(), 
           "Signup page should load and find elements using locators");
   }
   ```

2. **Page Navigation Tests:**
   Verify that page transitions work correctly between page objects:

   ```java
   @Test
   public void testNavigationBetweenPages() {
       WebDriver driver = WebDriverManager.getDriver();
       
       // Test page navigation chain
       SignupPage signupPage = new SignupPage(driver);
       signupPage.navigateToSignupPage();
       
       // Enter test data and navigate to next page
       SuccessPage successPage = signupPage.submitSignupForm("test@example.com", "Test@1234");
       
       // Verify navigation worked correctly
       Assert.assertTrue(successPage.isSignupSuccessful(), 
           "Should have navigated to success page");
   }
   ```

3. **Configuration Integration Tests:**
   Verify that configuration settings are correctly applied:

   ```java
   @Test
   public void testConfigurationIntegration() {
       // Set system property to control configuration
       System.setProperty("browser", "chrome");
       
       // Verify WebDriverManager uses the correct configuration
       WebDriver driver = WebDriverManager.getDriver();
       
       // Assertions to verify configuration was applied correctly
       // ...
   }
   ```

#### Test Environment Requirements

Integration tests require:
1. A controlled test environment with access to the file system for screenshots and reports
2. Network access to download WebDriver binaries (if not already cached)
3. Ability to launch browsers (real or headless)
4. Access to the staging environment for some tests

#### Example Integration Test

```java
public class PageObjectIntegrationTest extends TestBase {
    
    @Test
    public void testSignupPageIntegration() {
        // Create page objects using real WebDriver
        SignupPage signupPage = new SignupPage(driver);
        
        // Navigate and interact with page
        signupPage.navigateToSignupPage();
        
        // Verify page object correctly interacts with WebDriver
        boolean pageLoaded = signupPage.isSignupPageLoaded();
        
        // Assert expected results
        Assert.assertTrue(pageLoaded, "Signup page should load correctly");
        
        // Test element interactions
        signupPage.enterEmail("test@example.com");
        
        // Verify field was populated correctly
        String emailValue = driver.findElement(SignupPageLocators.EMAIL_FIELD)
            .getAttribute("value");
        Assert.assertEquals(emailValue, "test@example.com", 
            "Email field should contain entered value");
    }
}
```

### 3.3 End-to-End Testing

End-to-end testing validates the complete signup flow from a user's perspective, interacting with the actual application through the browser.

#### Complete Signup Flow Testing

1. **Positive Test Flow:**
   Test the successful signup path with valid credentials:

   ```java
   @Test
   public void testPositiveSignupFlow() {
       // Generate random test data
       String email = RandomDataGenerator.generateRandomEmail();
       String password = RandomDataGenerator.generateRandomPassword();
       
       // Navigate to signup page
       SignupPage signupPage = new SignupPage(driver);
       signupPage.navigateToSignupPage();
       
       // Complete signup form
       SuccessPage successPage = signupPage.submitSignupForm(email, password);
       
       // Verify successful signup
       Assert.assertTrue(successPage.isSignupSuccessful(), 
           "Signup should be successful");
       Assert.assertTrue(successPage.getConfirmationMessage().contains("successfully"), 
           "Success message should be displayed");
   }
   ```

2. **Data-Driven Signup Tests:**
   Test the signup flow with multiple data sets:

   ```java
   @Test(dataProvider = "signupTestData")
   public void testSignupWithDifferentDataSets(String email, String password, boolean expectSuccess) {
       SignupPage signupPage = new SignupPage(driver);
       signupPage.navigateToSignupPage();
       
       // Execute signup with test data
       SuccessPage successPage = signupPage.submitSignupForm(email, password);
       
       // Verify results match expectations
       if (expectSuccess) {
           Assert.assertTrue(successPage.isSignupSuccessful(), 
               "Signup should succeed with valid data: " + email);
       } else {
           Assert.assertFalse(successPage.isSignupSuccessful(), 
               "Signup should fail with invalid data: " + email);
       }
   }
   
   @DataProvider(name = "signupTestData")
   public Object[][] provideSignupData() {
       return new Object[][] {
           { "valid@example.com", "StrongP@ss1", true },
           { "test@domain.com", "Another@Pass2", true }
       };
   }
   ```

3. **Validation Tests:**
   Test form validation behavior:

   ```java
   @Test
   public void testEmailValidation() {
       SignupPage signupPage = new SignupPage(driver);
       signupPage.navigateToSignupPage();
       
       // Enter invalid email format
       signupPage.enterEmail("invalid-email");
       signupPage.enterPassword("ValidP@ss1");
       signupPage.acceptTerms();
       signupPage.clickSignUp();
       
       // Verify error message
       Assert.assertTrue(signupPage.hasEmailError(), 
           "Email error message should be displayed");
       Assert.assertTrue(signupPage.getEmailErrorMessage()
           .contains("valid email"), 
           "Error should indicate invalid email format");
   }
   ```

#### Cross-Browser Testing Approach

End-to-end tests run against multiple browsers to ensure compatibility:

1. **Primary Testing on Chrome:**
   - All test cases run on Chrome as the primary browser
   - Both headless and UI modes supported

2. **Regression Testing on Other Browsers:**
   - Core test cases run on Firefox and Edge
   - Focus on critical path functionality
   - Primarily headless mode for CI/CD

3. **Browser Configuration:**
   - Browser-specific configurations handled through ChromeConfig, FirefoxConfig, EdgeConfig
   - Each browser has dedicated options for headless mode, download settings, etc.

#### Test Data Management

1. **Dynamic Email Generation:**
   - Each test generates unique emails using timestamp: `test_{timestamp}@example.com`
   - Prevents test collision and data dependency

2. **Password Generation:**
   - Random passwords that meet complexity requirements
   - Generated through RandomDataGenerator utility

3. **User Account Models:**
   - UserAccount class encapsulates test user data
   - Factory methods for creating different account types

#### Environment Configuration

End-to-end tests run against the staging environment:
- URL: https://editor-staging.storydoc.com/sign-up
- Configuration loaded from staging.properties
- Environment-specific settings controlled through ConfigurationManager

#### Example End-to-End Test

```java
@Test(description = "Verify successful signup with valid credentials")
public void testPositiveSignupFlow() {
    // Arrange: Generate test data
    String email = RandomDataGenerator.generateRandomEmail();
    String password = RandomDataGenerator.generateRandomPassword();
    
    // Act: Navigate to signup page and submit form
    SignupPage signupPage = new SignupPage(driver);
    signupPage.navigateToSignupPage();
    
    // Use fluent interface for better readability
    SuccessPage successPage = signupPage
        .enterEmail(email)
        .enterPassword(password)
        .acceptTerms()
        .clickSignUp();
    
    // Assert: Verify successful signup
    Assert.assertTrue(successPage.isSignupSuccessful(), 
        "Signup should complete successfully");
    
    String confirmationMessage = successPage.getConfirmationMessage();
    Assert.assertTrue(confirmationMessage.contains("successfully"), 
        "Success message should indicate successful account creation");
}
```

## 4. Test Automation Implementation

### Framework Structure and Organization

The test automation implementation leverages the Page Object Model pattern and follows a structured organization:

#### Key Packages Structure

```
com.storydoc
  ├── core/                 # Core framework components
  │   ├── BasePage.java     # Base class for all page objects
  │   ├── TestBase.java     # Base class for all test classes
  │   ├── WebDriverManager.java  # Manages WebDriver instances
  │   └── RetryAnalyzer.java    # Handles test retries
  ├── config/               # Configuration management
  │   ├── ConfigurationManager.java
  │   ├── BrowserType.java
  │   └── ConfigProperties.java
  ├── pages/                # Page objects
  │   ├── SignupPage.java
  │   └── SuccessPage.java
  ├── locators/             # Element locators
  │   ├── SignupPageLocators.java
  │   └── SuccessPageLocators.java
  ├── tests/                # Test classes
  │   └── SignupTests.java
  ├── utils/                # Utility classes
  │   ├── WaitUtils.java
  │   ├── RandomDataGenerator.java
  │   ├── ScreenshotUtils.java
  │   └── AssertUtils.java
  ├── reports/              # Reporting components
  │   ├── ExtentManager.java
  │   └── ExtentTestManager.java
  ├── constants/            # Constants
  │   ├── TimeoutConstants.java
  │   └── MessageConstants.java
  └── exceptions/           # Custom exceptions
      └── FrameworkException.java
```

#### Test Organization

Tests are organized by feature and test type:

```
src/test/java/com/storydoc/tests/
  ├── unit/                # Unit tests for framework components
  ├── integration/         # Integration tests for component interactions  
  └── e2e/                 # End-to-end tests
      ├── signup/          # Signup feature tests
      │   ├── PositiveSignupTests.java
      │   └── ValidationTests.java
      └── CrossBrowserTests.java
```

### TestNG Test Structure

Tests use TestNG annotations for setup, execution, and teardown:

```java
public class SignupTests extends TestBase {
    
    private SignupPage signupPage;
    
    @BeforeMethod
    public void setUp() {
        // Initialize page objects
        signupPage = new SignupPage(driver);
    }
    
    @Test(description = "Verify successful signup with valid credentials")
    public void testPositiveSignupFlow() {
        // Test implementation
    }
    
    @Test(dataProvider = "signupTestData")
    public void testSignupWithDifferentDataSets(UserAccount userAccount) {
        // Data-driven test implementation
    }
    
    @DataProvider(name = "signupTestData")
    public Object[][] signupTestData() {
        // Return test data
    }
    
    @AfterMethod
    public void tearDown() {
        // Cleanup
    }
}
```

### Data-Driven Testing

The framework implements data-driven testing using TestNG data providers:

1. **Object Arrays Approach:**

```java
@DataProvider(name = "emailValidationData")
public Object[][] provideEmailData() {
    return new Object[][] {
        {"invalid", false, "Invalid email format"},
        {"test@example", false, "Incomplete domain"},
        {"test@example.com", true, "Valid email format"}
    };
}

@Test(dataProvider = "emailValidationData")
public void testEmailValidation(String email, boolean isValid, String testCase) {
    SignupPage signupPage = new SignupPage(driver);
    signupPage.navigateToSignupPage();
    signupPage.enterEmail(email);
    
    if (isValid) {
        Assert.assertFalse(signupPage.hasEmailError(), 
            "No error should be shown for valid email: " + testCase);
    } else {
        Assert.assertTrue(signupPage.hasEmailError(), 
            "Error should be shown for invalid email: " + testCase);
    }
}
```

2. **Model-Based Approach:**

```java
@DataProvider(name = "userAccountData")
public Object[][] provideUserAccountData() {
    return new Object[][] {
        {UserAccount.createValidAccount()},
        {UserAccount.createCustomAccount("test@example.com", "Password123!")},
        {new UserAccount.UserAccountBuilder()
            .email("custom@domain.com")
            .password("AnotherP@ss1")
            .termsAccepted(true)
            .build()}
    };
}

@Test(dataProvider = "userAccountData")
public void testSignupWithUserAccounts(UserAccount account) {
    SignupPage signupPage = new SignupPage(driver);
    signupPage.navigateToSignupPage();
    
    SuccessPage successPage = signupPage.submitSignupForm(account);
    
    Assert.assertTrue(successPage.isSignupSuccessful(), 
        "Signup should succeed with account: " + account.getEmail());
}
```

### Parallel Execution

The framework supports parallel test execution through TestNG:

1. **Configuration in testng.xml:**

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Storydoc Signup Test Suite" parallel="classes" thread-count="3">
    <test name="Signup Tests">
        <classes>
            <class name="com.storydoc.tests.e2e.signup.PositiveSignupTests"/>
            <class name="com.storydoc.tests.e2e.signup.ValidationTests"/>
            <class name="com.storydoc.tests.e2e.CrossBrowserTests"/>
        </classes>
    </test>
</suite>
```

2. **Thread-Safe Implementation:**
   - WebDriverManager uses ThreadLocal to store WebDriver instances
   - ExtentTestManager uses ThreadLocal for ExtentTest instances
   - Page objects don't share state between threads
   - Configuration is thread-safe through synchronized accessors

3. **Resource Management:**
   - Configurable thread count based on available resources
   - Each thread gets its own browser instance
   - Proper cleanup after test execution through @AfterMethod

### Wait Strategies

The framework implements robust wait strategies for reliable test execution:

1. **Explicit Waits:**
   - Primary wait strategy using ExpectedConditions
   - Implemented in BasePage for element interactions
   - Configurable timeouts through TimeoutConstants

```java
protected WebElement waitForElementVisible(By locator) {
    return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
}

protected WebElement waitForElementClickable(By locator) {
    return wait.until(ExpectedConditions.elementToBeClickable(locator));
}
```

2. **Custom Wait Conditions:**
   - Specialized wait conditions for application-specific scenarios
   - Implemented in WaitUtils class

```java
public static boolean waitForPageLoad(WebDriver driver) {
    try {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.PAGE_LOAD_TIMEOUT_SECONDS));
        wait.until(driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete"));
        return true;
    } catch (TimeoutException e) {
        return false;
    }
}
```

3. **Fluent Waits:**
   - Used for scenarios requiring polling with configurable intervals
   - Ignores specific exceptions during polling

```java
public static FluentWait<WebDriver> fluent(WebDriver driver) {
    return new FluentWait<>(driver)
            .withTimeout(Duration.ofSeconds(TimeoutConstants.DEFAULT_TIMEOUT_SECONDS))
            .pollingEvery(Duration.ofMillis(TimeoutConstants.POLLING_INTERVAL_MILLIS))
            .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
}
```

### Retry Mechanism

For handling flaky tests, the framework implements a retry mechanism:

```java
public class RetryAnalyzer implements IRetryAnalyzer {
    
    private int retryCount = 0;
    
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < TimeoutConstants.MAX_RETRY_ATTEMPTS) {
            retryCount++;
            LogUtils.info("Retrying test: " + result.getName() + " - Retry attempt: " + retryCount);
            return true;
        }
        return false;
    }
}

@Test(retryAnalyzer = RetryAnalyzer.class)
public void testSignupFlow() {
    // Test implementation
}
```

### Exception Handling

The framework implements a robust exception handling strategy:

1. **Custom Exceptions:**
   - FrameworkException as the base class
   - ConfigurationException for configuration issues

2. **Try-Catch Patterns:**
   - Page objects catch and handle Selenium exceptions
   - Meaningful error messages with context
   - Screenshots captured on exceptions

3. **Centralized Logging:**
   - All exceptions logged with appropriate level
   - Stack traces captured for troubleshooting
   - Test context included in logs

## 5. CI/CD Integration

### CI/CD Pipeline Configuration

The Selenium automation framework is integrated with CI/CD pipelines to enable automated test execution on code changes, scheduled runs, and on-demand testing.

#### Jenkins Pipeline Configuration

```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8.6'
        jdk 'JDK 11'
    }
    
    parameters {
        choice(name: 'BROWSER', choices: ['chrome', 'firefox', 'edge'], description: 'Browser to run tests against')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run in headless mode')
        string(name: 'TEST_GROUPS', defaultValue: 'signup', description: 'TestNG groups to run')
        string(name: 'THREAD_COUNT', defaultValue: '3', description: 'Parallel execution thread count')
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }
        
        stage('Unit Tests') {
            steps {
                sh 'mvn test -Dgroups="unit"'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Integration Tests') {
            steps {
                sh 'mvn test -Dgroups="integration"'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('End-to-End Tests') {
            steps {
                sh 'mvn test -Dgroups="${params.TEST_GROUPS}" -Dbrowser="${params.BROWSER}" -Dheadless="${params.HEADLESS}" -Dthreadcount="${params.THREAD_COUNT}"'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/extent-reports',
                        reportFiles: 'index.html',
                        reportName: 'Selenium Test Report'
                    ])
                }
            }
        }
    }
    
    post {
        always {
            archiveArtifacts artifacts: 'target/screenshots/**/*.png', allowEmptyArchive: true
            cleanWs()
        }
        failure {
            emailext (
                subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                body: "Tests failed. Check the report at: ${env.BUILD_URL}",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
            slackSend channel: '#qa-alerts', 
                color: 'danger', 
                message: "Test Execution Failed: ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
        }
        success {
            slackSend channel: '#qa-alerts', 
                color: 'good', 
                message: "Test Execution Successful: ${env.JOB_NAME} ${env.BUILD_NUMBER} (<${env.BUILD_URL}|Open>)"
        }
    }
}
```

#### GitHub Actions Workflow

```yaml
name: Selenium Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 0 * * *'  # Daily at midnight
  workflow_dispatch:
    inputs:
      browser:
        description: 'Browser to test'
        required: true
        default: 'chrome'
        type: choice
        options:
        - chrome
        - firefox
        - edge
      headless:
        description: 'Run in headless mode'
        required: true
        default: true
        type: boolean

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
    
    - name: Build with Maven
      run: mvn -B clean compile
    
    - name: Run Unit Tests
      run: mvn -B test -Dgroups="unit"
    
    - name: Run Integration Tests  
      run: mvn -B test -Dgroups="integration"
    
    - name: Run End-to-End Tests
      run: |
        mvn -B test -Dgroups="signup" \
          -Dbrowser=${{ github.event.inputs.browser || 'chrome' }} \
          -Dheadless=${{ github.event.inputs.headless || 'true' }}
    
    - name: Upload Test Reports
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-reports
        path: |
          target/extent-reports/
          target/surefire-reports/
          target/screenshots/
    
    - name: Publish Test Results
      uses: EnricoMi/publish-unit-test-result-action@v2
      if: always()
      with:
        files: "**/target/surefire-reports/*.xml"
    
    - name: Notify on Failure
      if: failure()
      uses: rtCamp/action-slack-notify@v2
      env:
        SLACK_WEBHOOK: ${{ secrets.SLACK_WEBHOOK }}
        SLACK_COLOR: 'danger'
        SLACK_TITLE: 'Test Execution Failed'
        SLACK_MESSAGE: 'Selenium tests failed. Check the report.'
```

### Trigger Mechanisms

The framework supports various trigger mechanisms for test execution:

1. **Code Changes:**
   - Tests execute automatically when code is pushed to main branches
   - Tests run as part of pull request validation

2. **Scheduled Execution:**
   - Daily regression tests at specified times
   - Weekend comprehensive test runs

3. **On-Demand Execution:**
   - Manual triggers with customizable parameters
   - Support for targeted feature testing

4. **Release Validation:**
   - Pre-release validation runs with extended coverage
   - Release candidate validation with specific configurations

### Parameterized Execution

Both CI/CD configurations support parameterized test execution:

| Parameter | Description | Examples |
|-----------|-------------|----------|
| `browser` | Target browser for testing | chrome, firefox, edge |
| `headless` | Whether to run in headless mode | true, false |
| `environment` | Target environment | staging, dev |
| `groups` | TestNG groups to run | signup, regression, smoke |
| `threadCount` | Parallel execution threads | 1-5 |

Example command-line execution:

```bash
mvn test -Dgroups="signup" -Dbrowser="chrome" -Dheadless="true" -Denv="staging"
```

### Reporting Integration

Test results are integrated with CI/CD systems for visibility and tracking:

1. **Jenkins Integration:**
   - JUnit XML reports for test results tracking
   - HTML reports published as build artifacts
   - Historical trend analysis
   - Email notifications on failures
   - Slack notifications for test status

2. **GitHub Actions Integration:**
   - XML report publishing for GitHub UI
   - Artifact storage for HTML reports and screenshots
   - Pull request status updates
   - Workflow dispatch for on-demand execution

## 6. Test Reporting

### ExtentReports Integration

The framework uses ExtentReports for comprehensive test reporting with rich visualization and detailed test information.

#### Report Configuration

The ExtentReports integration is configured through the ExtentManager and ExtentTestManager classes:

```java
public class ExtentManager {
    private static ExtentReports extent;
    
    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/extent-reports/index.html");
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setDocumentTitle("Storydoc Signup Test Report");
            sparkReporter.config().setReportName("Selenium Test Automation Results");
            sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
            
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            
            // Set system info
            extent.setSystemInfo("OS", System.getProperty("os.name"));
            extent.setSystemInfo("Browser", ConfigurationManager.getInstance().getBrowser().toString());
            extent.setSystemInfo("Environment", ConfigurationManager.getInstance().getEnvironment().toString());
        }
        return extent;
    }
}
```

```java
public class ExtentTestManager {
    private static final ThreadLocal<ExtentTest> extentTestMap = new ThreadLocal<>();
    
    public static synchronized ExtentTest getTest() {
        return extentTestMap.get();
    }
    
    public static synchronized ExtentTest startTest(String testName) {
        ExtentTest test = ExtentManager.getInstance().createTest(testName);
        extentTestMap.set(test);
        return test;
    }
    
    public static synchronized void endTest() {
        ExtentManager.getInstance().flush();
        extentTestMap.remove();
    }
    
    public static synchronized void logInfo(String message) {
        getTest().log(Status.INFO, message);
    }
    
    public static synchronized void logPass(String message) {
        getTest().log(Status.PASS, message);
    }
    
    public static synchronized void logFail(String message) {
        getTest().log(Status.FAIL, message);
    }
    
    public static synchronized void logScreenshot(String path, String title) {
        getTest().addScreenCaptureFromPath(path, title);
    }
}
```

#### Report Structure

The generated HTML reports include:

1. **Dashboard Page:**
   - Summary of test execution
   - Pass/fail/skip count
   - Execution time statistics
   - Environment information
   - Browser details

2. **Test Details Page:**
   - Step-by-step test execution logs
   - Timestamps for each step
   - Status of each step (pass/fail/info)
   - Screenshots for failed steps
   - Exception details for failures

3. **Categories and Tags:**
   - Tests grouped by feature
   - Tags for test types (smoke, regression)
   - Filtering capability

### Screenshot Capture Strategy

Screenshots are captured at key points during test execution:

1. **Automatic Capture on Failure:**
   - The TestBase class captures screenshots when tests fail
   - Screenshots are embedded in the ExtentReports

```java
@AfterMethod
public void tearDown(ITestResult result) {
    if (result.getStatus() == ITestResult.FAILURE) {
        String screenshotPath = ScreenshotUtils.captureScreenshot(result.getName());
        ExtentTestManager.logScreenshot(screenshotPath, "Failure Screenshot");
        ExtentTestManager.logFail(result.getThrowable().getMessage());
    }
}
```

2. **Manual Capture at Key Points:**
   - Tests can explicitly capture screenshots at important steps
   - Used for documentation of critical UI states

```java
public void takeScreenshot(String description) {
    String screenshotPath = ScreenshotUtils.captureScreenshot(description);
    ExtentTestManager.logScreenshot(screenshotPath, description);
}
```

### Logging Integration

The framework integrates logging with test reporting:

1. **Log4j2 Configuration:**
   - Console output for immediate feedback
   - File logging for permanent record
   - Different log levels for granular control

2. **Log Correlation with Tests:**
   - Each log entry contains test name and timestamp
   - Logs linked to test execution in reports

3. **Log Levels Usage:**
   - ERROR: Test failures and exceptions
   - WARN: Potential issues that don't fail tests
   - INFO: Test steps and significant actions
   - DEBUG: Detailed information for troubleshooting

### Failure Analysis and Reporting

The reporting system provides comprehensive information for failure analysis:

1. **Exception Details:**
   - Full stack trace of exceptions
   - Context of failure (test step, page, action)
   - Screenshots of UI state at failure point

2. **Categorization of Failures:**
   - Application defects
   - Environment issues
   - Test automation issues
   - Data-related failures

3. **Trend Analysis:**
   - Historical pass/fail trends
   - Recurring failures identification
   - Flaky test detection

## 7. Test Data Management

### Data Generation Approach

The framework uses a dynamic data generation approach to create test data on-demand, ensuring tests have fresh data for each execution.

#### RandomDataGenerator Utility

The core component of this approach is the RandomDataGenerator utility class:

```java
public final class RandomDataGenerator {
    
    /**
     * Generates a random email address using timestamp to ensure uniqueness
     * 
     * @return A unique email address for test use
     */
    public static String generateRandomEmail() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String prefix = "test";
        return prefix + "_" + timestamp + "@example.com";
    }
    
    /**
     * Generates a random password that meets the application's requirements
     * 
     * @return A password that meets complexity requirements
     */
    public static String generateRandomPassword() {
        // Generate a random string with a mix of uppercase, lowercase, digits, and special characters
        String upperCaseLetters = RandomStringUtils.random(1, 'A', 'Z', false, false);
        String lowerCaseLetters = RandomStringUtils.random(1, 'a', 'z', false, false);
        String numbers = RandomStringUtils.randomNumeric(1);
        String specialChar = RandomStringUtils.random(1, SPECIAL_CHARS);
        
        // Create the remaining part of the password
        String remainingChars = RandomStringUtils.random(DEFAULT_PASSWORD_LENGTH - 4, 
                ALPHA_NUMERIC_STRING + SPECIAL_CHARS);
        
        // Combine all parts and shuffle
        String combinedPassword = upperCaseLetters + lowerCaseLetters + numbers + specialChar + remainingChars;
        
        // Shuffle the password characters
        char[] pwdChars = combinedPassword.toCharArray();
        for (int i = 0; i < pwdChars.length; i++) {
            int j = random.nextInt(pwdChars.length);
            char temp = pwdChars[i];
            pwdChars[i] = pwdChars[j];
            pwdChars[j] = temp;
        }
        
        return new String(pwdChars);
    }
    
    // Other data generation methods...
}
```

#### UserAccount Model

Test data is encapsulated in the UserAccount model:

```java
public class UserAccount {
    private String email;
    private String password;
    private boolean termsAccepted;
    
    public UserAccount(String email, String password, boolean termsAccepted) {
        // Constructor implementation
    }
    
    public static UserAccount createValidAccount() {
        String email = RandomDataGenerator.generateRandomEmail();
        String password = RandomDataGenerator.generateRandomPassword();
        return new UserAccount(email, password, true);
    }
    
    public static UserAccount createCustomAccount(String email, String password) {
        return new UserAccount(email, password, true);
    }
    
    // Getters, setters, and builder pattern implementation...
}
```

### Test Data Types

The framework utilizes different types of test data:

1. **Dynamic Test Data:**
   - Generated at runtime for each test execution
   - Ensures test independence and repeatability
   - Examples: Email addresses, passwords, names

2. **Static Test Data:**
   - Predefined data for specific test scenarios
   - Maintained in constants or property files
   - Examples: Error messages, validation rules

3. **Environmental Data:**
   - Specific to test environments
   - Loaded from configuration files
   - Examples: URLs, timeouts, feature toggles

### Data Organization

Test data is organized according to its usage:

1. **Test Class Level:**
   - Data specific to a test class
   - Defined as constants or through data providers

```java
@DataProvider(name = "validUserAccounts")
public Object[][] provideValidUserAccounts() {
    return new Object[][] {
        {UserAccount.createValidAccount()},
        {UserAccount.createCustomAccount("test1@example.com", "Valid@Pass1")},
        {UserAccount.createCustomAccount("test2@example.com", "Valid@Pass2")}
    };
}
```

2. **Test Method Level:**
   - Data specific to a test method
   - Generated or constructed inside the test method

```java
@Test
public void testSignupWithRandomData() {
    String email = RandomDataGenerator.generateRandomEmail();
    String password = RandomDataGenerator.generateRandomPassword();
    
    // Use data in test...
}
```

3. **Framework Level:**
   - Common data used across tests
   - Defined in utility classes or constants

```java
public class TestConstants {
    public static final String VALID_PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    public static final String EMAIL_DOMAIN = "example.com";
    // Other constants...
}
```

### Test Data Management Best Practices

1. **Data Independence:**
   - Each test generates or obtains its own data
   - No shared state between tests
   - No dependencies on data created by other tests

2. **Realistic Data:**
   - Test data mimics real user data
   - Valid format and structure
   - Meets application validation rules

3. **Edge Cases Coverage:**
   - Data includes boundary values
   - Special character handling
   - Different formats and patterns

4. **Data Volume Control:**
   - Limited data creation to necessary elements
   - Cleanup of created data when possible
   - Monitoring of test data volume

### Data Cleanup Strategy

1. **User Account Cleanup:**
   - Tests clean up created accounts when possible
   - Scheduled cleanup jobs for test accounts
   - Isolation through unique email patterns

2. **Test Data Identification:**
   - Test data follows specific patterns
   - Email prefix "test_" for identification
   - Timestamp inclusion for tracking

3. **Environment Reset:**
   - Regular reset of test environments
   - Scheduled data purge for staging
   - Backup before major test runs

## 8. Cross-Browser Testing Strategy

### Supported Browsers

The framework provides support for multiple browsers to ensure cross-browser compatibility:

| Browser | Version Support | Priority | Driver |
|---------|-----------------|----------|--------|
| Chrome | Latest 2 versions | Primary | ChromeDriver |
| Firefox | Latest 2 versions | Secondary | GeckoDriver |
| Edge | Latest version | Tertiary | EdgeDriver |

Browser-specific configuration is managed through dedicated configuration classes:

```java
public class ChromeConfig {
    public static ChromeOptions getOptions() {
        ChromeOptions options = new ChromeOptions();
        
        if (ConfigurationManager.getInstance().isHeadless()) {
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        
        // Common configurations
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        
        // Additional configurations...
        
        return options;
    }
}
```

Similar configuration classes exist for Firefox and Edge.

### Browser Driver Management

The framework uses WebDriverManager for automatic driver management:

```java
public class DriverFactory {
    public static WebDriver createDriver() {
        BrowserType browserType = ConfigurationManager.getInstance().getBrowser();
        WebDriver driver;
        
        switch (browserType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(ChromeConfig.getOptions());
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(FirefoxConfig.getOptions());
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(EdgeConfig.getOptions());
                break;
            default:
                throw new FrameworkException("Browser not supported: " + browserType);
        }
        
        // Configure common timeouts and settings
        
        return driver;
    }
}
```

This approach offers several benefits:
- Automatic download and management of browser drivers
- Version compatibility handling
- Cross-platform support
- No manual driver installation required

### Testing Priorities by Browser

The framework implements a tiered approach to cross-browser testing:

1. **Tier 1 (Primary Browser - Chrome):**
   - All test cases executed
   - Both headless and headed modes
   - Primary focus for development and debugging
   - Full coverage of features and edge cases

2. **Tier 2 (Secondary Browser - Firefox):**
   - Critical path test cases
   - Mostly headless execution in CI/CD
   - Core functionality validation
   - Representative sample of edge cases

3. **Tier 3 (Tertiary Browser - Edge):**
   - Smoke test cases only
   - Always headless in CI/CD
   - Basic functionality verification
   - Limited edge case coverage

### Test Execution Strategy

For CI/CD and comprehensive testing, parallel execution across browsers is used:

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Cross-Browser Test Suite" parallel="tests" thread-count="3">
    <test name="Chrome Tests">
        <parameter name="browser" value="chrome"/>
        <classes>
            <class name="com.storydoc.tests.SignupTests"/>
        </classes>
    </test>
    <test name="Firefox Tests">
        <parameter name="browser" value="firefox"/>
        <classes>
            <class name="com.storydoc.tests.SignupTests"/>
        </classes>
    </test>
    <test name="Edge Tests">
        <parameter name="browser" value="edge"/>
        <classes>
            <class name="com.storydoc.tests.SignupTests"/>
        </classes>
    </test>
</suite>
```

Test classes accept the browser parameter:

```java
@Parameters({"browser"})
@BeforeMethod
public void setUp(String browser) {
    System.setProperty("browser", browser);
    driver = WebDriverManager.getDriver();
    signupPage = new SignupPage(driver);
}
```

### Headless vs. Headed Mode

The framework supports both headless and headed browser modes:

```java
// Configuration in properties file
headless=true

// Usage in browser configurations
if (ConfigurationManager.getInstance().isHeadless()) {
    options.addArguments("--headless");
    options.addArguments("--window-size=1920,1080");
}
```

1. **Headless Mode:**
   - Used primarily in CI/CD environments
   - Faster execution and lower resource usage
   - No visible browser window
   - Supports all test scenarios except visual verification

2. **Headed Mode:**
   - Used primarily in local development
   - Allows visual observation of test execution
   - Useful for debugging and development
   - Required for certain types of visual testing

### Cross-Browser Issue Handling

When cross-browser issues are identified, the framework provides mechanisms for browser-specific handling:

```java
protected void clickElement(By locator) {
    try {
        BrowserType browserType = ConfigurationManager.getInstance().getBrowser();
        
        switch (browserType) {
            case FIREFOX:
                // Firefox-specific click handling
                WebElement element = driver.findElement(locator);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].scrollIntoView(true);", element);
                element.click();
                break;
                
            default:
                // Standard click for other browsers
                driver.findElement(locator).click();
                break;
        }
    } catch (Exception e) {
        // Fallback to JavaScript click
        WebElement element = driver.findElement(locator);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", element);
    }
}
```

## 9. Failure Management and Analysis

### Retry Mechanisms

The framework implements robust retry mechanisms to handle transient failures that might occur during test execution.

#### Test-Level Retry with TestNG

For handling flaky tests at the test method level, the framework uses TestNG's IRetryAnalyzer:

```java
public class RetryAnalyzer implements IRetryAnalyzer {
    
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int MAX_RETRY_COUNT = 2;
    
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY_COUNT) {
            retryCount++;
            logger.warn("Retrying test: {} - Attempt: {}", result.getName(), retryCount);
            return true;
        }
        return false;
    }
}
```

To apply the retry analyzer to a test:

```java
@Test(retryAnalyzer = RetryAnalyzer.class)
public void testSignupFlow() {
    // Test implementation
}
```

#### Element-Level Retry in BasePage

For handling transient element interaction issues:

```java
protected void safeClick(By locator) {
    int attempts = 0;
    while (attempts < 3) {
        try {
            WebElement element = waitForElementClickable(locator);
            element.click();
            return;
        } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
            attempts++;
            logger.warn("Click attempt {} failed for element: {}", attempts, locator);
            if (attempts == 3) {
                logger.error("Failed to click after 3 attempts: {}", locator);
                throw e;
            }
            try {
                Thread.sleep(TimeoutConstants.RETRY_INTERVAL_MILLIS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

### Failure Categorization

The framework categorizes failures to facilitate analysis and resolution:

1. **Application Defects:**
   - Incorrect behavior in the application
   - Missing or incorrect UI elements
   - Validation errors
   - Example: Success message not displayed after signup

2. **Environment Issues:**
   - Network connectivity problems
   - Environment unavailability
   - Database issues
   - Example: Staging environment down or unreachable

3. **Test Automation Issues:**
   - Locator changes causing element not found
   - Timing issues in test execution
   - Browser compatibility issues
   - Example: Element locator no longer valid after UI update

4. **Data-Related Failures:**
   - Invalid test data
   - Data dependency failures
   - Example: Test using an email that was already registered

### Root Cause Analysis

For recurring or complex failures, the framework supports detailed root cause analysis:

1. **Failure Pattern Recognition:**
   - Track failure frequency and conditions
   - Identify common factors in failures
   - Look for timing, browser, or environment correlations

2. **Systematic Investigation:**
   - Controlled test execution with specific conditions
   - Instrumentation for detailed behavior logging
   - Step-by-step validation of assumptions

3. **RCA Documentation:**
   - Document findings in standardized format
   - Share lessons learned with the team
   - Update test documentation with known issues

### Decision Trees for Common Failures

The framework implements decision trees for common failure types to standardize analysis:

1. **Element Not Found Exceptions:**

```
Element Not Found Exception
|
├── Is locator correct? (Check locator definition)
|   ├── No → Update locator
|   └── Yes → Continue
|
├── Is element in the DOM? (Inspect page source)
|   ├── No → Check if navigation successful
|   |   ├── No → Fix navigation issue
|   |   └── Yes → Check if element is dynamically loaded
|   |       ├── Yes → Add wait condition
|   |       └── No → Report application bug (missing element)
|   └── Yes → Continue
|
├── Is timing issue? (Add explicit wait and retry)
|   ├── Yes → Add appropriate wait strategy
|   └── No → Continue
|
└── Is element in different frame/window?
    ├── Yes → Add frame/window handling
    └── No → Deeper investigation required
```

2. **Click Interception Exceptions:**

```
Element Click Intercepted Exception
|
├── Is element visible in viewport? (Check screenshot)
|   ├── No → Add scrollIntoView before click
|   └── Yes → Continue
|
├── Is element covered by another element? (Check z-index)
|   ├── Yes → Identify covering element
|   |   ├── Is it a loading indicator? → Add wait for it to disappear
|   |   ├── Is it a popup/overlay? → Add code to dismiss it first
|   |   └── Other element → Try JavaScript click as alternative
|   └── No → Continue
|
├── Is element state changing during interaction?
|   ├── Yes → Add stabilization wait before click
|   └── No → Continue
|
└── Try alternative click method (JavaScript click)
```

### Flaky Test Management

The framework implements a structured approach to managing flaky tests:

1. **Identification:**
   - Track test results over time
   - Calculate flakiness ratio (failures/total runs)
   - Flag tests exceeding flakiness threshold (e.g., >5%)

2. **Quarantine Process:**
   - Move highly flaky tests to a quarantined group
   - Continue to execute but don't block builds
   - Prioritize for investigation and stabilization

```xml
<test name="Quarantined Tests">
    <groups>
        <run>
            <include name="quarantined"/>
        </run>
    </groups>
    <classes>
        <class name="com.storydoc.tests.FlakyTests"/>
    </classes>
</test>
```

3. **Stabilization:**
   - Add diagnostic logging
   - Review and enhance wait strategies
   - Improve synchronization with application state
   - Consider test refactoring or splitting

## 10. Test Environment Management

### Environment Types

The framework supports multiple environment types for different testing purposes:

| Environment | Purpose | Configuration | URL |
|-------------|---------|--------------|-----|
| Local | Development and debugging | local.properties | http://localhost:port |
| Dev | Integration testing | dev.properties | https://editor-dev.storydoc.com |
| Staging | End-to-end testing | staging.properties | https://editor-staging.storydoc.com |

### Environment Configuration

Environment-specific configuration is managed through property files:

```properties
# staging-config.properties
browser=chrome
headless=true
base.url=https://editor-staging.storydoc.com
signup.url=https://editor-staging.storydoc.com/sign-up
timeout.seconds=10
page.load.timeout=30
screenshot.path=./test-output/screenshots
report.path=./test-output/reports
```

The environment selection is handled through system properties:

```java
String env = System.getProperty("env", "staging");
InputStream input = getClass().getClassLoader().getResourceAsStream(env + "-config.properties");
properties.load(input);
```

### Local Development Setup

For local development and testing, the framework requires:

1. **Development Environment:**
   - JDK 11 or higher
   - Maven 3.8+ or Gradle
   - IDE (IntelliJ IDEA, Eclipse, or VS Code)
   - Git for version control

2. **Browser Requirements:**
   - Chrome (latest version)
   - Firefox (latest version)
   - Edge (optional, latest version)

3. **Setup Steps:**
   ```bash
   # Clone the repository
   git clone https://github.com/organization/storydoc-selenium.git
   
   # Navigate to project directory
   cd storydoc-selenium
   
   # Build the project
   mvn clean compile
   
   # Run tests locally
   mvn test -Denv=dev -Dbrowser=chrome -Dheadless=false
   ```

### CI/CD Environment

For CI/CD integration, the environment requires:

1. **Jenkins Agent Setup:**
   - JDK 11 installation
   - Maven 3.8+ installation
   - Chrome, Firefox, and Edge installations
   - Display server for headed tests (Xvfb for Linux)

2. **GitHub Actions Runner:**
   - Ubuntu-latest runner
   - Browser installations through setup actions
   - JDK and Maven through standard actions

3. **Environment Variables:**
   - `BROWSER`: Default browser for testing
   - `HEADLESS`: Whether to run in headless mode
   - `TEST_ENV`: Target environment (staging, dev)
   - `THREAD_COUNT`: Number of parallel threads

### Browser Setup

The framework handles browser setup automatically:

1. **WebDriverManager Integration:**
   - Automatic download of appropriate driver binaries
   - Version compatibility management
   - Cross-platform support

```java
public static WebDriver initDriver() {
    WebDriver driver;
    String browser = ConfigurationManager.getInstance().getBrowser().toLowerCase();
    
    switch (browser) {
        case "chrome":
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(ChromeConfig.getOptions());
            break;
        case "firefox":
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver(FirefoxConfig.getOptions());
            break;
        case "edge":
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver(EdgeConfig.getOptions());
            break;
        default:
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(ChromeConfig.getOptions());
    }
    
    driver.manage().window().maximize();
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    
    return driver;
}
```

## 11. Performance Testing Considerations

While the primary focus of the Selenium automation framework is functional testing, it incorporates several performance testing considerations to ensure efficient test execution and monitor application responsiveness.

### Page Load Time Measurement

The framework measures and logs page load times to identify performance issues:

```java
public class PerformanceMonitor {
    
    private static final Logger logger = LogManager.getLogger(PerformanceMonitor.class);
    
    public static long measurePageLoad(WebDriver driver, String url) {
        long startTime = System.currentTimeMillis();
        
        driver.get(url);
        
        // Wait for document.readyState to be complete
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TimeoutConstants.PAGE_LOAD_TIMEOUT_SECONDS));
        wait.until(driver1 -> ((JavascriptExecutor) driver1).executeScript("return document.readyState").equals("complete"));
        
        long endTime = System.currentTimeMillis();
        long loadTime = endTime - startTime;
        
        logger.info("Page load time for {}: {} ms", url, loadTime);
        
        return loadTime;
    }
}
```

### Navigation Timing API Integration

For more detailed performance metrics, the framework can leverage the Navigation Timing API:

```java
public static Map<String, Long> getNavigationTimings(WebDriver driver) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    
    Map<String, Long> timings = new HashMap<>();
    
    try {
        // Get timing data from browser
        Object perfData = js.executeScript(
            "var performance = window.performance || window.mozPerformance || window.msPerformance || window.webkitPerformance || {}; " +
            "var timing = performance.timing || {}; " +
            "return timing;");
        
        if (perfData instanceof Map) {
            Map<String, Object> timingData = (Map<String, Object>) perfData;
            
            // Calculate key metrics
            Long navigationStart = (Long) timingData.get("navigationStart");
            Long responseEnd = (Long) timingData.get("responseEnd");
            Long domComplete = (Long) timingData.get("domComplete");
            Long loadEventEnd = (Long) timingData.get("loadEventEnd");
            
            // Store calculated metrics
            if (navigationStart != null && responseEnd != null) {
                timings.put("serverResponseTime", responseEnd - navigationStart);
            }
            
            if (navigationStart != null && domComplete != null) {
                timings.put("domProcessingTime", domComplete - navigationStart);
            }
            
            if (navigationStart != null && loadEventEnd != null) {
                timings.put("pageLoadTime", loadEventEnd - navigationStart);
            }
        }
    } catch (Exception e) {
        logger.warn("Failed to collect navigation timing metrics", e);
    }
    
    return timings;
}
```

### Test Execution Performance

The framework monitors and reports on test execution performance:

1. **Test Duration Tracking:**
   - Each test method's execution time is recorded
   - Long-running tests are flagged for optimization
   - Historical execution time trends are analyzed

```java
@Listeners(TestExecutionTimeListener.class)
public class SignupTests extends TestBase {
    // Test methods
}

public class TestExecutionTimeListener implements ITestListener {
    
    private static final Logger logger = LogManager.getLogger(TestExecutionTimeListener.class);
    private static final ThreadLocal<Long> testStartTime = new ThreadLocal<>();
    
    @Override
    public void onTestStart(ITestResult result) {
        testStartTime.set(System.currentTimeMillis());
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = System.currentTimeMillis() - testStartTime.get();
        logger.info("Test '{}' completed in {} ms", result.getName(), duration);
        
        // Log slow tests for optimization
        if (duration > 10000) {
            logger.warn("Slow test detected: '{}' took {} ms", result.getName(), duration);
        }
    }
}
```

### Performance Thresholds

The framework defines performance thresholds to identify potential issues:

| Metric | Warning Threshold | Critical Threshold | Action |
|--------|-------------------|-------------------|--------|
| Page Load Time | > 3 seconds | > 5 seconds | Log warning, investigate if persistent |
| Test Execution Time | > 30 seconds | > 60 seconds | Flag for optimization |
| Suite Execution Time | > 10 minutes | > 15 minutes | Review parallelization options |
| DOM Element Count | > 1000 elements | > 2000 elements | Check for memory-intensive pages |
| Memory Usage | > 1 GB | > 1.5 GB | Investigate memory leaks |

### Performance Optimization Strategies

The framework implements several strategies to optimize test execution performance:

1. **Page Object Optimization:**
   - Efficient locator strategies (prefer IDs and data-testids)
   - Minimal DOM traversal
   - Smart element caching when appropriate

2. **Wait Strategy Optimization:**
   - Specific wait conditions instead of generic waits
   - Appropriate timeout values based on operation type
   - Fluent waits with polling for complex scenarios

3. **Browser Optimization:**
   - Headless mode for CI/CD execution
   - Minimal browser extensions and plugins
   - Memory management through regular browser restarts

## 12. Quality Metrics and Reporting

### Key Quality Metrics

The framework tracks several key metrics to evaluate test quality and effectiveness:

#### Test Success Rate

| Metric | Description | Target | Calculation | Reporting Frequency |
|--------|-------------|--------|-------------|-------------------|
| Overall Pass Rate | Percentage of all tests that pass | >95% | (Passed Tests / Total Tests) * 100 | Every run + Daily |
| Critical Path Pass Rate | Pass rate for critical functionality tests | >98% | (Passed Critical Tests / Total Critical Tests) * 100 | Every run |
| Pass Rate by Browser | Pass rate broken down by browser | >90% for all browsers | (Passed Tests per Browser / Total Tests per Browser) * 100 | Daily |

#### Test Stability Metrics

| Metric | Description | Target | Calculation | Reporting Frequency |
|--------|-------------|--------|-------------|-------------------|
| Flakiness Rate | Percentage of tests that show inconsistent results | <5% | (Flaky Tests / Total Tests) * 100 | Weekly |
| Retry Success Rate | Percentage of retried tests that pass on retry | >70% | (Successful Retries / Total Retries) * 100 | Weekly |
| Failure Distribution | Categorized breakdown of failure causes | N/A | Count by failure category | Weekly |

#### Test Coverage Metrics

| Metric | Description | Target | Calculation | Reporting Frequency |
|--------|-------------|--------|-------------|-------------------|
| Feature Coverage | Percentage of features with automated tests | 100% for signup flow | (Features with Tests / Total Features) * 100 | Monthly |
| Scenario Coverage | Percentage of identified scenarios with tests | >90% | (Scenarios with Tests / Total Scenarios) * 100 | Monthly |
| Browser Coverage | Percentage of supported browsers being tested | 100% | (Tested Browsers / Supported Browsers) * 100 | Weekly |

#### Execution Metrics

| Metric | Description | Target | Calculation | Reporting Frequency |
|--------|-------------|--------|-------------|-------------------|
| Average Test Duration | Average execution time per test | <30 seconds | Sum of all test durations / Number of tests | Daily |
| Suite Execution Time | Total time to execute entire test suite | <15 minutes | End time - Start time | Every run |
| Test Execution Frequency | How often tests are executed | At least daily | Count of test runs per time period | Weekly |

### Quality Gates

The framework establishes quality gates that must be met for releases:

| Gate | Criteria | Action if Not Met |
|------|----------|------------------|
| Build Promotion | >95% pass rate on all tests | Block promotion until fixed |
| Release Readiness | >98% pass rate on critical tests | Delay release or scope reduction |
| Daily Regression | <5% flakiness rate | Quarantine flaky tests |
| Performance | Execution time within 20% of baseline | Optimize slow tests before next run |

### Action Plans for Missed Targets

The framework defines specific action plans for when quality targets are missed:

1. **Low Pass Rate:**
   - Immediate investigation of failed tests
   - Root cause analysis and categorization
   - Prioritized fixes based on impact
   - Verification runs after fixes

2. **High Flakiness Rate:**
   - Identification of flaky tests
   - Moving flaky tests to quarantine group
   - Detailed logging and analysis of flaky behavior
   - Refactoring with improved synchronization

3. **Test Coverage Gaps:**
   - Gap analysis to identify missing coverage
   - Prioritization of missing test scenarios
   - Implementation plan with timeline
   - Verification of new test effectiveness

4. **Slow Execution:**
   - Performance profiling of slow tests
   - Optimization of wait strategies
   - Review of test dependencies
   - Parallelization improvements

## 13. Test Maintenance Strategy

### Keeping Tests Up-to-Date

Maintaining automated tests is crucial for ensuring their continued effectiveness. The framework includes a comprehensive strategy for test maintenance.

#### Regular Review Process

The test suite undergoes regular reviews to ensure it remains current:

1. **Weekly Triage:**
   - Review of failed tests
   - Quick fixes for simple issues
   - Prioritization of complex problems

2. **Bi-Weekly Maintenance:**
   - Deep dive into flaky tests
   - Update of outdated test approaches
   - Cleanup of unused code

3. **Monthly Health Assessment:**
   - Comprehensive review of all tests
   - Coverage analysis against application changes
   - Technical debt identification

### Handling UI Changes

The framework implements strategies for adapting to UI changes:

#### Locator Resilience

1. **Prioritize Stable Locators:**
   - Prefer data-testid attributes specifically designed for testing
   - Use IDs and unique attributes when available
   - Avoid brittle locators like text content or CSS classes that change frequently

```java
// Recommended locator strategy
public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-input']");

// Fallback if data-testid not available
public static final By EMAIL_FIELD_FALLBACK = By.id("email");

// Last resort with multiple attributes to increase specificity
public static final By EMAIL_FIELD_COMPLEX = By.cssSelector("input[type='email'][name='email']");
```

2. **Alternative Locator Strategies:**
   - Implement fallback locators for critical elements
   - Create methods that try multiple locator strategies

```java
protected WebElement findElementWithFallback(By primaryLocator, By fallbackLocator) {
    try {
        return driver.findElement(primaryLocator);
    } catch (NoSuchElementException e) {
        logger.warn("Primary locator failed: {}. Trying fallback locator: {}", 
            primaryLocator, fallbackLocator);
        return driver.findElement(fallbackLocator);
    }
}
```

### Refactoring Strategies

The framework includes strategies for refactoring tests to improve maintainability:

#### Code Duplication Elimination

1. **Extract Common Methods:**
   - Move repeated code into utility methods
   - Create helper methods for common sequences
   - Use inheritance to share behavior

```java
// Before refactoring
@Test
public void testSignupWithEmail1() {
    driver.get(ConfigurationManager.getInstance().getSignupUrl());
    driver.findElement(SignupPageLocators.EMAIL_FIELD).sendKeys("test1@example.com");
    driver.findElement(SignupPageLocators.PASSWORD_FIELD).sendKeys("Test@1234");
    driver.findElement(SignupPageLocators.TERMS_CHECKBOX).click();
    driver.findElement(SignupPageLocators.SIGNUP_BUTTON).click();
    // Assertions...
}

@Test
public void testSignupWithEmail2() {
    driver.get(ConfigurationManager.getInstance().getSignupUrl());
    driver.findElement(SignupPageLocators.EMAIL_FIELD).sendKeys("test2@example.com");
    driver.findElement(SignupPageLocators.PASSWORD_FIELD).sendKeys("Test@1234");
    driver.findElement(SignupPageLocators.TERMS_CHECKBOX).click();
    driver.findElement(SignupPageLocators.SIGNUP_BUTTON).click();
    // Assertions...
}

// After refactoring with Page Objects and data-driven approach
@Test(dataProvider = "emails")
public void testSignupWithDifferentEmails(String email) {
    SignupPage signupPage = new SignupPage(driver);
    signupPage.navigateToSignupPage();
    
    SuccessPage successPage = signupPage.submitSignupForm(email, "Test@1234");
    
    Assert.assertTrue(successPage.isSignupSuccessful(), 
        "Signup should succeed with email: " + email);
}

@DataProvider(name = "emails")
public Object[][] provideEmails() {
    return new Object[][] {
        {"test1@example.com"},
        {"test2@example.com"}
    };
}
```

### Code Standards

The framework maintains strict code standards to ensure consistency and quality:

#### Naming Conventions

1. **Test Method Naming:**
   - Descriptive of behavior being tested
   - Format: `shouldXxxWhenYyy` or `testThatXxxHappensWhenYyy`
   - Examples: `shouldDisplaySuccessMessageWhenSignupCompletes`

2. **Page Object Method Naming:**
   - Action methods start with verbs (click, enter, select)
   - Query methods start with is/has/get (isPageLoaded, hasError, getText)
   - Examples: `enterEmail`, `isErrorDisplayed`, `getConfirmationMessage`

3. **Locator Naming:**
   - ALL_CAPS for constants
   - Element type suffix (FIELD, BUTTON, CHECKBOX)
   - Examples: `EMAIL_FIELD`, `SIGNUP_BUTTON`, `TERMS_CHECKBOX`

#### Documentation Standards

1. **Class Documentation:**
   - Purpose of the class
   - Responsibilities
   - Usage notes if applicable

2. **Method Documentation:**
   - JavaDoc for all public methods
   - Parameter and return value descriptions
   - Exception information if applicable

```java
/**
 * Submits the signup form with the provided user data.
 *
 * @param email The email address to use for registration
 * @param password The password to use for registration
 * @return The SuccessPage that appears after successful registration
 * @throws TimeoutException if the form submission takes too long
 */
public SuccessPage submitSignupForm(String email, String password) {
    // Method implementation
}
```

## 14. Best Practices

### Locator Strategies

Effective element location is critical for test stability and maintainability. The framework follows these best practices for locator strategies:

#### Preferred Locator Types

1. **Data Attributes for Testing:**
   - Custom attributes specifically for test automation are the best option
   - Example: `data-testid`, `data-automation-id`, `data-test`

```java
// Ideal locator using dedicated test attribute
public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-input']");
```

2. **ID Attributes:**
   - Unique IDs are the next best option
   - Generally stable across UI changes
   - Usually unique on the page

```java
public static final By PASSWORD_FIELD = By.id("password");
```

3. **Name Attributes:**
   - Commonly used for form elements
   - May be used as fallbacks for inputs

```java
public static final By EMAIL_FIELD_FALLBACK = By.name("email");
```

4. **CSS Selectors:**
   - More readable and typically faster than XPath
   - Use when attributes or simple selectors aren't sufficient
   - Keep as simple as possible

```java
public static final By SIGNUP_FORM = By.cssSelector("form.signup-form");
```

#### Anti-patterns to Avoid

1. **Text-Based Locators:**
   - Avoid selecting elements by text content when possible
   - Text changes frequently and may be translated
   - May fail with minor text changes

```java
// Avoid this approach when possible
By.xpath("//button[text()='Sign Up']")