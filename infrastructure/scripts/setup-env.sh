#!/bin/bash
# setup-env.sh
# Sets up the test execution environment for the Selenium automation framework

# Exit script immediately if any command exits with non-zero status
set -e

# Define directory paths
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
PROJECT_ROOT=$(cd "${SCRIPT_DIR}/../.." && pwd)
TEST_DIR="${PROJECT_ROOT}/src/test"
CONFIG_DIR="${TEST_DIR}/config"
DRIVERS_DIR="${TEST_DIR}/drivers"
TEST_OUTPUT_DIR="${PROJECT_ROOT}/test-output"
SCREENSHOTS_DIR="${TEST_OUTPUT_DIR}/screenshots"
LOGS_DIR="${TEST_OUTPUT_DIR}/logs"
REPORTS_DIR="${TEST_OUTPUT_DIR}/reports"

# Default values
ENV="staging"
BROWSER="chrome"
HEADLESS="false"
SKIP_DRIVERS="false"
LOG_LEVEL="INFO"

# Version
VERSION="1.0.0"

# Display usage information
print_usage() {
    echo "Usage: $(basename $0) [OPTIONS]"
    echo "Sets up the environment for Selenium test execution"
    echo ""
    echo "Options:"
    echo "  -e ENV      Set environment (dev, staging, prod) [default: staging]"
    echo "  -b BROWSER  Set browser (chrome, firefox, edge) [default: chrome]"
    echo "  -h          Run browser in headless mode [default: false]"
    echo "  -s          Skip browser driver setup [default: false]"
    echo "  -l LEVEL    Set log level (DEBUG, INFO, WARN, ERROR) [default: INFO]"
    echo "  -?          Display this help and exit"
    echo ""
    echo "Examples:"
    echo "  $(basename $0) -e staging -b chrome"
    echo "  $(basename $0) -e prod -b firefox -h"
    echo ""
    echo "Supported environments: dev, staging, prod"
    echo "Supported browsers: chrome, firefox, edge"
}

# Parse command-line arguments
parse_arguments() {
    while getopts "e:b:hsl:?" opt; do
        case ${opt} in
            e)
                ENV=$OPTARG
                if [[ ! "$ENV" =~ ^(dev|staging|prod)$ ]]; then
                    echo "Error: Invalid environment '$ENV'. Use dev, staging, or prod."
                    print_usage
                    return 1
                fi
                ;;
            b)
                BROWSER=$OPTARG
                if [[ ! "$BROWSER" =~ ^(chrome|firefox|edge)$ ]]; then
                    echo "Error: Invalid browser '$BROWSER'. Use chrome, firefox, or edge."
                    print_usage
                    return 1
                fi
                ;;
            h)
                HEADLESS="true"
                ;;
            s)
                SKIP_DRIVERS="true"
                ;;
            l)
                LOG_LEVEL=$OPTARG
                if [[ ! "$LOG_LEVEL" =~ ^(DEBUG|INFO|WARN|ERROR)$ ]]; then
                    echo "Error: Invalid log level '$LOG_LEVEL'. Use DEBUG, INFO, WARN, or ERROR."
                    print_usage
                    return 1
                fi
                ;;
            \?)
                print_usage
                return 1
                ;;
        esac
    done

    echo "Environment: $ENV"
    echo "Browser: $BROWSER"
    echo "Headless mode: $HEADLESS"
    echo "Skip driver setup: $SKIP_DRIVERS"
    echo "Log level: $LOG_LEVEL"
    
    return 0
}

