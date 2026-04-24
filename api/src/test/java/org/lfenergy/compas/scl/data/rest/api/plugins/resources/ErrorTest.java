// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {

    private Error createSample() {
        return new Error("ERR_CODE", "Something went wrong")
                .putDetailsItem("field", "value");
    }

    @Test
    void shouldCreateUsingAllArgsConstructor() {
        Error error = new Error("CODE", "MESSAGE");

        assertEquals("CODE", error.getCode());
        assertEquals("MESSAGE", error.getMessage());
        assertNotNull(error.getDetails());
        assertTrue(error.getDetails().isEmpty());
    }

    @Test
    void shouldSetAndGetProperties() {
        Error error = new Error();

        error.setCode("CODE");
        error.setMessage("MESSAGE");

        Map<String, Object> details = new HashMap<>();
        details.put("key", 123);

        error.setDetails(details);

        assertEquals("CODE", error.getCode());
        assertEquals("MESSAGE", error.getMessage());
        assertEquals(details, error.getDetails());
    }

    @Test
    void shouldSupportFluentSetters() {
        Error error = new Error()
                .code("CODE")
                .message("MESSAGE")
                .putDetailsItem("key", "value");

        assertEquals("CODE", error.getCode());
        assertEquals("MESSAGE", error.getMessage());
        assertEquals("value", error.getDetails().get("key"));
    }

    @Test
    void putDetailsItemShouldInitializeMapIfNull() {
        Error error = new Error();
        error.setDetails(null);

        error.putDetailsItem("key", "value");

        assertNotNull(error.getDetails());
        assertEquals("value", error.getDetails().get("key"));
    }

    @Test
    void removeDetailsItemShouldRemoveKey() {
        Error error = new Error()
                .putDetailsItem("key1", "value1")
                .putDetailsItem("key2", "value2");

        error.removeDetailsItem("key1");

        assertFalse(error.getDetails().containsKey("key1"));
        assertTrue(error.getDetails().containsKey("key2"));
    }

    @Test
    void removeDetailsItemShouldHandleNullMap() {
        Error error = new Error();
        error.setDetails(null);

        assertDoesNotThrow(() -> error.removeDetailsItem("key"));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        Map<String, Object> details = new HashMap<>();
        details.put("key", "value");

        Error a = new Error("CODE", "MESSAGE").details(details);
        Error b = new Error("CODE", "MESSAGE").details(new HashMap<>(details));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        Error error = createSample();

        assertNotEquals(null, error);
        assertNotEquals(new Object(), error);
    }

    @Test
    void toStringShouldContainFields() {
        Error error = createSample();
        String result = error.toString();

        assertNotNull(result);
        assertTrue(result.contains("class Error"));
        assertTrue(result.contains("code"));
        assertTrue(result.contains("message"));
        assertTrue(result.contains("details"));
    }
}