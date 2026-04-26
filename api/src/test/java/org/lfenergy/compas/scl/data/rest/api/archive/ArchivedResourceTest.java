// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.archive;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArchivedResourceTest {

    private ResourceTag createTag() {
        return new ResourceTag().key("KEY").value("VALUE");
    }

    private ArchivedResource createSample() {
        return new ArchivedResource()
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
                .fields(List.of(createTag()));
    }

    @Test
    void shouldSetAndGetProperties() {
        ArchivedResource resource = new ArchivedResource();
        OffsetDateTime now = OffsetDateTime.now();
        ResourceTag tag = createTag();

        resource.setUuid("uuid-1");
        resource.setLocation("LOCATION");
        resource.setName("NAME");
        resource.setNote("NOTE");
        resource.setAuthor("AUTHOR");
        resource.setApprover("APPROVER");
        resource.setType("TYPE");
        resource.setContentType("application/xml");
        resource.setVoltage("110");
        resource.setVersion("1.0.0");
        resource.setModifiedAt(now);
        resource.setArchivedAt(now);
        resource.setFields(List.of(tag));

        assertEquals("uuid-1", resource.getUuid());
        assertEquals("LOCATION", resource.getLocation());
        assertEquals("NAME", resource.getName());
        assertEquals("NOTE", resource.getNote());
        assertEquals("AUTHOR", resource.getAuthor());
        assertEquals("APPROVER", resource.getApprover());
        assertEquals("TYPE", resource.getType());
        assertEquals("application/xml", resource.getContentType());
        assertEquals("110", resource.getVoltage());
        assertEquals("1.0.0", resource.getVersion());
        assertEquals(now, resource.getModifiedAt());
        assertEquals(now, resource.getArchivedAt());
        assertEquals(1, resource.getFields().size());
        assertEquals(tag, resource.getFields().get(0));
    }

    @Test
    void shouldSupportFluentSetters() {
        OffsetDateTime now = OffsetDateTime.now();
        ResourceTag tag = createTag();

        ArchivedResource resource = new ArchivedResource()
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
                .addFieldsItem(tag);

        assertEquals("uuid-1", resource.getUuid());
        assertEquals("LOCATION", resource.getLocation());
        assertEquals("NAME", resource.getName());
        assertEquals("NOTE", resource.getNote());
        assertEquals("AUTHOR", resource.getAuthor());
        assertEquals("APPROVER", resource.getApprover());
        assertEquals("TYPE", resource.getType());
        assertEquals("application/xml", resource.getContentType());
        assertEquals("110", resource.getVoltage());
        assertEquals("1.0.0", resource.getVersion());
        assertEquals(now, resource.getModifiedAt());
        assertEquals(now, resource.getArchivedAt());
        assertEquals(1, resource.getFields().size());
        assertEquals(tag, resource.getFields().get(0));
    }

    @Test
    void addFieldsItemShouldInitializeListIfNull() {
        ArchivedResource resource = new ArchivedResource();
        resource.setFields(null);

        ResourceTag tag = createTag();
        resource.addFieldsItem(tag);

        assertNotNull(resource.getFields());
        assertEquals(1, resource.getFields().size());
        assertEquals(tag, resource.getFields().get(0));
    }

    @Test
    void removeFieldsItemShouldRemoveItem() {
        ResourceTag tag = createTag();

        ArchivedResource resource = new ArchivedResource()
                .addFieldsItem(tag);

        resource.removeFieldsItem(tag);

        assertTrue(resource.getFields().isEmpty());
    }

    @Test
    void removeFieldsItemShouldHandleNullsGracefully() {
        ArchivedResource resource = new ArchivedResource();
        resource.setFields(null);

        assertDoesNotThrow(() -> resource.removeFieldsItem(null));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        OffsetDateTime now = OffsetDateTime.now();
        ResourceTag tag = createTag();

        ArchivedResource a = new ArchivedResource()
                .uuid("uuid-1").location("LOC").name("NAME").note("NOTE")
                .author("AUTHOR").approver("APPROVER").type("TYPE")
                .contentType("application/xml").voltage("110").version("1.0.0")
                .modifiedAt(now).archivedAt(now).fields(List.of(tag));

        ArchivedResource b = new ArchivedResource()
                .uuid("uuid-1").location("LOC").name("NAME").note("NOTE")
                .author("AUTHOR").approver("APPROVER").type("TYPE")
                .contentType("application/xml").voltage("110").version("1.0.0")
                .modifiedAt(now).archivedAt(now).fields(List.of(tag));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        ArchivedResource resource = createSample();

        assertNotEquals(null, resource);
        assertNotEquals(new Object(), resource);
    }

    @Test
    void toStringShouldContainFields() {
        ArchivedResource resource = createSample();
        String result = resource.toString();

        assertNotNull(result);
        assertTrue(result.contains("class ArchivedResource"));
        assertTrue(result.contains("uuid"));
        assertTrue(result.contains("name"));
        assertTrue(result.contains("author"));
        assertTrue(result.contains("version"));
        assertTrue(result.contains("fields"));
    }
}
