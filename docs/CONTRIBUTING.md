# Contributing to Storydoc Selenium Automation Framework

Thank you for your interest in contributing to the Storydoc Selenium Automation Framework! This document provides guidelines and instructions for contributing to this project.

## Table of Contents
- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Coding Standards](#coding-standards)
- [Testing Guidelines](#testing-guidelines)
- [Submitting Changes](#submitting-changes)
- [Issue Reporting](#issue-reporting)
- [Documentation](#documentation)
- [Code Review Process](#code-review-process)
- [Project Structure](#project-structure)

## Code of Conduct

This project adheres to a Code of Conduct that establishes expected behavior for all contributors. By participating, you are expected to uphold this code.

In summary:
- Use welcoming and inclusive language
- Be respectful of different viewpoints and experiences
- Gracefully accept constructive criticism
- Focus on what is best for the community
- Show empathy towards other community members

## Getting Started

To get started with contributing to the Storydoc Selenium Automation Framework, follow these steps:

1. **Fork the repository** to your GitHub account
2. **Clone your fork** to your local development environment:
   ```bash
   git clone https://github.com/your-username/storydoc-selenium-framework.git
   cd storydoc-selenium-framework
   ```
3. **Set up the development environment** by following the instructions in [SETUP.md](SETUP.md)
4. **Create a new branch** for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```
   or
   ```bash
   git checkout -b fix/issue-description
   ```

Before starting work, make sure to:
- Read the [README.md](README.md) for a project overview
- Understand the [architecture](ARCHITECTURE.md) of the framework
- Familiarize yourself with the [Page Object Model](PAGE-OBJECTS.md) implementation
- Review the [test strategy](TEST-STRATEGY.md) for the project

## Development Workflow

We follow a standard GitHub workflow for contributions:

1. **Create an issue** describing the proposed change (if one doesn't already exist)
2. **Create a branch** from the main branch with a descriptive name
3. **Make your changes** in your branch, following our coding standards
4. **Write or update tests** to verify your changes
5. **Update documentation** related to your changes
6. **Commit your changes** with clear commit messages
7. **Push your branch** to your fork
8. **Submit a pull request** for review

### Branch Naming Conventions

Use the following naming conventions for branches:

- `feature/short-description` - For new features
- `fix/issue-description` - For bug fixes
- `refactor/component-name` - For code refactoring
- `docs/description` - For documentation changes
- `test/description` - For test additions or modifications

### Commit Messages

Write clear, descriptive commit messages that explain the purpose of the change:

```
[Component] Brief description of the change

More detailed explanation if needed, including the motivation for the change and how it addresses the issue.

Resolves: #issue_number
```

Examples:
- `[PageObjects] Add new dashboard page object`
- `[Locators] Update signup page locators for UI changes`
- `[Tests] Add cross-browser tests for signup flow`

## Coding Standards

We follow Google Java Style Guide with some project-specific adaptations for this framework. Adherence to these standards ensures consistent, maintainable code across the project.

### Java Standards

- Use Java 11 features and syntax
- Follow standard Java naming conventions:
  - `CamelCase` for class names
  - `camelCase` for method and variable names
  - `UPPER_SNAKE_CASE` for constants
- Add meaningful Javadoc comments to all public classes and methods
- Keep methods focused on a single responsibility
- Limit method length to improve readability and testability
- Use appropriate exception handling with specific exceptions

### Page Object Standards

All Page Objects must:

1. Extend the `BasePage` class
2. Provide a constructor that calls the parent constructor
3. Use locators from the corresponding Locator class
4. Implement methods that represent user actions
5. Return appropriate page objects for navigation methods
6. Support method chaining for sequential actions
7. Include appropriate wait strategies
8. Implement an `isPageLoaded()` method for verification

Example:
```java
public class LoginPage extends BasePage {
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    public boolean isPageLoaded() {
        return isElementVisible(LoginPageLocators.LOGIN_FORM);
    }
    
    public LoginPage enterUsername(String username) {
        type(LoginPageLocators.USERNAME_FIELD, username);
        return this;
    }
    
    public LoginPage enterPassword(String password) {
        type(LoginPageLocators.PASSWORD_FIELD, password);
        return this;
    }
    
    public DashboardPage clickLogin() {
        click(LoginPageLocators.LOGIN_BUTTON);
        return new DashboardPage(driver);
    }
}
```

### Locator Standards

All Locator classes must:

1. Be placed in the `com.storydoc.locators` package
2. Have a private constructor to prevent instantiation
3. Use static final fields for all locators
4. Follow a clear naming convention for locators
5. Group related locators with comments
6. Use the most reliable locator strategy available

Example:
```java
public class LoginPageLocators {
    // Private constructor to prevent instantiation
    private LoginPageLocators() {
        throw new IllegalStateException("Utility class");
    }
    
    // Form elements
    public static final By LOGIN_FORM = By.cssSelector("form[data-testid='login-form']");
    public static final By USERNAME_FIELD = By.cssSelector("input[data-testid='username-input']");
    public static final By PASSWORD_FIELD = By.cssSelector("input[data-testid='password-input']");
    public static final By LOGIN_BUTTON = By.cssSelector("button[data-testid='login-button']");
    
    // Error messages
    public static final By ERROR_MESSAGE = By.cssSelector("[data-testid='error-message']");
}
```

### Test Standards

All tests must:

1. Extend the appropriate test base class
2. Follow the Arrange-Act-Assert pattern
3. Have clear, descriptive method names
4. Include appropriate TestNG annotations
5. Be independent and non-interfering
6. Clean up after themselves
7. Fail for one reason only
8. Include detailed failure messages in assertions

Example:
```java
@Test(description = "Verify login with valid credentials redirects to dashboard")
public void testSuccessfulLogin() {
    // Arrange
    String username = "testuser";
    String password = "testpass";
    
    // Act
    DashboardPage dashboardPage = loginPage
        .enterUsername(username)
        .enterPassword(password)
        .clickLogin();
    
    // Assert
    Assert.assertTrue(dashboardPage.isPageLoaded(), 
        "Dashboard page did not load after successful login");
}
```

## Testing Guidelines

All code contributions must include appropriate tests. This framework uses multiple levels of testing as described in the [Test Strategy](TEST-STRATEGY.md).

### Test Requirements

1. **Unit Tests** for utilities and helper classes
   - Test isolated functionality with mocked dependencies
   - Focus on edge cases and error handling
   - Aim for high code coverage of utilities

2. **Integration Tests** for page objects and framework components
   - Test interaction between components
   - Verify page object behavior with real or mocked WebDriver
   - Test configuration loading and application

3. **End-to-End Tests** for complete workflows
   - Test actual user journeys
   - Include cross-browser testing where appropriate
   - Verify error handling and recovery

### Test Categories

Use TestNG groups to categorize tests for selective execution:

```java
@Test(groups = {"unit", "utils"})  // Unit test for utilities
@Test(groups = {"integration", "pageobjects"})  // Integration test for page objects
@Test(groups = {"e2e", "signup", "regression"})  // End-to-end test for signup flow
```

### Running Tests

Before submitting a pull request, ensure all tests pass:

```bash
# Run all tests
mvn test

# Run specific test categories
mvn test -Dgroups="unit,integration"

# Run tests with specific browser
mvn test -Dbrowser=firefox -Dgroups="e2e"
```

## Submitting Changes

To submit your changes for review:

1. Ensure your code meets our standards and passes all tests
2. Push your branch to your fork on GitHub
3. Submit a pull request (PR) to the main repository
4. Complete the pull request template with all required information
5. Link the PR to any relevant issues it addresses

### Pull Request Process

1. Pull requests must be based on the latest main branch
2. Pull requests must pass all automated checks (build, tests, linting)
3. Pull requests must be reviewed by at least one maintainer
4. Pull requests should address a single feature or fix
5. Large changes should be broken down into smaller, logical pull requests

### Pull Request Template

When creating a pull request, you'll be presented with our [PR template](.github/PULL_REQUEST_TEMPLATE.md). Complete all sections to help reviewers understand your changes:

- Description of changes
- Type of change (bugfix, feature, etc.)
- Related issues
- Testing performed
- Checklist of quality standards

### Code Review Criteria

When reviewing pull requests, maintainers will look for:

- Adherence to coding standards
- Test coverage of changes
- Proper documentation
- Performance considerations
- Maintainability and readability
- Consistency with the framework architecture

## Issue Reporting

We use GitHub Issues to track bugs, features, and improvements. Before creating a new issue:

1. Search existing issues to avoid duplicates
2. Use the appropriate issue template
3. Provide all requested information

### Bug Reports

Use the [Bug Report template](.github/ISSUE_TEMPLATE/bug_report.md) for reporting bugs. Include:

- Clear description of the issue
- Steps to reproduce
- Expected behavior
- Actual behavior
- Environment details (browser, OS, versions)
- Screenshots or logs if applicable

### Feature Requests

Use the [Feature Request template](.github/ISSUE_TEMPLATE/feature_request.md) for suggesting enhancements. Include:

- Description of the feature
- Problem it solves
- Proposed implementation approach
- Benefits to the framework
- Any alternatives considered

## Documentation

Documentation is crucial for the maintainability and usability of our framework. All code changes should include appropriate documentation updates.

### Types of Documentation

1. **Code Documentation**
   - JavaDoc comments for all public classes and methods
   - Inline comments for complex logic
   - Self-documenting code with clear naming

2. **Framework Documentation**
   - Update relevant Markdown files in the `docs/` directory
   - Update README.md for significant changes
   - Add/update examples for new features

3. **Test Documentation**
   - Clear test method names and descriptions
   - Documented test data and expected outcomes
   - Comments explaining test scenarios

### Documentation Standards

- Use Markdown for all documentation files
- Follow a consistent structure within documentation
- Include code examples for key functionality
- Keep documentation up-to-date with code changes
- Use diagrams (Mermaid) for complex workflows

## Code Review Process

All pull requests go through a code review process before being merged.

### Reviewer Assignment

Reviewers are automatically assigned based on the [CODEOWNERS](.github/CODEOWNERS) file, which maps different parts of the codebase to different maintainers. Typically:

- Core framework changes: @test-architect @qa-lead
- Page objects and locators: @qa-automation-team
- Test cases: @qa-automation-team @qa-lead
- Documentation: @qa-lead

### Review Flow

1. Automated checks run when the PR is created
2. Reviewers are notified of the PR
3. Reviewers provide feedback through GitHub's review system
4. The author addresses feedback with additional commits
5. Once approved, a maintainer will merge the PR

### Review Response

When receiving review feedback:

1. Address all comments and suggestions
2. Explain any disagreements respectfully with technical reasoning
3. Push additional commits to address feedback
4. Request re-review when all items are addressed

### Merge Criteria

For a PR to be merged, it must:

1. Pass all automated checks
2. Have at least one approval from a maintainer
3. Have all review comments addressed
4. Meet the quality standards of the project

## Project Structure

Understanding the project structure is important for effective contributions. The framework is organized as follows:

```
src/test/
├── com/storydoc/
│   ├── config/         # Configuration classes
│   ├── constants/      # Constant values
│   ├── core/           # Core framework classes
│   ├── exceptions/     # Custom exceptions
│   ├── locators/       # Element locators by page
│   ├── models/         # Data models
│   ├── pages/          # Page Objects
│   ├── reports/        # Reporting utilities
│   ├── tests/          # Test classes
│   └── utils/          # Utility classes
├── config/             # Configuration files
├── data/               # Test data
├── drivers/            # WebDriver binaries
├── logs/               # Execution logs
└── results/            # Test results
```

### Key Components

- **BasePage.java**: The foundation of all page objects
- **WebDriverManager.java**: Manages WebDriver instances
- **ConfigurationManager.java**: Handles framework configuration
- **BaseTest.java**: Base class for all test classes
- **Locator classes**: Store element selectors for each page
- **Page Objects**: Represent application pages and their behavior
- **Test classes**: Contain actual test scenarios

Refer to [ARCHITECTURE.md](ARCHITECTURE.md) for a detailed explanation of the framework components and their interactions.

## License and Attribution

By contributing to this project, you agree that your contributions will be licensed under the same license as the main project.

All contributors will be acknowledged in the project's CONTRIBUTORS.md file. Significant contributions may also be highlighted in release notes.

## Getting Help

If you need help with the contribution process or have questions about the framework:

1. Check the documentation in the `docs/` directory
2. Look for existing issues that might address your question
3. Reach out to the maintainers through GitHub issues
4. Join the project's communication channels (if available)

Thank you for contributing to the Storydoc Selenium Automation Framework!