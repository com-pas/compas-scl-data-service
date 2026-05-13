// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.archive;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ArchivedResourcesSearchTest {

    private ArchivedResourcesSearch createSample() {
        OffsetDateTime now = OffsetDateTime.now();
        return new ArchivedResourcesSearch()
                .uuid("uuid-1")
                .location("LOCATION")
                .name("NAME")
                .approver("APPROVER")
                .contentType("application/xml")
                .type("TYPE")
                .voltage("110")
                .from(now.minusDays(7))
                .to(now);
    }

    @Test
    void shouldSetAndGetProperties() {
        ArchivedResourcesSearch search = new ArchivedResourcesSearch();
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        search.setUuid("uuid-1");
        search.setLocation("LOCATION");
        search.setName("NAME");
        search.setApprover("APPROVER");
        search.setContentType("application/xml");
        search.setType("TYPE");
        search.setVoltage("110");
        search.setFrom(from);
        search.setTo(to);

        assertEquals("uuid-1", search.getUuid());
        assertEquals("LOCATION", search.getLocation());
        assertEquals("NAME", search.getName());
        assertEquals("APPROVER", search.getApprover());
        assertEquals("application/xml", search.getContentType());
        assertEquals("TYPE", search.getType());
        assertEquals("110", search.getVoltage());
        assertEquals(from, search.getFrom());
        assertEquals(to, search.getTo());
    }

    @Test
    void shouldSupportFluentSetters() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        ArchivedResourcesSearch search = new ArchivedResourcesSearch()
                .uuid("uuid-1")
                .location("LOCATION")
                .name("NAME")
                .approver("APPROVER")
                .contentType("application/xml")
                .type("TYPE")
                .voltage("110")
                .from(from)
                .to(to);

        assertEquals("uuid-1", search.getUuid());
        assertEquals("LOCATION", search.getLocation());
        assertEquals("NAME", search.getName());
        assertEquals("APPROVER", search.getApprover());
        assertEquals("application/xml", search.getContentType());
        assertEquals("TYPE", search.getType());
        assertEquals("110", search.getVoltage());
        assertEquals(from, search.getFrom());
        assertEquals(to, search.getTo());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        ArchivedResourcesSearch a = new ArchivedResourcesSearch()
                .uuid("uuid-1").location("LOC").name("NAME").approver("APPROVER")
                .contentType("application/xml").type("TYPE").voltage("110")
                .from(from).to(to);

        ArchivedResourcesSearch b = new ArchivedResourcesSearch()
                .uuid("uuid-1").location("LOC").name("NAME").approver("APPROVER")
                .contentType("application/xml").type("TYPE").voltage("110")
                .from(from).to(to);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        OffsetDateTime now = OffsetDateTime.now();

        ArchivedResourcesSearch a = new ArchivedResourcesSearch().uuid("uuid-1").from(now);
        ArchivedResourcesSearch b = new ArchivedResourcesSearch().uuid("uuid-2").from(now);

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        ArchivedResourcesSearch search = createSample();

        assertNotEquals(null, search);
        assertNotEquals(new Object(), search);
    }

    @Test
    void toStringShouldContainFields() {
        ArchivedResourcesSearch search = createSample();
        String result = search.toString();

        assertNotNull(result);
        assertTrue(result.contains("class ArchivedResourcesSearch"));
        assertTrue(result.contains("uuid"));
        assertTrue(result.contains("location"));
        assertTrue(result.contains("name"));
        assertTrue(result.contains("from"));
        assertTrue(result.contains("to"));
    }
}
