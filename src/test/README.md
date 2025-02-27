# Storydoc Selenium Automation Framework

## Overview

The Storydoc Selenium Automation Framework is a robust, maintainable test automation solution designed to automate testing of the Storydoc signup process. This framework implements industry best practices for UI automation to ensure reliable test execution and simplified test maintenance.

### Key Features

- **Page Object Model (POM)** design pattern for improved maintainability
- **Separate locator files** to isolate UI changes and reduce maintenance effort
- **Configurable test execution** for multiple browsers and environments
- **Comprehensive reporting** with screenshots on failures
- **Framework utilities** for common operations and improved reliability
- **CI/CD integration** ready for automated test execution

This framework focuses on automating the positive signup flow for the Storydoc application, validating that users can successfully create accounts.

## Project Structure

```
selenium-automation-framework/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── storydoc/
│   │               ├── core/
│   │               │   ├── WebDriverManager.java
│   │               │   └── ConfigurationManager.java
│   │               ├── pages/
│   │               │   ├── BasePage.java
│   │               │   ├── SignupPage.java
│   │               │   └── SuccessPage.java
│   │               ├── locators/
│   │               │   ├── SignupPageLocators.java
│   │               │   └── SuccessPageLocators.java
│   │               └── utils/
│   │                   ├── WaitUtils.java
│   │                   └── RandomDataGenerator.java
│   └── test/
│       └── java/
│           └── com/
│               └── storydoc/
│                   ├── tests/
│                   │   ├── BaseTest.java
│                   │   └── SignupTests.java
│                   └── listeners/
│                       ├── TestListener.java
│                       └── RetryAnalyzer.java
├── test-output/         # Generated test reports
├── config/
│   ├── config.properties
│   ├── staging.properties
│   └── dev.properties
├── testng.xml
├── pom.xml
└── README.md            # This file
```

## Prerequisites

Before using this framework, ensure you have the following installed:

* **Java JDK 11** or higher
* **Maven 3.8.x** or higher
* **Browsers**:
  * Chrome (latest 2 versions)
  * Firefox (latest 2 versions)
  * Edge (latest version)
* **IDE** (recommended):
  * IntelliJ IDEA
  * Eclipse
  * VS Code with Java extensions

## Installation

1. **Clone the repository**

```bash
git clone https://github.com/your-organization/selenium-automation-framework.git
cd selenium-automation-framework
```

2. **Install dependencies**

```bash
mvn clean install -DskipTests
```

3. **Verify setup**

```bash
mvn test-compile
```

## Configuration

The framework uses a configuration system that allows you to customize various aspects of the test execution.

### Main Configuration File

The primary configuration file is located at `config/config.properties`:

```properties
# Browser Configuration
browser=chrome
headless=false

# URLs
base.url=https://editor-staging.storydoc.com
signup.url=https://editor-staging.storydoc.com/sign-up

# Timeouts (in seconds)
timeout.seconds=10
page.load.timeout=30

# Reporting
screenshots.dir=test-output/screenshots
reports.dir=test-output/reports
```

### Environment-Specific Configuration

You can create environment-specific property files (e.g., `dev.properties`, `staging.properties`) that override the values in the main configuration file. Specify the environment at runtime using the `env` system property.

### Browser Configuration

The framework supports the following browsers:
- chrome
- firefox
- edge

You can specify the browser at runtime or in the configuration file. Browser drivers are managed automatically using WebDriverManager.

### Timeout Settings

Configure various timeout settings to accommodate your application's performance characteristics:

| Setting | Default | Description |
|---------|---------|-------------|
| timeout.seconds | 10 | Default explicit wait timeout in seconds |
| page.load.timeout | 30 | Maximum time to wait for page loads in seconds |
| script.timeout | 30 | Maximum time to wait for script execution in seconds |

## Test Execution

### Running Tests with Maven

To run all tests:

```bash
mvn clean test
```

To run specific test classes:

```bash
mvn clean test -Dtest=SignupTests
```

### Customizing Test Execution

You can customize the test execution using system properties:

```bash
mvn clean test -Dbrowser=firefox -Denv=staging -Dheadless=true
```

Available parameters:

| Parameter | Description | Example |
|-----------|-------------|---------|
| browser | Browser to use for testing | -Dbrowser=chrome |
| env | Environment configuration to use | -Denv=staging |
| headless | Run in headless mode | -Dheadless=true |
| suite | TestNG suite to run | -Dsuite=signup |

### Using TestNG XML Files

You can execute specific test suites defined in TestNG XML files:

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

Sample TestNG file (`testng.xml`):

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Storydoc Signup Test Suite">
    <test name="Signup Tests">
        <classes>
            <class name="com.storydoc.tests.SignupTests"/>
        </classes>
    </test>
</suite>
```

For parallel execution:

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Parallel Test Suite" parallel="classes" thread-count="3">
    <test name="Signup Tests">
        <classes>
            <class name="com.storydoc.tests.SignupTests"/>
            <!-- Additional test classes -->
        </classes>
    </test>
</suite>
```

## Test Implementation Guide

### Page Object Model Pattern

This framework implements the Page Object Model (POM) design pattern, which creates an abstraction of the page UI with a dedicated class for each page. This approach:

