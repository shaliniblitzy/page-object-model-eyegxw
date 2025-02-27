package com.storydoc.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.RandomStringUtils;
import com.storydoc.constants.MessageConstants;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Utility class for generating passwords that meet the Storydoc application's
 * complexity requirements for testing the signup process. Provides various methods
 * to create randomly generated passwords with different levels of complexity and
 * characteristics.
 */
public final class PasswordGenerator {
    
    private static final Logger LOGGER = LogManager.getLogger(PasswordGenerator.class);
    
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERIC_CHARS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{};:,.<>?";
    
    private static final int DEFAULT_PASSWORD_LENGTH = 12;
    private static final int MIN_PASSWORD_LENGTH = 8;
    
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Private constructor to prevent instantiation of this utility class.
     * 
     * @throws IllegalStateException if an attempt is made to instantiate this class
     */
    private PasswordGenerator() {
        throw new IllegalStateException("Utility class, do not instantiate");
    }
    
    /**
     * Generates a random password with default length (12 characters) that meets
     * standard complexity requirements.
     * 
     * @return A randomly generated password that meets complexity requirements
     */
    public static String generatePassword() {
        return generatePassword(DEFAULT_PASSWORD_LENGTH);
    }
    
    /**
     * Generates a random password of specified length that meets standard complexity
     * requirements.
     * 
     * @param length The desired length of the password
     * @return A randomly generated password that meets complexity requirements
     * @throws IllegalArgumentException if length is less than the minimum required
     */
    public static String generatePassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password length must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        
        // Start with a random password
        StringBuilder password = new StringBuilder(RandomStringUtils.random(length, true, true));
        
        // Ensure we have at least one lowercase letter
        if (!containsAny(password.toString(), LOWERCASE_CHARS)) {
            setRandomCharAt(password, LOWERCASE_CHARS.charAt(secureRandom.nextInt(LOWERCASE_CHARS.length())));
        }
        
        // Ensure we have at least one uppercase letter
        if (!containsAny(password.toString(), UPPERCASE_CHARS)) {
            setRandomCharAt(password, UPPERCASE_CHARS.charAt(secureRandom.nextInt(UPPERCASE_CHARS.length())));
        }
        
        // Ensure we have at least one digit
        if (!containsAny(password.toString(), NUMERIC_CHARS)) {
            setRandomCharAt(password, NUMERIC_CHARS.charAt(secureRandom.nextInt(NUMERIC_CHARS.length())));
        }
        
        // Ensure we have at least one special character
        if (!containsAny(password.toString(), SPECIAL_CHARS)) {
            setRandomCharAt(password, SPECIAL_CHARS.charAt(secureRandom.nextInt(SPECIAL_CHARS.length())));
        }
        
