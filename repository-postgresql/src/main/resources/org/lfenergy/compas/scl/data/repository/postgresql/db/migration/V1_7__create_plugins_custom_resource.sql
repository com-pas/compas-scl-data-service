/**
 * SPDX-FileCopyrightText: 2026 BearingPoint GmbH
 *
 * SPDX-License-Identifier: Apache-2.0
 */

--
-- Creating table to hold plugins custom resources.
--
create table plugins_custom_resource (
    id uuid not null default gen_random_uuid(),
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

create unique index plugins_custom_resource_unique_version on plugins_custom_resource(type, tenant, name, version);
create index plugins_custom_resource_type on plugins_custom_resource(type);
create index plugins_custom_resource_tenant on plugins_custom_resource(tenant);
create index plugins_custom_resource_uploaded_at on plugins_custom_resource(uploaded_at);

comment on table plugins_custom_resource is 'Table holding plugins custom resources for non-SCL data.';
comment on column plugins_custom_resource.id is 'Unique identifier for the resource entry';
comment on column plugins_custom_resource.type is 'Plugin resource type following <plugin-name>_<type> convention';
comment on column plugins_custom_resource.tenant is 'Tenant identifier, defaults to default';
comment on column plugins_custom_resource.name is 'Name of the resource';
comment on column plugins_custom_resource.description is 'Optional description of the resource';
comment on column plugins_custom_resource.content_type is 'MIME type of the content (application/json, application/xml)';
comment on column plugins_custom_resource.content is 'The actual resource content (JSON or XML as text)';
comment on column plugins_custom_resource.version is 'Semantic version of the resource';
comment on column plugins_custom_resource.data_compatibility_version is 'Data compatibility version (semver)';
comment on column plugins_custom_resource.uploaded_at is 'Timestamp when the resource was uploaded';
