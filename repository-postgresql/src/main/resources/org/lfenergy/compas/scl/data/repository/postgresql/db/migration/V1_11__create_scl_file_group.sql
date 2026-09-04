-- SPDX-FileCopyrightText: 2026 Alliander N.V.
--
-- SPDX-License-Identifier: Apache-2.0

CREATE TABLE scl_file_group (
    id uuid NOT NULL default gen_random_uuid(),
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),

    primary key (id)
);

INSERT INTO scl_file_group(code, name, description) VALUES 
  ('CID', 'CID', 'Configured IED Description'),
  ('ICD', 'ICD', 'IED Capability Description'),
  ('IID', 'IID', 'IED Instance Description'),
  ('ISD', 'ISD', 'IED Specification Description'),
  ('SCD', 'SCD', 'Substation Configuration Description'),
  ('SSD', 'SSD', 'Substation Specification Description'),
  ('SED', 'SED', 'System Exchange Description'),
  ('STD', 'STD', 'System Template Definition'),
  ('LNT', 'LNodeTypeLibrary', 'LNode Type Library'),
  ('BAY', 'BayTypical', 'Bay Typical');