# Check system requirements
check_system_requirements() {
    echo "Checking system requirements..."
    
    # Check Java version
    if ! command -v java &> /dev/null; then
        echo "Error: Java is not installed."
        return 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed 's/^1\.//' | cut -d'.' -f1)
    if [[ "$JAVA_VERSION" -lt 11 ]]; then
        echo "Error: Java 11 or higher is required. Found version: $JAVA_VERSION"
        return 1
    fi
    echo "✓ Java version check passed (Java $JAVA_VERSION)"
    
    # Check Maven version
    if ! command -v mvn &> /dev/null; then
        echo "Error: Maven is not installed."
        return 1
    fi
    
    MVN_VERSION=$(mvn --version | head -1 | awk '{print $3}')
    MVN_MAJOR=$(echo $MVN_VERSION | cut -d'.' -f1)
    MVN_MINOR=$(echo $MVN_VERSION | cut -d'.' -f2)
    
    if [[ "$MVN_MAJOR" -lt 3 ]] || [[ "$MVN_MAJOR" -eq 3 && "$MVN_MINOR" -lt 8 ]]; then
        echo "Error: Maven 3.8 or higher is required. Found version: $MVN_VERSION"
        return 1
    fi
    echo "✓ Maven version check passed (Maven $MVN_VERSION)"
    
    # Check available memory (simple check for Linux/macOS)
    if command -v free &> /dev/null; then
        TOTAL_MEM_KB=$(free | grep -i 'mem:' | awk '{print $2}')
        TOTAL_MEM_GB=$((TOTAL_MEM_KB / 1024 / 1024))
        
        if [[ "$TOTAL_MEM_GB" -lt 8 ]]; then
            echo "Warning: Less than 8GB RAM available ($TOTAL_MEM_GB GB). This may impact performance."
        else
            echo "✓ Memory check passed ($TOTAL_MEM_GB GB available)"
        fi
    elif command -v sysctl &> /dev/null && [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS memory check
        TOTAL_MEM_B=$(sysctl -n hw.memsize)
        TOTAL_MEM_GB=$((TOTAL_MEM_B / 1024 / 1024 / 1024))
        
        if [[ "$TOTAL_MEM_GB" -lt 8 ]]; then
            echo "Warning: Less than 8GB RAM available ($TOTAL_MEM_GB GB). This may impact performance."
        else
            echo "✓ Memory check passed ($TOTAL_MEM_GB GB available)"
        fi
    else
        echo "Warning: Could not determine available memory. At least 8GB RAM is recommended."
    fi
    
    # Check available disk space
    if command -v df &> /dev/null; then
        FREE_SPACE_KB=$(df -k "$PROJECT_ROOT" | tail -1 | awk '{print $4}')
        FREE_SPACE_GB=$((FREE_SPACE_KB / 1024 / 1024))
        
        if [[ "$FREE_SPACE_GB" -lt 10 ]]; then
            echo "Warning: Less than 10GB free disk space available ($FREE_SPACE_GB GB). This may impact performance."
        else
            echo "✓ Disk space check passed ($FREE_SPACE_GB GB available)"
        fi
    else
        echo "Warning: Could not determine available disk space. At least 10GB free space is recommended."
    fi
    
    return 0
}

# Create necessary directories
create_directories() {
    echo "Creating necessary directories..."
    
    mkdir -p "$TEST_OUTPUT_DIR"
    echo "✓ Created test output directory: $TEST_OUTPUT_DIR"
    
    mkdir -p "$SCREENSHOTS_DIR"
    echo "✓ Created screenshots directory: $SCREENSHOTS_DIR"
    
    mkdir -p "$LOGS_DIR"
    echo "✓ Created logs directory: $LOGS_DIR"
    
    mkdir -p "$REPORTS_DIR"
    echo "✓ Created reports directory: $REPORTS_DIR"
    
    if [[ "$SKIP_DRIVERS" == "false" ]]; then
        mkdir -p "$DRIVERS_DIR"
        echo "✓ Created drivers directory: $DRIVERS_DIR"
    fi
    
    # Set appropriate permissions
    chmod -R 755 "$TEST_OUTPUT_DIR"
    echo "✓ Set permissions for output directories"
}

