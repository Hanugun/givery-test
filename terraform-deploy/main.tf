terraform {
  required_providers {
    heroku = {
      source  = "heroku/heroku"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = "ap-northeast-2"
}

provider "heroku" {
  api_key = var.heroku_api_key
}

# Create a security group for RDS (for the MySQL database on AWS)
resource "aws_security_group" "rds_sg" {
  name = "rds-security-group"

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Create an RDS instance for MySQL (this is your AWS database)
resource "aws_db_instance" "akka_mysql_db" {
  allocated_storage      = 20
  storage_type           = "gp2"
  engine                 = "mysql"
  engine_version         = "8.0.35"
  instance_class         = "db.t3.micro"
  db_name                = "akka_db"
  username               = "admin"
  password               = "admin_password"
  publicly_accessible    = true
  skip_final_snapshot    = true
  deletion_protection    = false
  backup_retention_period = 7

  vpc_security_group_ids = [aws_security_group.rds_sg.id]

  tags = {
    Name = "AkkaMySQLDatabase"
  }
}

# Heroku app setup for deploying the Scala RestAPI server
resource "heroku_app" "scala_api" {
  name   = var.heroku_app_name
  region = "us"
}

# Heroku build from your GitHub repository
resource "heroku_build" "scala_build" {
  app_id = heroku_app.scala_api.id

  source {
    url = "https://github.com/Hanugun/givery-test/archive/refs/heads/main.tar.gz"  # GitHub repository tarball
  }
}

# Heroku environment variables (used for your database connection and other configuration)
resource "heroku_config" "scala_env" {
  vars = {
    DATABASE_URL = aws_db_instance.akka_mysql_db.endpoint
    SCALA_ENV    = "development"
  }
}

# Output the RDS endpoint (this is the MySQL database endpoint on AWS)
output "rds_endpoint" {
  value = aws_db_instance.akka_mysql_db.endpoint
}

# Output the Heroku app URL (this is the URL where your Heroku-hosted API will be accessible)
output "heroku_app_url" {
  value = heroku_app.scala_api.web_url
}

# Variables for sensitive data
variable "heroku_api_key" {
  type      = string
  sensitive = true
}

variable "heroku_app_name" {
  type    = string
  default = "givery-rest-api"
}
