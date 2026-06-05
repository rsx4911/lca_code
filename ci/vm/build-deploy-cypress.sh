#!/usr/bin/env bash
set -euo pipefail

echo "=== INSTALL SYSTEM DEPENDENCIES ==="
export DEBIAN_FRONTEND=noninteractive

apt-get update

apt-get install -y \
  curl \
  git \
  ca-certificates \
  gnupg \
  jq \
  unzip \
  maven \
  openjdk-21-jdk \
  tomcat10 \
  xvfb \
  libgtk2.0-0 \
  libgtk-3-0 \
  libgbm-dev \
  libnotify-dev \
  libnss3 \
  libxss1 \
  libasound2 \
  libxtst6 \
  xauth \
  libx11-xcb1 \
  libxcomposite1 \
  libxcursor1 \
  libxdamage1 \
  libxi6 \
  libxrandr2 \
  libatk1.0-0 \
  libatk-bridge2.0-0 \
  libcups2 \
  libdrm2 \
  libxkbcommon0 \
  python3 \
  python3-pip \
  python3-venv

echo "=== INSTALL NODE 20 ==="
curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
apt-get install -y nodejs

echo "=== TOOL VERSIONS ==="
java -version
mvn -version
node --version
npm --version
git --version
python3 --version

echo "=== PREPARE WORKSPACE ==="
rm -rf /opt/lca-ci
mkdir -p /opt/lca-ci
cd /opt/lca-ci

echo "=== VERIFY GITHUB TOKEN ==="
if [[ -z "${GH_REPO_READ_TOKEN:-}" ]]; then
  echo "GH_REPO_READ_TOKEN is empty. Add this GitHub secret or change clone strategy."
  exit 1
fi

if [[ -z "${GITHUB_REPOSITORY_NAME:-}" ]]; then
  echo "GITHUB_REPOSITORY_NAME is empty."
  exit 1
fi

if [[ -z "${GITHUB_BRANCH_NAME:-}" ]]; then
  echo "GITHUB_BRANCH_NAME is empty."
  exit 1
fi

echo "=== BUILD GREENDELTA DEPENDENCIES ==="

echo "--- Clone and build GreenDelta/search-wrapper ---"
git clone --depth 1 https://github.com/GreenDelta/search-wrapper.git /opt/lca-ci/search-wrapper
cd /opt/lca-ci/search-wrapper
git rev-parse HEAD
mvn clean install -DskipTests

echo "--- Clone and build GreenDelta/olca-modules ---"
git clone --depth 1 https://github.com/GreenDelta/olca-modules.git /opt/lca-ci/olca-modules
cd /opt/lca-ci/olca-modules
git rev-parse HEAD
mvn clean install -DskipTests

echo "=== CLONE CURRENT LCA APPLICATION BRANCH ==="
git clone \
  --depth 1 \
  --branch "${GITHUB_BRANCH_NAME}" \
  "https://x-access-token:${GH_REPO_READ_TOKEN}@github.com/${GITHUB_REPOSITORY_NAME}.git" \
  /opt/lca-ci/repo

cd /opt/lca-ci/repo

echo "=== LCA REPO INFO ==="
pwd
git rev-parse HEAD
ls -la

echo "=== BUILD LCA WAR ==="
mvn clean package -P appserver-stage -DskipTests

echo "=== LOCATE WAR FILE ==="
WAR_FILE="$(find . -path '*/target/*.war' -type f | head -1)"

if [[ -z "${WAR_FILE}" ]]; then
  echo "No WAR file found under target directories."
  find . -path '*/target/*' -type f | sort | head -100
  exit 1
fi

echo "Found WAR: ${WAR_FILE}"

echo "=== DEPLOY WAR TO TOMCAT ==="
systemctl stop tomcat10 || true

rm -rf /var/lib/tomcat10/webapps/ROOT
rm -f /var/lib/tomcat10/webapps/ROOT.war

cp "${WAR_FILE}" /var/lib/tomcat10/webapps/ROOT.war
chown tomcat:tomcat /var/lib/tomcat10/webapps/ROOT.war || true

systemctl start tomcat10

echo "=== WAIT FOR APP ON LOCALHOST ==="
for i in {1..60}; do
  HTTP_CODE="$(curl -s -o /tmp/lca-health.html -w "%{http_code}" http://localhost:8080/ || true)"
  echo "Attempt ${i}: HTTP ${HTTP_CODE}"

  if [[ "${HTTP_CODE}" =~ ^(200|301|302)$ ]]; then
    echo "App is responding."
    break
  fi

  sleep 10
done

HTTP_CODE="$(curl -s -o /tmp/lca-health.html -w "%{http_code}" http://localhost:8080/ || true)"

if [[ ! "${HTTP_CODE}" =~ ^(200|301|302)$ ]]; then
  echo "App did not become healthy. Tomcat logs:"
  journalctl -u tomcat10 --no-pager -n 300 || true
  ls -la /var/lib/tomcat10/webapps || true
  exit 1
fi

