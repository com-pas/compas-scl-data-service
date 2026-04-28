-- SPDX-FileCopyrightText: 2026 BearingPoint GmbH
--
-- SPDX-License-Identifier: Apache-2.0
CREATE TABLE archived_resource (
    id UUID PRIMARY KEY,
    resource_id UUID,
    name VARCHAR(255) NOT NULL,
    version VARCHAR(50) NOT NULL,
    location_id VARCHAR(255),
    location VARCHAR(255),
    note TEXT,
    author VARCHAR(255) NOT NULL,
    approver VARCHAR(255),
    type VARCHAR(50) NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    voltage VARCHAR(50),
    modified_at TIMESTAMPTZ NOT NULL,
    archived_at TIMESTAMPTZ NOT NULL,
    archiving_comment TEXT
);

CREATE INDEX idx_archived_resource_resource_id ON archived_resource(resource_id);
CREATE INDEX idx_archived_resource_archived_at ON archived_resource(archived_at);

CREATE TABLE archived_resource_tag (
    archived_resource_id UUID NOT NULL,
    tag_key VARCHAR(255) NOT NULL,
    tag_value VARCHAR(255) NOT NULL
);

CREATE INDEX idx_archived_resource_tag_resource_id ON archived_resource_tag(archived_resource_id);