# Setup browser drivers
setup_browser_drivers() {
    if [[ "$SKIP_DRIVERS" == "true" ]]; then
        echo "Skipping browser driver setup as requested."
        return 0
    fi
    
    echo "Setting up browser drivers..."
    
    # Determine OS type
    OS_TYPE="linux"
    if [[ "$OSTYPE" == "darwin"* ]]; then
        OS_TYPE="mac"
    elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" || "$OSTYPE" == "win32" ]]; then
        OS_TYPE="win"
    fi
    
    case "$BROWSER" in
        chrome)
            echo "Setting up ChromeDriver..."
            # Get latest chromedriver version
            CHROME_DRIVER_VERSION=$(curl -s "https://chromedriver.storage.googleapis.com/LATEST_RELEASE")
            
            # Download appropriate driver
            CHROME_DRIVER_URL="https://chromedriver.storage.googleapis.com/$CHROME_DRIVER_VERSION/chromedriver_"
            
            if [[ "$OS_TYPE" == "linux" ]]; then
                CHROME_DRIVER_URL+="linux64.zip"
            elif [[ "$OS_TYPE" == "mac" ]]; then
                CHROME_DRIVER_URL+="mac64.zip"
            elif [[ "$OS_TYPE" == "win" ]]; then
                CHROME_DRIVER_URL+="win32.zip"
            fi
            
            TEMP_DIR=$(mktemp -d)
            DOWNLOAD_FILE="$TEMP_DIR/chromedriver.zip"
            
            echo "Downloading ChromeDriver from: $CHROME_DRIVER_URL"
            curl -L -o "$DOWNLOAD_FILE" "$CHROME_DRIVER_URL"
            
            # Extract driver
            unzip -o "$DOWNLOAD_FILE" -d "$DRIVERS_DIR"
            rm -f "$DOWNLOAD_FILE"
            
            # Set executable permission
            if [[ "$OS_TYPE" != "win" ]]; then
                chmod +x "$DRIVERS_DIR/chromedriver"
            fi
            
            echo "✓ ChromeDriver setup complete (version $CHROME_DRIVER_VERSION)"
            ;;
            
        firefox)
            echo "Setting up GeckoDriver..."
            # Get latest geckodriver version
            FIREFOX_DRIVER_VERSION=$(curl -s https://api.github.com/repos/mozilla/geckodriver/releases/latest | grep tag_name | cut -d '"' -f 4)
            
            # Download appropriate driver
            FIREFOX_DRIVER_URL="https://github.com/mozilla/geckodriver/releases/download/$FIREFOX_DRIVER_VERSION/geckodriver-$FIREFOX_DRIVER_VERSION-"
            
            if [[ "$OS_TYPE" == "linux" ]]; then
                FIREFOX_DRIVER_URL+="linux64.tar.gz"
            elif [[ "$OS_TYPE" == "mac" ]]; then
                FIREFOX_DRIVER_URL+="macos.tar.gz"
            elif [[ "$OS_TYPE" == "win" ]]; then
                FIREFOX_DRIVER_URL+="win64.zip"
            fi
            
            TEMP_DIR=$(mktemp -d)
            DOWNLOAD_FILE="$TEMP_DIR/geckodriver.archive"
            
            echo "Downloading GeckoDriver from: $FIREFOX_DRIVER_URL"
            curl -L -o "$DOWNLOAD_FILE" "$FIREFOX_DRIVER_URL"
            
            # Extract driver
            if [[ "$FIREFOX_DRIVER_URL" == *".tar.gz" ]]; then
                tar -xzf "$DOWNLOAD_FILE" -C "$DRIVERS_DIR"
            else
                unzip -o "$DOWNLOAD_FILE" -d "$DRIVERS_DIR"
            fi
            rm -f "$DOWNLOAD_FILE"
            
            # Set executable permission
            if [[ "$OS_TYPE" != "win" ]]; then
                chmod +x "$DRIVERS_DIR/geckodriver"
            fi
            
            echo "✓ GeckoDriver setup complete (version $FIREFOX_DRIVER_VERSION)"
            ;;
            
        edge)
            echo "Setting up EdgeDriver..."
            # Get latest edgedriver version using curl and grep
            # This is simplified - in a real scenario you might need more robust version detection
            EDGE_DRIVER_VERSION="latest"
            
            # Download appropriate driver
            EDGE_DRIVER_URL="https://msedgedriver.azureedge.net/$EDGE_DRIVER_VERSION/edgedriver_"
            
            if [[ "$OS_TYPE" == "linux" ]]; then
                EDGE_DRIVER_URL+="linux64.zip"
            elif [[ "$OS_TYPE" == "mac" ]]; then
                EDGE_DRIVER_URL+="mac64.zip"
            elif [[ "$OS_TYPE" == "win" ]]; then
                EDGE_DRIVER_URL+="win64.zip"
            fi
            
            TEMP_DIR=$(mktemp -d)
            DOWNLOAD_FILE="$TEMP_DIR/edgedriver.zip"
            
            echo "Downloading EdgeDriver from: $EDGE_DRIVER_URL"
            curl -L -o "$DOWNLOAD_FILE" "$EDGE_DRIVER_URL"
            
            # Extract driver
            unzip -o "$DOWNLOAD_FILE" -d "$DRIVERS_DIR"
            rm -f "$DOWNLOAD_FILE"
            
            # Set executable permission
            if [[ "$OS_TYPE" != "win" ]]; then
                chmod +x "$DRIVERS_DIR/msedgedriver"
            fi
            
            echo "✓ EdgeDriver setup complete"
            ;;
    esac
    
    return 0
}

