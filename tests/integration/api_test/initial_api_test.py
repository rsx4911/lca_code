import requests
import pytest
import os
from datetime import datetime

VM_IP = os.environ.get("VM_IP")
if not VM_IP:
    raise RuntimeError("Environment variable 'VM_IP' is not set")
BASE_URL = f"http://{VM_IP}:8080/lca-collaboration"

@pytest.fixture(scope="session")
def auth_token():
    url = f"{BASE_URL}/ws/public/login"
    payload = {"username": "administrator", "password": "admin"}
    response = requests.post(url, json=payload)
    assert response.status_code == 200, f"Login failed: {response.text}"
    return response.cookies.get("JSESSIONID")

@pytest.fixture
def headers(auth_token):
    return {"Accept": "application/json", "Content-Type": "application/json", "Cookie": f"JSESSIONID={auth_token}"}

def test_get_repository_info(headers):
    group = "Argonne_National_Lab"
    repo = "By_Product_Hydrogen"
    url = f"{BASE_URL}/ws/repository/{group}/{repo}"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200, f"Failed to get repository info: {response.text}"

def test_create_test_files(tmp_path):
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
    zip_filename = tmp_path / "test_data.zip"

    unit_group_id = f"test_unit_group_{timestamp}"
    flow_property_id = f"test_flow_property_{timestamp}"

    zip_filename.write_text("Dummy test data")
    assert zip_filename.exists(), "Failed to create ZIP test file"

@pytest.mark.dependency(depends=["test_create_test_files"])
def test_make_test_commit(headers, tmp_path):
    group = "Argonne_National_Lab"
    repo = "By_Product_Hydrogen"
    zip_filename = tmp_path / "test_data.zip"

    url = f"{BASE_URL}/ws/repository/import/{group}/{repo}"
    with open(zip_filename, 'rb') as zip_file:
        files = {'file': (zip_filename.name, zip_file, 'application/zip')}
        data = {'commitMessage': 'Test commit via API', 'format': 'json-ld'}

        response = requests.post(url, files=files, data=data, headers={"Cookie": headers["Cookie"]})
        assert response.status_code == 200, f"Commit failed: {response.text}"

def test_get_recent_commits(headers):
    group = "Argonne_National_Lab"
    repo = "By_Product_Hydrogen"
    url = f"{BASE_URL}/ws/history/{group}/{repo}"

    response = requests.get(url, headers=headers)
    assert response.status_code == 200, f"Failed to get commits: {response.text}"
    commits = response.json()
    assert isinstance(commits, list) and len(commits) > 0, "No commits found"

def test_verify_commit_data(headers):
    group = "Argonne_National_Lab"
    repo = "By_Product_Hydrogen"
    url = f"{BASE_URL}/ws/public/search?repositoryId={group}/{repo}"

    response = requests.get(url, headers=headers)
    assert response.status_code == 200, f"Failed to verify commit data: {response.text}"
    results = response.json()
    assert "data" in results, "No data found in repository search results"
