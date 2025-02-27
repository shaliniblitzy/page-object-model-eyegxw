# WebDriver Executables Directory

This directory is designated for browser-specific WebDriver executables if manual management is required. However, the framework primarily uses WebDriverManager library to automatically download and manage driver binaries.

## Automated Driver Management

The framework uses WebDriverManager library (io.github.bonigarcia.wdm) to automatically download, configure, and manage browser driver binaries. This eliminates the need to manually download and update WebDriver executables as browser versions change.

```java
// Example from WebDriverManager.java
WebDriverManager.chromedriver().setup();
driver = new ChromeDriver(getChromeOptions());
```

## Supported Browsers

The automation framework supports the following browsers:
- Chrome
- Firefox
- Edge

## Manual Driver Management

If automatic driver management cannot be used (e.g., in restricted environments without internet access), you can manually download driver executables and place them in this directory:

1. Download the appropriate WebDriver for your browser version
2. Place the executable in this directory
3. Update `DriverFactory.java` to use local driver path instead of WebDriverManager

Example for manual driver path configuration:
```java
// Example modification to DriverFactory.java
System.setProperty("webdriver.chrome.driver", "src/test/drivers/chromedriver.exe");
return new ChromeDriver(chromeOptions);
```

## Driver Download Links

- Chrome: https://chromedriver.chromium.org/downloads
- Firefox: https://github.com/mozilla/geckodriver/releases
- Edge: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/

## Framework Integration

The driver management is handled by the following framework components:

- `WebDriverManager.java`: Singleton class that manages WebDriver instances throughout the test lifecycle
- `DriverFactory.java`: Factory class responsible for creating WebDriver instances based on browser type
- `BrowserType.java`: Enum defining supported browser types (CHROME, FIREFOX, EDGE)
- Browser configuration classes: `ChromeConfig.java`, `FirefoxConfig.java`, `EdgeConfig.java`