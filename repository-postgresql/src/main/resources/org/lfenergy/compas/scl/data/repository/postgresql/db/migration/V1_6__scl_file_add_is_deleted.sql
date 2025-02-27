-- Adding the boolean field 'is_deleted' with default value false
ALTER TABLE scl_file
    ADD COLUMN is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN scl_file.is_deleted IS 'Indicates if the SCL entry is marked as deleted (soft delete)';
