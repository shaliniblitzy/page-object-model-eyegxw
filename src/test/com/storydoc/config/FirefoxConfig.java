package com.storydoc.config;

import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.storydoc.config.ConfigurationManager;
import com.storydoc.constants.TimeoutConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for configuring Firefox-specific WebDriver options for the Selenium automation framework.
 * This class centralizes all Firefox browser configurations including headless mode, window size, download settings,
 * and other Firefox-specific capabilities.
 */
public class FirefoxConfig {
    
    private static final Logger LOGGER = LogManager.getLogger(FirefoxConfig.class);
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/109.0";
    
    /**
     * Private constructor to prevent instantiation as this is a utility class
     */
    private FirefoxConfig() {
        throw new IllegalStateException("Utility class - do not instantiate");
    }
    
    /**
     * Creates and configures FirefoxOptions with appropriate settings based on the application configuration
     * 
     * @return Configured FirefoxOptions instance ready for WebDriver initialization
     */
    public static FirefoxOptions getOptions() {
        LOGGER.debug("Configuring Firefox options");
        
        FirefoxOptions options = new FirefoxOptions();
        FirefoxProfile profile = createFirefoxProfile();
        
        options.setProfile(profile);
        
        // Set headless mode based on configuration
        if (ConfigurationManager.getInstance().isHeadless()) {
            LOGGER.debug("Configuring Firefox for headless execution");
            options.addArguments("--headless");
        }
        
        // Add standard Firefox arguments
        options.addArguments("--start-maximized");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-application-cache");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        
        // Set page load timeout
        options.setPageLoadTimeout(java.time.Duration.ofSeconds(TimeoutConstants.PAGE_LOAD_TIMEOUT_SECONDS));
        
        LOGGER.debug("Firefox options configured successfully");
        return options;
    }
    
    /**
     * Creates FirefoxOptions specifically configured for headless execution
     * 
     * @return FirefoxOptions configured for headless execution
     */
    public static FirefoxOptions getHeadlessOptions() {
        LOGGER.debug("Configuring Firefox for headless execution");
        
        FirefoxOptions options = new FirefoxOptions();
        FirefoxProfile profile = createFirefoxProfile();
        
        options.setProfile(profile);
        options.addArguments("--headless");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        // Set page load timeout
        options.setPageLoadTimeout(java.time.Duration.ofSeconds(TimeoutConstants.PAGE_LOAD_TIMEOUT_SECONDS));
        
        LOGGER.debug("Headless Firefox options configured successfully");
        return options;
    }
    
    /**
     * Creates and configures a Firefox profile with appropriate preferences
     * 
     * @return Configured Firefox profile
     */
    private static FirefoxProfile createFirefoxProfile() {
        LOGGER.debug("Creating Firefox profile");
        
        FirefoxProfile profile = new FirefoxProfile();
        
        // Configure download settings
        configureDownloadSettings(profile);
        
        // Disable notifications
        disableNotifications(profile);
        
        // Disable native events for better cross-platform stability
        profile.setPreference("webdriver.enable.native.events", false);
        
        // Disable Firefox updates
        profile.setPreference("app.update.auto", false);
        profile.setPreference("app.update.enabled", false);
        
        // Configure privacy and security settings
        profile.setPreference("privacy.trackingprotection.enabled", false);
        profile.setPreference("browser.privatebrowsing.autostart", false);
        
        LOGGER.debug("Firefox profile created successfully");
        return profile;
    }
    
    /**
     * Configures Firefox download settings including default download directory and download behavior
     * 
     * @param profile Firefox profile to configure
     * @return Firefox profile with download settings configured
     */
    private static FirefoxProfile configureDownloadSettings(FirefoxProfile profile) {
        LOGGER.debug("Configuring Firefox download settings");
        
        // Set download folder
        String downloadPath = System.getProperty("user.dir") + "/downloads";
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.manager.showWhenStarting", false);
        profile.setPreference("browser.download.dir", downloadPath);
        
        // Set MIME types to be downloaded automatically
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk", 
                "application/pdf," +
                "application/x-pdf," +
                "application/octet-stream," +
                "text/csv," +
                "application/csv," +
                "application/excel," +
                "application/vnd.ms-excel," +
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet," +
                "application/zip");
        
        // Disable the download progress popup that can interfere with tests
        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
        profile.setPreference("browser.download.manager.closeWhenDone", true);
        profile.setPreference("pdfjs.disabled", true);
        
        return profile;
    }
    
    /**
     * Disables Firefox notifications that can interfere with test automation
     * 
     * @param profile Firefox profile to configure
     * @return Firefox profile with notification settings configured
     */
    private static FirefoxProfile disableNotifications(FirefoxProfile profile) {
        LOGGER.debug("Disabling Firefox notifications");
        
        // Disable push notifications
        profile.setPreference("dom.push.enabled", false);
        profile.setPreference("dom.webnotifications.enabled", false);
        
        // Disable popup blocker
        profile.setPreference("dom.disable_beforeunload", true);
        profile.setPreference("dom.popup_allowed_events", "");
        
        return profile;
    }
    
    /**
     * Adds performance logging capabilities to Firefox for performance testing
     * 
     * @param options FirefoxOptions to configure
     * @return FirefoxOptions with performance logging enabled
     */
    private static FirefoxOptions addPerformanceLogging(FirefoxOptions options) {
        LOGGER.debug("Adding performance logging to Firefox options");
        
        // Set log level
        options.setLogLevel(FirefoxDriverLogLevel.TRACE);
        
        // Configure performance logging capabilities if needed
        // This would typically involve adding Firefox-specific logging preferences
        Map<String, Object> perfLogPrefs = new HashMap<>();
        perfLogPrefs.put("traceCategories", "browser,navigation,devtools.timeline,content");
        
        return options;
    }
}