--
-- Creating table to hold Location Data. The Location is identified by its ID.
--

create table location (
    id uuid not null,
    key varchar(255) not null,
    name varchar(255) not null,
    description varchar(255),
    resource_id uuid default null,
    primary key (id)
);

comment on table location is 'Table holding all the Location Data and its resource assignments. The id is unique (pk).';
comment on column location.id is 'Unique ID generated according to standards';
comment on column location.key is 'The key of the Location';
comment on column location.name is 'The name of the Location';
comment on column location.description is 'The description of the Location';
comment on column location.resource_id is 'The unique ID of the assigned resource, NULL if no resource is assigned';
