#!/bin/bash
set -e

echo "Starting prerequisite installation..."

# ------------------ Java 21.0.3 ------------------
echo "Checking Java 21..."
if java -version 2>&1 | grep "21.0.3"; then
  echo "Java 21.0.3 is already installed."
else
  echo "Installing Java 21.0.3..."
  sudo apt-get update
  sudo apt-get install -y wget
  wget https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.3%2B9/OpenJDK21U-jdk_x64_linux_hotspot_21.0.3_9.tar.gz
  sudo mkdir -p /opt/java
  sudo tar -xzf OpenJDK21U-jdk_x64_linux_hotspot_21.0.3_9.tar.gz -C /opt/java
  export JAVA_HOME=/opt/java/jdk-21.0.3+9
  export PATH=$JAVA_HOME/bin:$PATH
  echo "Java 21.0.3 installed."
fi

# ------------------ MariaDB 10.11 ------------------
echo "Checking MariaDB..."
if mariadb --version 2>/dev/null | grep "10.11"; then
  echo "MariaDB 10.11 is already installed."
else
  echo "Installing MariaDB 10.11..."
  curl -LsS https://downloads.mariadb.com/MariaDB/mariadb_repo_setup | sudo bash
  sudo apt-get update -y
  sudo apt-get install -y mariadb-server
  sudo systemctl enable mariadb
  sudo systemctl start mariadb
  echo "MariaDB 10.11 installed and started."
fi

# ------------------ OpenSearch 2.19 ------------------
echo "Checking OpenSearch..."
if [ -d "/usr/share/opensearch" ] && /usr/share/opensearch/bin/opensearch --version | grep "2.19"; then
  echo "OpenSearch 2.19 is already installed."
else
  echo "Installing OpenSearch 2.19..."
  curl -L -O https://artifacts.opensearch.org/releases/bundle/opensearch/2.19.0/opensearch-2.19.0-linux-x64.tar.gz
  tar -xzf opensearch-2.19.0-linux-x64.tar.gz
  sudo mv opensearch-2.19.0 /usr/share/opensearch
  sudo chmod -R 755 /usr/share/opensearch
  sudo nohup /usr/share/opensearch/bin/opensearch > /dev/null 2>&1 &
  echo "OpenSearch 2.19 installed and started."
fi

# ------------------ Tomcat 10.1.20 ------------------
echo "Checking Tomcat..."
if [ -d "/opt/tomcat" ] && /opt/tomcat/bin/version.sh | grep "10.1.20"; then
  echo "Tomcat 10.1.20 is already installed."
else
  echo "Installing Tomcat 10.1.20..."
  curl -O https://dlcdn.apache.org/tomcat/tomcat-10/v10.1.20/bin/apache-tomcat-10.1.20.tar.gz
  tar -xzf apache-tomcat-10.1.20.tar.gz
  sudo mv apache-tomcat-10.1.20 /opt/tomcat
  sudo chmod +x /opt/tomcat/bin/*.sh
  sudo nohup /opt/tomcat/bin/startup.sh > /dev/null 2>&1 &
  echo "Tomcat 10.1.20 installed and started."
fi

# ------------------ Nginx 1.28.x ------------------
echo "Checking Nginx 1.28..."
installed_version=$(nginx -v 2>&1 | grep -o '[0-9.]*' || true)

if [[ "$installed_version" == 1.28.* ]]; then
  echo "Nginx 1.28 is already installed (version: $installed_version)."
else
  echo "Installing Nginx 1.28..."
  sudo apt-get update
  sudo apt-get install -y curl gnupg2 ca-certificates lsb-release
  curl https://nginx.org/keys/nginx_signing.key | gpg --dearmor | sudo tee /usr/share/keyrings/nginx-archive-keyring.gpg >/dev/null
  echo "deb [signed-by=/usr/share/keyrings/nginx-archive-keyring.gpg] http://nginx.org/packages/ubuntu $(lsb_release -cs) nginx" | sudo tee /etc/apt/sources.list.d/nginx.list
  sudo apt-get update
  sudo apt-get install -y nginx=1.28.* || {
    echo "Failed to install Nginx 1.28 — version may not be available in apt cache.";
    exit 1;
  }
  echo "Nginx 1.28 installed. Version: $(nginx -v 2>&1)"
fi

echo "All prerequisites installed successfully."
