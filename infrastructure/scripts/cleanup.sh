#!/bin/bash

# Script to clean up test artifacts, temporary files, and processes
# after test execution in both local and CI/CD environments

# Set base directory and derived paths
BASE_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/../..
TEST_RESULTS_DIR="${BASE_DIR}/src/test/results"
REPORTS_DIR="${BASE_DIR}/target/extent-reports"
SCREENSHOTS_DIR="${BASE_DIR}/src/test/screenshots"
LOGS_DIR="${BASE_DIR}/src/test/logs"
DOCKER_COMPOSE_GRID="${BASE_DIR}/infrastructure/selenium-grid/docker-compose.yml"
DOCKER_COMPOSE_APP="${BASE_DIR}/infrastructure/docker/docker-compose.yml"

# Print script usage information
print_usage() {
    echo "Usage: $(basename "$0") [OPTION]"
    echo "Clean up test artifacts, temporary files, and processes after test execution."
    echo ""
    echo "Options:"
    echo "  all        Clean up everything (results, screenshots, logs, grid, docker, webdriver, temp)"
    echo "  results    Clean up test result files and reports"
    echo "  screenshots Clean up screenshot files"
    echo "  logs       Clean up log files"
    echo "  grid       Stop Selenium Grid Docker containers"
    echo "  docker     Stop application Docker containers"
    echo "  webdriver  Kill orphaned WebDriver processes"
    echo "  temp       Clean up temporary files"
    echo "  help       Display this help message"
    echo ""
    echo "Examples:"
    echo "  $(basename "$0") all"
    echo "  $(basename "$0") results screenshots"
}

