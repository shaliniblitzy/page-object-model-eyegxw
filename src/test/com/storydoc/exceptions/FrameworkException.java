package com.storydoc.exceptions;

/**
 * A custom runtime exception for handling framework-specific errors that occur during test execution.
 * Extends RuntimeException to create unchecked exceptions for framework-level errors.
 * 
 * This exception is thrown when issues occur within the framework components rather than
 * application-specific failures, providing clearer distinction between framework failures
 * and application failures during troubleshooting.
 */
public class FrameworkException extends RuntimeException {
    
    /**
     * Constructs a new FrameworkException with no message or cause.
     */
    public FrameworkException() {
        super();
    }
    
    /**
     * Constructs a new FrameworkException with the specified message.
     * 
     * @param message The detailed error message explaining the framework issue
     */
    public FrameworkException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new FrameworkException with the specified message and cause.
     * 
     * @param message The detailed error message explaining the framework issue
     * @param cause The underlying exception that caused this framework exception
     */
    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new FrameworkException with the specified cause.
     * 
     * @param cause The underlying exception that caused this framework exception
     */
    public FrameworkException(Throwable cause) {
        super(cause);
    }
}