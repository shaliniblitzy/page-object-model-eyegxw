package com.storydoc.config;

/**
 * Enum that defines the available browser types supported by the Selenium automation framework.
 * This enum is used throughout the framework to specify which browsers can be used for test execution,
 * maintain type safety, and enable cross-browser testing capabilities.
 */
public enum BrowserType {
    /**
     * Google Chrome browser
     */
    CHROME,
    
    /**
     * Mozilla Firefox browser
     */
    FIREFOX,
    
    /**
     * Microsoft Edge browser
     */
    EDGE
}