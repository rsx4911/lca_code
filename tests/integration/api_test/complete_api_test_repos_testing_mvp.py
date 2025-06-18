# Updated and aligned with api_test_priorities.yaml
#pytest==8.2.1
#requests==2.31.0

# API Test Coverage for MVP Repositories
"""
README / Onboarding:
----------------------
This test suite validates core API functionality for the LCA Commons MVP repositories.
It uses endpoint paths loaded dynamically from `api_test_priorities.yaml`, ensuring tests remain
stable across future endpoint changes.

How to Run:
1. Place `complete_api_test_repos_testing_mvp.py` in your `tests/` directory.
2. Ensure `api_test_priorities.yaml` is in the root or specified relative path.
3. Create a `.env` or set environment variables `AUTH_USERNAME` and `AUTH_PASSWORD`.
4. Run tests via: `pytest tests/`

Requirements:
- requests==2.31.0
- pytest==8.2.1
- pyyaml==6.0.1 'pip install pytest requests pyyaml'

Coverage Matrix:
----------------
| Test Case                                     | Route Description                            | Method | 
|----------------------------------------------|----------------------------------------------|--------|
| test_repo_history                             | Get repository commit history                | GET    |
| test_commit_dataset_history_param             | Get dataset commit history                   | GET    |
| test_glad_push_success                        | Push repository data to GLAD                 | POST   |
| test_glad_push_missing_config                 | Push to GLAD with missing configuration      | POST   |
| test_prepare_repository_download_param        | Prepare repository for download              | GET    |
| test_prepare_download_by_category_param       | Prepare download with category selection     | POST   |
| test_prepare_download_by_direct_selection_param | Prepare download with direct selection     | PUT    |
| test_prepare_single_dataset_download_param    | Prepare single dataset download              | GET    |
| test_export_repository                        | Export a repository                          | GET    | 
| test_upload_repository_avatar_param           | Upload repository avatar                     | PUT    | 
| test_import_repository_data_param             | Import repository data via multipart         | POST   |
| test_download_jsonld1_prepare                 | Prepare JSON-LD1 download                    | GET    | 
| test_get_repository_avatar                    | Retrieve repository avatar                   | GET    | 
| test_search_repository                        | Public repository search                     | GET    | 
| test_list_all_repositories                    | List all repositories                        | GET    | 
| test_list_all_groups                          | List all user groups                         | GET    | 
| test_download_invalid_token                   | Download with invalid token                  | GET    | 
"""

# Updated and aligned with api_test_priorities.yaml
import requests
import pytest
import os
import yaml

# Load endpoint routes dynamically from YAML config
with open("tests/integration/api_test/api_test_priorities.yaml", "r") as f:
    config = yaml.safe_load(f)["api_test_priorities"]

VM_IP = os.environ.get("VM_IP")
if not VM_IP:
    raise RuntimeError("Environment variable 'VM_IP' is not set")
BASE_URL = f"http://{VM_IP}:8080/lca-collaboration"

MVP_REPOS = [
    ("Argonne_National_Lab", "By_Product_Hydrogen"),
    ("National_Energy_Technology_Lab", "NETL_CO2_Capture"),
    ("U_Washington_Biofuels_Bioproducts_Lab", "UW_Biofuel_Sorghum"),
    ("US_Forest_Service_Forest_Products_Lab", "USFS_Lumber_Process"),
    ("CORRIM", "CORRIM_Structural_Timber"),
    ("NIST", "NIST_Cement_Inventory")
]

# Updated ROUTES loader for nested keys under each priority
ROUTES = {
    method: {
        entry["description"]: entry["path"]
        for priority in config.values()
        for route_group in priority.values()
        if isinstance(route_group, list)
        for entry in route_group
        if entry["method"] == method
    }
    for method in ["GET", "POST", "PUT", "DELETE"]
}

def get_path(description):
    for method in ROUTES:
        if description in ROUTES[method]:
            return ROUTES[method][description]
    raise KeyError(f"Description '{description}' not found in YAML routes.")

