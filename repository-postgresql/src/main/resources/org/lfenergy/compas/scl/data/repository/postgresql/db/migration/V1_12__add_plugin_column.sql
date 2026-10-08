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

-- For now set all existing rows to 'unknown'
-- Manual migration is required to set the correct plugin for existing rows.
update plugins_custom_resource
set plugin = 'unknown'
where plugin is null;

drop index if exists plugins_custom_resource_unique_version;

create unique index plugins_custom_resource_unique_version
    on plugins_custom_resource(plugin, type, tenant, name, version);
create index plugins_custom_resource_plugin on plugins_custom_resource(plugin);

comment on column plugins_custom_resource.plugin is 'Plugin identifier owning the resource';
