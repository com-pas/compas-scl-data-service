-- SPDX-FileCopyrightText: 2026 Alliander N.V.
--
-- SPDX-License-Identifier: Apache-2.0

create table scl_file_group (
    id uuid not null default gen_random_uuid(),
    code varchar(3) not null unique,
    name varchar(50) not null,
    description varchar(255),

    primary key (id)
);

insert into scl_file_group(code, name, description) values 
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

/* Set LNodeTypeLibrary type to LNT */
update scl_file set type = 'LNT' where id = 'fc55c46d-c109-4ccd-bf66-9f1d0e135689';
