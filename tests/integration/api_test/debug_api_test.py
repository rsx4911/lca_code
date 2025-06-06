import requests
import json
import os
import zipfile
import time
from datetime import datetime

#API TESTING AND DEBUGGING SCRIPT - SAVE AS debug_api_test.py

def login(base_url):
    """Logs in to the API and returns the auth token."""
    username = input("Enter your username: ")
    password = input("Enter your password: ")
    
    url = f"{base_url}/ws/public/login"
    payload = {
        "username": username,
        "password": password
    }

    try:
        response = requests.post(url, json=payload)
        if response.status_code == 200:
            print("Login successful.")
            return response.cookies.get("JSESSIONID")
        else:
            print(f"Login failed with status code: {response.status_code}")
            print(f"Response content: {response.text}")
            return None
    except requests.exceptions.RequestException as e:
        print(f"Login request failed: {str(e)}")
        return None

def get_repository_info(base_url, token, group, repo):
    """Gets repository metadata to check supported types."""
    url = f"{base_url}/ws/repository/{group}/{repo}"
    
    cookies = {"JSESSIONID": token}
    headers = {
        "Accept": "application/json",
        "Content-Type": "application/json"
    }
    
    try:
        response = requests.get(url, cookies=cookies, headers=headers)
        if response.status_code == 200:
            repo_info = response.json()
            print("\nRepository Information:")
            print(json.dumps(repo_info, indent=2))
            return repo_info
        else:
            print(f"Failed to get repository info: {response.status_code}")
            return None
    except requests.exceptions.RequestException as e:
        print(f"Error getting repository info: {e}")
        return None

def create_unit_group(timestamp):
    """Creates a unit group matching repository format."""
    return {
        "@type": "UnitGroup",
        "@id": f"test_unit_group_{timestamp}",
        "name": "Test Units",
        "description": "Test unit group for API testing",
        "category": "Technical unit groups",
        "version": "00.00.000",
        "defaultFlowProperty": {
            "@type": "FlowProperty",
            "@id": f"test_flow_property_{timestamp}",
            "name": "Test Property",
            "category": "Technical flow properties",
            "refUnit": "kg"
        },
        "units": [
            {
                "@type": "Unit",
                "@id": f"test_unit_kg_{timestamp}",
                "name": "kg",
                "description": "Kilogram",
                "conversionFactor": 1.0,
                "isRefUnit": True
            },
            {
                "@type": "Unit",
                "@id": f"test_unit_g_{timestamp}",
                "name": "g",
                "description": "Gram",
                "conversionFactor": 0.001
            }
        ]
    }

def create_flow_property(timestamp, unit_group_id):
    """Creates a flow property matching repository format."""
    return {
        "@type": "FlowProperty",
        "@id": f"test_flow_property_{timestamp}",
        "name": "Test Property",
        "description": "Test flow property for API testing",
        "category": "Technical flow properties",
        "version": "00.00.000",
        "flowPropertyType": "PHYSICAL_QUANTITY",
        "unitGroup": {
            "@type": "UnitGroup",
            "@id": unit_group_id,
            "name": "Test Units",
            "category": "Technical unit groups"
        }
    }

def create_test_flow(timestamp, flow_property_id, unit_group_id):
    """Creates a test flow with proper property references."""
    return {
        "@type": "Flow",
        "@id": f"test_flow_{timestamp}",
        "name": "Test Material Flow",
        "description": "A test flow for API testing",
        "category": "Elementary flows/emission/air",
        "version": "00.00.000",
        "flowType": "ELEMENTARY_FLOW",
        "cas": "",
        "formula": "-",
        "lastChange": datetime.now().isoformat() + "Z",
        "flowProperties": [{
            "@type": "FlowPropertyFactor",
            "isRefFlowProperty": True,
            "flowProperty": {
                "@type": "FlowProperty",
                "@id": flow_property_id,
                "name": "Test Property",
                "category": "Technical flow properties"
            },
            "conversionFactor": 1.0
        }]
    }

def create_repository_metadata():
    """Creates the openlca.json metadata file."""
    return {
        "schemaVersion": 2,
        "repositoryClientVersion": 6,
        "repositoryServerVersion": 5
    }

