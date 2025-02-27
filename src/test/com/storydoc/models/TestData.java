package com.storydoc.models;

import com.storydoc.utils.EmailGenerator;
import com.storydoc.utils.PasswordGenerator;
import com.storydoc.utils.FileUtils;
import com.storydoc.config.ConfigurationManager;

import org.json.JSONObject; // v20230227
import org.json.JSONArray; // v20230227
import org.apache.logging.log4j.Logger; // v2.19.0
import org.apache.logging.log4j.LogManager; // v2.19.0

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;

/**
 * A utility class that manages test data for Storydoc signup automation tests.
 * Provides methods for generating, loading, and managing test data such as emails,
 * passwords, and user account information that are required for testing the signup process.
 */
public class TestData {

    private static final Logger LOGGER = LogManager.getLogger(TestData.class);
    private static final String TEST_DATA_FILE = "testdata.json";
    private static final String EMAIL_DOMAIN = "example.com";
    private static final int DEFAULT_PASSWORD_LENGTH = 12;

    /**
     * Constants class for account data property keys
     */
    public static final class AccountDataKeys {
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String ACCEPTED_TERMS = "acceptedTerms";

        /**
         * Private constructor to prevent instantiation
         */
        private AccountDataKeys() {
            throw new IllegalStateException("Utility class - should not be instantiated");
        }
    }

    /**
     * Generates a unique email address using a timestamp suffix
     *
     * @return A unique email address for test purposes
     */
    public static String generateUniqueEmail() {
        String email = EmailGenerator.generateEmail();
        LOGGER.info("Generated unique email: {}", email);
        return email;
    }

    /**
     * Generates a unique email address with a custom prefix
     *
     * @param prefix The prefix to use in the email address
     * @return A unique email address with the specified prefix
     */
    public static String generateUniqueEmailWithPrefix(String prefix) {
        String email = EmailGenerator.generateEmailWithPrefix(prefix);
        LOGGER.info("Generated unique email with prefix '{}': {}", prefix, email);
        return email;
    }

    /**
     * Generates a valid password that meets Storydoc complexity requirements
     *
     * @return A valid password for test purposes
     */
    public static String generateValidPassword() {
        String password = PasswordGenerator.generatePassword();
        LOGGER.info("Generated valid password (not logged for security)");
        return password;
    }

    /**
     * Generates a custom password with specified length
     *
     * @param length The desired length of the password
     * @return A password with the specified length
     */
    public static String generateCustomPassword(int length) {
        String password = PasswordGenerator.generatePasswordWithLength(length);
        LOGGER.info("Generated password with length: {}", length);
        return password;
    }

    /**
     * Loads test data from the JSON test data file
     *
     * @return The loaded test data as a JSONObject
     */
    public static JSONObject loadTestDataFromJson() {
        try {
            String dataDirectory = FileUtils.getDataDirectory();
            Path filePath = Paths.get(dataDirectory, TEST_DATA_FILE);
            String fileContent = FileUtils.readFile(filePath.toString());
            JSONObject testData = new JSONObject(fileContent);
            LOGGER.info("Successfully loaded test data from: {}", filePath);
            return testData;
        } catch (IOException e) {
            LOGGER.error("Failed to load test data file: {}", TEST_DATA_FILE, e);
            return new JSONObject();
        }
    }

    /**
     * Retrieves a specific test data item by its key from the JSON test data
     *
     * @param key The key to retrieve data for
     * @return The test data value for the specified key
     */
    public static Object getTestDataByKey(String key) {
        JSONObject testData = loadTestDataFromJson();
        if (testData.has(key)) {
            Object value = testData.get(key);
            LOGGER.debug("Retrieved test data for key: {}", key);
            return value;
        } else {
            LOGGER.warn("Key not found in test data: {}", key);
            return null;
        }
    }

    /**
     * Creates an account data Map populated with data from the test data file
     *
     * @param accountKey The key in the test data file for the account to retrieve
     * @return A Map containing account data properties from the test data file
     */
    public static Map<String, Object> createAccountDataFromTestData(String accountKey) {
        JSONObject testData = loadTestDataFromJson();
        Map<String, Object> accountData = new HashMap<>();
        
        if (testData.has("accounts") && testData.getJSONObject("accounts").has(accountKey)) {
            JSONObject account = testData.getJSONObject("accounts").getJSONObject(accountKey);
            
            accountData.put(AccountDataKeys.EMAIL, account.getString(AccountDataKeys.EMAIL));
            accountData.put(AccountDataKeys.PASSWORD, account.getString(AccountDataKeys.PASSWORD));
            accountData.put(AccountDataKeys.ACCEPTED_TERMS, account.getBoolean(AccountDataKeys.ACCEPTED_TERMS));
            
            LOGGER.info("Created account data from test data key: {}", accountKey);
        } else {
            LOGGER.warn("Account key not found in test data: {}", accountKey);
        }
        
        return accountData;
    }

    /**
     * Creates a Map with randomly generated valid account data
     *
     * @return A Map with randomly generated account data
     */
    public static Map<String, Object> createRandomAccountData() {
        String email = generateUniqueEmail();
        String password = generateValidPassword();
        
        Map<String, Object> accountData = new HashMap<>();
        accountData.put(AccountDataKeys.EMAIL, email);
        accountData.put(AccountDataKeys.PASSWORD, password);
        accountData.put(AccountDataKeys.ACCEPTED_TERMS, true);
        
        LOGGER.info("Created random account data with email: {}", email);
        return accountData;
    }

    /**
     * Creates a Map with custom account data based on provided parameters
     *
     * @param email The email address to use
     * @param password The password to use
     * @param acceptTerms Whether terms are accepted
     * @return A Map with custom account data
     */
    public static Map<String, Object> createCustomAccountData(String email, String password, boolean acceptTerms) {
        Map<String, Object> accountData = new HashMap<>();
        accountData.put(AccountDataKeys.EMAIL, email);
        accountData.put(AccountDataKeys.PASSWORD, password);
        accountData.put(AccountDataKeys.ACCEPTED_TERMS, acceptTerms);
        
        LOGGER.info("Created custom account data with email: {}", email);
        return accountData;
    }
}