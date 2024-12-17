--
-- Add 'location_id' column to the scl_file table.
--
ALTER TABLE scl_file ADD COLUMN location_id uuid DEFAULT NULL;
ALTER TABLE scl_file ADD CONSTRAINT fk_scl_file_location
        foreign key (location_id)
        REFERENCES location(id)
        ON DELETE CASCADE;