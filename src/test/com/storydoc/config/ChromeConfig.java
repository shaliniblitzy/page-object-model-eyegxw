package com.storydoc.config;

import org.openqa.selenium.chrome.ChromeOptions;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

import com.storydoc.config.ConfigurationManager;
import com.storydoc.constants.TimeoutConstants;

/**
 * Utility class responsible for configuring Chrome-specific WebDriver options for the Selenium automation framework.
 * This class centralizes all Chrome browser configurations including headless mode, window size, download settings,
 * and other Chrome-specific capabilities.
 */
public class ChromeConfig {
    
    private static final Logger LOGGER = LogManager.getLogger(ChromeConfig.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36";
    
    /**
     * Private constructor to prevent instantiation as this is a utility class
     */
    private ChromeConfig() {
        throw new IllegalStateException("Utility class - do not instantiate");
    }
    
    /**
     * Creates and configures ChromeOptions with appropriate settings based on the application configuration
     *
     * @return Configured ChromeOptions instance ready for WebDriver initialization
     */
    public static ChromeOptions getOptions() {
        LOGGER.debug("Configuring Chrome options");
        ChromeOptions options = new ChromeOptions();
        
        // Set headless mode based on configuration
        if (ConfigurationManager.getInstance().isHeadless()) {
            LOGGER.debug("Configuring Chrome for headless execution");
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        
        // Add standard Chrome arguments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        
        // Configure browser window
        if (ConfigurationManager.getInstance().shouldMaximizeBrowser()) {
            options.addArguments("--start-maximized");
        }
        
        // Configure download settings and capabilities
        configureDownloadSettings(options);
        disableInfobars(options);
        
        // Add user agent
        options.addArguments("--user-agent=" + USER_AGENT);
        
        LOGGER.debug("Chrome options configured successfully");
        return options;
    }
    
    /**
     * Creates ChromeOptions specifically configured for headless execution
     *
     * @return ChromeOptions configured for headless execution
     */
    public static ChromeOptions getHeadlessOptions() {
        LOGGER.debug("Configuring Chrome options for headless execution");
        ChromeOptions options = new ChromeOptions();
        
        // Add headless mode arguments
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        // Add additional arguments for headless stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        
        // Configure download settings and capabilities
        configureDownloadSettings(options);
        disableInfobars(options);
        
        // Add user agent to avoid detection as headless browser
        options.addArguments("--user-agent=" + USER_AGENT);
        
        LOGGER.debug("Headless Chrome options configured successfully");
        return options;
    }
    
    /**
     * Configures Chrome download settings including default download directory and download behavior
     *
     * @param options ChromeOptions to configure
     * @return ChromeOptions with download settings configured
     */
    private static ChromeOptions configureDownloadSettings(ChromeOptions options) {
        LOGGER.debug("Configuring Chrome download settings");
        
        // Get existing preferences or create new if none exist
        Map<String, Object> prefs = new HashMap<>();
        if (options.getExperimentalOption("prefs") != null) {
            prefs = (Map<String, Object>) options.getExperimentalOption("prefs");
        }
        
        // Set download directory to project's download folder
        String downloadPath = System.getProperty("user.dir") + "/downloads";
        prefs.put("download.default_directory", downloadPath);
        
        // Configure download behavior
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", false);
        
        // Apply preferences to ChromeOptions
        options.setExperimentalOption("prefs", prefs);
        
        LOGGER.debug("Chrome download settings configured with path: {}", downloadPath);
        return options;
    }
    
    /**
     * Disables Chrome infobars that can interfere with test automation
     *
     * @param options ChromeOptions to configure
     * @return ChromeOptions with infobar settings configured
     */
    private static ChromeOptions disableInfobars(ChromeOptions options) {
        LOGGER.debug("Disabling Chrome infobars");
        
        // Add arguments to disable infobars
        options.addArguments("--disable-infobars");
        
        // Get existing preferences or create new if none exist
        Map<String, Object> prefs = new HashMap<>();
        if (options.getExperimentalOption("prefs") != null) {
            prefs = (Map<String, Object>) options.getExperimentalOption("prefs");
        }
        
        // Add notification settings to preferences
        prefs.put("profile.default_content_setting_values.notifications", 2);
        
        // Apply updated preferences to ChromeOptions
        options.setExperimentalOption("prefs", prefs);
        
        LOGGER.debug("Chrome infobars disabled");
        return options;
    }
    
    /**
     * Adds performance logging capabilities to Chrome for performance testing
     *
     * @param options ChromeOptions to configure
     * @return ChromeOptions with performance logging enabled
     */
    private static ChromeOptions addPerformanceLogging(ChromeOptions options) {
        LOGGER.debug("Adding performance logging capabilities to Chrome");
        
        // Create logging preferences
        Map<String, Object> logPrefs = new HashMap<>();
        logPrefs.put("browser", "ALL");
        logPrefs.put("performance", "ALL");
        
        // Apply logging preferences to ChromeOptions
        options.setCapability("goog:loggingPrefs", logPrefs);
        
        LOGGER.debug("Chrome performance logging enabled");
        return options;
    }
}