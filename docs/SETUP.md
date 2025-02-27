# Storydoc Selenium Automation Framework - Setup Guide

This document provides detailed instructions for setting up the Storydoc Selenium Automation Framework on your local development environment, configuring the test environment, and executing the automated tests.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [IDE Setup](#ide-setup)
- [Framework Configuration](#framework-configuration)
- [Browser Configuration](#browser-configuration)
- [Test Execution](#test-execution)
- [CI/CD Integration](#cicd-integration)
- [Troubleshooting](#troubleshooting)

## Prerequisites

The following software components are required to run the Storydoc Selenium Automation Framework:

| Component | Version | Description |
| --- | --- | --- |
| Java Development Kit (JDK) | 11 or higher | Core programming language runtime |
| Maven | 3.8.x+ | Build automation and dependency management |
| Git | Latest | Version control system |
| Chrome Browser | Latest or Latest-1 | Primary test browser |
| Firefox Browser | Latest or Latest-1 | Secondary test browser (optional) |
| Edge Browser | Latest | Optional test browser |
| IntelliJ IDEA / Eclipse | Latest | Recommended IDEs |

### Hardware Requirements

- **CPU**: 4+ cores recommended
- **Memory**: 8GB minimum, 16GB recommended
- **Disk Space**: 1GB for framework and dependencies
- **Network**: Reliable internet connection for driver downloads and application access

## Installation

### 1. Install Java JDK 11+

Download and install Java JDK 11 or higher from [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or [OpenJDK](https://adoptopenjdk.net/).

Verify installation:
```bash
java -version
```

### 2. Install Maven

Download Maven from [Apache Maven website](https://maven.apache.org/download.cgi) and follow the [installation instructions](https://maven.apache.org/install.html).

Verify installation:
```bash
mvn -version
```

### 3. Install Git

Download and install Git from [Git website](https://git-scm.com/downloads).

Verify installation:
```bash
git --version
```

### 4. Clone the Repository

```bash
git clone https://github.com/your-organization/storydoc-selenium-framework.git
cd storydoc-selenium-framework
```

### 5. Install Project Dependencies

```bash
mvn clean install -DskipTests
```

This command downloads all the required dependencies defined in the `pom.xml` file.

## IDE Setup

### IntelliJ IDEA

1. Open IntelliJ IDEA
2. Select "Open" or "Import Project"
3. Navigate to the cloned repository and select the root folder
4. Choose "Import project from external model" and select "Maven"
5. Follow the prompts to complete the import

#### Recommended Plugins

- TestNG Integration
- Maven Integration
- Selenium UI Testing Integration

### Eclipse

1. Open Eclipse
2. Select "File > Import..."
3. Choose "Maven > Existing Maven Projects"
4. Navigate to the cloned repository
5. Select the `pom.xml` file and click "Finish"

#### Recommended Plugins

- TestNG for Eclipse
- m2e (Maven Integration for Eclipse)

## Framework Configuration

The framework uses property files to manage configuration settings. The main configuration file is located at:

```
src/main/resources/config.properties
```

### Base Configuration

The default configuration contains:

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

### Environment-Specific Configuration

For environment-specific settings, create or modify the following files:

- `src/main/resources/config-dev.properties` - Development environment settings
- `src/main/resources/config-staging.properties` - Staging environment settings
- `src/main/resources/config-prod.properties` - Production environment settings (if applicable)

To specify which environment configuration to use:

```bash
mvn test -Denv=staging
```

### Running with Different Browsers

To run tests with a specific browser:

```bash
mvn test -Dbrowser=firefox
```

Supported browser values:
- `chrome` (default)
- `firefox`
- `edge`

### Headless Mode

To run browsers in headless mode:

```bash
mvn test -Dheadless=true
```

## Browser Configuration

The framework uses WebDriverManager to handle browser driver binaries automatically. No manual driver downloads are required.

### Chrome Options

Chrome-specific configurations can be modified in:
```
src/main/java/driver/ChromeDriverFactory.java
```

### Firefox Options

Firefox-specific configurations can be modified in:
```
src/main/java/driver/FirefoxDriverFactory.java
```

### Edge Options

Edge-specific configurations can be modified in:
```
src/main/java/driver/EdgeDriverFactory.java
```

## Test Execution

### Running All Tests

To run all tests:

```bash
mvn clean test
```

### Running Specific Test Classes

To run a specific test class:

```bash
mvn clean test -Dtest=SignupTest
```

### Running Specific Test Methods

To run a specific test method:

```bash
mvn clean test -Dtest=SignupTest#testPositiveSignupFlow
```

### Running Test Groups

To run a group of tests:

```bash
mvn clean test -Dgroups=smoke
```

### Test Suites

The framework includes predefined TestNG XML suites:

- `signup-suite.xml` - Tests for signup process
- `regression-suite.xml` - Complete regression test suite
- `smoke-suite.xml` - Critical path smoke tests

To run a specific suite:

```bash
mvn clean test -DsuiteXmlFile=signup-suite.xml
```

## CI/CD Integration

### Jenkins Setup

1. Create a new Jenkins Pipeline job
2. Configure the job to use the Jenkinsfile from the repository root:

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

### GitHub Actions Setup

Create a workflow file at `.github/workflows/selenium-tests.yml`:

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
```

## Troubleshooting

### Common Issues and Solutions

#### WebDriver Initialization Failure

**Symptom**: Tests fail during WebDriver initialization.

**Solutions**:
- Ensure the browser is installed and up-to-date
- Check network connectivity for driver downloads
- Try running with `-Dwebdriver.manager.offline=true` if you're behind a strict proxy

#### Element Not Found Exceptions

**Symptom**: Tests fail with `NoSuchElementException`.

**Solutions**:
- Verify the application UI hasn't changed
- Review the locator strategies in the Locator Repository
- Increase timeout values in the configuration file

#### Test Execution Hangs

**Symptom**: Test execution seems to hang indefinitely.

**Solutions**:
- Check for alerts or unexpected dialogs blocking the execution
- Verify the application is responding properly
- Check for network issues or environment-specific problems
- Increase or adjust wait timeouts

#### Browser Crashes

**Symptom**: Browser crashes during test execution.

**Solutions**:
- Ensure sufficient system resources (memory)
- Update browser to the latest version
- Try running in headless mode
- Check for browser extensions causing conflicts

### Getting Help

If you encounter issues not covered in this troubleshooting guide:

1. Check the project's issue tracker for similar problems
2. Review the test logs and screenshots for error details
3. Contact the QA Automation team for assistance

---

## Version History

| Version | Date | Description |
| --- | --- | --- |
| 1.0 | 2023-09-15 | Initial document creation |