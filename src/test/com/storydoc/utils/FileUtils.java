package com.storydoc.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.Logger; // v2.19.0
import org.apache.logging.log4j.LogManager; // v2.19.0

import com.storydoc.config.ConfigurationManager;
import com.storydoc.constants.MessageConstants;
import com.storydoc.exceptions.FrameworkException;
import com.storydoc.utils.LogUtils;

/**
 * Utility class providing file system operations for the Selenium test automation framework.
 * Handles file creation, reading, writing, and directory management operations to support
 * test execution, reporting, and data management.
 */
public class FileUtils {
    
    private static final Logger LOGGER = LogManager.getLogger(FileUtils.class);
    private static final String TEST_RESOURCES_DIR = "src/test/resources";
    private static final String SCREENSHOTS_DIR = TEST_RESOURCES_DIR + "/screenshots";
    private static final String REPORTS_DIR = TEST_RESOURCES_DIR + "/reports";
    private static final String TEST_DATA_DIR = TEST_RESOURCES_DIR + "/data";
    private static final String LOGS_DIR = TEST_RESOURCES_DIR + "/logs";
    
    /**
     * Creates a directory if it doesn't already exist
     * 
     * @param directoryPath Path of the directory to create
     * @return True if directory exists or was successfully created, false otherwise
     */
    public static boolean createDirectory(String directoryPath) {
        LOGGER.debug("Creating directory: {}", directoryPath);
        File directory = new File(directoryPath);
        
        if (directory.exists()) {
            LOGGER.debug("Directory already exists: {}", directoryPath);
            return true;
        }
        
        try {
            boolean result = directory.mkdirs();
            LogUtils.info(FileUtils.class, "Directory creation result for " + directoryPath + ": " + result);
            return result;
        } catch (IOException | SecurityException e) {
            LogUtils.error(FileUtils.class, "Failed to create directory: " + directoryPath, e);
            return false;
        }
    }
    
    /**
     * Gets the path to the screenshots directory, creating it if it doesn't exist
     * 
     * @return The absolute path to the screenshots directory
     * @throws FrameworkException if directory creation fails
     */
    public static String getScreenshotsDirectory() {
        LOGGER.debug("Getting screenshots directory");
        
        if (!createDirectory(SCREENSHOTS_DIR)) {
            String errorMessage = "Failed to create screenshots directory: " + SCREENSHOTS_DIR;
            LogUtils.error(errorMessage);
            throw new FrameworkException(errorMessage);
        }
        
        return new File(SCREENSHOTS_DIR).getAbsolutePath();
    }
    
    /**
     * Gets the path to the reports directory, creating it if it doesn't exist
     * 
     * @return The absolute path to the reports directory
     * @throws FrameworkException if directory creation fails
     */
    public static String getReportsDirectory() {
        LOGGER.debug("Getting reports directory");
        
        if (!createDirectory(REPORTS_DIR)) {
            String errorMessage = "Failed to create reports directory: " + REPORTS_DIR;
            LogUtils.error(errorMessage);
            throw new FrameworkException(errorMessage);
        }
        
        return new File(REPORTS_DIR).getAbsolutePath();
    }
    
    /**
     * Gets the path to the test data directory, creating it if it doesn't exist
     * 
     * @return The absolute path to the test data directory
     * @throws FrameworkException if directory creation fails
     */
    public static String getTestDataDirectory() {
        LOGGER.debug("Getting test data directory");
        
        if (!createDirectory(TEST_DATA_DIR)) {
            String errorMessage = "Failed to create test data directory: " + TEST_DATA_DIR;
            LogUtils.error(errorMessage);
            throw new FrameworkException(errorMessage);
        }
        
        return new File(TEST_DATA_DIR).getAbsolutePath();
    }
    
    /**
     * Gets the path to the logs directory, creating it if it doesn't exist
     * 
     * @return The absolute path to the logs directory
     * @throws FrameworkException if directory creation fails
     */
    public static String getLogsDirectory() {
        LOGGER.debug("Getting logs directory");
        
        if (!createDirectory(LOGS_DIR)) {
            String errorMessage = "Failed to create logs directory: " + LOGS_DIR;
            LogUtils.error(errorMessage);
            throw new FrameworkException(errorMessage);
        }
        
        return new File(LOGS_DIR).getAbsolutePath();
    }
    
