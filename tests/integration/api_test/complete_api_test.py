# Updated and aligned with api_test_priorities.yaml
import requests
import pytest
import os

BASE_URL = "https://lcacommons.gov/lca-collaboration"

@pytest.fixture(scope="session")
def auth_token():
    url = f"{BASE_URL}/ws/public/login"
    payload = {
        "username": os.getenv("AUTH_USERNAME", "valid_user"),
        "password": os.getenv("AUTH_PASSWORD", "valid_pass")
    }
    response = requests.post(url, json=payload)
    assert response.status_code == 200
    return response.cookies.get("JSESSIONID")

@pytest.fixture
def headers(auth_token):
    return {
        "Accept": "application/json",
        "Content-Type": "application/json",
        "Cookie": f"JSESSIONID={auth_token}"
    }

# P0 Critical - Example

def test_login_success():
    url = f"{BASE_URL}/ws/public/login"
    payload = {
        "username": os.getenv("AUTH_USERNAME", "valid_user"),
        "password": os.getenv("AUTH_PASSWORD", "valid_pass")
    }
    response = requests.post(url, json=payload)
    assert response.status_code == 200


def test_login_invalid():
    url = f"{BASE_URL}/ws/public/login"
    payload = {
        "username": "invalid_user",
        "password": "wrong_pass"
    }
    response = requests.post(url, json=payload)
    assert response.status_code == 401


def test_list_all_groups(headers):
    url = f"{BASE_URL}/ws/group"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_list_all_repositories(headers):
    url = f"{BASE_URL}/ws/respository"  # Note: typo in YAML is preserved here
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_basic_search(headers):
    url = f"{BASE_URL}/ws/public/search"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_prepare_repository_download(headers):
    url = f"{BASE_URL}/ws/public/download/json/prepare/Federal_LCA_Commons/test_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200
    global token
    token = response.text


def test_download_json_package(headers):
    global token
    url = f"{BASE_URL}/ws/public/download/json/{token}"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_browse_dataset_by_id(headers):
    url = f"{BASE_URL}/ws/public/browse/Federal_LCA_Commons/test_repo/FLOW/sample_id"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]


def test_search_by_group(headers):
    url = f"{BASE_URL}/ws/public/search?group=testgroup"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_search_by_repository(headers):
    url = f"{BASE_URL}/ws/public/search?repositoryId=testgroup/testrepo"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_paginated_search(headers):
    url = f"{BASE_URL}/ws/public/search?page=2&pageSize=20"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_download_invalid_token(headers):
    url = f"{BASE_URL}/ws/public/download/json/invalid-token"
    response = requests.get(url, headers=headers)
    assert response.status_code == 404


def test_commit_history(headers):
    url = f"{BASE_URL}/ws/history/Federal_LCA_Commons/test_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 204]


def test_commit_dataset_history(headers):
    url = f"{BASE_URL}/ws/history/Federal_LCA_Commons/test_repo/FLOW/sample_id"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 204]


def test_flow_link_search(headers):
    url = f"{BASE_URL}/ws/public/search/flowLinks/sample_flow_id"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

    # P2 - Additional Functional Tests

def test_login_2fa_required():
    url = f"{BASE_URL}/ws/public/login"
    payload = {
        "username": os.getenv("AUTH_USERNAME", "valid_user"),
        "password": os.getenv("AUTH_PASSWORD", "valid_pass"),
        "token": "invalid_2fa_token"
    }
    response = requests.post(url, json=payload)
    assert response.status_code in [200, 401]


def test_login_already_authenticated(headers):
    url = f"{BASE_URL}/ws/public/login"
    payload = {
        "username": os.getenv("AUTH_USERNAME", "valid_user"),
        "password": os.getenv("AUTH_PASSWORD", "valid_pass")
    }
    response = requests.post(url, json=payload, headers=headers)
    assert response.status_code in [200, 409]


def test_list_groups_pagination(headers):
    url = f"{BASE_URL}/ws/group?page=2&pageSize=20"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_list_groups_filtering(headers):
    url = f"{BASE_URL}/ws/group?filter=test&onlyIfCanWrite=true"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_get_nonexistent_group(headers):
    url = f"{BASE_URL}/ws/group/nonexistent-group"
    response = requests.get(url, headers=headers)
    assert response.status_code == 404


def test_create_group_invalid_name(headers):
    url = f"{BASE_URL}/ws/group/invalid!group$name"
    response = requests.post(url, headers=headers)
    assert response.status_code == 400


def test_get_group_avatar(headers):
    url = f"{BASE_URL}/ws/group/avatar/test_group"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]


def test_list_repositories_with_pagination(headers):
    url = f"{BASE_URL}/ws/repository?page=2&pageSize=20&group=testgroup"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200


def test_get_nonexistent_repository(headers):
    url = f"{BASE_URL}/ws/repository/testgroup/nonexistent_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code == 404


def test_get_repository_meta(headers):
    url = f"{BASE_URL}/ws/repository/meta/Federal_LCA_Commons/test_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]


def test_get_repository_avatar(headers):
    url = f"{BASE_URL}/ws/repository/avatar/Federal_LCA_Commons/test_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]


def test_export_repository(headers):
    url = f"{BASE_URL}/ws/repository/export/Federal_LCA_Commons/test_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]


