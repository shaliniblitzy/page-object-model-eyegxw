package com.storydoc.constants;

/**
 * Constants class defining all timeout values used throughout the Selenium automation framework.
 * Centralizes timeout configurations to provide consistent synchronization behavior across all components
 * and improve maintainability when timing adjustments are needed.
 */
public final class TimeoutConstants {

    /**
     * Default timeout in seconds when a specific timeout is not specified.
     * Used as a fallback value for general waiting operations.
     */
    public static final int DEFAULT_TIMEOUT_SECONDS = 10;

    /**
     * Timeout in seconds to wait for an element to become visible.
     * Used with explicit waits for element visibility.
     */
    public static final int ELEMENT_VISIBLE_TIMEOUT_SECONDS = 10;

    /**
     * Timeout in seconds to wait for an element to become clickable.
     * Used with explicit waits for element to be both visible and enabled.
     */
    public static final int ELEMENT_CLICKABLE_TIMEOUT_SECONDS = 10;

    /**
     * Timeout in seconds to wait for an element to be present in the DOM.
     * Used with explicit waits for element presence, regardless of visibility.
     */
    public static final int ELEMENT_PRESENCE_TIMEOUT_SECONDS = 10;

    /**
     * Timeout in seconds to wait for an element to become invisible.
     * Used with explicit waits for element invisibility or removal.
     */
    public static final int ELEMENT_INVISIBLE_TIMEOUT_SECONDS = 10;

    /**
     * Timeout in seconds to wait for a page to load.
     * Used with WebDriver's pageLoadTimeout setting.
     */
    public static final int PAGE_LOAD_TIMEOUT_SECONDS = 30;

    /**
     * Timeout in seconds to wait for a page's ready state to be complete.
     * Used when checking document.readyState via JavaScript.
     */
    public static final int PAGE_READY_STATE_TIMEOUT_SECONDS = 30;

    /**
     * Timeout in seconds to wait for AJAX calls to complete.
     * Used when waiting for asynchronous operations to finish.
     */
    public static final int AJAX_TIMEOUT_SECONDS = 15;

    /**
     * Timeout in seconds for implicit waits.
     * Set to 0 as we prefer explicit waits for better control.
     */
    public static final int IMPLICITLY_WAIT_TIMEOUT_SECONDS = 0;

    /**
     * Timeout in seconds for JavaScript execution.
     * Used with WebDriver's scriptTimeout setting.
     */
    public static final int SCRIPT_TIMEOUT_SECONDS = 10;

    /**
     * Polling interval in milliseconds for wait conditions.
     * Defines how frequently to check for a condition during an explicit wait.
     */
    public static final int POLLING_INTERVAL_MILLIS = 500;

    /**
     * Interval in milliseconds between retry attempts.
     * Used when implementing retry logic for flaky operations.
     */
    public static final int RETRY_INTERVAL_MILLIS = 200;

    /**
     * Maximum number of retry attempts for failed operations.
     * Used to limit retry attempts to prevent infinite loops.
     */
    public static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Brief pause duration in milliseconds.
     * Used for short, intentional pauses when necessary.
     */
    public static final int BRIEF_PAUSE_MILLIS = 1000;

    /**
     * Medium pause duration in milliseconds.
     * Used for medium-length, intentional pauses when necessary.
     */
    public static final int MEDIUM_PAUSE_MILLIS = 3000;

    /**
     * Long pause duration in milliseconds.
     * Used for longer, intentional pauses when necessary.
     */
    public static final int LONG_PAUSE_MILLIS = 5000;

    /**
     * Timeout in seconds to wait for form submission processing.
     * Used when waiting for a form to be submitted and response received.
     */
    public static final int FORM_SUBMISSION_TIMEOUT_SECONDS = 15;
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TimeoutConstants() {
        // Utility class - do not instantiate
    }
}