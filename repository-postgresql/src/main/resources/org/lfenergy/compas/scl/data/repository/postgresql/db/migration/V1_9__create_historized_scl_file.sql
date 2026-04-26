-- SPDX-FileCopyrightText: 2026 BearingPoint GmbH
--
-- SPDX-License-Identifier: Apache-2.0

create table historized_scl_file (
    id                      uuid not null default gen_random_uuid(),
    location_id             uuid,
    scl_file_id             uuid not null,
    scl_file_major_version  smallint not null,
    scl_file_minor_version  smallint not null,
    scl_file_patch_version  smallint not null,
    content_type            varchar(255) not null,
    filename                varchar(255) not null,
    comment                 varchar(255),
    changed_at              timestamp with time zone not null default now(),
    archived                boolean not null default false,
    available               boolean not null default true,

    primary key (id),

    constraint fk_historized_scl_file_location
        foreign key (location_id)
        references location (id)
        on delete set null,

    constraint fk_historized_scl_file_scl_file
        foreign key (scl_file_id, scl_file_major_version, scl_file_minor_version, scl_file_patch_version)
        references scl_file (id, major_version, minor_version, patch_version)
        on update cascade
        on delete cascade
);

create index idx_historized_scl_file_location_id on historized_scl_file (location_id);
create index idx_historized_scl_file_scl_file_id on historized_scl_file (scl_file_id);

comment on table historized_scl_file is 'Tracks SCL file versions assigned to locations, replacing the legacy data_resource_history table.';
comment on column historized_scl_file.location_id       is 'FK to location; null when not assigned to any location.';
comment on column historized_scl_file.scl_file_id       is 'FK part 1 - ID of the referenced scl_file row.';
comment on column historized_scl_file.scl_file_major_version is 'FK part 2 - major version of the referenced scl_file row.';
comment on column historized_scl_file.scl_file_minor_version is 'FK part 3 - minor version of the referenced scl_file row.';
comment on column historized_scl_file.scl_file_patch_version is 'FK part 4 - patch version of the referenced scl_file row.';
