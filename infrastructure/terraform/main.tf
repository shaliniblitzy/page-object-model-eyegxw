# ===================================================================
# Selenium Test Automation Infrastructure - Main Configuration
# ===================================================================
# This Terraform configuration provisions AWS resources required to run
# automated Selenium tests for the Storydoc signup process including
# compute instances, networking, storage, and monitoring.
# ===================================================================

# Configure Terraform version and required providers
terraform {
  required_version = ">= 1.0.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.1"
    }
    tls = {
      source  = "hashicorp/tls"
      version = "~> 4.0"
    }
    local = {
      source  = "hashicorp/local"
      version = "~> 2.2"
    }
  }
}

# Configure AWS provider
provider "aws" {
  region  = var.region
  default_tags {
    tags = var.tags
  }
}

# Define local variables
locals {
  name_prefix = "${var.project_name}-${var.environment}"
  common_tags = var.tags
  browser_node_count = {
    chrome  = var.chrome_nodes
    firefox = var.firefox_nodes
    edge    = var.edge_nodes
  }
  selenium_grid_hub_ip = var.selenium_grid_enabled && length(aws_instance.selenium_grid_hub) > 0 ? aws_instance.selenium_grid_hub[0].private_ip : "localhost"
}

# ===================================================================
# Data Sources
# ===================================================================

# Get available AWS Availability Zones
data "aws_availability_zones" "available" {
  state = "available"
}

# Find latest Ubuntu 20.04 LTS AMI
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  filter {
    name   = "root-device-type"
    values = ["ebs"]
  }
}

# ===================================================================
# Network Infrastructure
# ===================================================================

# Create VPC for test infrastructure
resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-vpc"
  })
}

# Create public subnets across available AZs
resource "aws_subnet" "public" {
  for_each = {
    for idx, cidr in var.subnet_cidrs : "subnet-${idx}" => {
      cidr = cidr
      az   = data.aws_availability_zones.available.names[idx % length(data.aws_availability_zones.available.names)]
    }
  }

  vpc_id                  = aws_vpc.main.id
  cidr_block              = each.value.cidr
  availability_zone       = each.value.az
  map_public_ip_on_launch = true

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-${each.key}"
  })
}

# Create Internet Gateway for public access
resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-igw"
  })
}

# Create route table for public subnets
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-public-rt"
  })
}

# Associate public subnets with the public route table
resource "aws_route_table_association" "public" {
  for_each = aws_subnet.public

  subnet_id      = each.value.id
  route_table_id = aws_route_table.public.id
}

# ===================================================================
# Security Configuration
# ===================================================================

# Create security group for Selenium Grid infrastructure
resource "aws_security_group" "selenium_grid" {
  name        = "${local.name_prefix}-selenium-sg"
  description = "Security group for Selenium Grid infrastructure"
  vpc_id      = aws_vpc.main.id

  # SSH access
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "SSH access"
  }

  # Selenium Grid Hub ports
  ingress {
    from_port   = 4444
    to_port     = 4446
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Selenium Grid ports"
  }

  # HTTP access
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTP access"
  }

  # HTTPS access
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTPS access"
  }

  # Allow all outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound traffic"
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-selenium-sg"
  })
}

# ===================================================================
# SSH Key Configuration
# ===================================================================

# Generate SSH key pair for instance access
resource "tls_private_key" "ssh_key" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

# Create AWS key pair from generated key
resource "aws_key_pair" "selenium_key" {
  key_name   = "${local.name_prefix}-key"
  public_key = tls_private_key.ssh_key.public_key_openssh
}

# Save private key to local file
resource "local_file" "private_key" {
  content         = tls_private_key.ssh_key.private_key_pem
  filename        = "${path.module}/${local.name_prefix}-key.pem"
  file_permission = "0600"
}

# ===================================================================
# Storage Resources
# ===================================================================

# Generate random suffix for S3 bucket name
resource "random_id" "bucket_suffix" {
  byte_length = 8
}

# Create S3 bucket for test artifacts and reports
resource "aws_s3_bucket" "test_artifacts" {
  bucket = "${var.project_name}-reports-${random_id.bucket_suffix.hex}"

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-test-artifacts"
  })
}

# Configure lifecycle policy for test artifacts
resource "aws_s3_bucket_lifecycle_configuration" "test_artifacts_lifecycle" {
  bucket = aws_s3_bucket.test_artifacts.id

  rule {
    id     = "test-reports-expiration"
    status = "Enabled"

    expiration {
      days = var.storage_retention_days
    }
  }
}

# ===================================================================
# Compute Resources
# ===================================================================

