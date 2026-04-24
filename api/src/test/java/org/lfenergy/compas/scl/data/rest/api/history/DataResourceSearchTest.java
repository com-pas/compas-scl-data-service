// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.history;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DataResourceSearchTest {

    private DataResourceSearch createSample() {
        OffsetDateTime now = OffsetDateTime.now();
        return new DataResourceSearch()
                .uuid("uuid-1")
                .type("SCD")
                .name("NAME")
                .location("LOCATION")
                .author("AUTHOR")
                .from(now.minusDays(7))
                .to(now);
    }

    @Test
    void shouldSetAndGetProperties() {
        DataResourceSearch search = new DataResourceSearch();
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        search.setUuid("uuid-1");
        search.setType("SCD");
        search.setName("NAME");
        search.setLocation("LOCATION");
        search.setAuthor("AUTHOR");
        search.setFrom(from);
        search.setTo(to);

        assertEquals("uuid-1", search.getUuid());
        assertEquals("SCD", search.getType());
        assertEquals("NAME", search.getName());
        assertEquals("LOCATION", search.getLocation());
        assertEquals("AUTHOR", search.getAuthor());
        assertEquals(from, search.getFrom());
        assertEquals(to, search.getTo());
    }

    @Test
    void shouldSupportFluentSetters() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        DataResourceSearch search = new DataResourceSearch()
                .uuid("uuid-1")
                .type("SCD")
                .name("NAME")
                .location("LOCATION")
                .author("AUTHOR")
                .from(from)
                .to(to);

        assertEquals("uuid-1", search.getUuid());
        assertEquals("SCD", search.getType());
        assertEquals("NAME", search.getName());
        assertEquals("LOCATION", search.getLocation());
        assertEquals("AUTHOR", search.getAuthor());
        assertEquals(from, search.getFrom());
        assertEquals(to, search.getTo());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        OffsetDateTime from = OffsetDateTime.now().minusDays(7);
        OffsetDateTime to = OffsetDateTime.now();

        DataResourceSearch a = new DataResourceSearch()
                .uuid("uuid-1").type("SCD").name("NAME").location("LOCATION")
                .author("AUTHOR").from(from).to(to);

        DataResourceSearch b = new DataResourceSearch()
                .uuid("uuid-1").type("SCD").name("NAME").location("LOCATION")
                .author("AUTHOR").from(from).to(to);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        OffsetDateTime now = OffsetDateTime.now();

        DataResourceSearch a = new DataResourceSearch().uuid("uuid-1").from(now);
        DataResourceSearch b = new DataResourceSearch().uuid("uuid-2").from(now);

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        DataResourceSearch search = createSample();

        assertNotEquals(null, search);
        assertNotEquals(new Object(), search);
    }

    @Test
    void toStringShouldContainFields() {
        DataResourceSearch search = createSample();
        String result = search.toString();

        assertNotNull(result);
        assertTrue(result.contains("class DataResourceSearch"));
        assertTrue(result.contains("uuid"));
        assertTrue(result.contains("type"));
        assertTrue(result.contains("name"));
        assertTrue(result.contains("from"));
        assertTrue(result.contains("to"));
    }
}
