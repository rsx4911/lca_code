# Webservice documentation

## Login
Login with your credentials; use JSESSIONID cookie for further calls

```bash
curl -X POST [server_address]/ws/public/login \
	 -d '{"username":"foo","password":"bar"}' \
	 -H "Content-Type: application/json" -i
```
```bash
HTTP/1.1 200 OK
...
Set-Cookie: JSESSIONID=8D184B7DFDA4751287BE363F7A1C9A04; Path=/; HttpOnly
...
```

## Logout
```bash
curl -X POST [server_address]/ws/public/logout \
	 -b "JSESSIONID=FOOBAR" -i
```
```bash
HTTP/1.1 200 OK
...
```

## Create a new repository
```bash
curl -X POST [server_address]/ws/repository/create/REPOSITORY_NAME \
	 -b "JSESSIONID=FOOBAR" -i
```
```bash
HTTP/1.1 201 CREATED
...
```
 
## Delete an existing repository
```bash
curl -X DELETE [server_address]/ws/repository/delete/REPOSITORY_NAME \
	 -b "JSESSIONID=FOOBAR" -i
```
```bash
HTTP/1.1 200 OK
...
```

## Share an existing repository
```bash
curl -X POST [server_address]/ws/repository/share/REPOSITORY_NAME/WITH_USER \
	 -b "JSESSIONID=FOOBAR" -i
```
```bash
HTTP/1.1 200 OK
...
```

## Cancel sharing of an existing repository
```bash
curl -X POST [server_address]/ws/repository/unshare/REPOSITORY_NAME/WITH_USER \
	 -b "JSESSIONID=FOOBAR" -i
```
```bash
HTTP/1.1 200 OK
...
```

## List users with access to repository
```bash
curl -X GET [server_address]/ws/repository/shared/REPOSITORY_NAME \
	 -b "JSESSIONID=FOOBAR" -i
```
```bash
HTTP/1.1 200 OK
...
["user1","user2"]
```

## List repositories current user can access
```bash
curl -X GET [server_address]/ws/user/shared \
	 -b "JSESSIONID=FOOBAR" -i
```
```
HTTP/1.1 200 OK
...
["username1/repository1","username2/repository2"]
```

## Request a commit
```bash
curl -X GET [server_address]/ws/repository/commit/request/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -i
```
```
HTTP/1.1 200 OK
...
```

## Commit data
```bash
curl -X POST [server_address]/ws/repository/commit/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -d '{"message":"commit message text","data":[{"type":"ACTOR","refId":"","version":"1.2.1","lastChange":14983897636,"json":"{}"}]}' \
	 -H "Content-Type: application/json" \
	 -b "JSESSIONID=FOOBAR" -i
```

## Request a fetch
```bash
curl -X GET [server_address]/ws/repository/fetch/request/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -i
```

## Fetch data
```bash
curl -X GET [server_address]/ws/repository/fetch/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -i
```

## List commit entries
```bash
curl -X GET [server_address]/ws/repository/fetch/commits/REPOSITORY_OWNER/REPOSITORY_NAME/LATEST_COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -i
```

## List file references for a commit
```bash
curl -X GET [server_address]/ws/repository/fetch/references/REPOSITORY_OWNER/REPOSITORY_NAME/COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -i
```

## Get data set content
```bash
curl -X GET [server_address]/ws/repository/fetch/data/REPOSITORY_OWNER/REPOSITORY_NAME/TYPE/REF_ID/COMMIT_ID \
	 -b "JSESSIONID=FOOBAR" -i
```