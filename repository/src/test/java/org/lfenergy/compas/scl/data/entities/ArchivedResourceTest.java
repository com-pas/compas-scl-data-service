// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ArchivedResourceTest {

    private ArchivedResource buildEntity(UUID id) {
        var now = OffsetDateTime.now();
        var e = new ArchivedResource();
        e.id = id;
        e.resourceId = UUID.randomUUID();
        e.name = "test-resource";
        e.version = "1.0.0";
        e.locationId = UUID.randomUUID().toString();
        e.location = "LOC-01";
        e.note = "a note";
        e.author = "alice";
        e.approver = "bob";
        e.type = "SCD";
        e.contentType = "application/xml";
        e.voltage = "110kV";
        e.modifiedAt = now.minusDays(1);
        e.archivedAt = now;
        e.archivingComment = "archived for release";
        return e;
    }

    // ---- defaults ----------------------------------------------------------

    @Test
    void newEntity_ThenFieldsListIsEmptyByDefault() {
        var entity = new ArchivedResource();
        assertNotNull(entity.fields);
        assertTrue(entity.fields.isEmpty());
    }

    // ---- equals() ----------------------------------------------------------

    @Test
    void equals_WhenSameInstance_ThenReturnsTrue() {
        var e = buildEntity(UUID.randomUUID());
        assertTrue(e.equals(e));
    }

    @Test
    void equals_WhenNull_ThenReturnsFalse() {
        var e = buildEntity(UUID.randomUUID());
        assertFalse(e.equals(null));
    }

    @Test
    void equals_WhenDifferentClass_ThenReturnsFalse() {
        var e = buildEntity(UUID.randomUUID());
        assertFalse(e.equals("string"));
    }

    @Test
    void equals_WhenAllFieldsEqual_ThenReturnsTrue() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.archivedAt = now;
        a.modifiedAt = now.minusDays(1);

        var b = buildEntity(id);
        b.resourceId = a.resourceId;
        b.locationId = a.locationId;
        b.archivedAt = now;
        b.modifiedAt = now.minusDays(1);

        assertEquals(a, b);
    }

    @Test
    void equals_WhenDifferentId_ThenReturnsFalse() {
        var a = buildEntity(UUID.randomUUID());
        var b = buildEntity(UUID.randomUUID());
        b.resourceId = a.resourceId;
        b.locationId = a.locationId;
        b.modifiedAt = a.modifiedAt;
        b.archivedAt = a.archivedAt;

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentName_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.archivedAt = now;
        a.modifiedAt = now;

        var b = buildEntity(id);
        b.resourceId = a.resourceId;
        b.locationId = a.locationId;
        b.archivedAt = now;
        b.modifiedAt = now;
        b.name = "different-name";

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentVersion_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.archivedAt = now;
        a.modifiedAt = now;

        var b = buildEntity(id);
        b.resourceId = a.resourceId;
        b.locationId = a.locationId;
        b.archivedAt = now;
        b.modifiedAt = now;
        b.version = "2.0.0";

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentType_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.archivedAt = now;
        a.modifiedAt = now;

        var b = buildEntity(id);
        b.resourceId = a.resourceId;
        b.locationId = a.locationId;
        b.archivedAt = now;
        b.modifiedAt = now;
        b.type = "CID";

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentAuthor_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.archivedAt = now;
        a.modifiedAt = now;

        var b = buildEntity(id);
        b.resourceId = a.resourceId;
        b.locationId = a.locationId;
        b.archivedAt = now;
        b.modifiedAt = now;
        b.author = "charlie";

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentFields_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.archivedAt = now;
        a.modifiedAt = now;

        var b = buildEntity(id);
        b.resourceId = a.resourceId;
        b.locationId = a.locationId;
        b.archivedAt = now;
        b.modifiedAt = now;

        var tag = new ResourceTag();
        tag.key = "k";
        tag.value = "v";
        b.fields = new ArrayList<>(List.of(tag));

        assertNotEquals(a, b);
    }

    @Test
    void equals_WhenDifferentArchivingComment_ThenReturnsFalse() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.archivedAt = now;
        a.modifiedAt = now;

        var b = buildEntity(id);
        b.resourceId = a.resourceId;
        b.locationId = a.locationId;
        b.archivedAt = now;
        b.modifiedAt = now;
        b.archivingComment = "different comment";

        assertNotEquals(a, b);
    }

    // ---- hashCode() --------------------------------------------------------

    @Test
    void hashCode_WhenEqualObjects_ThenSameHashCode() {
        var id = UUID.randomUUID();
        var now = OffsetDateTime.now();

        var a = buildEntity(id);
        a.archivedAt = now;
        a.modifiedAt = now.minusDays(1);

        var b = buildEntity(id);
        b.resourceId = a.resourceId;
        b.locationId = a.locationId;
        b.archivedAt = now;
        b.modifiedAt = now.minusDays(1);

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_WhenNullFields_ThenDoesNotThrow() {
        var entity = new ArchivedResource();
        assertDoesNotThrow(entity::hashCode);
    }
}
