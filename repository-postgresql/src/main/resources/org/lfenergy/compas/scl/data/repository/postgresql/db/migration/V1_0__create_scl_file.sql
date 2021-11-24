/**
 * SPDX-FileCopyrightText: 2021 Alliander N.V.
 *
 * SPDX-License-Identifier: Apache-2.0
*/

--
-- Creating table to hold SCL Data. The SCL is identified by it's ID, but there can be multiple versions.
--
create table scl_file (
    id uuid not null,
    major_version smallint not null,
    minor_version smallint not null,
    patch_version smallint not null,
    type varchar(3) not null,
    name varchar(255) not null,
    scl_data text not null,
    primary key (id, major_version, minor_version, patch_version)
);

create index scl_file_type on scl_file(type);

comment on table scl_file is 'Table holding all the SCL Data. The combination id and version are unique (pk).';
comment on column scl_file.id is 'Unique ID generated according to standards';
comment on column scl_file.major_version is 'Versioning according to Semantic Versioning (Major Position)';
comment on column scl_file.minor_version is 'Versioning according to Semantic Versioning (Minor Position)';
comment on column scl_file.patch_version is 'Versioning according to Semantic Versioning (Patch Position)';
comment on column scl_file.type is 'The type of SCL stored';
comment on column scl_file.name is 'The name of the SCL File';
comment on column scl_file.scl_data is ' The SCL XML Content';