def create_test_files():
    """Creates all test files with proper structure."""
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
    zip_filename = "test_data.zip"
    
    try:
        # Create unique IDs
        unit_group_id = f"test_unit_group_{timestamp}"
        flow_property_id = f"test_flow_property_{timestamp}"
        
        # Create all data objects
        unit_group = create_unit_group(timestamp)
        flow_property = create_flow_property(timestamp, unit_group_id)
        flow = create_test_flow(timestamp, flow_property_id, unit_group_id)
        metadata = create_repository_metadata()
        
        # Create ZIP with correct structure
        with zipfile.ZipFile(zip_filename, 'w', zipfile.ZIP_DEFLATED) as zipf:
            # Add repository metadata
            zipf.writestr("openlca.json", json.dumps(metadata, indent=2))
            
            # Add unit group
            zipf.writestr(
                f"unit_groups/{unit_group_id}.json",
                json.dumps(unit_group, indent=2)
            )
            
            # Add flow property
            zipf.writestr(
                f"flow_properties/{flow_property_id}.json",
                json.dumps(flow_property, indent=2)
            )
            
            # Add flow
            zipf.writestr(
                f"flows/{flow['@id']}.json",
                json.dumps(flow, indent=2)
            )
        
        print(f"\nTest ZIP file created: {zip_filename}")
        print("\nRepository metadata:")
        print(json.dumps(metadata, indent=2))
        print("\nUnit group data:")
        print(json.dumps(unit_group, indent=2))
        print("\nFlow property data:")
        print(json.dumps(flow_property, indent=2))
        print("\nFlow data:")
        print(json.dumps(flow, indent=2))
        
        return zip_filename, flow["@id"]
    except Exception as e:
        print(f"Error creating test files: {e}")
        return None, None

def get_recent_commits(base_url, token, group, repo):
    """Fetches recent commits to get the hash of our latest commit."""
    # Try multiple endpoints to find commits
    endpoints = [
        # Direct history endpoint
        f"{base_url}/ws/history/{group}/{repo}",
        # Repository activities
        f"{base_url}/ws/repository/{group}/{repo}/activity",
        # Search endpoint with commits
        f"{base_url}/ws/history/search/{group}/{repo}"
    ]
    
    cookies = {"JSESSIONID": token}
    headers = {
        "Accept": "application/json",
        "Content-Type": "application/json"
    }
    
    for url in endpoints:
        try:
            print(f"\nTrying commits endpoint: {url}")
            response = requests.get(url, cookies=cookies, headers=headers)
            print(f"Response status: {response.status_code}")
            
            if response.status_code == 200:
                try:
                    data = response.json()
                    print("\nResponse data:")
                    print(json.dumps(data, indent=2))
                    
                    # Check for commits in various response formats
                    commits = None
                    if isinstance(data, dict):
                        commits = data.get('data', []) or data.get('commits', []) or data.get('activities', [])
                    elif isinstance(data, list):
                        commits = data
                    
                    if commits and len(commits) > 0:
                        # Look for our commit based on message and timestamp
                        for commit in commits:
                            message = commit.get('message', '')
                            if 'Test commit via API' in message:
                                commit_hash = commit.get('id') or commit.get('commitId') or commit.get('hash')
                                if commit_hash:
                                    print(f"Found matching commit hash: {commit_hash}")
                                    return commit_hash
                            
                except json.JSONDecodeError as e:
                    print(f"Failed to parse response from {url}: {e}")
            else:
                print(f"Response content: {response.text}")
                
        except requests.exceptions.RequestException as e:
            print(f"Error with endpoint {url}: {e}")
    
    print("Could not find commit hash in any endpoint")
    return None

