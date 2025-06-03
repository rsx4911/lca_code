ALTER TABLE setting CHANGE COLUMN value value TEXT NULL DEFAULT NULL;
ALTER TABLE release_info
  CHANGE COLUMN description description TEXT NULL DEFAULT NULL,
  CHANGE COLUMN source_info source_info TEXT NULL DEFAULT NULL,
  CHANGE COLUMN contact_info contact_info TEXT NULL DEFAULT NULL,
  CHANGE COLUMN change_log change_log TEXT NULL DEFAULT NULL,
  CHANGE COLUMN project_info project_info TEXT NULL DEFAULT NULL,
  CHANGE COLUMN project_funding project_funding TEXT NULL DEFAULT NULL,
  CHANGE COLUMN appropriate_use appropriate_use TEXT NULL DEFAULT NULL,
  CHANGE COLUMN dq_assessment dq_assessment TEXT NULL DEFAULT NULL,
  CHANGE COLUMN citation citation TEXT NULL DEFAULT NULL;
