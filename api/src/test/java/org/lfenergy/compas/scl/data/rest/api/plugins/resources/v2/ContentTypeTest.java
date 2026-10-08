// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentTypeTest {

    @Test
    void contentTypeShouldRoundTripValues() {
        assertEquals("application/json", ContentType.APPLICATION_JSON.toString());
        assertEquals("application/xml", ContentType.APPLICATION_XML.toString());
        assertEquals(ContentType.APPLICATION_JSON, ContentType.fromValue("application/json"));
        assertEquals(ContentType.APPLICATION_XML, ContentType.fromString("application/xml"));
    }

    @Test
    void contentTypeFromValueShouldThrowForUnknown() {
        assertThrows(IllegalArgumentException.class,
                () -> ContentType.fromValue("text/plain"));
        assertThrows(IllegalArgumentException.class,
                () -> ContentType.fromString("text/plain"));
    }
}
