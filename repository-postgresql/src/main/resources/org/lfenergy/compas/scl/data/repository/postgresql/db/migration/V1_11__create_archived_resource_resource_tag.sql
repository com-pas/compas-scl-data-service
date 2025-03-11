--
-- Creating many-to-many reference table to hold references to Archived Resource Data and Resource Tag Data.
--

create table archived_resource_resource_tag
(
    archived_resource_id uuid,
    resource_tag_id uuid,
    primary key (archived_resource_id, resource_tag_id),
    constraint fk_archived_resource
        foreign key (archived_resource_id)
        REFERENCES archived_resource(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    constraint fk_resource_tag
       foreign key (resource_tag_id)
       REFERENCES resource_tag(id)
       ON UPDATE CASCADE
       ON DELETE CASCADE
);

comment on table archived_resource_resource_tag is 'Table holding all the references for the many-to-many relation between the archived_resource table and the  resource_tag table';
comment on column archived_resource_resource_tag.archived_resource_id is 'Unique archived_resource ID generated according to standards';
comment on column archived_resource_resource_tag.resource_tag_id is 'Unique resource_tag ID generated according to standards';
