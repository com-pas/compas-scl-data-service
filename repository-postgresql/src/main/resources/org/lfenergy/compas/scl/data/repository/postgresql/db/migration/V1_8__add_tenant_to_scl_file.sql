/**
 * SPDX-FileCopyrightText: 2026 Alliander N.V.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

--
-- Add tenant column to scl_file for multi-tenant data isolation.
-- Existing rows default to 'global' (the no-auth default tenant).
--
ALTER TABLE scl_file
    ADD COLUMN tenant varchar(255) not null default 'global';

CREATE INDEX scl_file_tenant ON scl_file(tenant);

COMMENT ON COLUMN scl_file.tenant IS 'Tenant identifier: ''global'' when auth is disabled, OIDC realm name when auth is enabled.';