- Separates test logic from page representation
- Improves code maintainability
- Reduces code duplication
- Makes tests more readable

### Creating a New Page Object

1. Create a locator file for the page elements:

```java
public class NewPageLocators {
    public static final By HEADER = By.cssSelector("h1.header");
    public static final By SUBMIT_BUTTON = By.id("submit");
    // Add more locators as needed
}
```

2. Create a page object class extending BasePage:

```java
public class NewPage extends BasePage {
    
    public NewPage(WebDriver driver) {
        super(driver);
    }
    
    public boolean isPageLoaded() {
        return isElementPresent(NewPageLocators.HEADER);
    }
    
    public void submitForm() {
        click(NewPageLocators.SUBMIT_BUTTON);
    }
    
    // Add more page-specific methods
}
```

### Creating a New Test

1. Create a test class extending BaseTest:

```java
public class NewFeatureTest extends BaseTest {
    
    private NewPage newPage;
    
    @BeforeMethod
    public void setupTest() {
        newPage = new NewPage(driver);
        // Initialize other required pages
    }
    
    @Test
    public void testNewFeature() {
        // Arrange
        String testData = RandomDataGenerator.generateRandomData();
        
        // Act
        newPage.performAction(testData);
        
        // Assert
        Assert.assertTrue(newPage.isActionSuccessful(), 
            "Feature action was not successful");
    }
}
```

### Best Practices

1. **Page Objects**:
   - Keep page objects focused on representing a single page
   - Implement methods that represent user actions
   - Return new page objects when navigation occurs
   - Use fluent interfaces when appropriate

2. **Locators**:
   - Keep all locators in separate files
   - Use meaningful names for locators
   - Prefer stable locator strategies (IDs, data-testid attributes)
   - Document any complex locators

3. **Tests**:
   - Follow Arrange-Act-Assert pattern
   - Keep tests independent and isolated
   - Use unique test data for each test run
   - Assert meaningful outcomes

4. **Synchronization**:
   - Use explicit waits for element interactions
   - Implement custom wait conditions for complex scenarios
   - Avoid Thread.sleep() in tests

## Reporting

### Test Execution Reports

The framework generates comprehensive HTML reports using ExtentReports after test execution. Reports are saved in the `test-output/reports` directory.

### Screenshot Capture

Screenshots are automatically captured when tests fail and embedded in the HTML report. Screenshots are stored in the `test-output/screenshots` directory.

### Logs

Test execution logs are generated using Log4j and can be found in the `test-output/logs` directory. The log level can be configured in the `log4j2.xml` file.

## CI/CD Integration

### Jenkins Integration

Create a Jenkins pipeline job using the following Jenkinsfile:

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
        
        stage('Execute Tests') {
            steps {
                sh 'mvn test -Dbrowser=chrome -Dheadless=true -Denv=staging'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'test-output/reports',
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
                body: "Test execution failed. See ${env.BUILD_URL} for details.",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
    }
}
```

### GitHub Actions Integration

Create a GitHub workflow file at `.github/workflows/selenium-tests.yml`:

```yaml
name: Selenium Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 0 * * *'  # Daily run at midnight

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
      run: mvn -B test -Dbrowser=chrome -Dheadless=true -Denv=staging
    
    - name: Publish Test Report
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-reports
        path: test-output/reports/
    
    - name: Publish Test Results
      uses: EnricoMi/publish-unit-test-result-action@v2
      if: always()
      with:
        files: "**/target/surefire-reports/*.xml"
```

## Troubleshooting

### Common Issues

#### WebDriver Initialization Failures

**Symptoms**: Tests fail during WebDriver initialization with errors like "Session not created" or "Browser not found".

**Solutions**:
- Ensure the specified browser is installed
- Update WebDriverManager to the latest version
- Check browser compatibility with the WebDriver version
- For CI environments, ensure headless mode is enabled

#### Element Not Found Exceptions

**Symptoms**: Tests fail with `NoSuchElementException` or `ElementNotFoundException`.

**Solutions**:
- Verify locators are correct and unique
- Increase wait timeouts for slow-loading applications
- Check if elements are inside iframes or shadow DOM
- Verify the application state is correct before finding elements

#### Test Flakiness

**Symptoms**: Tests occasionally fail and pass without code changes.

**Solutions**:
- Implement more robust wait strategies
- Use more reliable locators
- Add retry logic for unstable tests
- Ensure test data is unique for each test run
- Review application behavior for race conditions

#### Configuration Problems

**Symptoms**: Tests use incorrect URLs or settings.

**Solutions**:
- Verify the correct environment property file is being used
- Check command-line parameters are correctly passed
- Ensure property files are included in the classpath
- Review ConfigurationManager initialization

### Getting Help

If you encounter issues not covered in this troubleshooting guide:

1. Check the detailed logs in the `test-output/logs` directory
2. Review test reports and screenshots for failure points
3. Consult the Selenium WebDriver documentation
4. Search for similar issues in the project's issue tracker
5. Contact the framework maintainers for support

## Contributing

We welcome contributions to improve the framework. Please follow these steps:

1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Add or update tests as needed
5. Update documentation
6. Submit a pull request

All code should follow the project's coding standards and include appropriate tests.