--
-- Creating table to hold SCL Data. The SCL is identified by it's ID, but there can be multiple versions.
--
create table scl_file (
    id uuid not null,
    version varchar(20) not null,
    type varchar(3) not null,
    name varchar(255) not null,
    scl_data text not null,
    primary key (id, version)
);

create index scl_file_type on scl_file(type);

comment on table scl_file is 'Table holding all the SCL Data. The combination id and version are unique (pk).';
comment on column scl_file.id is 'Unique ID generated according to standards';
comment on column scl_file.version is 'Versioning according to Semantic Versioning';
comment on column scl_file.type is 'The type of SCL stored';
comment on column scl_file.name is 'The name of the SCL File';
comment on column scl_file.scl_data is ' The SCL XML Content';
