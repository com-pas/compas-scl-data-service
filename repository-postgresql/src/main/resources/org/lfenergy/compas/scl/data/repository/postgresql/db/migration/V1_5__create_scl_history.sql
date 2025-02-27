--
-- Creating table to hold SCL History Data. The SCL is identified by it's ID, but there can be multiple versions.
--
create table scl_history (
    id uuid not null,
    name varchar(255) not null,
    major_version smallint not null,
    minor_version smallint not null,
    patch_version smallint not null,
    type varchar(3) not null,
    author varchar(255) not null,
    comment varchar(255) not null,
    changedAt TIMESTAMP WITH TIME ZONE not null,
    archived boolean not null default false,
    available boolean not null default true,
    primary key (id, major_version, minor_version, patch_version)
);

create index scl_history_type on scl_history(type);

comment on table scl_history is 'Table holding all the SCL History Data. The combination id and version are unique (pk).';
comment on column scl_history.id is 'Unique ID generated according to standards';
comment on column scl_history.name is 'The name of the SCL File';
comment on column scl_history.major_version is 'Versioning according to Semantic Versioning (Major Position)';
comment on column scl_history.minor_version is 'Versioning according to Semantic Versioning (Minor Position)';
comment on column scl_history.patch_version is 'Versioning according to Semantic Versioning (Patch Position)';
comment on column scl_history.type is 'The type of SCL stored';
comment on column scl_history.author is 'The author of the change in this version of the SCL File';
comment on column scl_history.comment is 'The comment of the change in this version of the SCL File';
comment on column scl_history.changedAt is 'The date of the change in this version of the SCL File';
comment on column scl_history.archived is 'Is this version of the SCL File archived';
comment on column scl_history.available is 'Is this version of the SCL File available';
