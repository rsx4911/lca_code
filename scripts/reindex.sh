#!/bin/bash

BASE_URL="http://localhost:8080/lca-collaboration"
LOGIN_URL="$BASE_URL/ws/public/login"
REINDEX_URL="$BASE_URL/ws/admin/area/reindex"
STATUS_URL="$BASE_URL/ws/admin/area/serverInfo"
PUBLIC_CHECK_URL="$BASE_URL/ws/public/config/userRoutes"
COOKIE_FILE="cookie.txt"

# Step 0: Wait for login endpoint to become available (max 5 retries, 10s delay)
MAX_RETRIES=10
retry=1
echo "Checking if login endpoint is up..."

until curl --output /dev/null --silent --insecure --head --fail "$PUBLIC_CHECK_URL"; do
  if [ $retry -ge $MAX_RETRIES ]; then
    echo "Login endpoint $PUBLIC_CHECK_URL is not available after $MAX_RETRIES attempts."
    exit 1
  fi
  echo "Login URL not available. Retrying in 10 seconds... ($retry/$MAX_RETRIES)"
  sleep 10
  retry=$((retry + 1))
done

# Step 1: Login and store cookie
echo "Logging in..."
curl "$LOGIN_URL" \
  -H 'Accept: */*' \
  -H 'Accept-Language: en-GB,en-US;q=0.9,en;q=0.8' \
  -H 'Connection: keep-alive' \
  -H 'Content-Type: application/json' \
  -H 'Origin: http://localhost:8080' \
  -H 'Referer: http://localhost:8080/lca-collaboration/login' \
  -H 'User-Agent: Mozilla/5.0' \
  -H 'X-Requested-With: XMLHttpRequest' \
  --data-raw '{"username":"administrator","password":"admin"}' \
  --insecure \
  -c "$COOKIE_FILE" \
  -s -o /dev/null

# Step 2: Trigger reindex
echo "Triggering reindex..."
curl "$REINDEX_URL" \
  -X PUT \
  -H 'Accept: */*' \
  -H 'Accept-Language: en-GB,en-US;q=0.9,en;q=0.8' \
  -H 'Connection: keep-alive' \
  -H 'Origin: http://localhost:8080' \
  -H 'Referer: http://localhost:8080/lca-collaboration/administration/overview' \
  -H 'User-Agent: Mozilla/5.0' \
  -H 'X-Requested-With: XMLHttpRequest' \
  --insecure \
  -b "$COOKIE_FILE" \
  -s -o /dev/null

# Step 3: Wait before checking
echo "Waiting for 2 seconds..."
sleep 2

# Step 4: Poll for indexing status (max 5 times)
echo "Checking indexing status (max 5 tries)..."
MAX_ATTEMPTS=40
attempt=1

while [ $attempt -le $MAX_ATTEMPTS ]; do
  echo "Attempt $attempt..."
  response=$(curl "$STATUS_URL" \
    -H 'Accept: */*' \
    -H 'Accept-Language: en-GB,en-US;q=0.9,en;q=0.8' \
    -H 'Connection: keep-alive' \
    -H 'Referer: http://localhost:8080/lca-collaboration/administration/overview' \
    -H 'User-Agent: Mozilla/5.0' \
    -H 'X-Requested-With: XMLHttpRequest' \
    --insecure \
    -b "$COOKIE_FILE" \
    -s)

  indexingTasks=$(echo "$response" | grep -o '"indexingTasks":\[[^]]*\]' | sed 's/"indexingTasks":\[//' | sed 's/\]//')

  if [[ -z "$indexingTasks" ]]; then
    echo "Indexing completed."
    break
  else
    echo "Still indexing..."
    sleep 5
  fi

  attempt=$((attempt + 1))
done

if [[ $attempt -gt $MAX_ATTEMPTS ]]; then
  echo "Indexing not completed after $MAX_ATTEMPTS attempts. Please check manually."
fi

# Cleanup
rm -f "$COOKIE_FILE"
