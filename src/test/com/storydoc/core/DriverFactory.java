package com.storydoc.core;

import com.storydoc.config.BrowserType;
import com.storydoc.config.ConfigurationManager;
import com.storydoc.config.ChromeConfig;
import com.storydoc.config.EdgeConfig;
import com.storydoc.config.EnvironmentType;
import com.storydoc.config.FirefoxConfig;
import com.storydoc.constants.TimeoutConstants;
import com.storydoc.exceptions.FrameworkException;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * Factory class responsible for creating and configuring WebDriver instances
 * based on browser type and environment settings. This component abstracts
 * browser creation logic and centralizes browser configuration to ensure
 * consistent test execution across different environments.
 */
public class DriverFactory {
    
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static final String REMOTE_DRIVER_URL = "http://localhost:4444/wd/hub";
    private static final String BROWSER_NOT_SUPPORTED_MESSAGE = "Browser not supported: ";
    
    /**
     * Factory method that creates a WebDriver instance based on the configured browser type and environment
     * 
     * @return Configured WebDriver instance for the specified browser
     * @throws FrameworkException if there is an error creating the WebDriver instance
     */
    public static WebDriver createDriver() {
        BrowserType browserType = ConfigurationManager.getInstance().getBrowser();
        EnvironmentType environmentType = ConfigurationManager.getInstance().getEnvironment();
        
        logger.info("Creating WebDriver instance for browser: {} in environment: {}", browserType, environmentType);
        
        WebDriver driver;
        
        switch (environmentType) {
            case LOCAL:
                driver = createLocalDriver(browserType);
                break;
            case DEV:
            case STAGING:
                driver = createRemoteDriver(browserType);
                break;
            default:
                throw new FrameworkException("Environment not supported: " + environmentType);
        }
        
        configureTimeouts(driver);
        
        return driver;
    }
    
    /**
     * Creates a WebDriver instance for local execution
     * 
     * @param browserType the type of browser to create
     * @return Configured local WebDriver instance
     * @throws FrameworkException if there is an error creating the WebDriver instance
     */
    private static WebDriver createLocalDriver(BrowserType browserType) {
        WebDriver driver;
        
        switch (browserType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(ChromeConfig.getOptions());
                logger.debug("Created Chrome WebDriver instance");
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(FirefoxConfig.getOptions());
                logger.debug("Created Firefox WebDriver instance");
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(EdgeConfig.getOptions());
                logger.debug("Created Edge WebDriver instance");
                break;
            default:
                logger.error("Unsupported browser type: {}", browserType);
                throw new FrameworkException(BROWSER_NOT_SUPPORTED_MESSAGE + browserType);
        }
        
        logger.info("Successfully created local WebDriver instance for browser: {}", browserType);
        return driver;
    }
    
    /**
     * Creates a WebDriver instance for remote execution (e.g., Selenium Grid, cloud provider)
     * 
     * @param browserType the type of browser to create
     * @return Configured remote WebDriver instance
     * @throws FrameworkException if there is an error creating the WebDriver instance
     */
    private static WebDriver createRemoteDriver(BrowserType browserType) {
        WebDriver driver;
        
        try {
            URL remoteUrl = new URL(REMOTE_DRIVER_URL);
            
            switch (browserType) {
                case CHROME:
                    driver = new RemoteWebDriver(remoteUrl, ChromeConfig.getOptions());
                    logger.debug("Created remote Chrome WebDriver instance");
                    break;
                case FIREFOX:
                    driver = new RemoteWebDriver(remoteUrl, FirefoxConfig.getOptions());
                    logger.debug("Created remote Firefox WebDriver instance");
                    break;
                case EDGE:
                    driver = new RemoteWebDriver(remoteUrl, EdgeConfig.getOptions());
                    logger.debug("Created remote Edge WebDriver instance");
                    break;
                default:
                    logger.error("Unsupported browser type: {}", browserType);
                    throw new FrameworkException(BROWSER_NOT_SUPPORTED_MESSAGE + browserType);
            }
            
            logger.info("Successfully created remote WebDriver instance for browser: {}", browserType);
            return driver;
        } catch (MalformedURLException e) {
            logger.error("Invalid remote WebDriver URL: {}", REMOTE_DRIVER_URL, e);
            throw new FrameworkException("Invalid remote WebDriver URL: " + REMOTE_DRIVER_URL, e);
        }
    }
    
    /**
     * Sets up standard timeouts for the WebDriver instance
     * 
     * @param driver WebDriver instance to configure
     */
    private static void configureTimeouts(WebDriver driver) {
        logger.debug("Configuring WebDriver timeouts");
        
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(TimeoutConstants.PAGE_LOAD_TIMEOUT_SECONDS));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(TimeoutConstants.SCRIPT_TIMEOUT_SECONDS));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(TimeoutConstants.IMPLICITLY_WAIT_TIMEOUT_SECONDS));
        
        if (ConfigurationManager.getInstance().shouldMaximizeBrowser()) {
            logger.debug("Maximizing browser window");
            driver.manage().window().maximize();
        }
        
        logger.debug("WebDriver timeouts configured successfully");
    }
}