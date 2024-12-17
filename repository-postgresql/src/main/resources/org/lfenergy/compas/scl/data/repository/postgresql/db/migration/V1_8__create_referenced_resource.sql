--
-- Create table to hold Referenced Resource Data. A Referenced Resource is identified by its ID.
--

create table referenced_resource
(
    id uuid not null,
    type varchar(255),
    content_type varchar(255) not null,
    filename varchar(255) not null,
    author varchar(255) not null,
    approver varchar(255),
    location_id uuid,
    scl_file_id uuid not null,
    scl_file_major_version smallint not null,
    scl_file_minor_version smallint not null,
    scl_file_patch_version smallint not null,
    primary key (id, scl_file_major_version, scl_file_minor_version, scl_file_patch_version),
    constraint fk_scl_file
        foreign key (scl_file_id, scl_file_major_version, scl_file_minor_version, scl_file_patch_version)
        REFERENCES scl_file(id, major_version, minor_version, patch_version)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    constraint fk_location
        foreign key (location_id)
        REFERENCES location(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

comment on table referenced_resource is 'Table holding all referenced resources. The id is unique (pk)';
comment on column referenced_resource.id is 'Unique referenced resource ID generated according to standards';
comment on column referenced_resource.content_type is 'The type of content stored';
comment on column referenced_resource.type is 'The type of content stored';
comment on column referenced_resource.filename is 'The name of the uploaded file';
comment on column referenced_resource.author is 'The name of the author of the file';
comment on column referenced_resource.approver is 'The name of the approver of the file';
comment on column referenced_resource.location_id is 'Unique location ID generated according to standards';
comment on column referenced_resource.scl_file_id is 'Unique resource_tag ID generated according to standards';
comment on column referenced_resource.scl_file_major_version is 'Versioning according to Semantic Versioning (Major Position)';
comment on column referenced_resource.scl_file_minor_version is 'Versioning according to Semantic Versioning (Minor Position)';
comment on column referenced_resource.scl_file_patch_version is 'Versioning according to Semantic Versioning (Patch Position)';