    /**
     * Reads the contents of a file as a string
     * 
     * @param filePath Path to the file to read
     * @return The contents of the file as a string
     * @throws FrameworkException if file cannot be read
     */
    public static String readFile(String filePath) {
        LOGGER.debug("Reading file: {}", filePath);
        File file = new File(filePath);
        
        if (!file.exists()) {
            String errorMessage = MessageConstants.ERROR_FILE_NOT_FOUND + ": " + filePath;
            LogUtils.error(errorMessage);
            throw new FrameworkException(errorMessage);
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            return content.toString();
        } catch (IOException e) {
            String errorMessage = MessageConstants.ERROR_FILE_OPERATION + ": " + filePath;
            LogUtils.error(errorMessage, e);
            throw new FrameworkException(errorMessage, e);
        }
    }
    
    /**
     * Writes a string to a file, overwriting any existing content
     * 
     * @param filePath Path to the file to write
     * @param content Content to write to the file
     * @return True if the write operation was successful, false otherwise
     */
    public static boolean writeFile(String filePath, String content) {
        LOGGER.debug("Writing to file: {}", filePath);
        File file = new File(filePath);
        
        // Ensure parent directories exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            createDirectory(parentDir.getPath());
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(content);
            LogUtils.info(FileUtils.class, "Successfully wrote to file: " + filePath);
            return true;
        } catch (IOException e) {
            LogUtils.error(FileUtils.class, "Failed to write to file: " + filePath, e);
            return false;
        }
    }
    
    /**
     * Appends a string to an existing file, creating the file if it doesn't exist
     * 
     * @param filePath Path to the file to append to
     * @param content Content to append to the file
     * @return True if the append operation was successful, false otherwise
     */
    public static boolean appendToFile(String filePath, String content) {
        LOGGER.debug("Appending to file: {}", filePath);
        File file = new File(filePath);
        
        // Ensure parent directories exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            createDirectory(parentDir.getPath());
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(content);
            LogUtils.info(FileUtils.class, "Successfully appended to file: " + filePath);
            return true;
        } catch (IOException e) {
            LogUtils.error(FileUtils.class, "Failed to append to file: " + filePath, e);
            return false;
        }
    }
    
    /**
     * Copies a file from source to destination
     * 
     * @param sourceFile Source file to copy
     * @param destinationFile Destination file
     * @return True if the copy operation was successful, false otherwise
     * @throws FrameworkException if source file doesn't exist
     */
    public static boolean copyFile(File sourceFile, File destinationFile) {
        LOGGER.debug("Copying file from {} to {}", sourceFile.getPath(), destinationFile.getPath());
        
        if (!sourceFile.exists()) {
            String errorMessage = MessageConstants.ERROR_FILE_NOT_FOUND + ": " + sourceFile.getPath();
            LogUtils.error(errorMessage);
            throw new FrameworkException(errorMessage);
        }
        
        // Ensure parent directories of destination file exist
        File parentDir = destinationFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            createDirectory(parentDir.getPath());
        }
        
        try {
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            LogUtils.info(FileUtils.class, "Successfully copied file from " + sourceFile.getPath() + 
                          " to " + destinationFile.getPath());
            return true;
        } catch (IOException e) {
            LogUtils.error(FileUtils.class, "Failed to copy file from " + sourceFile.getPath() + 
                         " to " + destinationFile.getPath(), e);
            return false;
        }
    }
    
    /**
     * Deletes a file if it exists
     * 
     * @param filePath Path to the file to delete
     * @return True if the file was successfully deleted or didn't exist, false otherwise
     */
    public static boolean deleteFile(String filePath) {
        LOGGER.debug("Deleting file: {}", filePath);
        File file = new File(filePath);
        
        if (!file.exists()) {
            LogUtils.info(FileUtils.class, "File doesn't exist, nothing to delete: " + filePath);
            return true;
        }
        
        try {
            boolean result = file.delete();
            LogUtils.info(FileUtils.class, "File deletion result for " + filePath + ": " + result);
            return result;
        } catch (SecurityException e) {
            LogUtils.error(FileUtils.class, "Failed to delete file: " + filePath, e);
            return false;
        }
    }
    
    /**
     * Checks if a file exists
     * 
     * @param filePath Path to the file to check
     * @return True if the file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
    
    /**
     * Checks if a directory exists
     * 
     * @param directoryPath Path to the directory to check
     * @return True if the directory exists, false otherwise
     */
    public static boolean directoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        return directory.exists() && directory.isDirectory();
    }
    
    /**
     * Gets the absolute path of a file or directory
     * 
     * @param relativePath Relative path to convert to absolute path
     * @return The absolute path
     */
    public static String getAbsolutePath(String relativePath) {
        File file = new File(relativePath);
        return file.getAbsolutePath();
    }
    
    /**
     * Gets the name of a file from its path
     * 
     * @param filePath Path to extract the file name from
     * @return The file name
     */
    public static String getFileName(String filePath) {
        File file = new File(filePath);
        return file.getName();
    }
}