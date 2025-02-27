package com.storydoc.utils;

import org.apache.logging.log4j.Logger; // v2.19.0
import org.apache.logging.log4j.LogManager; // v2.19.0
import org.apache.logging.log4j.ThreadContext; // v2.19.0

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class that provides a unified logging interface for the entire test automation framework.
 * Encapsulates Log4j2 functionality with convenience methods for different log levels and standard formatting,
 * supporting hierarchical logging for different framework components.
 */
public final class LogUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(LogUtils.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Private constructor to prevent instantiation of utility class
     */
    private LogUtils() {
        throw new IllegalStateException("Utility class - cannot be instantiated");
    }
    
    /**
     * Gets a Logger instance for the specified class
     * 
     * @param clazz Class to get logger for
     * @return Logger instance for the provided class
     */
    public static Logger getLogger(Class<?> clazz) {
        return LogManager.getLogger(clazz);
    }
    
    /**
     * Logs a message at DEBUG level
     * 
     * @param message Message to log
     */
    public static void debug(String message) {
        LOGGER.debug(formatMessage(message));
    }
    
    /**
     * Logs a message with class name at DEBUG level
     * 
     * @param clazz Class context for the log message
     * @param message Message to log
     */
    public static void debug(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        logger.debug(formatMessage(message));
    }
    
    /**
     * Logs a message at INFO level
     * 
     * @param message Message to log
     */
    public static void info(String message) {
        LOGGER.info(formatMessage(message));
    }
    
    /**
     * Logs a message with class name at INFO level
     * 
     * @param clazz Class context for the log message
     * @param message Message to log
     */
    public static void info(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        logger.info(formatMessage(message));
    }
    
    /**
     * Logs a message at WARN level
     * 
     * @param message Message to log
     */
    public static void warn(String message) {
        LOGGER.warn(formatMessage(message));
    }
    
    /**
     * Logs a message with class name at WARN level
     * 
     * @param clazz Class context for the log message
     * @param message Message to log
     */
    public static void warn(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        logger.warn(formatMessage(message));
    }
    
    /**
     * Logs a message at ERROR level
     * 
     * @param message Message to log
     */
    public static void error(String message) {
        LOGGER.error(formatMessage(message));
    }
    
    /**
     * Logs a message with an exception at ERROR level
     * 
     * @param message Message to log
     * @param throwable Exception to log
     */
    public static void error(String message, Throwable throwable) {
        LOGGER.error(formatMessage(message), throwable);
    }
    
    /**
     * Logs a message with class name at ERROR level
     * 
     * @param clazz Class context for the log message
     * @param message Message to log
     */
    public static void error(Class<?> clazz, String message) {
        Logger logger = getLogger(clazz);
        logger.error(formatMessage(message));
    }
    
    /**
     * Logs a message with class name and exception at ERROR level
     * 
     * @param clazz Class context for the log message
     * @param message Message to log
     * @param throwable Exception to log
     */
    public static void error(Class<?> clazz, String message, Throwable throwable) {
        Logger logger = getLogger(clazz);
        logger.error(formatMessage(message), throwable);
    }
    
    /**
     * Sets the current test ID in the thread context for correlation in logs
     * 
     * @param testId ID of the current test
     */
    public static void setTestContext(String testId) {
        ThreadContext.put("testId", testId);
    }
    
    /**
     * Clears the test context from thread-local storage
     */
    public static void clearTestContext() {
        ThreadContext.remove("testId");
        // ThreadContext.clearAll(); // Alternative to clear all contexts
    }
    
    /**
     * Formats a log message with consistent structure
     * 
     * @param message Original message
     * @return Formatted message string
     */
    private static String formatMessage(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String testId = ThreadContext.get("testId");
        
        StringBuilder formattedMessage = new StringBuilder();
        formattedMessage.append("[").append(timestamp).append("]");
        
        if (testId != null && !testId.isEmpty()) {
            formattedMessage.append(" [Test: ").append(testId).append("]");
        }
        
        formattedMessage.append(" ").append(message);
        
        return formattedMessage.toString();
    }
    
    /**
     * Logs method entry with parameters (useful for debugging)
     * 
     * @param clazz Class containing the method
     * @param methodName Name of the method being entered
     * @param parameters Method parameters
     */
    public static void logMethodEntry(Class<?> clazz, String methodName, Object[] parameters) {
        StringBuilder message = new StringBuilder();
        message.append("Entering method: ").append(methodName).append("(");
        
        if (parameters != null && parameters.length > 0) {
            for (int i = 0; i < parameters.length; i++) {
                if (i > 0) {
                    message.append(", ");
                }
                message.append(parameters[i]);
            }
        }
        
        message.append(")");
        Logger logger = getLogger(clazz);
        logger.debug(formatMessage(message.toString()));
    }
    
    /**
     * Logs method exit with return value (useful for debugging)
     * 
     * @param clazz Class containing the method
     * @param methodName Name of the method being exited
     * @param returnValue Value being returned from the method
     */
    public static void logMethodExit(Class<?> clazz, String methodName, Object returnValue) {
        StringBuilder message = new StringBuilder();
        message.append("Exiting method: ").append(methodName);
        
        if (returnValue != null) {
            message.append(" with return value: ").append(returnValue);
        } else {
            message.append(" with return value: null");
        }
        
        Logger logger = getLogger(clazz);
        logger.debug(formatMessage(message.toString()));
    }
}