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
	f_team BIGINT,
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
	ds_path VARCHAR(4000) NOT NULL,
	f_user BIGINT,
	date TIMESTAMP NOT NULL,
	text VARCHAR(4000),
	restricted_to_role VARCHAR(255),
	f_reply_to BIGINT
);

CREATE TABLE task_assignments(
	id BIGINT NOT NULL,
	comment VARCHAR(4000),
	start_date TIMESTAMP NOT NULL,
	end_date TIMESTAMP,
	iteration BIGINT NOT NULL DEFAULT 1,
	canceled BOOLEAN NOT NULL DEFAULT false,
	f_assigned_to BIGINT NOT NULL,
	f_ended_by BIGINT,
	f_task BIGINT
);

CREATE TABLE reviews(
	id BIGINT NOT NULL,
	name VARCHAR(255),
	repository_path VARCHAR(255) NOT NULL,
	state VARCHAR(255) NOT NULL,
	comment VARCHAR(4000),
	start_date TIMESTAMP NOT NULL,
	end_date TIMESTAMP,
	f_initiator BIGINT NOT NULL
);

CREATE TABLE review_references(
	id BIGINT NOT NULL,
	ds_type VARCHAR(255) NOT NULL,
	ds_ref_id VARCHAR(36) NOT NULL,
	ds_commit_id VARCHAR(36) NOT NULL,
	ds_name VARCHAR(255) NOT NULL,
	f_review BIGINT
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