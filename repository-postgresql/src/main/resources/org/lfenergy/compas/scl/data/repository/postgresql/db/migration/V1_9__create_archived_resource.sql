--
-- Creating table to hold Archived Resource Data. An Archived Resource is identified by its ID.
--

create table archived_resource (
    id uuid not null,
    scl_file_id uuid,
    archived_at TIMESTAMP WITH TIME ZONE not null,
    scl_file_major_version smallint,
    scl_file_minor_version smallint,
    scl_file_patch_version smallint,
    referenced_resource_id uuid,
    referenced_resource_major_version smallint,
    referenced_resource_minor_version smallint,
    referenced_resource_patch_version smallint,
    primary key (id),
    constraint fk_scl_file
        foreign key (scl_file_id, scl_file_major_version, scl_file_minor_version, scl_file_patch_version)
        references scl_file(id, major_version, minor_version, patch_version)
        on update cascade
        on delete cascade,
    constraint fk_referenced_resource
        foreign key (referenced_resource_id, referenced_resource_major_version, referenced_resource_minor_version, referenced_resource_patch_version)
        references referenced_resource(id, scl_file_major_version, scl_file_minor_version, scl_file_patch_version)
        on update cascade
        on delete cascade
);

comment on table archived_resource is 'Table holding all the Archived Resource Data and its location assignments. The id is unique (pk).';
comment on column archived_resource.id is 'Unique ID generated according to standards';
comment on column archived_resource.scl_file_id is 'Unique ID generated according to standards';
comment on column archived_resource.scl_file_major_version is 'Versioning according to Semantic Versioning (Major Position)';
comment on column archived_resource.scl_file_minor_version is 'Versioning according to Semantic Versioning (Minor Position)';
comment on column archived_resource.scl_file_patch_version is 'Versioning according to Semantic Versioning (Patch Position)';
comment on column archived_resource.referenced_resource_id is 'Unique ID generated according to standards';
comment on column archived_resource.referenced_resource_major_version is 'Versioning according to Semantic Versioning (Major Position)';
comment on column archived_resource.referenced_resource_minor_version is 'Versioning according to Semantic Versioning (Minor Position)';
comment on column archived_resource.referenced_resource_patch_version is 'Versioning according to Semantic Versioning (Patch Position)';
