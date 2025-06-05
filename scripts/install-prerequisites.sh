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
  echo "export JAVA_HOME=/opt/java/jdk-21.0.3+9" | sudo tee /etc/profile.d/java.sh
  echo 'export PATH=$JAVA_HOME/bin:$PATH' | sudo tee -a /etc/profile.d/java.sh
  source /etc/profile.d/java.sh
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

# ------------------ MariaDB Database & User Setup ------------------
echo "Setting up MariaDB database and user..."

DB_NAME="${DB_NAME:-lca}"
DB_USER="${DB_USER:?lca}"
DB_PASS="${DB_PASSWORD:?lca}"

until sudo mariadb -e "SELECT 1;" >/dev/null 2>&1; do
  echo "Waiting for MariaDB to start..."
  sleep 2
done

sudo mariadb -e "CREATE DATABASE IF NOT EXISTS \`${DB_NAME}\`;"
USER_EXISTS=$(sudo mariadb -N -e "SELECT COUNT(*) FROM mysql.user WHERE user = '${DB_USER}' AND host = 'localhost';")

if [ "$USER_EXISTS" -eq 0 ]; then
  echo "Creating MariaDB user '${DB_USER}'..."
  sudo mariadb -e "CREATE USER '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASS}';"
else
  echo "User '${DB_USER}' exists. Updating password..."
  sudo mariadb -e "ALTER USER '${DB_USER}'@'localhost' IDENTIFIED BY '${DB_PASS}';"
fi

sudo mariadb -e "GRANT ALL PRIVILEGES ON \`${DB_NAME}\`.* TO '${DB_USER}'@'localhost';"
sudo mariadb -e "FLUSH PRIVILEGES;"
echo "MariaDB database '${DB_NAME}' and user '${DB_USER}' configured."

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

  if id "opensearch" &>/dev/null; then
    echo "User 'opensearch' already exists."
  else
    echo "Creating user 'opensearch'..."
    sudo useradd --no-create-home --system --shell /usr/sbin/nologin opensearch
  fi
  sudo chown -R opensearch:opensearch /usr/share/opensearch

  echo "Disabling OpenSearch SSL and security plugin..."
  sudo sed -i '/plugins.security.ssl/d' /usr/share/opensearch/config/opensearch.yml
  echo "" | sudo tee -a /usr/share/opensearch/config/opensearch.yml
  echo "# Disable security plugin for development use only" | sudo tee -a /usr/share/opensearch/config/opensearch.yml
  echo "plugins.security.disabled: true" | sudo tee -a /usr/share/opensearch/config/opensearch.yml
  echo "network.host: 0.0.0.0" | sudo tee -a /usr/share/opensearch/config/opensearch.yml
  echo "http.port: 9200" | sudo tee -a /usr/share/opensearch/config/opensearch.yml
  echo "Creating systemd service for OpenSearch..."
  sudo bash -c 'cat > /etc/systemd/system/opensearch.service' <<EOF
[Unit]
Description=OpenSearch
Documentation=https://opensearch.org/
Wants=network-online.target
After=network-online.target

[Service]
Type=simple
User=opensearch
Group=opensearch
Environment=OPENSEARCH_HOME=/usr/share/opensearch
WorkingDirectory=/usr/share/opensearch
ExecStart=/usr/share/opensearch/bin/opensearch
Restart=always
LimitNOFILE=65536
LimitNPROC=4096
TimeoutStartSec=120

[Install]
WantedBy=multi-user.target
EOF

  sudo systemctl daemon-reload
  sudo systemctl enable opensearch
  sudo systemctl start opensearch
  echo "OpenSearch 2.19 installed and started."
fi

# ------------------ Tomcat 10.1.20 ------------------
echo "Checking Tomcat..."
if [ -d "/opt/tomcat" ] && /opt/tomcat/bin/version.sh | grep "10.1.20"; then
  echo "Tomcat 10.1.20 is already installed."
else
  echo "Installing Tomcat 10.1.20..."
  curl -O https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.20/bin/apache-tomcat-10.1.20.tar.gz
  tar -xzf apache-tomcat-10.1.20.tar.gz
  sudo mv apache-tomcat-10.1.20 /opt/tomcat
  sudo chmod +x /opt/tomcat/bin/*.sh

  echo "Creating systemd service for Tomcat..."
  sudo bash -c 'cat > /etc/systemd/system/tomcat.service' <<EOF
[Unit]
Description=Apache Tomcat
After=network.target

[Service]
Type=forking
User=root
Group=root
Environment=JAVA_HOME=/opt/java/jdk-21.0.3+9
Environment=CATALINA_PID=/opt/tomcat/temp/tomcat.pid
Environment=CATALINA_HOME=/opt/tomcat
Environment=CATALINA_BASE=/opt/tomcat
ExecStart=/opt/tomcat/bin/startup.sh
ExecStop=/opt/tomcat/bin/shutdown.sh
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

  sudo systemctl daemon-reload
  sudo systemctl enable tomcat
  sudo systemctl start tomcat
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
    echo "Failed to install Nginx 1.28 — version may not be available.";
    exit 1;
  }
  echo "Nginx 1.28 installed. Version: $(nginx -v 2>&1)"
fi

echo "All prerequisites installed successfully."
