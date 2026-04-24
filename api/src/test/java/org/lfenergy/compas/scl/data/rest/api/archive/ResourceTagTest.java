// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.archive;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceTagTest {

    private ResourceTag createSample() {
        return new ResourceTag()
                .key("FIELD_KEY")
                .value("FIELD_VALUE");
    }

    @Test
    void shouldSetAndGetProperties() {
        ResourceTag tag = new ResourceTag();

        tag.setKey("KEY");
        tag.setValue("VALUE");

        assertEquals("KEY", tag.getKey());
        assertEquals("VALUE", tag.getValue());
    }

    @Test
    void shouldSupportFluentSetters() {
        ResourceTag tag = new ResourceTag()
                .key("KEY")
                .value("VALUE");

        assertEquals("KEY", tag.getKey());
        assertEquals("VALUE", tag.getValue());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        ResourceTag a = new ResourceTag().key("KEY").value("VALUE");
        ResourceTag b = new ResourceTag().key("KEY").value("VALUE");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        ResourceTag a = new ResourceTag().key("KEY1").value("VALUE");
        ResourceTag b = new ResourceTag().key("KEY2").value("VALUE");

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        ResourceTag tag = createSample();

        assertNotEquals(null, tag);
        assertNotEquals(new Object(), tag);
    }

    @Test
    void toStringShouldContainFields() {
        ResourceTag tag = createSample();
        String result = tag.toString();

        assertNotNull(result);
        assertTrue(result.contains("class ResourceTag"));
        assertTrue(result.contains("key"));
        assertTrue(result.contains("value"));
    }
}
