DELETE
FROM scl_file;
DELETE
FROM scl_history;

-- Insert test data into scl_history table
INSERT INTO scl_history (id, name, major_version, minor_version, patch_version, type, author, comment, changedAt,
                         archived, available)
VALUES ('1e40a51a-2e4f-482e-9410-72bb6177ec47', 'Test Item 11', 1, 0, 0, 'SSD', 'Author 1',
        'Initial creation of Test Item 1', '2023-10-15T14:25:12.510436+02:00', false, false),
       ('1e40a51a-2e4f-482e-9410-72bb6177ec47', 'Test Item 11', 1, 1, 0, 'SSD', 'Author 1',
        'Minor update to Test Item 2', '2024-09-15T14:25:12.510436+02:00', false, false),
       ('1e40a51a-2e4f-482e-9410-72bb6177ec47', 'Test Item 11', 2, 0, 0, 'SSD', 'Author 1',
        'Patch update for Test Item 3', '2024-10-12T14:25:12.510436+02:00', false, true),
       ('2e40a51a-2e4f-482e-9410-72bb6177ec47', 'Test Item 12', 1, 0, 0, 'SCD', 'Author 1',
        'Major update for Test Item 4', '2023-09-15T14:25:12.510436+02:00', false, false),
       ('2e40a51a-2e4f-482e-9410-72bb6177ec47', 'Test Item 12', 2, 0, 0, 'SCD', 'Author 1',
        'Initial creation of Test Item 5', '2024-10-15T14:25:12.510436+02:00', false, false),
       ('3e40a51a-2e4f-482e-9410-72bb6177ec47', 'Test Item 3', 1, 0, 0, 'SSD', 'Author 2',
        'Patch update for Test Item 6', '2023-11-15T14:25:12.510436+02:00', false, false),
       ('3e40a51a-2e4f-482e-9410-72bb6177ec47', 'Test Item 3', 1, 0, 1, 'SSD', 'Author 2',
        'Minor update to Test Item 7', '2024-10-15T14:20:12.510436+02:00', false, false),
       ('3e40a51a-2e4f-482e-9410-72bb6177ec47', 'Test Item 3', 1, 1, 0, 'SSD', 'Author 2',
        'Archived version of Test Item 8', '2024-10-10T16:25:12.510436+02:00', false, false);

-- Insert test data into scl_file table
INSERT INTO scl_file(id, major_version, minor_version, patch_version, type, name, scl_data, creation_date, created_by)
VALUES ('1e40a51a-2e4f-482e-9410-72bb6177ec47', 2, 0, 0, 'SSD', 'Test Item 11', '<xml>Hello World</xml>',
        '2024-10-15T14:25:12.510436+02:00', 'Author 1');

