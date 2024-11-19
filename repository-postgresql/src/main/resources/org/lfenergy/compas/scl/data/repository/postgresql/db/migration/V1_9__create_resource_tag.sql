--
-- Creating table to hold Resource Tag Data. The Resource Tag is identified by its ID.
--

create table resource_tag (
    id uuid not null,
    key varchar(255) not null,
    value varchar(255),
    archived_resource_id uuid,
    primary key (id)
);

comment on table resource_tag is 'Table holding all the Resource Tag data. The id is unique (pk).';
comment on column resource_tag.id is 'Unique ID generated according to standards';
comment on column resource_tag.key is 'The key of the Resource Tag';
comment on column resource_tag.value is 'The value of the Resource Tag';
comment on column resource_tag.archived_resource_id is 'The uuid of a archived resource associated to the Resource Tag';