// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.locations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaginationTest {

    private Pagination createSample() {
        return new Pagination()
                .page(0)
                .pageSize(20);
    }

    @Test
    void shouldSetAndGetProperties() {
        Pagination pagination = new Pagination();

        pagination.setPage(1);
        pagination.setPageSize(10);

        assertEquals(1, pagination.getPage());
        assertEquals(10, pagination.getPageSize());
    }

    @Test
    void shouldSupportFluentSetters() {
        Pagination pagination = new Pagination()
                .page(1)
                .pageSize(10);

        assertEquals(1, pagination.getPage());
        assertEquals(10, pagination.getPageSize());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        Pagination a = new Pagination().page(0).pageSize(20);
        Pagination b = new Pagination().page(0).pageSize(20);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        Pagination a = new Pagination().page(0).pageSize(10);
        Pagination b = new Pagination().page(1).pageSize(10);

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        Pagination pagination = createSample();

        assertNotEquals(null, pagination);
        assertNotEquals(new Object(), pagination);
    }

    @Test
    void toStringShouldContainFields() {
        Pagination pagination = createSample();
        String result = pagination.toString();

        assertNotNull(result);
        assertTrue(result.contains("class Pagination"));
        assertTrue(result.contains("page"));
        assertTrue(result.contains("pageSize"));
    }
}
