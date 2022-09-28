/**
 * SPDX-FileCopyrightText: 2021 Alliander N.V.
 *
 * SPDX-License-Identifier: Apache-2.0
*/

--
-- Creating table to hold SCL Labels. Labels should be unique, because they are part of the PK.
--
create table scl_label (
    scl_id uuid not null,
    major_version smallint not null,
    minor_version smallint not null,
    patch_version smallint not null,
    label_value varchar(255) not null,
    primary key (scl_id, major_version, minor_version, patch_version, label_value),
    constraint fk_label_to_scl
      foreign key(scl_id, major_version, minor_version, patch_version)
	  references scl_file(id, major_version, minor_version, patch_version)
      on delete cascade
);

comment on table scl_label is 'Table holding all the labels. The combination id, version and label is unique (pk).';
comment on column scl_label.scl_id is 'Unique ID generated according to standards';
comment on column scl_label.major_version is 'Versioning according to Semantic Versioning (Major Position)';
comment on column scl_label.minor_version is 'Versioning according to Semantic Versioning (Minor Position)';
comment on column scl_label.patch_version is 'Versioning according to Semantic Versioning (Patch Position)';
comment on column scl_label.label_value is 'The label stored';
