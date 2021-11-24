/**
 * SPDX-FileCopyrightText: 2021 Alliander N.V.
 *
 * SPDX-License-Identifier: Apache-2.0
*/

--
-- Adding created by to the scl_file table.
--
alter table scl_file add column created_by varchar(255);

update scl_file set created_by = 'unknown';

alter table scl_file alter column created_by set not null;
