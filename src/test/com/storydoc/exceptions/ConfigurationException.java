package com.storydoc.exceptions;

/**
 * Custom exception for handling configuration-related errors in the Selenium automation framework.
 * This exception is thrown when there are issues with loading, parsing or accessing configuration settings.
 * 
 * Extends FrameworkException to maintain consistent exception hierarchy and provide 
 * specialized error handling for configuration issues.
 */
public class ConfigurationException extends FrameworkException {
    
    /**
     * Serialization version UID for maintaining compatibility across different JVM implementations.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new ConfigurationException with no message or cause.
     */
    public ConfigurationException() {
        super();
    }
    
    /**
     * Constructs a new ConfigurationException with the specified message.
     * 
     * @param message The detailed error message explaining the configuration issue
     */
    public ConfigurationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ConfigurationException with the specified cause.
     * 
     * @param cause The underlying exception that caused this configuration exception
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a new ConfigurationException with the specified message and cause.
     * 
     * @param message The detailed error message explaining the configuration issue
     * @param cause The underlying exception that caused this configuration exception
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}