# Create Selenium Grid Hub instance
resource "aws_instance" "selenium_grid_hub" {
  count = var.selenium_grid_enabled ? 1 : 0

  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type
  subnet_id              = values(aws_subnet.public)[0].id
  vpc_security_group_ids = [aws_security_group.selenium_grid.id]
  key_name               = aws_key_pair.selenium_key.key_name

  user_data = <<-EOF
    #!/bin/bash
    apt-get update
    apt-get install -y openjdk-11-jdk docker.io docker-compose
    systemctl enable docker
    systemctl start docker
    
    # Create Selenium Grid Hub configuration
    mkdir -p /opt/selenium-grid
    cat > /opt/selenium-grid/docker-compose.yml <<'EOT'
    version: '3'
    services:
      hub:
        image: selenium/hub:${var.selenium_version}
        ports:
          - "4444:4444"
          - "4442:4442"
          - "4443:4443"
        environment:
          - GRID_MAX_SESSION=16
          - GRID_BROWSER_TIMEOUT=300
          - GRID_TIMEOUT=300
    EOT
    
    cd /opt/selenium-grid
    docker-compose up -d
    
    # Install monitoring tools
    apt-get install -y prometheus-node-exporter
    systemctl enable prometheus-node-exporter
    systemctl start prometheus-node-exporter
  EOF

  root_block_device {
    volume_size = var.storage_size_gb
    volume_type = "gp3"
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-selenium-hub"
    Role = "Selenium Grid Hub"
  })
}

# Create browser node instances
resource "aws_instance" "browser_nodes" {
  for_each = var.selenium_grid_enabled ? {
    for idx, browser in flatten([
      for browser, count in local.browser_node_count : [
        for i in range(count) : {
          name  = "${browser}-${i + 1}"
          type  = browser
          index = i
        }
      ]
    ]) : browser.name => browser
  } : {}

  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type
  subnet_id              = values(aws_subnet.public)[each.value.index % length(aws_subnet.public)].id
  vpc_security_group_ids = [aws_security_group.selenium_grid.id]
  key_name               = aws_key_pair.selenium_key.key_name

  user_data = <<-EOF
    #!/bin/bash
    apt-get update
    apt-get install -y openjdk-11-jdk docker.io
    systemctl enable docker
    systemctl start docker
    
    # Install browser-specific dependencies
    BROWSER_TYPE="${each.value.type}"
    
    case $BROWSER_TYPE in
      chrome)
        # Install Chrome
        wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
        echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" > /etc/apt/sources.list.d/google-chrome.list
        apt-get update
        apt-get install -y google-chrome-stable
        ;;
      firefox)
        # Install Firefox
        apt-get install -y firefox
        ;;
      edge)
        # Install Edge
        curl https://packages.microsoft.com/keys/microsoft.asc | gpg --dearmor > microsoft.gpg
        install -o root -g root -m 644 microsoft.gpg /etc/apt/trusted.gpg.d/
        echo "deb [arch=amd64] https://packages.microsoft.com/repos/edge stable main" > /etc/apt/sources.list.d/microsoft-edge.list
        apt-get update
        apt-get install -y microsoft-edge-stable
        ;;
    esac
    
    # Start Selenium Node
    docker run -d \
      --name selenium-node-$BROWSER_TYPE \
      -p 5555:5555 \
      -e SE_EVENT_BUS_HOST=${local.selenium_grid_hub_ip} \
      -e SE_EVENT_BUS_PUBLISH_PORT=4442 \
      -e SE_EVENT_BUS_SUBSCRIBE_PORT=4443 \
      -e SE_NODE_MAX_SESSIONS=4 \
      selenium/node-$BROWSER_TYPE:${var.selenium_version}
    
    # Install monitoring tools
    apt-get install -y prometheus-node-exporter
    systemctl enable prometheus-node-exporter
    systemctl start prometheus-node-exporter
  EOF

  root_block_device {
    volume_size = var.storage_size_gb
    volume_type = "gp3"
  }

  tags = merge(local.common_tags, {
    Name     = "${local.name_prefix}-${each.key}"
    Role     = "Selenium Grid Node"
    Browser  = each.value.type
  })

  depends_on = [aws_instance.selenium_grid_hub]
}

