UPDATE setting SET data = null, value = 'PUBLIC' WHERE type = 'LIBRARY_SETTING' AND name = 'ACCESS' AND data LIKE '%"PUBLIC"%';
UPDATE setting SET data = null, value = 'USER' WHERE type = 'LIBRARY_SETTING' AND name = 'ACCESS' AND data LIKE '%"USER"%';
UPDATE setting SET data = null, value = 'MEMBER' WHERE type = 'LIBRARY_SETTING' AND name = 'ACCESS' AND value IS NULL;
