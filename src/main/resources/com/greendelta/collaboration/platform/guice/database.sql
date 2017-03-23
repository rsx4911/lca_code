CREATE TABLE users(
	id BIGINT NOT NULL, 
	username VARCHAR(255) NOT NULL, 
	name VARCHAR(255) NOT NULL, 
	email VARCHAR(255) NOT NULL, 
	hash VARCHAR(64) NOT NULL, 
	salt VARCHAR(16) NOT NULL, 
	avatar BLOB(16 M), 
	two_factor_secret VARCHAR(255),
	admin BOOLEAN NOT NULL DEFAULT false, 
	can_create_groups BOOLEAN NOT NULL DEFAULT false, 
	can_create_repositories BOOLEAN NOT NULL DEFAULT false, 
	messaging_enabled BOOLEAN NOT NULL DEFAULT true, 
	messaging_restricted BOOLEAN NOT NULL DEFAULT false, 
	show_online_status BOOLEAN NOT NULL DEFAULT true, 
	show_read_receipt BOOLEAN NOT NULL DEFAULT true, 
	notifications INT NOT NULL DEFAULT 0
);

CREATE TABLE teams(
	id BIGINT NOT NULL, 
	teamname VARCHAR(255) NOT NULL, 
	name VARCHAR(255) NOT NULL, 
	avatar BLOB(16 M)
);

CREATE TABLE team_users(
	f_team BIGINT NOT NULL, 
	f_user BIGINT NOT NULL
);

CREATE TABLE memberships(
	id BIGINT NOT NULL, 
	f_user BIGINT, 
	f_team BIGINT, 
	member_of VARCHAR(255) NOT NULL, 
	role VARCHAR(255) NOT NULL
);

CREATE TABLE messages(
	id BIGINT NOT NULL,
	f_from_user BIGINT NOT NULL,
	f_to_user BIGINT NOT NULL,
	f_team BIGINT NOT NULL,
	date TIMESTAMP NOT NULL,
	text VARCHAR(4000) NOT NULL,
	read_date TIMESTAMP,
	show_read_receipt BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE blocked_users(
	f_user BIGINT NOT NULL,
	f_blocked BIGINT NOT NULL
);

CREATE TABLE version(
	version INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE comments(
	id BIGINT NOT NULL,
	repository_path VARCHAR(255) NOT NULL,
	ds_type VARCHAR(255) NOT NULL,
	ds_ref_id VARCHAR(36) NOT NULL,
	ds_commit_id VARCHAR(36) NOT NULL,
	ds_field VARCHAR(4000) NOT NULL,
	f_user BIGINT,
	date TIMESTAMP NOT NULL,
	text VARCHAR(4000),
	restricted_to_role VARCHAR(255),
	f_reply_to BIGINT
);

INSERT INTO version VALUES (2);

INSERT INTO users VALUES (
	1, 
	'admin', 
	'Administrator', 
	'admin@yourdomain.com', 
	'4beff40c3cf2ef51b5b840851b2836b2d1d6e25f2f1e7ec2e9e5fb5562638a1c', 
	'b7658c0e20a4134e', 
	null,
	null,
	true, 
	true, 
	true, 
	true,
	false,
	true,
	true,
	0
);