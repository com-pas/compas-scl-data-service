--
-- Creating table to hold Archived Resource Data. A Archived Resource is identified by its ID, major-, minor- and patch version.
--

create table archived_resource (
    id uuid not null,
    name varchar(255) not null,
    major_version smallint not null,
    minor_version smallint not null,
    patch_version smallint not null,
    location varchar(255),
    note varchar(255),
    author varchar(255),
    approver varchar(255),
    type varchar(255),
    content_type varchar(3),
    voltage varchar(255),
    modified_at  TIMESTAMP WITH TIME ZONE not null,
    archived_at  TIMESTAMP WITH TIME ZONE not null,
    primary key (id, major_version, minor_version, patch_version)
);

comment on table archived_resource is 'Table holding all the Archived Resource Data and its location assignments. The id, the major-, minor- and patch versions are unique (pk).';
comment on column archived_resource.id is 'Unique ID generated according to standards';
comment on column archived_resource.name is 'The name of the Location';
comment on column archived_resource.major_version is 'The major version of the Archived Resource';
comment on column archived_resource.minor_version is 'The minor version of the Archived Resource';
comment on column archived_resource.patch_version is 'The patch version of the Archived Resource';
comment on column archived_resource.location is 'The assigned Location of the Archived Resource';
comment on column archived_resource.note is 'The note of Archived Resource';
comment on column archived_resource.approver is 'The approver of the Archived Resource';
comment on column archived_resource.type is 'The type of the Archived Resource';
comment on column archived_resource.content_type is 'The content type of the Archived Resource';
comment on column archived_resource.voltage is 'The voltage of the Archived Resource';
comment on column archived_resource.modified_at is 'The modified timestamp of the Archived Resource';
comment on column archived_resource.archived_at is 'The archivedAt timestamp of the Archived Resource';
