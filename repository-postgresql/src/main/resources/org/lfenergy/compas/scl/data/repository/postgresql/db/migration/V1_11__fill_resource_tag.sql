--clear values on startup

TRUNCATE TABLE resource_tag;

insert into resource_tag (id, key, value, archived_resource_id)
values ('bf71f378-5a13-47af-bbfc-aa668af14e25', 'test_tag', 'test_value', 'af71f378-5a13-47af-bbfc-aa668af14e25');

insert into resource_tag (id, key, value, archived_resource_id)
values ('bf71f378-5a13-47af-bbfc-aa668af15e25', 'test_tag_1', 'test_value_1', 'af71f378-5a13-47af-bbfc-aa668af14e25');

insert into resource_tag (id, key, value, archived_resource_id)
values ('bf71f378-5a13-47af-bbfc-aa668af74e25', 'test_tag', 'test_value', 'af71f378-5a13-47af-bbfc-aa668af14e27');