        LOGGER.debug("Generated complex password of length: {}", length);
        return password.toString();
    }
    
    /**
     * Generates a simple alphanumeric password of specified length (for less restrictive
     * testing).
     * 
     * @param length The desired length of the password
     * @return A randomly generated alphanumeric password
     * @throws IllegalArgumentException if length is less than the minimum required
     */
    public static String generateSimplePassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password length must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        
        String password = RandomStringUtils.randomAlphanumeric(length);
        LOGGER.debug("Generated simple alphanumeric password of length: {}", length);
        return password;
    }
    
    /**
     * Generates an extra strong password with high complexity for security testing.
     * 
     * @param length The desired length of the password
     * @return A randomly generated high-complexity password
     * @throws IllegalArgumentException if length is less than the minimum required
     */
    public static String generateStrongPassword(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password length must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }
        
        // Determine character counts to ensure a balanced mix
        int specialCharsCount = Math.max(length / 4, 2); // At least 2 special chars or 25% of length
        int digitCount = Math.max(length / 4, 2); // At least 2 digits or 25% of length
        int uppercaseCount = Math.max(length / 4, 2); // At least 2 uppercase or 25% of length
        int lowercaseCount = length - specialCharsCount - digitCount - uppercaseCount;
        
        StringBuilder password = new StringBuilder();
        
        // Add special characters
        for (int i = 0; i < specialCharsCount; i++) {
            password.append(SPECIAL_CHARS.charAt(secureRandom.nextInt(SPECIAL_CHARS.length())));
        }
        
        // Add digits
        for (int i = 0; i < digitCount; i++) {
            password.append(NUMERIC_CHARS.charAt(secureRandom.nextInt(NUMERIC_CHARS.length())));
        }
        
        // Add uppercase letters
        for (int i = 0; i < uppercaseCount; i++) {
            password.append(UPPERCASE_CHARS.charAt(secureRandom.nextInt(UPPERCASE_CHARS.length())));
        }
        
        // Add lowercase letters
        for (int i = 0; i < lowercaseCount; i++) {
            password.append(LOWERCASE_CHARS.charAt(secureRandom.nextInt(LOWERCASE_CHARS.length())));
        }
        
        // Shuffle the password characters to increase randomness
        String shuffled = shuffleString(password.toString());
        
        LOGGER.debug("Generated strong password of length: {}", length);
        return shuffled;
    }
    
    /**
     * Generates a password with a specific prefix followed by random characters.
     * 
     * @param prefix The prefix to use at the start of the password
     * @param totalLength The total desired length of the password (including prefix)
     * @return A password starting with the specified prefix followed by random characters
     * @throws IllegalArgumentException if prefix is null/empty or totalLength is insufficient
     */
    public static String generatePasswordWithPrefix(String prefix, int totalLength) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Prefix cannot be null or empty");
        }
        
        // Make sure we have room for the prefix plus at least 4 more chars for complexity
        if (totalLength < prefix.length() + 4) {
            throw new IllegalArgumentException("Total length must be at least prefix length + 4 characters");
        }
        
        int remainingLength = totalLength - prefix.length();
        
        // Generate random characters for the remaining length
        StringBuilder password = new StringBuilder(prefix);
        String randomPart = RandomStringUtils.random(remainingLength, 
            LOWERCASE_CHARS + UPPERCASE_CHARS + NUMERIC_CHARS + SPECIAL_CHARS);
        password.append(randomPart);
        
        // Ensure the password meets complexity requirements
        String result = password.toString();
        if (!isPasswordComplex(result)) {
            // If not complex, generate a complex password and replace the end of the prefix
            String complexEnd = generatePassword(remainingLength);
            result = prefix + complexEnd;
        }
        
        LOGGER.debug("Generated password with prefix '{}' of total length: {}", prefix, totalLength);
        return result;
    }
    
    /**
     * Generates passwords that fail specific validation rules for negative testing.
     * 
     * @param invalidationType The type of validation failure to generate ("TOO_SHORT", 
     *                        "NO_UPPERCASE", "NO_LOWERCASE", "NO_DIGITS", "NO_SPECIAL")
     * @return A password designed to fail specific validation criteria
     * @throws IllegalArgumentException if invalidationType is not recognized
     */
    public static String generateInvalidPassword(String invalidationType) {
        String password;
        
        switch (invalidationType.toUpperCase()) {
            case "TOO_SHORT":
                password = RandomStringUtils.randomAlphanumeric(MIN_PASSWORD_LENGTH - 1);
                break;
                
            case "NO_UPPERCASE":
                // Generate password with only lowercase, digits and special chars
                password = RandomStringUtils.random(DEFAULT_PASSWORD_LENGTH, 
                    LOWERCASE_CHARS + NUMERIC_CHARS + SPECIAL_CHARS);
                // Ensure it has lowercase, digits and special chars
                if (!containsAny(password, LOWERCASE_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        LOWERCASE_CHARS.charAt(secureRandom.nextInt(LOWERCASE_CHARS.length()));
                }
                if (!containsAny(password, NUMERIC_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        NUMERIC_CHARS.charAt(secureRandom.nextInt(NUMERIC_CHARS.length()));
                }
                if (!containsAny(password, SPECIAL_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        SPECIAL_CHARS.charAt(secureRandom.nextInt(SPECIAL_CHARS.length()));
                }
                break;
                
            case "NO_LOWERCASE":
                // Generate password with only uppercase, digits and special chars
                password = RandomStringUtils.random(DEFAULT_PASSWORD_LENGTH, 
                    UPPERCASE_CHARS + NUMERIC_CHARS + SPECIAL_CHARS);
                // Ensure it has uppercase, digits and special chars
                if (!containsAny(password, UPPERCASE_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        UPPERCASE_CHARS.charAt(secureRandom.nextInt(UPPERCASE_CHARS.length()));
                }
                if (!containsAny(password, NUMERIC_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        NUMERIC_CHARS.charAt(secureRandom.nextInt(NUMERIC_CHARS.length()));
                }
                if (!containsAny(password, SPECIAL_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        SPECIAL_CHARS.charAt(secureRandom.nextInt(SPECIAL_CHARS.length()));
                }
                break;
                
            case "NO_DIGITS":
                // Generate password with only letters and special chars
                password = RandomStringUtils.random(DEFAULT_PASSWORD_LENGTH, 
                    LOWERCASE_CHARS + UPPERCASE_CHARS + SPECIAL_CHARS);
                // Ensure it has lowercase, uppercase and special chars
                if (!containsAny(password, LOWERCASE_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        LOWERCASE_CHARS.charAt(secureRandom.nextInt(LOWERCASE_CHARS.length()));
                }
                if (!containsAny(password, UPPERCASE_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        UPPERCASE_CHARS.charAt(secureRandom.nextInt(UPPERCASE_CHARS.length()));
                }
                if (!containsAny(password, SPECIAL_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        SPECIAL_CHARS.charAt(secureRandom.nextInt(SPECIAL_CHARS.length()));
                }
                break;
                
            case "NO_SPECIAL":
                // Generate password with only letters and digits
                password = RandomStringUtils.random(DEFAULT_PASSWORD_LENGTH, 
                    LOWERCASE_CHARS + UPPERCASE_CHARS + NUMERIC_CHARS);
                // Ensure it has lowercase, uppercase and digits
                if (!containsAny(password, LOWERCASE_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        LOWERCASE_CHARS.charAt(secureRandom.nextInt(LOWERCASE_CHARS.length()));
                }
                if (!containsAny(password, UPPERCASE_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        UPPERCASE_CHARS.charAt(secureRandom.nextInt(UPPERCASE_CHARS.length()));
                }
                if (!containsAny(password, NUMERIC_CHARS)) {
                    password = password.substring(0, password.length() - 1) + 
                        NUMERIC_CHARS.charAt(secureRandom.nextInt(NUMERIC_CHARS.length()));
                }
                break;
                
            default:
                throw new IllegalArgumentException("Unrecognized invalidation type: " + invalidationType);
        }
        
        LOGGER.debug("Generated invalid password of type: {}", invalidationType);
        return password;
    }
    
    /**
     * Checks if a password meets the standard complexity requirements.
     * 
     * @param password The password to check
     * @return true if password meets complexity requirements, false otherwise
     */
    public static boolean isPasswordComplex(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        
        boolean hasLowercase = containsAny(password, LOWERCASE_CHARS);
        boolean hasUppercase = containsAny(password, UPPERCASE_CHARS);
        boolean hasDigit = containsAny(password, NUMERIC_CHARS);
        boolean hasSpecial = containsAny(password, SPECIAL_CHARS);
        
        return hasLowercase && hasUppercase && hasDigit && hasSpecial;
    }
    
    /**
     * Generates a password with a UUID component to ensure uniqueness.
     * 
     * @return A unique password containing a UUID component
     */
    public static String generateUniquePassword() {
        // Generate a UUID and take first 8 characters
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        // Ensure we have the required complexity by adding specific character types
        StringBuilder password = new StringBuilder(uuid);
        
        // Add one of each required character type if not already present
        if (!containsAny(password.toString(), UPPERCASE_CHARS)) {
            password.append(UPPERCASE_CHARS.charAt(secureRandom.nextInt(UPPERCASE_CHARS.length())));
        }
        
        if (!containsAny(password.toString(), LOWERCASE_CHARS)) {
            password.append(LOWERCASE_CHARS.charAt(secureRandom.nextInt(LOWERCASE_CHARS.length())));
        }
        
        if (!containsAny(password.toString(), NUMERIC_CHARS)) {
            password.append(NUMERIC_CHARS.charAt(secureRandom.nextInt(NUMERIC_CHARS.length())));
        }
        
        if (!containsAny(password.toString(), SPECIAL_CHARS)) {
            password.append(SPECIAL_CHARS.charAt(secureRandom.nextInt(SPECIAL_CHARS.length())));
        }
        
        LOGGER.debug("Generated unique password with UUID component");
        return password.toString();
    }
    
    /**
     * Checks if a string contains any character from the specified character set.
     * 
     * @param str The string to check
     * @param chars The character set to check against
     * @return true if the string contains any character from the set, false otherwise
     */
    private static boolean containsAny(String str, String chars) {
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (chars.indexOf(ch) >= 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sets a random position in the StringBuilder to the specified character.
     * 
     * @param sb The StringBuilder to modify
     * @param c The character to insert
     */
    private static void setRandomCharAt(StringBuilder sb, char c) {
        int index = secureRandom.nextInt(sb.length());
        sb.setCharAt(index, c);
    }
    
    /**
     * Shuffles the characters in a string to create a randomized version.
     * 
     * @param input The string to shuffle
     * @return A shuffled version of the input string
     */
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}