# Clean up test result files and reports
cleanup_test_results() {
    echo "Cleaning up test results..."
    local status=0
    
    # Clean up test results directory
    if [ -d "$TEST_RESULTS_DIR" ]; then
        echo "Removing files from $TEST_RESULTS_DIR"
        rm -rf "$TEST_RESULTS_DIR"/* || status=1
    else
        echo "Test results directory does not exist: $TEST_RESULTS_DIR"
        mkdir -p "$TEST_RESULTS_DIR"
    fi
    
    # Clean up reports directory
    if [ -d "$REPORTS_DIR" ]; then
        echo "Removing files from $REPORTS_DIR"
        rm -rf "$REPORTS_DIR"/* || status=1
    else
        echo "Reports directory does not exist: $REPORTS_DIR"
        mkdir -p "$REPORTS_DIR"
    fi
    
    if [ $status -eq 0 ]; then
        echo "Test results cleanup completed successfully."
    else
        echo "Test results cleanup completed with errors."
    fi
    
    return $status
}

# Clean up screenshot files
cleanup_screenshots() {
    echo "Cleaning up screenshots..."
    local status=0
    
    if [ -d "$SCREENSHOTS_DIR" ]; then
        echo "Removing files from $SCREENSHOTS_DIR"
        rm -rf "$SCREENSHOTS_DIR"/* || status=1
    else
        echo "Screenshots directory does not exist: $SCREENSHOTS_DIR"
        mkdir -p "$SCREENSHOTS_DIR"
    fi
    
    if [ $status -eq 0 ]; then
        echo "Screenshots cleanup completed successfully."
    else
        echo "Screenshots cleanup completed with errors."
    fi
    
    return $status
}

# Clean up log files
cleanup_logs() {
    echo "Cleaning up logs..."
    local status=0
    
    if [ -d "$LOGS_DIR" ]; then
        echo "Removing files from $LOGS_DIR"
        rm -rf "$LOGS_DIR"/* || status=1
    else
        echo "Logs directory does not exist: $LOGS_DIR"
        mkdir -p "$LOGS_DIR"
    fi
    
    if [ $status -eq 0 ]; then
        echo "Logs cleanup completed successfully."
    else
        echo "Logs cleanup completed with errors."
    fi
    
    return $status
}

# Stop Selenium Grid Docker containers
stop_selenium_grid() {
    echo "Stopping Selenium Grid containers..."
    local status=0
    
    if [ -f "$DOCKER_COMPOSE_GRID" ]; then
        echo "Running docker-compose down for Selenium Grid..."
        cd "$(dirname "$DOCKER_COMPOSE_GRID")" && docker-compose -f "$(basename "$DOCKER_COMPOSE_GRID")" down || status=1
    else
        echo "Selenium Grid docker-compose file not found: $DOCKER_COMPOSE_GRID"
        status=1
    fi
    
    if [ $status -eq 0 ]; then
        echo "Selenium Grid containers stopped successfully."
    else
        echo "Failed to stop Selenium Grid containers."
    fi
    
    return $status
}

# Stop application Docker containers
stop_docker_containers() {
    echo "Stopping application Docker containers..."
    local status=0
    
    if [ -f "$DOCKER_COMPOSE_APP" ]; then
        echo "Running docker-compose down for application..."
        cd "$(dirname "$DOCKER_COMPOSE_APP")" && docker-compose -f "$(basename "$DOCKER_COMPOSE_APP")" down || status=1
    else
        echo "Application docker-compose file not found: $DOCKER_COMPOSE_APP"
        # This is not always an error, as the application might not be containerized
        echo "Skipping application container cleanup."
    fi
    
    if [ $status -eq 0 ]; then
        echo "Application containers stopped successfully."
    else
        echo "Failed to stop application containers."
    fi
    
    return $status
}

# Kill orphaned WebDriver processes
kill_webdriver_processes() {
    echo "Killing WebDriver processes..."
    local status=0
    
    # Kill chromedriver processes
    if pgrep -x "chromedriver" > /dev/null; then
        echo "Killing chromedriver processes..."
        pkill -9 -x "chromedriver" || status=1
    else
        echo "No chromedriver processes found."
    fi
    
    # Kill geckodriver processes
    if pgrep -x "geckodriver" > /dev/null; then
        echo "Killing geckodriver processes..."
        pkill -9 -x "geckodriver" || status=1
    else
        echo "No geckodriver processes found."
    fi
    
    # Kill edgedriver processes
    if pgrep -x "msedgedriver" > /dev/null; then
        echo "Killing msedgedriver processes..."
        pkill -9 -x "msedgedriver" || status=1
    else
        echo "No msedgedriver processes found."
    fi
    
    # Check for any Java processes that might be WebDriver-related
    java_processes=$(ps aux | grep -i "selenium" | grep -v grep)
    if [ -n "$java_processes" ]; then
        echo "Potential Selenium-related Java processes found:"
        echo "$java_processes"
        echo "Please terminate these manually if needed."
    fi
    
    if [ $status -eq 0 ]; then
        echo "WebDriver processes terminated successfully."
    else
        echo "Failed to terminate some WebDriver processes."
    fi
    
    return $status
}

# Clean up temporary files
cleanup_temp_files() {
    echo "Cleaning up temporary files..."
    local status=0
    local temp_dir=""
    
    # Determine the temp directory based on OS
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        temp_dir="/tmp"
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux
        temp_dir="/tmp"
    elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "win32" ]]; then
        # Windows with Git Bash or similar
        temp_dir="/c/Windows/Temp"
        if [ ! -d "$temp_dir" ]; then
            temp_dir="$TEMP"
        fi
    else
        echo "Unsupported operating system: $OSTYPE"
        return 1
    fi
    
    echo "Using temp directory: $temp_dir"
    
    # Clean up WebDriver temp files
    echo "Removing WebDriver temporary files..."
    if [ -d "$temp_dir" ]; then
        # Remove chromedriver temp files
        find "$temp_dir" -name "scoped_dir*" -type d -exec rm -rf {} \; 2>/dev/null || status=1
        # Remove geckodriver temp files
        find "$temp_dir" -name "rust_mozprofile*" -type d -exec rm -rf {} \; 2>/dev/null || status=1
        # Remove any other selenium temp files
        find "$temp_dir" -name "selenium-*" -exec rm -rf {} \; 2>/dev/null || status=1
    else
        echo "Temp directory does not exist: $temp_dir"
        status=1
    fi
    
    if [ $status -eq 0 ]; then
        echo "Temporary files cleanup completed successfully."
    else
        echo "Temporary files cleanup completed with errors."
    fi
    
    return $status
}

# Main function
main() {
    # Process command line arguments
    if [ $# -eq 0 ] || [ "$1" = "help" ] || [ "$1" = "--help" ] || [ "$1" = "-h" ]; then
        print_usage
        return 0
    fi
    
    local exit_status=0
    local run_all=false
    
    # Check if 'all' is one of the arguments
    for arg in "$@"; do
        if [ "$arg" = "all" ]; then
            run_all=true
            break
        fi
    done
    
    # Run all cleanup functions if 'all' was specified
    if [ "$run_all" = true ]; then
        cleanup_test_results || exit_status=$?
        cleanup_screenshots || exit_status=$?
        cleanup_logs || exit_status=$?
        stop_selenium_grid || exit_status=$?
        stop_docker_containers || exit_status=$?
        kill_webdriver_processes || exit_status=$?
        cleanup_temp_files || exit_status=$?
    else
        # Run specific cleanup functions based on arguments
        for arg in "$@"; do
            case "$arg" in
                "results")
                    cleanup_test_results || exit_status=$?
                    ;;
                "screenshots")
                    cleanup_screenshots || exit_status=$?
                    ;;
                "logs")
                    cleanup_logs || exit_status=$?
                    ;;
                "grid")
                    stop_selenium_grid || exit_status=$?
                    ;;
                "docker")
                    stop_docker_containers || exit_status=$?
                    ;;
                "webdriver")
                    kill_webdriver_processes || exit_status=$?
                    ;;
                "temp")
                    cleanup_temp_files || exit_status=$?
                    ;;
                *)
                    echo "Unknown option: $arg"
                    print_usage
                    return 1
                    ;;
            esac
        done
    fi
    
    if [ $exit_status -eq 0 ]; then
        echo "Cleanup completed successfully."
    else
        echo "Cleanup completed with errors."
    fi
    
    return $exit_status
}

# Call main with all script arguments
main "$@"