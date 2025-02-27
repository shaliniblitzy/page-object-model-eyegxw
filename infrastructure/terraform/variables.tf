# ===================================================================
# Terraform Variables for Selenium Test Automation Infrastructure
# ===================================================================
# This file defines all configurable parameters for provisioning 
# and managing test execution environments including CI/CD servers,
# Selenium Grid, and storage resources.
# ===================================================================

# ===================================================================
# General Project Variables
# ===================================================================

variable "project_name" {
  description = "The name of the project used for resource naming and tagging"
  type        = string
  default     = "storydoc-selenium"

  validation {
    condition     = length(var.project_name) > 0
    error_message = "Project name cannot be empty."
  }
}

variable "environment" {
  description = "The deployment environment (dev, staging, production)"
  type        = string
  default     = "dev"

  validation {
    condition     = contains(["dev", "staging", "production"], var.environment)
    error_message = "Environment must be one of: dev, staging, production."
  }
}

variable "region" {
  description = "The cloud provider region for resource deployment"
  type        = string
  default     = "us-west-2"
}

# ===================================================================
# Network Configuration
# ===================================================================

variable "vpc_cidr" {
  description = "CIDR block for the VPC where resources will be deployed"
  type        = string
  default     = "10.0.0.0/16"

  validation {
    condition     = can(cidrnetmask(var.vpc_cidr))
    error_message = "VPC CIDR must be a valid CIDR block."
  }
}

variable "subnet_cidrs" {
  description = "List of CIDR blocks for subnets within the VPC"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

# ===================================================================
# Compute Resource Sizing
# ===================================================================

variable "instance_type" {
  description = "EC2/VM instance type for test execution environments"
  type        = string
  default     = "t3.large"  # 2 vCPU, 8GB RAM by default
}

variable "instance_count" {
  description = "Number of instances to provision for parallel test execution"
  type        = number
  default     = 1

  validation {
    condition     = var.instance_count > 0
    error_message = "Instance count must be greater than 0."
  }
}

variable "min_cpu_cores" {
  description = "Minimum number of CPU cores required for test execution"
  type        = number
  default     = 4  # Based on recommendation for parallel execution

  validation {
    condition     = var.min_cpu_cores >= 2
    error_message = "Minimum CPU cores must be at least 2."
  }
}

variable "min_memory_gb" {
  description = "Minimum memory in GB required for test execution"
  type        = number
  default     = 8  # Based on recommendation for running browsers

  validation {
    condition     = var.min_memory_gb >= 4
    error_message = "Minimum memory must be at least 4GB."
  }
}

variable "storage_size_gb" {
  description = "Size of storage in GB for test reports, logs, and artifacts"
  type        = number
  default     = 20  # Based on recommendation for CI/CD server

  validation {
    condition     = var.storage_size_gb >= 10
    error_message = "Storage size must be at least 10GB."
  }
}

# ===================================================================
# Selenium Grid Configuration
# ===================================================================

variable "selenium_grid_enabled" {
  description = "Flag to enable/disable Selenium Grid deployment"
  type        = bool
  default     = false
}

variable "selenium_version" {
  description = "Version of Selenium Grid to deploy"
  type        = string
  default     = "4.8.3"  # Update to match the version used in your framework
}

variable "chrome_nodes" {
  description = "Number of Chrome browser nodes in Selenium Grid"
  type        = number
  default     = 2

  validation {
    condition     = var.chrome_nodes >= 0
    error_message = "Chrome nodes must be a non-negative integer."
  }
}

variable "firefox_nodes" {
  description = "Number of Firefox browser nodes in Selenium Grid"
  type        = number
  default     = 2

  validation {
    condition     = var.firefox_nodes >= 0
    error_message = "Firefox nodes must be a non-negative integer."
  }
}

variable "edge_nodes" {
  description = "Number of Edge browser nodes in Selenium Grid"
  type        = number
  default     = 1

  validation {
    condition     = var.edge_nodes >= 0
    error_message = "Edge nodes must be a non-negative integer."
  }
}

variable "selenium_container_cpu" {
  description = "CPU allocation for Selenium Grid containers (in vCPU units or millicores)"
  type        = string
  default     = "1"  # Equivalent to 1 vCPU
}

variable "selenium_container_memory" {
  description = "Memory allocation for Selenium Grid containers (with unit suffix like 2Gi)"
  type        = string
  default     = "2Gi"  # 2GB memory per container
}

# ===================================================================
# Storage Configuration
# ===================================================================

variable "remote_storage_enabled" {
  description = "Flag to enable/disable remote storage for test artifacts"
  type        = bool
  default     = true
}

variable "storage_retention_days" {
  description = "Number of days to retain test reports and artifacts"
  type        = number
  default     = 30  # Keep reports for 30 days by default

  validation {
    condition     = var.storage_retention_days > 0
    error_message = "Storage retention period must be greater than 0 days."
  }
}

# ===================================================================
# CI/CD Integration
# ===================================================================

variable "jenkins_integration_enabled" {
  description = "Flag to enable/disable Jenkins CI integration"
  type        = bool
  default     = true
}

variable "github_actions_enabled" {
  description = "Flag to enable/disable GitHub Actions integration"
  type        = bool
  default     = false
}

# ===================================================================
# Administrative
# ===================================================================

variable "notification_email" {
  description = "Email address for infrastructure alerts and notifications"
  type        = string
  default     = "qa-team@example.com"

  validation {
    condition     = can(regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", var.notification_email))
    error_message = "Please provide a valid email address."
  }
}

variable "tags" {
  description = "Map of tags to apply to all resources for organization and billing"
  type        = map(string)
  default = {
    Project     = "Storydoc-Selenium-Testing"
    ManagedBy   = "Terraform"
    Environment = "Development"
    Purpose     = "QA-Automation"
  }
}