echo "=== PREPARE TEST ARTIFACT DIRECTORY ==="
mkdir -p /opt/lca-ci/cypress-artifacts
mkdir -p /opt/lca-ci/cypress-artifacts/maven
mkdir -p /opt/lca-ci/cypress-artifacts/tomcat

echo "=== COLLECT MAVEN BUILD INFO ==="
find /opt/lca-ci/repo -path '*/target/*.war' -type f -print > /opt/lca-ci/cypress-artifacts/maven/war-files.txt || true
find /opt/lca-ci/repo -name 'pom.xml' -print > /opt/lca-ci/cypress-artifacts/maven/pom-files.txt || true

echo "=== DETECT AND RUN TESTS ==="
cd /opt/lca-ci/repo

TEST_EXIT=0

if [[ -d testing/tests ]]; then
  echo "Detected testing/tests directory."
  cd /opt/lca-ci/repo/testing/tests

  echo "=== TEST DIRECTORY CONTENTS ==="
  find . -maxdepth 3 -type f -print | sort | head -200

  if [[ -f package.json ]]; then
    echo "Detected Node/Cypress-style test project."

    if [[ -f package-lock.json ]]; then
      npm ci
    else
      npm install
    fi

    echo "=== CYPRESS VERSION CHECK ==="
    npx cypress --version || true

    export CYPRESS_BASE_URL="http://localhost:8080"

    mkdir -p cypress/screenshots cypress/videos cypress/reports mochawesome-report || true

    set +e
    npx cypress run \
      --config baseUrl="${CYPRESS_BASE_URL}" \
      --browser electron \
      --headless | tee /opt/lca-ci/cypress-artifacts/cypress-run-output.txt
    TEST_EXIT="${PIPESTATUS[0]}"
    set -e

    cp -R cypress/screenshots /opt/lca-ci/cypress-artifacts/screenshots 2>/dev/null || true
    cp -R cypress/videos /opt/lca-ci/cypress-artifacts/videos 2>/dev/null || true
    cp -R cypress/reports /opt/lca-ci/cypress-artifacts/reports 2>/dev/null || true
    cp -R mochawesome-report /opt/lca-ci/cypress-artifacts/mochawesome-report 2>/dev/null || true

  else
    echo "No package.json found under testing/tests. Running Python/pytest-style tests."

    python3 -m venv /opt/lca-ci/test-venv
    # shellcheck disable=SC1091
    source /opt/lca-ci/test-venv/bin/activate

    python -m pip install --upgrade pip

    if [[ -f requirements.txt ]]; then
      pip install -r requirements.txt
    fi

    pip install pytest requests

    export LCA_BASE_URL="http://localhost:8080"
    export BASE_URL="http://localhost:8080"

    set +e
    pytest -v . | tee /opt/lca-ci/cypress-artifacts/pytest-output.txt
    TEST_EXIT="${PIPESTATUS[0]}"
    set -e
  fi

elif [[ -f package.json ]]; then
  echo "No testing/tests directory found. Detected package.json in repo root. Running Cypress from repo root."

  if [[ -f package-lock.json ]]; then
    npm ci
  else
    npm install
  fi

  echo "=== CYPRESS VERSION CHECK ==="
  npx cypress --version || true

  export CYPRESS_BASE_URL="http://localhost:8080"

  mkdir -p cypress/screenshots cypress/videos cypress/reports mochawesome-report || true

  set +e
  npx cypress run \
    --config baseUrl="${CYPRESS_BASE_URL}" \
    --browser electron \
    --headless | tee /opt/lca-ci/cypress-artifacts/cypress-run-output.txt
  TEST_EXIT="${PIPESTATUS[0]}"
  set -e

  cp -R cypress/screenshots /opt/lca-ci/cypress-artifacts/screenshots 2>/dev/null || true
  cp -R cypress/videos /opt/lca-ci/cypress-artifacts/videos 2>/dev/null || true
  cp -R cypress/reports /opt/lca-ci/cypress-artifacts/reports 2>/dev/null || true
  cp -R mochawesome-report /opt/lca-ci/cypress-artifacts/mochawesome-report 2>/dev/null || true

else
  echo "Could not detect a test runner."
  echo "Expected either testing/tests or package.json."
  TEST_EXIT=1
fi

echo "Test exit code: ${TEST_EXIT}"

echo "=== COLLECT TOMCAT LOGS ==="
journalctl -u tomcat10 --no-pager -n 300 > /opt/lca-ci/cypress-artifacts/tomcat/tomcat-journal.log 2>/dev/null || true
cp -R /var/log/tomcat10 /opt/lca-ci/cypress-artifacts/tomcat/tomcat10-logs 2>/dev/null || true

echo "=== CREATE ARTIFACT ARCHIVE ==="
tar -czf /opt/lca-ci/cypress-artifacts.tar.gz -C /opt/lca-ci cypress-artifacts

if [[ "${TEST_EXIT}" -ne 0 ]]; then
  echo "CYPRESS_TEST_FAILED"
  exit "${TEST_EXIT}"
fi

echo "CYPRESS_TEST_SUCCESS"
