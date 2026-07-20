/**
 * SPDX-FileCopyrightText: 2026 BearingPoint GmbH
 *
 * SPDX-License-Identifier: Apache-2.0
 */

--
-- Creates a new plugin_resource table for the /plugins-resources API.
-- The existing plugins_custom_resource table used by the legacy
-- /plugins/resources API is left untouched.
--
create table plugin_resource (
    id uuid not null default gen_random_uuid(),
    plugin varchar(255) not null,
    type varchar(255) not null,
    tenant varchar(255) not null default 'default',
    name varchar(255) not null,
    description text,
    content_type varchar(50) not null,
    content text not null,
    version varchar(50) not null,
    data_compatibility_version varchar(50) not null,
    uploaded_at timestamp with time zone not null default now(),
    primary key (id)
);

create unique index plugin_resource_unique_version on plugin_resource(plugin, type, tenant, name, version);
create index plugin_resource_plugin on plugin_resource(plugin);
create index plugin_resource_type on plugin_resource(type);
create index plugin_resource_tenant on plugin_resource(tenant);
create index plugin_resource_uploaded_at on plugin_resource(uploaded_at);

comment on table plugin_resource is 'Table holding plugin-specific typed resources scoped by plugin and type. Written by the /plugins-resources API.';
comment on column plugin_resource.id is 'Unique identifier for the resource entry';
comment on column plugin_resource.plugin is 'Plugin identifier owning the resource';
comment on column plugin_resource.type is 'Resource type defined by the plugin';
comment on column plugin_resource.tenant is 'Tenant identifier, defaults to default';
comment on column plugin_resource.name is 'Name of the resource';
comment on column plugin_resource.description is 'Optional description of the resource';
comment on column plugin_resource.content_type is 'MIME type of the content (application/json, application/xml)';
comment on column plugin_resource.content is 'The actual resource content (JSON or XML as text)';
comment on column plugin_resource.version is 'Semantic version of the resource';
comment on column plugin_resource.data_compatibility_version is 'Data compatibility version (semver)';
comment on column plugin_resource.uploaded_at is 'Timestamp when the resource was uploaded';
