package com.storydoc.config;

import org.openqa.selenium.edge.EdgeOptions;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.HashMap;
import java.util.Map;

import com.storydoc.config.ConfigurationManager;
import com.storydoc.constants.TimeoutConstants;

/**
 * Utility class responsible for configuring Microsoft Edge-specific WebDriver options for the Selenium automation framework.
 * This class centralizes all Edge browser configurations including headless mode, window size, download settings,
 * and other Edge-specific capabilities.
 */
public class EdgeConfig {
    
    private static final Logger LOGGER = LogManager.getLogger(EdgeConfig.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59";
    
    /**
     * Private constructor to prevent instantiation as this is a utility class
     */
    private EdgeConfig() {
        throw new IllegalStateException("Utility class - do not instantiate");
    }
    
    /**
     * Creates and configures EdgeOptions with appropriate settings based on the application configuration
     *
     * @return Configured EdgeOptions instance ready for WebDriver initialization
     */
    public static EdgeOptions getOptions() {
        EdgeOptions options = new EdgeOptions();
        
        // Check if headless mode is enabled
        if (ConfigurationManager.getInstance().isHeadless()) {
            LOGGER.debug("Configuring Edge browser in headless mode");
            options.addArguments("--headless=new");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
        }
        
        // Add standard Edge arguments
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");
        
        // Configure download settings
        configureDownloadSettings(options);
        
        // Disable infobars
        disableInfobars(options);
        
        // Add performance logging if needed
        // addPerformanceLogging(options);
        
        // Add custom user agent if needed
        // options.addArguments("--user-agent=" + USER_AGENT);
        
        LOGGER.debug("Edge options configured: {}", options.toString());
        
        return options;
    }
    
    /**
     * Creates EdgeOptions specifically configured for headless execution
     *
     * @return EdgeOptions configured for headless execution
     */
    public static EdgeOptions getHeadlessOptions() {
        EdgeOptions options = new EdgeOptions();
        
        // Configure headless mode
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        // Additional headless configuration
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        
        // Configure downloads for headless mode
        configureDownloadSettings(options);
        
        LOGGER.debug("Edge headless options configured: {}", options.toString());
        
        return options;
    }
    
    /**
     * Configures Edge download settings including default download directory and download behavior
     *
     * @param options EdgeOptions to configure
     * @return EdgeOptions with download settings configured
     */
    public static EdgeOptions configureDownloadSettings(EdgeOptions options) {
        Map<String, Object> prefs = new HashMap<>();
        
        // Set download directory to prevent download dialogs
        prefs.put("download.default_directory", System.getProperty("user.dir") + "/target/downloads");
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);
        
        options.setExperimentalOption("prefs", prefs);
        
        return options;
    }
    
    /**
     * Disables Edge infobars that can interfere with test automation
     *
     * @param options EdgeOptions to configure
     * @return EdgeOptions with infobar settings configured
     */
    public static EdgeOptions disableInfobars(EdgeOptions options) {
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        
        // Disable "Chrome is being controlled by automated software" infobar
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        
        options.setExperimentalOption("prefs", prefs);
        
        return options;
    }
    
    /**
     * Adds performance logging capabilities to Edge for performance testing
     *
     * @param options EdgeOptions to configure
     * @return EdgeOptions with performance logging enabled
     */
    public static EdgeOptions addPerformanceLogging(EdgeOptions options) {
        // Edge uses the same logging preferences as Chrome
        Map<String, Object> loggingPrefs = new HashMap<>();
        loggingPrefs.put("browser", "ALL");
        loggingPrefs.put("performance", "ALL");
        
        options.setCapability("goog:loggingPrefs", loggingPrefs);
        
        return options;
    }
}