@pytest.fixture(scope="session")
def auth_token():
    url = f"{BASE_URL}{get_path('Login to the system')}"
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

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_repo_history(headers, group, repo):
    path = get_path("Get repository commit history")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 204]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_commit_dataset_history_param(headers, group, repo):
    path = get_path("Get dataset commit history")
    url = f"{BASE_URL}{path.format(group=group, repo=repo, type='FLOW', refId='sample_id')}"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 204, 404]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_glad_push_success(headers, group, repo):
    path = get_path("Push repository data to GLAD")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    payload = {
        "dataprovider": "Test Provider",
        "paths": ["Category1", "Category2"]
    }
    response = requests.post(url, json=payload, headers=headers)
    assert response.status_code in [200, 403]
    if response.status_code == 200:
        assert "pushed" in response.text.lower()

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_glad_push_missing_config(headers, group, repo):
    path = get_path("Push to GLAD with missing configuration")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    payload = {
        "dataprovider": "Test Provider",
        "paths": ["FLOWS/Elementary flows"]
    }
    response = requests.post(url, json=payload, headers=headers)
    assert response.status_code == 503
    assert "invalid" in response.text.lower() or "unavailable" in response.text.lower()

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_prepare_repository_download_param(headers, group, repo):
    path = get_path("Prepare repository for download")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_prepare_download_by_category_param(headers, group, repo):
    path = get_path("Prepare download with category selection")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    payload = [{
        "id": "categoryId1",
        "type": "CATEGORY",
        "name": "Test Category",
        "commitId": "commitId1"
    }]
    response = requests.post(url, json=payload, headers=headers)
    assert response.status_code in [200, 404]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_prepare_download_by_direct_selection_param(headers, group, repo):
    path = get_path("Prepare download with direct selection")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    payload = [{"type": "PROCESS", "refId": "processId1"}]
    response = requests.put(url, json=payload, headers=headers)
    assert response.status_code in [200, 404]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_prepare_single_dataset_download_param(headers, group, repo):
    path = get_path("Prepare single dataset download")
    url = f"{BASE_URL}{path.format(group=group, repo=repo, type='FLOW', refId='sample_id')}"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_export_repository(headers, group, repo):
    path = get_path("Export a repository")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_upload_repository_avatar_param(headers, group, repo):
    path = get_path("Upload repository avatar")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    with open('tests/assets/repo_avatar.png', 'rb') as f:
        files = {'file': ('repo_avatar.png', f, 'image/png')}
        response = requests.put(url, files=files, headers={"Cookie": headers["Cookie"]})
        assert response.status_code in [200, 403]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_import_repository_data_param(headers, group, repo):
    path = get_path("Import repository data via multipart")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    with open('tests/assets/data.zip', 'rb') as f:
        files = {'file': ('data.zip', f, 'application/zip')}
        data = {'commitMessage': 'CI test import', 'format': 'json-ld'}
        response = requests.post(url, files=files, data=data, headers={"Cookie": headers["Cookie"]})
        assert response.status_code in [200, 400]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_download_jsonld1_prepare(headers, group, repo):
    path = get_path("Prepare JSON-LD1 download")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_get_repository_avatar(headers, group, repo):
    path = get_path("Retrieve repository avatar")
    url = f"{BASE_URL}{path.format(group=group, repo=repo)}"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

@pytest.mark.parametrize("group,repo", MVP_REPOS)
def test_search_repository(headers, group, repo):
    path = get_path("Public repository search")
    url = f"{BASE_URL}{path}?repositoryId={group}/{repo}"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

def test_list_all_repositories(headers):
    path = get_path("List all repositories")
    url = f"{BASE_URL}{path}"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

def test_list_all_groups(headers):
    path = get_path("List all user groups")
    url = f"{BASE_URL}{path}"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

def test_download_invalid_token(headers):
    path = get_path("Download with invalid token")
    url = f"{BASE_URL}{path.format(token='invalid-token')}"
    response = requests.get(url, headers=headers)
    assert response.status_code == 404
# --- Additional Tests Requested ---

def test_login_invalid_credentials():
    url = f"{BASE_URL}{get_path('Login to the system')}"
    payload = {"username": "invalid_user", "password": "wrong_pass"}
    response = requests.post(url, json=payload)
    assert response.status_code == 401

def test_login_already_authenticated(headers):
    url = f"{BASE_URL}{get_path('Login to the system')}"
    payload = {
        "username": os.getenv("AUTH_USERNAME", "valid_user"),
        "password": os.getenv("AUTH_PASSWORD", "valid_pass")
    }
    response = requests.post(url, json=payload, headers=headers)
    assert response.status_code in [200, 409]

def test_browse_dataset_by_id(headers):
    url = f"{BASE_URL}/ws/public/browse/CORRIM/CORRIM_Structural_Timber/FLOW/sample_id"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

def test_get_nonexistent_group(headers):
    url = f"{BASE_URL}/ws/group/nonexistent-group"
    response = requests.get(url, headers=headers)
    assert response.status_code == 404

def test_get_group_avatar(headers):
    url = f"{BASE_URL}/ws/group/avatar/test_group"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

def test_history_commit_detail(headers):
    url = f"{BASE_URL}/ws/history/commit/CORRIM/CORRIM_Structural_Timber/invalid_commit"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

def test_history_previous_commit(headers):
    url = f"{BASE_URL}/ws/history/previousCommitId/CORRIM/CORRIM_Structural_Timber/FLOW/sample_id/commitId1"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

def test_upload_group_avatar(headers):
    url = f"{BASE_URL}/ws/group/avatar/test_group"
    with open('tests/assets/avatar.png', 'rb') as f:
        files = {'file': ('avatar.png', f, 'image/png')}
        response = requests.put(url, files=files, headers={"Cookie": headers["Cookie"]})
        assert response.status_code in [200, 403]
        if response.status_code == 200:
            assert "success" in response.text.lower()


# Create asset folder if needed
os.makedirs('tests/assets', exist_ok=True)
with open('tests/assets/avatar.png', 'wb') as f:
    f.write(b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR')
with open('tests/assets/repo_avatar.png', 'wb') as f:
    f.write(b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR')
with open('tests/assets/data.zip', 'wb') as f:
    f.write(b'PK\x03\x04')
"""
Ensure Asset Path Matches Script Context
The above block  is fine if you run pytest from the repo root like  -pytest tests/integration/api_test/
But if you run from another directory or CI, consider using an absolute path:
<
from pathlib import Path
BASE_DIR = Path(__file__).resolve().parents[3]  # adjust if deeper nested
ASSET_DIR = BASE_DIR / "tests" / "assets"
ASSET_DIR.mkdir(parents=True, exist_ok=True)
with open(ASSET_DIR / "avatar.png", "wb") as f:
>
"""

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
