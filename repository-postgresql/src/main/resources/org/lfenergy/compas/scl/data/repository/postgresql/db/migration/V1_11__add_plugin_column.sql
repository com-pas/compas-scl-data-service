/**
 * SPDX-FileCopyrightText: 2026 BearingPoint GmbH
 *
 * SPDX-License-Identifier: Apache-2.0
 */

--
-- Extend the existing plugins_custom_resource table for the /plugins-resources API.
-- Existing rows derive plugin from the type prefix: <plugin-name>_<resource-name>.
--
alter table plugins_custom_resource
    add column plugin varchar(255);

-- 1) Explicit mapping for template-generator,
-- because it has a different naming convention than the other plugins.
update plugins_custom_resource
set plugin = 'template-generator'
where plugin is null
  and type like 'template-generator%';

-- 2) Fallback for any remaining legacy rows that still follow "<plugin>_<resource>".
update plugins_custom_resource
set plugin = split_part(type, '_', 1)
where plugin is null
  and position('_' in type) > 0;

-- 3) Safety net if any row is still null (prevents NOT NULL migration failure).
-- If you prefer strict fail-fast, remove this and run a pre-check query instead.
update plugins_custom_resource
set plugin = 'unknown'
where plugin is null;

-- Enforce the new column for all future rows.
alter table plugins_custom_resource
    alter column plugin set not null;

drop index if exists plugins_custom_resource_unique_version;

create unique index plugins_custom_resource_unique_version
    on plugins_custom_resource(plugin, type, tenant, name, version);
create index plugins_custom_resource_plugin on plugins_custom_resource(plugin);

comment on column plugins_custom_resource.plugin is 'Plugin identifier owning the resource';
