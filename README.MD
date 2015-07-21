# Webservice documentation

## Login
Login with your credentials; use JSESSIONID cookie for further calls
```
curl -X POST [server_address]/ws/public/login \
	 -d '{"username":"foo","password":"bar"}' \
	 -H "Content-Type: application/json" -i
```

## Logout
```
curl -X POST [server_address]/ws/public/logout \
	 -b "JSESSIONID=FOOBAR" -v
```

## Create a new repository
```
curl -X POST [server_address]/ws/repository/create/REPOSITORY_NAME \
	 -b "JSESSIONID=FOOBAR" -v
```
 
## Delete an existing repository
```
curl -X DELETE [server_address]/ws/repository/delete/REPOSITORY_NAME \
	 -b "JSESSIONID=FOOBAR" -v
```

## Share an existing repository
```
curl -X POST [server_address]/ws/repository/share/REPOSITORY_NAME \
	 -b "JSESSIONID=FOOBAR" -v
```

## Cancel sharing of an existing repository
```
curl -X POST [server_address]/ws/repository/unshare/REPOSITORY_NAME \
	 -b "JSESSIONID=FOOBAR" -v
```

## List users with access to repository
```
curl -X GET [server_address]/ws/repository/shared/REPOSITORY_NAME \
	 -b "JSESSIONID=FOOBAR" -v
```

## List repositories current user can access
```
curl -X GET [server_address]/ws/user/shared \
	 -b "JSESSIONID=FOOBAR" -v
```

## Request a commit
```
curl -X GET [server_address]/ws/repository/commit/request/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -v
```

## Commit data
```
curl -X POST [server_address]/ws/repository/commit/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -d '{"message":"commit message text","data":[{"type":"ACTOR","refId":"","version":"1.2.1","lastChange":14983897636,"json":"{}"}]}' \
	 -H "Content-Type: application/json" \
	 -b "JSESSIONID=FOOBAR" -v
```

## Request a fetch
```
curl -X GET [server_address]/ws/repository/fetch/request/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -v
```

## Fetch data
```
curl -X GET [server_address]/ws/repository/fetch/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -v
```

## List commit entries
```
curl -X GET [server_address]/ws/repository/fetch/commits/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -v
```

## List file references for a commit
```
curl -X GET [server_address]/ws/repository/fetch/references/REPOSITORY_OWNER/REPOSITORY_NAME/COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -v
```

## Get data set content
```
curl -X GET [server_address]/ws/repository/fetch/data/REPOSITORY_OWNER/REPOSITORY_NAME/TYPE/REF_ID/COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -v
```