# Configure environment
configure_environment() {
    echo "Configuring environment for $ENV..."
    
    # Ensure config directory exists
    mkdir -p "$CONFIG_DIR"
    
    # Create or update environment-specific properties file
    ENV_PROPERTIES_FILE="$CONFIG_DIR/${ENV}.properties"
    ACTIVE_PROPERTIES_FILE="$CONFIG_DIR/active.properties"
    
    # Create environment properties file if it doesn't exist
    if [[ ! -f "$ENV_PROPERTIES_FILE" ]]; then
        echo "Creating environment properties file for $ENV..."
        
        cat > "$ENV_PROPERTIES_FILE" << EOF
# Auto-generated configuration for $ENV environment
# Created by setup-env.sh on $(date)

# Environment settings
environment=$ENV
base.url=https://editor-staging.storydoc.com
signup.url=https://editor-staging.storydoc.com/sign-up

# Browser settings
browser=$BROWSER
headless=$HEADLESS

# Timeouts
timeout.seconds=10
page.load.timeout=30

# Reporting
screenshots.dir=$SCREENSHOTS_DIR
reports.dir=$REPORTS_DIR
logs.dir=$LOGS_DIR

# Driver settings
webdriver.manager.enabled=true
webdriver.drivers.path=$DRIVERS_DIR
EOF

        if [[ "$ENV" == "dev" ]]; then
            # Dev-specific settings
            sed -i.bak "s#base.url=.*#base.url=https://editor-dev.storydoc.com#g" "$ENV_PROPERTIES_FILE"
            sed -i.bak "s#signup.url=.*#signup.url=https://editor-dev.storydoc.com/sign-up#g" "$ENV_PROPERTIES_FILE"
        elif [[ "$ENV" == "prod" ]]; then
            # Prod-specific settings
            sed -i.bak "s#base.url=.*#base.url=https://editor.storydoc.com#g" "$ENV_PROPERTIES_FILE"
            sed -i.bak "s#signup.url=.*#signup.url=https://editor.storydoc.com/sign-up#g" "$ENV_PROPERTIES_FILE"
        fi
        
        # Clean up backup files if they exist
        rm -f "$ENV_PROPERTIES_FILE.bak"
    fi
    
    # Copy environment properties to active properties
    cp "$ENV_PROPERTIES_FILE" "$ACTIVE_PROPERTIES_FILE"
    
    # Update browser settings in active properties
    sed -i.bak "s/browser=.*/browser=$BROWSER/g" "$ACTIVE_PROPERTIES_FILE"
    sed -i.bak "s/headless=.*/headless=$HEADLESS/g" "$ACTIVE_PROPERTIES_FILE"
    
    # Clean up backup files
    rm -f "$ACTIVE_PROPERTIES_FILE.bak"
    
    # Update webdriver.manager.enabled based on SKIP_DRIVERS
    if [[ "$SKIP_DRIVERS" == "true" ]]; then
        sed -i.bak "s/webdriver.manager.enabled=.*/webdriver.manager.enabled=true/g" "$ACTIVE_PROPERTIES_FILE"
    else
        sed -i.bak "s/webdriver.manager.enabled=.*/webdriver.manager.enabled=false/g" "$ACTIVE_PROPERTIES_FILE"
    fi
    rm -f "$ACTIVE_PROPERTIES_FILE.bak"
    
    # Configure log4j2 settings
    LOG4J_CONFIG_FILE="$CONFIG_DIR/log4j2.xml"
    
    if [[ ! -f "$LOG4J_CONFIG_FILE" || "$LOG_LEVEL" != "INFO" ]]; then
        echo "Creating/updating log4j2.xml with log level $LOG_LEVEL..."
        
        cat > "$LOG4J_CONFIG_FILE" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
        <File name="File" fileName="${LOGS_DIR}/selenium-test.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="$LOG_LEVEL">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
EOF
    fi
    
    echo "✓ Environment configured successfully"
    return 0
}

# Main function
main() {
    echo "======================================================"
    echo "Selenium Automation Framework Environment Setup v$VERSION"
    echo "======================================================"
    
    # Parse command-line arguments
    parse_arguments "$@"
    if [[ $? -ne 0 ]]; then
        return 1
    fi
    
    # Check system requirements
    check_system_requirements
    if [[ $? -ne 0 ]]; then
        echo "Error: System requirements check failed. Please fix the issues and try again."
        return 1
    fi
    
    # Create necessary directories
    create_directories
    
    # Setup browser drivers if needed
    if [[ "$SKIP_DRIVERS" == "false" ]]; then
        setup_browser_drivers
        if [[ $? -ne 0 ]]; then
            echo "Error: Browser driver setup failed. Please check the logs for details."
            return 1
        fi
    fi
    
    # Configure environment
    configure_environment
    if [[ $? -ne 0 ]]; then
        echo "Error: Environment configuration failed. Please check the logs for details."
        return 1
    fi
    
    echo ""
    echo "======================================================"
    echo "Setup completed successfully!"
    echo ""
    echo "You can now run tests with the following command:"
    echo "mvn test -Denv=$ENV -Dbrowser=$BROWSER -Dheadless=$HEADLESS"
    echo "======================================================"
    
    return 0
}

# Call main function with all arguments
main "$@"