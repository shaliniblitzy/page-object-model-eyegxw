package com.storydoc.utils;

import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Utility class for generating unique and valid email addresses for test data
 * in the Storydoc signup test automation framework.
 */
public class EmailGenerator {
    
    private static final String[] DEFAULT_DOMAINS = {"example.com", "test.com", "storydoc-test.com", "mailinator.com"};
    private static final String[] DEFAULT_PREFIXES = {"test", "user", "qa", "auto", "storydoc"};
    private static final Random random = new Random();
    
    /**
     * Generates a random email address using default settings.
     *
     * @return A randomly generated email address
     */
    public static String generateEmail() {
        return generateTimestampEmail();
    }
    
    /**
     * Generates an email address with a timestamp to ensure uniqueness.
     *
     * @return A timestamp-based unique email address
     */
    public static String generateTimestampEmail() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String prefix = getRandomPrefix();
        String domain = getRandomDomain();
        
        return prefix + "." + timestamp + "@" + domain;
    }
    
    /**
     * Generates an email address with a UUID to ensure uniqueness.
     *
     * @return A UUID-based unique email address
     */
    public static String generateUUIDEmail() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String prefix = getRandomPrefix();
        String domain = getRandomDomain();
        
        return prefix + "." + uuid + "@" + domain;
    }
    
    /**
     * Generates an email address with a specified prefix.
     *
     * @param prefix The prefix to use for the email address
     * @return An email address with the specified prefix
     */
    public static String generateEmailWithPrefix(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String domain = getRandomDomain();
        
        return prefix + "." + timestamp + "@" + domain;
    }
    
    /**
     * Generates an email address with a specified domain.
     *
     * @param domain The domain to use for the email address
     * @return An email address with the specified domain
     */
    public static String generateEmailWithDomain(String domain) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String prefix = getRandomPrefix();
        
        return prefix + "." + timestamp + "@" + domain;
    }
    
    /**
     * Generates an email address with a specified prefix and domain.
     *
     * @param prefix The prefix to use for the email address
     * @param domain The domain to use for the email address
     * @return An email address with the specified prefix and domain
     */
    public static String generateCustomEmail(String prefix, String domain) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        
        return prefix + "." + timestamp + "@" + domain;
    }
    
    /**
     * Returns a random domain from the default domains list.
     *
     * @return A randomly selected domain
     */
    private static String getRandomDomain() {
        int index = random.nextInt(DEFAULT_DOMAINS.length);
        return DEFAULT_DOMAINS[index];
    }
    
    /**
     * Returns a random prefix from the default prefixes list.
     *
     * @return A randomly selected prefix
     */
    private static String getRandomPrefix() {
        int index = random.nextInt(DEFAULT_PREFIXES.length);
        return DEFAULT_PREFIXES[index];
    }
}