provider "aws" {
  region = "ap-northeast-2" # Set your preferred AWS region
}

# Create a security group for EC2
resource "aws_security_group" "http_server" {
  name        = "http-server"
  description = "Allow HTTP and SSH inbound traffic"

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Allow public access to port 8080
  }
  ingress {
    from_port   = 22
    to_port     = 22
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

# Create a security group for RDS
resource "aws_security_group" "rds_sg" {
  name = "rds-security-group"

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Allow public access to MySQL (change this in production)
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Create an RDS instance for MySQL
resource "aws_db_instance" "akka_mysql_db" {
  allocated_storage    = 20
  storage_type         = "gp2"
  engine               = "mysql"
  engine_version       = "8.0.35"
  instance_class       = "db.t3.micro"
  db_name              = "akka_db"
  username             = "admin"
  password             = "admin_password"
  publicly_accessible  = true
  skip_final_snapshot  = true
  deletion_protection  = false
  backup_retention_period = 7

  vpc_security_group_ids = [aws_security_group.rds_sg.id]

  tags = {
    Name = "AkkaMySQLDatabase"
  }
}

# Create an EC2 instance for the Akka HTTP server
resource "aws_instance" "akka_http_server" {
  ami           = "ami-0023481579962abd4" # Amazon Linux 2 AMI (Change if needed)
  instance_type = "t2.micro"
  key_name      = "givery-keypair"
  security_groups = [aws_security_group.http_server.name]

  user_data = <<-EOF
              #!/bin/bash
              sudo yum update -y
              sudo yum install -y java-11-amazon-corretto-devel
              
              # Set JAVA_HOME and update PATH
              export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
              export PATH=$JAVA_HOME/bin:$PATH

              # Make JAVA_HOME persistent across reboots
              echo "export JAVA_HOME=\$(dirname \$(dirname \$(readlink -f \$(which java))))" >> /home/ec2-user/.bashrc
              echo "export PATH=\$JAVA_HOME/bin:\$PATH" >> /home/ec2-user/.bashrc
              # Install sbt
              echo "Installing sbt..."
              curl -L "https://www.scala-sbt.org/sbt-rpm.repo" > sbt.repo
              sudo mv sbt.repo /etc/yum.repos.d/
              sudo yum install -y sbt

              # Clone the GitHub repository
              cd /home/ec2-user
              git clone https://github.com/Hanugun/givery-test
              cd givery-test  # Match your repository's folder name

              # Update the application.conf file with the correct RDS endpoint
              sed -i "s|terraform-20240909163138474900000001.cz28mymaavna.ap-northeast-2.rds.amazonaws.com|${aws_db_instance.akka_mysql_db.endpoint}|" src/main/resources/application.conf

              # Run the application using nohup to keep it running after script ends
              nohup sbt run > akka_http_server.log 2>&1 &
              EOF
  tags = {
    Name = "AkkaHttpServer"
  }
}

# Output the public IP address of the EC2 instance
output "instance_ip" {
  value = aws_instance.akka_http_server.public_ip
}

# Output the RDS endpoint
output "rds_endpoint" {
  value = aws_db_instance.akka_mysql_db.endpoint
}
