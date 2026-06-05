#!/usr/bin/env bash
set -euo pipefail

echo "=== BASIC INFO ==="
whoami
hostname
uname -a

echo "=== PRIVATE NETWORK INFO ==="
ip addr show

echo "=== CLOUD-INIT FILE CHECK ==="
test -f /opt/lca-ci/hello.txt
cat /opt/lca-ci/hello.txt

echo "=== READY FILE CHECK ==="
test -f /opt/lca-ci/ready.txt
cat /opt/lca-ci/ready.txt

echo "=== LOCALHOST CHECK ==="
curl -I --max-time 5 http://localhost || true

echo "PRIVATE_VM_TEST_SUCCESS"
