/**
 * SPDX-FileCopyrightText: 2026 BearingPoint GmbH
 *
 * SPDX-License-Identifier: Apache-2.0
 */

--
-- Creating table to hold Location data. A Location is identified by its UUID.
--
create table location (
    id                 uuid         not null default gen_random_uuid(),
    key                varchar(255) not null,
    name               varchar(255) not null,
    description        text,
    assigned_resources int not null default 0,
    primary key (id)
);

create unique index location_unique_key  on location(key);
create unique index location_unique_name on location(name);

comment on table  location                    is 'Table holding all Location entries. The id is unique (pk).';
comment on column location.id                 is 'Unique identifier for the location entry';
comment on column location.key                is 'Short, machine-readable key for the location (unique)';
comment on column location.name               is 'Human-readable name of the location (unique)';
comment on column location.description        is 'Optional free-text description of the location';
comment on column location.assigned_resources is 'Number of resources currently assigned to this location. Maintained by the service layer.';