# Create test executor instance
resource "aws_instance" "test_executor" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type
  subnet_id              = values(aws_subnet.public)[0].id
  vpc_security_group_ids = [aws_security_group.selenium_grid.id]
  key_name               = aws_key_pair.selenium_key.key_name

  user_data = <<-EOF
    #!/bin/bash
    apt-get update
    apt-get install -y openjdk-11-jdk maven git docker.io
    systemctl enable docker
    systemctl start docker
    
    # Add Jenkins repository and install Jenkins for CI/CD only if enabled
    ${var.jenkins_integration_enabled ? "wget -q -O - https://pkg.jenkins.io/debian-stable/jenkins.io.key | apt-key add -" : "# Jenkins not enabled"}
    ${var.jenkins_integration_enabled ? "echo 'deb https://pkg.jenkins.io/debian-stable binary/' > /etc/apt/sources.list.d/jenkins.list" : ""}
    ${var.jenkins_integration_enabled ? "apt-get update" : ""}
    ${var.jenkins_integration_enabled ? "apt-get install -y jenkins" : ""}
    ${var.jenkins_integration_enabled ? "systemctl enable jenkins" : ""}
    ${var.jenkins_integration_enabled ? "systemctl start jenkins" : ""}
    
    # Create directory for test framework
    mkdir -p /opt/selenium-test-framework
    
    # Configure AWS CLI for S3 access
    apt-get install -y awscli
    
    # Write a setup script for the test framework
    cat > /opt/selenium-test-framework/setup.sh <<'EOT'
    #!/bin/bash
    # Clone the test framework repository
    git clone https://github.com/example/storydoc-selenium-tests.git /opt/selenium-test-framework/repo
    
    # Configure test properties
    cat > /opt/selenium-test-framework/repo/src/main/resources/config.properties <<'CONFEND'
    # Test execution configuration
    base.url=https://editor-staging.storydoc.com
    signup.url=https://editor-staging.storydoc.com/sign-up
    
    # Selenium Grid configuration
    selenium.grid.url=http://${local.selenium_grid_hub_ip}:4444/wd/hub
    selenium.grid.enabled=${var.selenium_grid_enabled}
    
    # Browser configuration
    browser=chrome
    headless=false
    
    # Reporting configuration
    reports.dir=/opt/selenium-test-framework/reports
    screenshots.dir=/opt/selenium-test-framework/screenshots
    
    # AWS S3 configuration
    s3.bucket=${aws_s3_bucket.test_artifacts.bucket}
    s3.enabled=${var.remote_storage_enabled}
    CONFEND
    
    # Set up Jenkins job for test execution if Jenkins is enabled
    ${var.jenkins_integration_enabled ? "# Jenkins job setup would go here" : "# Jenkins not enabled"}
    EOT
    
    chmod +x /opt/selenium-test-framework/setup.sh
    /opt/selenium-test-framework/setup.sh
    
    # Install monitoring tools
    apt-get install -y prometheus-node-exporter
    systemctl enable prometheus-node-exporter
    systemctl start prometheus-node-exporter
  EOF

  root_block_device {
    volume_size = var.storage_size_gb
    volume_type = "gp3"
  }

  tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-test-executor"
    Role = "Test Executor"
  })
}

# ===================================================================
# Monitoring Resources
# ===================================================================

# Create CloudWatch dashboard for Selenium infrastructure
resource "aws_cloudwatch_dashboard" "selenium_dashboard" {
  dashboard_name = "${local.name_prefix}-selenium-dashboard"
  
  # Using jsonencode to create the dashboard JSON dynamically
  dashboard_body = jsonencode({
    widgets = [
      {
        type = "metric"
        x    = 0
        y    = 0
        width = 12
        height = 6
        properties = {
          metrics = var.selenium_grid_enabled && length(aws_instance.selenium_grid_hub) > 0 ? [
            [ "AWS/EC2", "CPUUtilization", "InstanceId", aws_instance.selenium_grid_hub[0].id, { label = "Selenium Hub CPU" } ],
            [ ".", ".", ".", aws_instance.test_executor.id, { label = "Test Executor CPU" } ]
          ] : [
            [ "AWS/EC2", "CPUUtilization", "InstanceId", aws_instance.test_executor.id, { label = "Test Executor CPU" } ]
          ]
          view = "timeSeries"
          stacked = false
          region = var.region
          title = "CPU Utilization"
          period = 300
        }
      },
      {
        type = "metric"
        x    = 0
        y    = 6
        width = 12
        height = 6
        properties = {
          metrics = var.selenium_grid_enabled && length(aws_instance.selenium_grid_hub) > 0 ? [
            [ "AWS/EC2", "NetworkIn", "InstanceId", aws_instance.selenium_grid_hub[0].id, { label = "Selenium Hub Network In" } ],
            [ ".", "NetworkOut", ".", ".", { label = "Selenium Hub Network Out" } ],
            [ ".", "NetworkIn", ".", aws_instance.test_executor.id, { label = "Test Executor Network In" } ],
            [ ".", "NetworkOut", ".", ".", { label = "Test Executor Network Out" } ]
          ] : [
            [ "AWS/EC2", "NetworkIn", "InstanceId", aws_instance.test_executor.id, { label = "Test Executor Network In" } ],
            [ ".", "NetworkOut", ".", ".", { label = "Test Executor Network Out" } ]
          ]
          view = "timeSeries"
          stacked = false
          region = var.region
          title = "Network Traffic"
          period = 300
        }
      },
      {
        type = "metric"
        x    = 12
        y    = 0
        width = 12
        height = 6
        properties = {
          metrics = [
            [ "AWS/S3", "BucketSizeBytes", "BucketName", aws_s3_bucket.test_artifacts.bucket, "StorageType", "StandardStorage", { label = "Test Artifacts Storage" } ]
          ]
          view = "timeSeries"
          stacked = false
          region = var.region
          title = "S3 Storage Usage"
          period = 86400
        }
      }
    ]
  })
}

# Create CloudWatch alarm for high CPU utilization
resource "aws_cloudwatch_metric_alarm" "high_cpu_alarm" {
  alarm_name          = "${local.name_prefix}-high-cpu-alarm"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "This metric monitors EC2 CPU utilization for Selenium test infrastructure"
  
  dimensions = {
    InstanceId = aws_instance.test_executor.id
  }
  
  # Set up notifications to the designated email address if provided
  alarm_actions = var.notification_email != "" ? [] : []  # Would add SNS topic ARN here if configured
}