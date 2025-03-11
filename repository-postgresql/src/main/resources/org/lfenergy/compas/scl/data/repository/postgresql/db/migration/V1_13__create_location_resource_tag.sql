--
-- Creating many-to-many reference table to hold references to Location Data and Resource Tag Data.
--

create table location_resource_tag
(
    location_id uuid not null,
    resource_tag_id uuid not null,
    primary key (location_id, resource_tag_id),
    constraint fk_location
        foreign key (location_id)
        REFERENCES location(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    constraint fk_resource_tag
       foreign key (resource_tag_id)
       REFERENCES resource_tag(id)
       ON UPDATE CASCADE
       ON DELETE CASCADE
);

comment on table location_resource_tag is 'Table holding all the references for the many-to-many relation between the location table and the resource_tag table';
comment on column location_resource_tag.location_id is 'Unique location ID generated according to standards';
comment on column location_resource_tag.resource_tag_id is 'Unique resource_tag ID generated according to standards';
