git clone https://github.com/your-organization/storydoc-selenium-framework.git
cd storydoc-selenium-framework
```

### 2. Install Dependencies

```bash
mvn clean install -DskipTests
```

### 3. Run the Tests

```bash
mvn test
```

### 4. View Reports

Open `test-output/extent-reports/index.html` in a browser to view the test execution report.

## Framework Architecture

The framework follows a layered architecture with a clear separation of concerns:

```
Test Layer (Test Cases)
    │
    ▼
Page Object Layer (Page Objects)
    │
    ▼
Core Framework Layer (WebDriver, Configuration, Utilities)
    │
    ▼
Integration Layer (Selenium WebDriver, Browsers)
```

### Core Components

- **BasePage** - Parent class for all page objects with common utilities
- **WebDriverManager** - Manages WebDriver initialization and configuration
- **ConfigurationManager** - Handles test configuration parameters
- **Page Objects** - Represent web pages and their behaviors
- **Locator Repository** - Centralized storage for element identifiers
- **TestBase** - Base class for all test classes with setup/teardown logic
- **Utility Classes** - Common helper functions and test data generators

For detailed architecture information, see [ARCHITECTURE.md](./ARCHITECTURE.md).

## Usage Guide

### Basic Test Execution

```bash
# Run all tests
mvn test

# Run with specific browser
mvn test -Dbrowser=firefox

# Run with specific environment
mvn test -Denv=staging

# Run in headless mode
mvn test -Dheadless=true

# Run specific test class
mvn test -Dtest=SignupTest
```

### Configuration

The framework uses property files for configuration:

- `config.properties` - Base configuration
- Environment-specific files (e.g., `staging.properties`, `dev.properties`)

Example configuration:

```properties
# Browser Configuration
browser=chrome
headless=false

# URLs
base.url=https://editor-staging.storydoc.com
signup.url=https://editor-staging.storydoc.com/sign-up

# Timeouts
timeout.seconds=10
page.load.timeout=30

# Reporting
screenshots.dir=test-output/screenshots
reports.dir=test-output/reports
```

For detailed usage instructions, see [USAGE.md](./USAGE.md).

## Creating Tests

### Page Object Example

```java
public class SignupPage extends BasePage {
    
    public SignupPage(WebDriver driver) {
        super(driver);
    }
    
    public SignupPage navigateToSignupPage() {
        driver.get(ConfigurationManager.getInstance().getProperty("signup.url"));
        return this;
    }
    
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
    
    public SuccessPage clickSignUp() {
        click(SignupPageLocators.SIGNUP_BUTTON);
        return new SuccessPage(driver);
    }
}
```

### Locator Repository Example

```java
public class SignupPageLocators {
    // Form Elements
    public static final By SIGNUP_FORM = By.cssSelector("form[data-testid='signup-form']");
    public static final By EMAIL_FIELD = By.cssSelector("input[data-testid='email-input']");
    public static final By PASSWORD_FIELD = By.cssSelector("input[data-testid='password-input']");
    public static final By TERMS_CHECKBOX = By.cssSelector("input[data-testid='terms-checkbox']");
    public static final By SIGNUP_BUTTON = By.cssSelector("button[data-testid='signup-button']");
}
```

### Test Example

```java
public class SignupTest extends TestBase {
    
    private SignupPage signupPage;
    
    @BeforeMethod
    public void setupTest() {
        signupPage = new SignupPage(driver);
    }
    
    @Test
    public void testPositiveSignupFlow() {
        // Generate test data
        String email = RandomDataGenerator.generateRandomEmail();
        String password = RandomDataGenerator.generateRandomPassword();
        
        // Execute signup process
        SuccessPage successPage = signupPage
            .navigateToSignupPage()
            .enterEmail(email)
            .enterPassword(password)
            .acceptTerms()
            .clickSignUp();
        
        // Verify successful signup
        Assert.assertTrue(successPage.isSignupSuccessful(), 
            "Signup was not successful");
        Assert.assertTrue(successPage.getConfirmationMessage().contains("successfully"), 
            "Success message not displayed correctly");
    }
}
```

For detailed implementation information, see [src/test/README.md](../src/test/README.md).

## Reporting

The framework generates comprehensive HTML reports using ExtentReports:

- **Test Status** - Pass/fail status for each test
- **Test Steps** - Detailed steps executed during tests
- **Screenshots** - Automatically captured on test failures
- **Execution Time** - Duration of each test and total execution
- **Environment Details** - Browser, OS, and test environment information

Reports are generated in the `test-output/extent-reports` directory after test execution.

## CI/CD Integration

### Jenkins Pipeline

```groovy
pipeline {
    agent any
    
    tools {
        maven 'Maven 3.8.6'
        jdk 'JDK 11'
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
        
        stage('Test') {
            steps {
                sh 'mvn test -Denv=staging -Dbrowser=chrome -Dheadless=true'
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
                        reportName: 'Test Execution Report'
                    ])
                }
            }
        }
    }
    
    post {
        failure {
            emailext (
                subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
                body: "Tests failed. Check the results at: ${env.BUILD_URL}",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
    }
}
```

### GitHub Actions Workflow

```yaml
name: Selenium Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 0 * * *'  # Daily midnight run

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
    
    - name: Run Selenium Tests
      run: mvn -B test -Denv=staging -Dbrowser=chrome -Dheadless=true
    
    - name: Publish Test Report
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-reports
        path: target/extent-reports/
    
    - name: Publish Test Results
      uses: EnricoMi/publish-unit-test-result-action@v2
      if: always()
      with:
        files: "**/target/surefire-reports/*.xml"