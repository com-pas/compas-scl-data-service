/**
 * SPDX-FileCopyrightText: 2025 BearingPoint GmbH
 *
 * SPDX-License-Identifier: Apache-2.0
*/

--
-- Update SCL File Table to add soft deletion
--

ALTER TABLE scl_file 
ADD COLUMN is_deleted BOOLEAN DEFAULT false;

comment on column scl_file.is_deleted is 'Flag is the SCL File is deleted.';
