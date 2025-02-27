package com.storydoc.models;

import com.storydoc.utils.RandomDataGenerator;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.Objects;
import lombok.Builder; // version: 1.18.26

/**
 * Model class representing a user account for Storydoc signup automation.
 * Encapsulates user account data including email, password, and terms acceptance status,
 * providing methods to create and manipulate account data for test cases.
 */
public class UserAccount {
    
    private static final Logger LOGGER = LogManager.getLogger(UserAccount.class);
    
    private String email;
    private String password;
    private boolean termsAccepted;
    
    /**
     * Creates a new UserAccount instance with the specified properties
     * 
     * @param email The email address for the account
     * @param password The password for the account
     * @param termsAccepted Whether terms and conditions are accepted
     */
    public UserAccount(String email, String password, boolean termsAccepted) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        this.email = email;
        this.password = password;
        this.termsAccepted = termsAccepted;
    }
    
    /**
     * Creates a user account with randomly generated valid credentials
     * 
     * @return A new UserAccount instance with valid random data
     */
    public static UserAccount createValidAccount() {
        String email = RandomDataGenerator.generateRandomEmail();
        String password = RandomDataGenerator.generateRandomPassword();
        UserAccount account = new UserAccount(email, password, true);
        LOGGER.info("Created valid random account with email: {}", email);
        return account;
    }
    
    /**
     * Creates a user account with specified credentials
     * 
     * @param email The email address for the account
     * @param password The password for the account
     * @return A new UserAccount instance with specified credentials and terms accepted
     */
    public static UserAccount createCustomAccount(String email, String password) {
        UserAccount account = new UserAccount(email, password, true);
        LOGGER.info("Created custom account with email: {}", email);
        return account;
    }
    
    /**
     * Creates a user account with specified credentials and terms acceptance
     * 
     * @param email The email address for the account
     * @param password The password for the account
     * @param termsAccepted Whether terms and conditions are accepted
     * @return A new UserAccount instance with specified credentials and terms acceptance
     */
    public static UserAccount createCustomAccount(String email, String password, boolean termsAccepted) {
        UserAccount account = new UserAccount(email, password, termsAccepted);
        LOGGER.info("Created fully custom account with email: {}, terms accepted: {}", email, termsAccepted);
        return account;
    }
    
    /**
     * Gets the email address of the user account
     * 
     * @return The email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Gets the password of the user account
     * 
     * @return The password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Checks if terms and conditions are accepted
     * 
     * @return True if terms are accepted, false otherwise
     */
    public boolean isTermsAccepted() {
        return termsAccepted;
    }
    
    /**
     * Sets the email address for the user account
     * 
     * @param email The email address to set
     */
    public void setEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        this.email = email;
    }
    
    /**
     * Sets the password for the user account
     * 
     * @param password The password to set
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password;
    }
    
    /**
     * Sets whether terms and conditions are accepted
     * 
     * @param termsAccepted True if terms are accepted, false otherwise
     */
    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }
    
    /**
     * Returns a string representation of the user account (securely masks password)
     * 
     * @return String representation of the user account
     */
    @Override
    public String toString() {
        // Mask password with asterisks for security
        String maskedPassword = password != null ? "*".repeat(password.length()) : "null";
        
        return "UserAccount{" +
                "email='" + email + '\'' +
                ", password='" + maskedPassword + '\'' +
                ", termsAccepted=" + termsAccepted +
                '}';
    }
    
    /**
     * Builder class for creating UserAccount instances with a fluent API
     */
    public static class UserAccountBuilder {
        private String email;
        private String password;
        private boolean termsAccepted;
        
        /**
         * Default constructor for the builder
         */
        public UserAccountBuilder() {
            // Set termsAccepted to true as default for most test cases
            this.termsAccepted = true;
        }
        
        /**
         * Sets the email for the user account being built
         * 
         * @param email The email to set
         * @return This builder instance for method chaining
         */
        public UserAccountBuilder email(String email) {
            this.email = email;
            return this;
        }
        
        /**
         * Sets the password for the user account being built
         * 
         * @param password The password to set
         * @return This builder instance for method chaining
         */
        public UserAccountBuilder password(String password) {
            this.password = password;
            return this;
        }
        
        /**
         * Sets whether terms are accepted for the user account being built
         * 
         * @param termsAccepted True if terms are accepted, false otherwise
         * @return This builder instance for method chaining
         */
        public UserAccountBuilder termsAccepted(boolean termsAccepted) {
            this.termsAccepted = termsAccepted;
            return this;
        }
        
        /**
         * Builds a UserAccount instance with the configured properties
         * 
         * @return A UserAccount instance with the configured properties
         * @throws IllegalStateException if email or password is missing
         */
        public UserAccount build() {
            if (email == null || email.isEmpty()) {
                throw new IllegalStateException("Email must be provided");
            }
            if (password == null || password.isEmpty()) {
                throw new IllegalStateException("Password must be provided");
            }
            
            return new UserAccount(email, password, termsAccepted);
        }
    }
}