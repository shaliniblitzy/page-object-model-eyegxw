output "vpc_id" {
  description = "The ID of the VPC where test infrastructure is deployed"
  value       = aws_vpc.main.id
}

# Selenium Grid Hub Outputs
output "selenium_grid_hub_public_ip" {
  description = "Public IP address of the Selenium Grid Hub for remote WebDriver connections"
  value       = aws_instance.selenium_grid_hub.public_ip
}

output "selenium_grid_hub_url" {
  description = "Complete URL for connecting to Selenium Grid Hub in test configuration"
  value       = "http://${aws_instance.selenium_grid_hub.public_ip}:4444/wd/hub"
}

# Browser Nodes Output
output "browser_nodes_public_ips" {
  description = "Map of browser types to public IP addresses of node instances"
  value       = {
    for node in aws_instance.browser_nodes :
    node.tags.BrowserType => node.public_ip
  }
}

# Test Executor Outputs
output "test_executor_public_ip" {
  description = "Public IP address of the test executor instance"
  value       = aws_instance.test_executor.public_ip
}

output "test_executor_ssh_command" {
  description = "Ready-to-use SSH command for connecting to the test executor instance"
  value       = "ssh -i '${var.private_key_path}' ec2-user@${aws_instance.test_executor.public_ip}"
}

# Test Artifacts S3 Bucket Outputs
output "artifacts_bucket_name" {
  description = "Name of the S3 bucket where test reports and artifacts are stored"
  value       = aws_s3_bucket.test_artifacts.bucket
}

output "artifacts_bucket_url" {
  description = "URL for accessing the test artifacts bucket in a web browser"
  value       = "https://${aws_s3_bucket.test_artifacts.bucket_domain_name}"
}

# Monitoring Dashboard Output
output "dashboard_url" {
  description = "URL for accessing the CloudWatch dashboard for test infrastructure monitoring"
  value       = var.enable_monitoring ? "https://${var.aws_region}.console.aws.amazon.com/cloudwatch/home?region=${var.aws_region}#dashboards:name=${aws_cloudwatch_dashboard.selenium_dashboard[0].dashboard_name}" : "Monitoring not enabled"
}

# SSH Key Output
output "private_key_path" {
  description = "Local path to the private key file for SSH access to instances"
  value       = var.private_key_path
  sensitive   = true
}