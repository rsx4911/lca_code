CREATE TABLE users(id bigint(20) unsigned NOT NULL DEFAULT '0', name varchar(255) NOT NULL DEFAULT '', hash varchar(64) NOT NULL DEFAULT '', salt varchar(16) NOT NULL DEFAULT '', last_login timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(id))
CREATE TABLE sequence (name VARCHAR(50) NOT NULL, count DECIMAL(15), PRIMARY KEY (name))
INSERT INTO sequence(name, count) VALUES ('users', 0)