def verify_commit_data(base_url, token, group, repo, ref_id):
    """Verifies the committed data using repository contents search."""
    print("\n=== Verifying Committed Data ===")
    
    cookies = {"JSESSIONID": token}
    headers = {
        "Accept": "application/json",
        "Content-Type": "application/json"
    }
    
    # First check repository index status
    status_url = f"{base_url}/ws/repository/{group}/{repo}/indexStatus"
    try:
        print(f"\nChecking repository index status: {status_url}")
        response = requests.get(status_url, cookies=cookies, headers=headers)
        if response.status_code == 200:
            print(f"Index status response: {response.text}")
    except Exception as e:
        print(f"Error checking index status: {e}")
    
    # Try different data access endpoints
    timestamp = ref_id.split('_')[-1]  # Extract timestamp from ID
    endpoints = [
        # Check unit group
        f"{base_url}/ws/public/browse/{group}/{repo}/UNIT_GROUP/test_unit_group_{timestamp}",
        # Check flow property
        f"{base_url}/ws/public/browse/{group}/{repo}/FLOW_PROPERTY/test_flow_property_{timestamp}",
        # Check flow
        f"{base_url}/ws/public/browse/{group}/{repo}/FLOW/{ref_id}",
        # Repository contents
        f"{base_url}/ws/repository/{group}/{repo}/contents",
        # Search endpoint
        f"{base_url}/ws/public/search?repositoryId={group}/{repo}"
    ]
    
    for url in endpoints:
        try:
            print(f"\nChecking endpoint: {url}")
            response = requests.get(url, cookies=cookies, headers=headers)
            print(f"Response Status: {response.status_code}")
            
            if response.status_code == 200:
                try:
                    data = response.json()
                    print("\nResponse data:")
                    print(json.dumps(data, indent=2))
                    return True
                except json.JSONDecodeError:
                    print("Failed to parse JSON response")
            else:
                print(f"Response content: {response.text}")
                
        except Exception as e:
            print(f"Error checking endpoint {url}: {e}")
    
    return False

def make_test_commit(base_url, token, group, repo, zip_filename):
    """Makes a test commit to the repository."""
    url = f"{base_url}/ws/repository/import/{group}/{repo}"
    
    with open(zip_filename, 'rb') as zip_file:
        files = {
            'file': (zip_filename, zip_file, 'application/zip')
        }
        
        data = {
            'commitMessage': 'Test commit via API - Flow Data Test',
            'format': 'json-ld'
        }
        
        cookies = {"JSESSIONID": token}
        headers = {
            "Accept": "application/json"
        }
        
        try:
            print(f"\nAttempting to commit to: {url}")
            print(f"Commit message: {data['commitMessage']}")
            
            response = requests.post(
                url,
                files=files,
                data=data,
                cookies=cookies,
                headers=headers
            )
            
            print(f"Response Status Code: {response.status_code}")
            print(f"Response Headers: {response.headers}")
            print(f"Response Content: {response.text}")
            
            if response.status_code == 200:
                print("Commit successful!")
                return True
            else:
                print(f"Commit failed with status {response.status_code}")
                return False
                
        except requests.exceptions.RequestException as e:
            print(f"Request error during commit: {str(e)}")
            return False

def main():
    BASE_URL = "https://lcacommons.gov/lca-collaboration"
    
    # Log in first
    token = login(BASE_URL)
    if not token:
        print("Failed to log in. Exiting.")
        return
    
    # Get repository details
    group = input("Enter the group name (e.g., Federal_LCA_Commons): ")
    repo = input("Enter the repository name: ")
    
    # Get repository info
    repo_info = get_repository_info(BASE_URL, token, group, repo)
    if not repo_info:
        print("Failed to get repository information. Exiting.")
        return
    
    # Create test ZIP file with process and flow data
    zip_filename, ref_id = create_test_files()
    if not zip_filename:
        print("Failed to create test files. Exiting.")
        return
    
    try:
        # Attempt the commit
        success = make_test_commit(BASE_URL, token, group, repo, zip_filename)
        
        if success:
            print("\nTest commit completed successfully!")
            print("Waiting 5 seconds before verification...")
            time.sleep(5)
            
            # Try to get commit hash
            commit_hash = get_recent_commits(BASE_URL, token, group, repo)
            
            # Multiple verification attempts
            max_attempts = 5
            delays = [10, 20, 30, 45, 60]  # Increasing delays between attempts
            
            for attempt in range(max_attempts):
                print(f"\nVerification attempt {attempt + 1} of {max_attempts}")
                
                verify_success = verify_commit_data(BASE_URL, token, group, repo, ref_id)
                if verify_success:
                    print("\nCommit verification successful!")
                    break
                    
                if attempt < max_attempts - 1:
                    wait_time = delays[attempt]
                    print(f"\nWaiting {wait_time} seconds before next attempt...")
                    time.sleep(wait_time)
                else:
                    print("\nAll verification attempts failed - data might need more time to be indexed")
        else:
            print("\nTest commit failed!")
    
   

    
    finally:
        # Clean up the test file
        try:
            if os.path.exists(zip_filename):
                os.remove(zip_filename)
                print(f"\nCleaned up test file: {zip_filename}")
        except Exception as e:
            print(f"Warning: Could not clean up {zip_filename}: {e}")

if __name__ == "__main__":
    main()