--clear values on startup

TRUNCATE TABLE archived_resource;

insert into archived_resource (id, name, major_version, minor_version, patch_version, location, note, author, approver, type, content_type, voltage, modified_at, archived_at)
values ('af71f378-5a13-47af-bbfc-aa668af14e25', 'archived_resource_1', '0', '0', '1', 'test1', 'test note', 'user1', 'user2', 'Schütz', 'SSD', '110', '2019-08-24T14:15:22Z', '2019-08-24T14:15:22Z');

insert into archived_resource (id, name, major_version, minor_version, patch_version, location, note, author, approver, type, content_type, voltage, modified_at, archived_at)
values ('af71f378-5a13-47af-bbfc-aa668af14e26', 'archived_resource_2', '0', '1', '0', 'test2', 'test note', 'user2', 'user1', 'Leittechnik', 'IID', '220', '2020-08-24T14:15:22Z', '2021-08-24T14:15:22Z');

insert into archived_resource (id, name, major_version, minor_version, patch_version, location, note, author, approver, type, content_type, voltage, modified_at, archived_at)
values ('af71f378-5a13-47af-bbfc-aa668af14e27', 'archived_resource_3', '1', '0', '0', 'test3', 'test note', 'user3', 'user1', 'Schütz', 'ICD', '380', '2022-08-24T14:15:22Z', '2023-08-24T14:15:22Z');