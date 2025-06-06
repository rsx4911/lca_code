import requests
import pytest

BASE_URL = "https://lcacommons.gov/lca-collaboration"

@pytest.fixture(scope="session")
def auth_token():
    url = f"{BASE_URL}/ws/public/login"
    payload = {"username": "valid_user", "password": "valid_pass"}
    response = requests.post(url, json=payload)
    assert response.status_code == 200
    return response.cookies.get("JSESSIONID")

@pytest.fixture
def headers(auth_token):
    return {"Accept": "application/json", "Content-Type": "application/json", "Cookie": f"JSESSIONID={auth_token}"}

# P0 - Critical Tests

def test_login_success():
    url = f"{BASE_URL}/ws/public/login"
    payload = {"username": "valid_user", "password": "valid_pass"}
    response = requests.post(url, json=payload)
    assert response.status_code == 200

def test_list_all_groups(headers):
    url = f"{BASE_URL}/ws/group"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

def test_list_all_repositories(headers):
    url = f"{BASE_URL}/ws/repository"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

def test_browse_dataset_by_id(headers):
    url = f"{BASE_URL}/ws/public/browse/Federal_LCA_Commons/test_repo/FLOW/sample_id"
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

# P1 - Important Tests

def test_login_invalid_password():
    url = f"{BASE_URL}/ws/public/login"
    payload = {"username": "valid_user", "password": "invalid_pass"}
    response = requests.post(url, json=payload)
    assert response.status_code == 401

def test_login_missing_2fa():
    url = f"{BASE_URL}/ws/public/login"
    payload = {"username": "valid_user", "password": "valid_pass"}
    response = requests.post(url, json=payload)
    assert response.status_code == 200
    assert 'tokenRequired' in response.text

def test_get_group_by_id(headers):
    url = f"{BASE_URL}/ws/group/test_group"
    response = requests.get(url, headers=headers)
    assert response.status_code in [200, 404]

def test_create_group(headers):
    url = f"{BASE_URL}/ws/group/new_group"
    response = requests.post(url, headers=headers)
    assert response.status_code == 201

def test_delete_group(headers):
    url = f"{BASE_URL}/ws/group/new_group"
    response = requests.delete(url, headers=headers)
    assert response.status_code == 200

def test_update_group_setting(headers):
    url = f"{BASE_URL}/ws/group/settings/test_group/label"
    response = requests.put(url, json={"value": "Updated Label"}, headers=headers)
    assert response.status_code == 200

# Repository Management

def test_get_repository_details(headers):
    url = f"{BASE_URL}/ws/repository/Federal_LCA_Commons/test_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

def test_create_repository(headers):
    url = f"{BASE_URL}/ws/repository/Federal_LCA_Commons/new_repo"
    response = requests.post(url, headers=headers)
    assert response.status_code == 201

def test_delete_repository(headers):
    url = f"{BASE_URL}/ws/repository/Federal_LCA_Commons/new_repo"
    response = requests.delete(url, headers=headers)
    assert response.status_code == 200

# Membership Management

def test_get_group_memberships(headers):
    url = f"{BASE_URL}/ws/membership/test_group"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

def test_add_user_to_group(headers):
    url = f"{BASE_URL}/ws/membership/test_group/user/new_user/READER"
    response = requests.post(url, headers=headers)
    assert response.status_code == 201

def test_update_user_role_in_group(headers):
    url = f"{BASE_URL}/ws/membership/test_group/user/new_user/EDITOR"
    response = requests.put(url, headers=headers)
    assert response.status_code == 200

def test_delete_user_from_group(headers):
    url = f"{BASE_URL}/ws/membership/test_group/user/new_user"
    response = requests.delete(url, headers=headers)
    assert response.status_code == 200

def test_get_repository_memberships(headers):
    url = f"{BASE_URL}/ws/membership/Federal_LCA_Commons/test_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

def test_add_user_to_repository(headers):
    url = f"{BASE_URL}/ws/membership/Federal_LCA_Commons/test_repo/user/new_user/READER"
    response = requests.post(url, headers=headers)
    assert response.status_code == 201

def test_delete_user_from_repository(headers):
    url = f"{BASE_URL}/ws/membership/Federal_LCA_Commons/test_repo/user/new_user"
    response = requests.delete(url, headers=headers)
    assert response.status_code == 200


# Commit History

def test_get_repository_commit_history(headers):
    url = f"{BASE_URL}/ws/history/Federal_LCA_Commons/test_repo"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

# Search Variants

def test_search_by_group(headers):
    url = f"{BASE_URL}/ws/public/search?group=Federal_LCA_Commons"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

# Flow Links

def test_flow_link_search(headers):
    url = f"{BASE_URL}/ws/public/search/flowLinks/sample_flow_id"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

# Download Edge Cases

def test_invalid_token_download(headers):
    url = f"{BASE_URL}/ws/public/download/json/invalid-token"
    response = requests.get(url, headers=headers)
    assert response.status_code == 404



@pytest.fixture(scope="sessionP2")
def auth_token():
    url = f"{BASE_URL}/ws/public/login"
    payload = {"username": "valid_user", "password": "valid_pass"}
    response = requests.post(url, json=payload)
    assert response.status_code == 200
    return response.cookies.get("JSESSIONID")

@pytest.fixture
def headers(auth_token):
    return {"Accept": "application/json", "Content-Type": "application/json", "Cookie": f"JSESSIONID={auth_token}"}

# P2 - Secondary Tests (Optional Edge & Stress Cases)

def test_stress_search_pagination(headers):
    url = f"{BASE_URL}/ws/public/search?page=100&pageSize=10"
    response = requests.get(url, headers=headers)
    assert response.status_code == 200

def test_rapid_sequence_downloads(headers):
    url_prepare = f"{BASE_URL}/ws/public/download/json/prepare/Federal_LCA_Commons/test_repo"
    response_prepare = requests.get(url_prepare, headers=headers)
    assert response_prepare.status_code == 200
    token = response_prepare.text

    url_download = f"{BASE_URL}/ws/public/download/json/{token}"
    for _ in range(5):
        response_download = requests.get(url_download, headers=headers)
        assert response_download.status_code == 200

def test_multiple_simultaneous_logins():
    url = f"{BASE_URL}/ws/public/login"
    payload = {"username": "valid_user", "password": "valid_pass"}
    for _ in range(5):
        response = requests.post(url, json=payload)
        assert response.status_code == 200

def test_fuzz_search_inputs(headers):
    fuzz_inputs = ["?query=<script>", "?group=\" OR \"1\"=\"1"]
    for input_str in fuzz_inputs:
        url = f"{BASE_URL}/ws/public/search{input_str}"
        response = requests.get(url, headers=headers)
        assert response.status_code == 200 or response.status_code == 400

def test_rate_limit_handling(headers):
    url = f"{BASE_URL}/ws/public/search"
    responses = []
    for _ in range(50):
        response = requests.get(url, headers=headers)
        responses.append(response.status_code)
    assert 429 not in responses  # assuming no rate limit currently implemented, adjust based on actual system behavior