def test_download_jsonld1_prepare(headers):
    url = f"{BASE_URL}/ws/public/download/json1/prepare/Federal_LCA_Commons/test_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]


def test_download_jsonld1(headers):
    url = f"{BASE_URL}/ws/public/download/json1/invalid-token"
    response = requests.get(url, headers=headers)
    assert response.status_code == 404


def test_history_commit_detail(headers):
    url = f"{BASE_URL}/ws/history/commit/Federal_LCA_Commons/test_repo/invalid_commit"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]


def test_history_previous_commit(headers):
    url = f"{BASE_URL}/ws/history/previousCommitId/Federal_LCA_Commons/test_repo/FLOW/sample_id/commitId1"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]
# Multipart Upload Tests

def test_upload_group_avatar(headers):
    url = f"{BASE_URL}/ws/group/avatar/test_group"
    with open('tests/assets/avatar.png', 'rb') as f:
        files = {'file': ('avatar.png', f, 'image/png')}
        response = requests.put(url, files=files, headers={"Cookie": headers["Cookie"]})
        assert response.status_code in [200, 403]
        if response.status_code == 200:
            assert "success" in response.text.lower()


def test_upload_repository_avatar(headers):
    url = f"{BASE_URL}/ws/repository/avatar/Federal_LCA_Commons/test_repo"
    with open('tests/assets/repo_avatar.png', 'rb') as f:
        files = {'file': ('repo_avatar.png', f, 'image/png')}
        response = requests.put(url, files=files, headers={"Cookie": headers["Cookie"]})
        assert response.status_code in [200, 403]
        if response.status_code == 200:
            assert "success" in response.text.lower()


def test_import_repository_data(headers):
    url = f"{BASE_URL}/ws/repository/import/Federal_LCA_Commons/test_repo"
    with open('tests/assets/data.zip', 'rb') as f:
        files = {
            'file': ('data.zip', f, 'application/zip')
        }
        data = {'commitMessage': 'CI test import', 'format': 'json-ld'}
        response = requests.post(url, files=files, data=data, headers={"Cookie": headers["Cookie"]})
        assert response.status_code in [200, 400]
        if response.status_code == 400:
            assert "commitMessage" in response.text or "error" in response.text.lower()

# GLAD Integration

def test_glad_push_success(headers):
    url = f"{BASE_URL}/ws/datamanager/glad/push/Federal_LCA_Commons/test_repo"
    payload = {
        "dataprovider": "Test Provider",
        "paths": ["Category1", "Category2"]
    }
    response = requests.post(url, json=payload, headers=headers)
    assert response.status_code in [200, 403]
    if response.status_code == 200:
        assert "pushed" in response.text.lower()


def test_glad_push_missing_config(headers):
    url = f"{BASE_URL}/ws/datamanager/glad/push/Federal_LCA_Commons/test_repo"
    payload = {
        "dataprovider": "Test Provider",
        "paths": ["FLOWS/Elementary flows"]
    }
    response = requests.post(url, json=payload, headers=headers)
    assert response.status_code == 503
    assert "invalid" in response.text.lower() or "unavailable" in response.text.lower()

# Dataset Download Preparation

def test_prepare_download_by_category(headers):
    url = f"{BASE_URL}/ws/public/download/json/prepare/Federal_LCA_Commons/test_repo"
    payload = [{
        "id": "categoryId1",
        "type": "CATEGORY",
        "name": "Test Category",
        "commitId": "commitId1"
    }]
    response = requests.post(url, json=payload, headers=headers)
    assert response.status_code in [200, 404]
    if response.status_code == 404:
        assert "not found" in response.text.lower()


def test_prepare_download_by_direct_selection(headers):
    url = f"{BASE_URL}/ws/public/download/json/prepare/Federal_LCA_Commons/test_repo"
    payload = [{
        "type": "PROCESS",
        "refId": "processId1"
    }]
    response = requests.put(url, json=payload, headers=headers)
    assert response.status_code in [200, 404]
    if response.status_code == 404:
        assert "not found" in response.text.lower()


def test_prepare_single_dataset_download(headers):
    url = f"{BASE_URL}/ws/public/download/json/prepare/Federal_LCA_Commons/test_repo/FLOW/sample_id"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]
    if response.status_code == 404:
        assert "not found" in response.text.lower()

# Mock asset generation help
os.makedirs('tests/assets', exist_ok=True)
with open('tests/assets/avatar.png', 'wb') as f:
    f.write(b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR')
with open('tests/assets/repo_avatar.png', 'wb') as f:
    f.write(b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR')
with open('tests/assets/data.zip', 'wb') as f:
    f.write(b'PK\x03\x04')

# Example CI/CD usage in GitHub Actions (to be placed in .github/workflows/api-tests.yml):
# name: API Tests
# on: [push, pull_request]
# jobs:
#   test:
#     runs-on: ubuntu-latest
#     env:
#       AUTH_USERNAME: ${{ secrets.AUTH_USERNAME }}
#       AUTH_PASSWORD: ${{ secrets.AUTH_PASSWORD }}
#     steps:
#       - uses: actions/checkout@v3
#       - name: Set up Python
#         uses: actions/setup-python@v4
#         with:
#           python-version: '3.10'
#       - name: Install dependencies
#         run: |
#           python -m pip install --upgrade pip
#           pip install pytest requests
#       - name: Run API tests
#         run: pytest tests/
