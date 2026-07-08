/**
 * SPDX-FileCopyrightText: 2026 BearingPoint GmbH
 *
 * SPDX-License-Identifier: Apache-2.0
 */

--
-- Renames the plugins_custom_resource table to plugin_resource and introduces
-- an explicit non-nullable plugin column. Existing indexes and constraints are
-- renamed to match the new table name.
--

alter table plugins_custom_resource rename to plugin_resource;

alter index plugins_custom_resource_pkey rename to plugin_resource_pkey;
alter index plugins_custom_resource_unique_version rename to plugin_resource_unique_version_old;
alter index plugins_custom_resource_type rename to plugin_resource_type;
alter index plugins_custom_resource_tenant rename to plugin_resource_tenant;
alter index plugins_custom_resource_uploaded_at rename to plugin_resource_uploaded_at;

alter table plugin_resource add column plugin varchar(255);
update plugin_resource set plugin = '' where plugin is null;
alter table plugin_resource alter column plugin set not null;

drop index plugin_resource_unique_version_old;
create unique index plugin_resource_unique_version on plugin_resource(plugin, type, tenant, name, version);
create index plugin_resource_plugin on plugin_resource(plugin);

comment on table plugin_resource is 'Table holding plugin-specific typed resources scoped by plugin and type.';
comment on column plugin_resource.plugin is 'Plugin identifier owning the resource';
comment on column plugin_resource.type is 'Resource type defined by the plugin';
