package com.storydoc.utils;

import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils; // version: 3.12.0

/**
 * Utility class that provides static methods for generating random test data
 * for the Storydoc signup automation framework. Includes methods for generating
 * unique email addresses, compliant passwords, and other test data needed for
 * signup testing.
 */
public final class RandomDataGenerator {

    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final int DEFAULT_PASSWORD_LENGTH = 12;
    private static final String EMAIL_DOMAIN = "example.com";
    private static final Random random = new Random();

    /**
     * Private constructor to prevent instantiation of utility class
     */
    private RandomDataGenerator() {
        throw new IllegalStateException("Utility class - cannot be instantiated");
    }

    /**
     * Generates a random email address using timestamp to ensure uniqueness
     * 
     * @return A unique email address for test use
     */
    public static String generateRandomEmail() {
        // Generate a timestamp using current date and time
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        
        // Create a random string prefix
        String prefix = "test";
        
        // Format the email as prefix_timestamp@example.com
        return prefix + "_" + timestamp + "@" + EMAIL_DOMAIN;
    }

    /**
     * Generates a random password that meets the application's requirements
     * 
     * @param length The desired length of the password
     * @return A password that meets complexity requirements
     */
    public static String generateRandomPassword(int length) {
        // Validate the length parameter (minimum 8)
        if (length < 8) {
            length = 8; // Minimum password length for security
        }
        
        // Generate a random string with a mix of uppercase, lowercase, digits, and special characters
        String upperCaseLetters = RandomStringUtils.random(1, 'A', 'Z', false, false);
        String lowerCaseLetters = RandomStringUtils.random(1, 'a', 'z', false, false);
        String numbers = RandomStringUtils.randomNumeric(1);
        String specialChar = RandomStringUtils.random(1, SPECIAL_CHARS);
        
        // Create the remaining part of the password
        String remainingChars = RandomStringUtils.random(length - 4, 
                ALPHA_NUMERIC_STRING + SPECIAL_CHARS);
        
        // Combine all parts and shuffle
        String combinedPassword = upperCaseLetters + lowerCaseLetters + numbers + specialChar + remainingChars;
        
        // Convert to character array for shuffling
        char[] pwdChars = combinedPassword.toCharArray();
        for (int i = 0; i < pwdChars.length; i++) {
            int j = random.nextInt(pwdChars.length);
            char temp = pwdChars[i];
            pwdChars[i] = pwdChars[j];
            pwdChars[j] = temp;
        }
        
        // Return the generated password
        return new String(pwdChars);
    }

    /**
     * Generates a random password with default length
     * 
     * @return A password that meets complexity requirements
     */
    public static String generateRandomPassword() {
        // Call generateRandomPassword with DEFAULT_PASSWORD_LENGTH
        return generateRandomPassword(DEFAULT_PASSWORD_LENGTH);
    }

    /**
     * Generates a random name for testing purposes
     * 
     * @return A random name
     */
    public static String generateRandomName() {
        // Generate a random string between 5-10 characters
        int nameLength = random.nextInt(6) + 5; // 5-10 characters
        String name = generateRandomAlphaString(nameLength);
        
        // Capitalize the first letter
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    /**
     * Generates a random alphanumeric string of specified length
     * 
     * @param length The desired length of the string
     * @return A random string of specified length
     */
    public static String generateRandomString(int length) {
        // Use RandomStringUtils to generate an alphanumeric string of the specified length
        return RandomStringUtils.randomAlphanumeric(length);
    }

    /**
     * Generates a random alphabetic string of specified length
     * 
     * @param length The desired length of the string
     * @return A random alphabetic string
     */
    public static String generateRandomAlphaString(int length) {
        // Use RandomStringUtils to generate an alphabetic string of the specified length
        return RandomStringUtils.randomAlphabetic(length);
    }

    /**
     * Generates a random numeric string of specified length
     * 
     * @param length The desired length of the string
     * @return A random numeric string
     */
    public static String generateRandomNumeric(int length) {
        // Use RandomStringUtils to generate a numeric string of the specified length
        return RandomStringUtils.randomNumeric(length);
    }

    /**
     * Generates a unique identifier using UUID
     * 
     * @return A unique identifier
     */
    public static String generateUniqueId() {
        // Generate a random UUID
        UUID uuid = UUID.randomUUID();
        
        // Convert to string and remove hyphens
        return uuid.toString().replace("-", "");
    }

    /**
     * Generates a timestamp string in the specified format
     * 
     * @return Current timestamp as string
     */
    public static String getTimestamp() {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        
        // Format using predefined pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        
        // Return formatted timestamp
        return now.format(formatter);
    }
}