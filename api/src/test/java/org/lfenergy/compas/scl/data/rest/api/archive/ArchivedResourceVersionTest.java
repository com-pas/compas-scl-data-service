// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.archive;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArchivedResourceVersionTest {

    private ResourceTag createTag() {
        return new ResourceTag().key("KEY").value("VALUE");
    }

    private ArchivedResourceVersion createSample() {
        return new ArchivedResourceVersion()
                .uuid("uuid-1")
                .location("LOCATION")
                .name("NAME")
                .note("NOTE")
                .author("AUTHOR")
                .approver("APPROVER")
                .type("TYPE")
                .contentType("application/xml")
                .voltage("110")
                .version("1.0.0")
                .modifiedAt(OffsetDateTime.now())
                .archivedAt(OffsetDateTime.now())
                .fields(List.of(createTag()))
                .comment("COMMENT")
                .archived(true);
    }

    @Test
    void shouldSetAndGetAllProperties() {
        ArchivedResourceVersion version = new ArchivedResourceVersion();
        OffsetDateTime now = OffsetDateTime.now();
        ResourceTag tag = createTag();

        version.setUuid("uuid-1");
        version.setLocation("LOCATION");
        version.setName("NAME");
        version.setNote("NOTE");
        version.setAuthor("AUTHOR");
        version.setApprover("APPROVER");
        version.setType("TYPE");
        version.setContentType("application/xml");
        version.setVoltage("110");
        version.setVersion("1.0.0");
        version.setModifiedAt(now);
        version.setArchivedAt(now);
        version.setFields(List.of(tag));
        version.setComment("COMMENT");
        version.setArchived(true);

        assertEquals("uuid-1", version.getUuid());
        assertEquals("LOCATION", version.getLocation());
        assertEquals("NAME", version.getName());
        assertEquals("NOTE", version.getNote());
        assertEquals("AUTHOR", version.getAuthor());
        assertEquals("APPROVER", version.getApprover());
        assertEquals("TYPE", version.getType());
        assertEquals("application/xml", version.getContentType());
        assertEquals("110", version.getVoltage());
        assertEquals("1.0.0", version.getVersion());
        assertEquals(now, version.getModifiedAt());
        assertEquals(now, version.getArchivedAt());
        assertEquals(1, version.getFields().size());
        assertEquals("COMMENT", version.getComment());
        assertTrue(version.getArchived());
    }

    @Test
    void shouldSupportFluentSetters() {
        OffsetDateTime now = OffsetDateTime.now();
        ResourceTag tag = createTag();

        ArchivedResourceVersion version = new ArchivedResourceVersion()
                .uuid("uuid-1")
                .location("LOCATION")
                .name("NAME")
                .note("NOTE")
                .author("AUTHOR")
                .approver("APPROVER")
                .type("TYPE")
                .contentType("application/xml")
                .voltage("110")
                .version("1.0.0")
                .modifiedAt(now)
                .archivedAt(now)
                .fields(new ArrayList<>())
                .addFieldsItem(tag)
                .comment("COMMENT")
                .archived(true);

        assertEquals("uuid-1", version.getUuid());
        assertEquals("LOCATION", version.getLocation());
        assertEquals("NAME", version.getName());
        assertEquals("COMMENT", version.getComment());
        assertTrue(version.getArchived());
        assertEquals(1, version.getFields().size());
    }

    @Test
    void defaultArchivedShouldBeFalse() {
        ArchivedResourceVersion version = new ArchivedResourceVersion();

        assertFalse(version.getArchived());
    }

    @Test
    void addFieldsItemShouldInitializeListIfNull() {
        ArchivedResourceVersion version = new ArchivedResourceVersion();
        version.setFields(null);

        ResourceTag tag = createTag();
        version.addFieldsItem(tag);

        assertNotNull(version.getFields());
        assertEquals(1, version.getFields().size());
    }

    @Test
    void removeFieldsItemShouldRemoveItem() {
        ResourceTag tag = createTag();

        ArchivedResourceVersion version = new ArchivedResourceVersion()
                .addFieldsItem(tag);

        version.removeFieldsItem(tag);

        assertTrue(version.getFields().isEmpty());
    }

    @Test
    void removeFieldsItemShouldHandleNullsGracefully() {
        ArchivedResourceVersion version = new ArchivedResourceVersion();
        version.setFields(null);

        assertDoesNotThrow(() -> version.removeFieldsItem(null));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        OffsetDateTime now = OffsetDateTime.now();
        ResourceTag tag = createTag();

        ArchivedResourceVersion a = new ArchivedResourceVersion()
                .uuid("uuid-1").location("LOC").name("NAME").note("NOTE")
                .author("AUTHOR").approver("APPROVER").type("TYPE")
                .contentType("application/xml").voltage("110").version("1.0.0")
                .modifiedAt(now).archivedAt(now).fields(List.of(tag))
                .comment("COMMENT").archived(false);

        ArchivedResourceVersion b = new ArchivedResourceVersion()
                .uuid("uuid-1").location("LOC").name("NAME").note("NOTE")
                .author("AUTHOR").approver("APPROVER").type("TYPE")
                .contentType("application/xml").voltage("110").version("1.0.0")
                .modifiedAt(now).archivedAt(now).fields(List.of(tag))
                .comment("COMMENT").archived(false);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        ArchivedResourceVersion version = createSample();

        assertNotEquals(null, version);
        assertNotEquals(new Object(), version);
    }

    @Test
    void toStringShouldContainFields() {
        ArchivedResourceVersion version = createSample();
        String result = version.toString();

        assertNotNull(result);
        assertTrue(result.contains("class ArchivedResourceVersion"));
        assertTrue(result.contains("uuid"));
        assertTrue(result.contains("comment"));
        assertTrue(result.contains("archived"));
    }
}
