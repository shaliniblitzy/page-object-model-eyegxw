package com.storydoc.config;

/**
 * Enum representing the different environment types that the automation framework can target.
 * This enum allows the framework to configure different settings for various test environments
 * (local, development, staging) to ensure proper test execution against the appropriate environment.
 * 
 * Used by ConfigurationManager to load appropriate environment-specific configuration files and settings.
 */
public enum EnvironmentType {
    /**
     * Local environment for development and debugging tests on local machine
     */
    LOCAL,
    
    /**
     * Development environment for testing against development server
     */
    DEV,
    
    /**
     * Staging environment for testing against staging server (pre-production)
     */
    STAGING
}