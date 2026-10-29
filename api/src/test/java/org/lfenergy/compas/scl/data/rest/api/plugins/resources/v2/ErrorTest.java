// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorTest {

    private Error createSample() {
        return new Error()
                .code("ERR_CODE")
                .message("Something went wrong")
                .details(Map.of("field", "value"));
    }

    @Test
    void shouldSetAndGetProperties() {
        Error error = new Error();
        Map<String, Object> details = new HashMap<>();
        details.put("field", "name");

        error.setCode("ERR_CODE");
        error.setMessage("Something went wrong");
        error.setDetails(details);

        assertEquals("ERR_CODE", error.getCode());
        assertEquals("Something went wrong", error.getMessage());
        assertEquals(details, error.getDetails());
    }

    @Test
    void shouldSupportFluentSetters() {
        Map<String, Object> details = Map.of("field", "name");

        Error error = new Error()
                .code("ERR_CODE")
                .message("Something went wrong")
                .details(details);

        assertEquals("ERR_CODE", error.getCode());
        assertEquals("Something went wrong", error.getMessage());
        assertEquals(details, error.getDetails());
    }

    @Test
    void putDetailsItemShouldInitializeMapIfNull() {
        Error error = new Error();
        error.setDetails(null);

        error.putDetailsItem("field", "value");

        assertNotNull(error.getDetails());
        assertEquals("value", error.getDetails().get("field"));
    }

    @Test
    void putDetailsItemShouldAddToExistingMap() {
        Error error = new Error().details(new HashMap<>(Map.of("existing", "1")));

        error.putDetailsItem("added", "2");

        assertEquals(2, error.getDetails().size());
        assertEquals("1", error.getDetails().get("existing"));
        assertEquals("2", error.getDetails().get("added"));
    }

    @Test
    void removeDetailsItemShouldRemoveItem() {
        Error error = new Error().details(new HashMap<>(Map.of("field", "value")));

        error.removeDetailsItem("field");

        assertTrue(error.getDetails().isEmpty());
    }

    @Test
    void removeDetailsItemShouldHandleNullsGracefully() {
        Error error = new Error();
        error.setDetails(null);

        assertDoesNotThrow(() -> error.removeDetailsItem("field"));
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        Map<String, Object> details = Map.of("field", "value");

        Error a = new Error().code("ERR").message("MSG").details(details);
        Error b = new Error().code("ERR").message("MSG").details(details);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        Error a = new Error().code("ERR_A").message("MSG");
        Error b = new Error().code("ERR_B").message("MSG");

        assertNotEquals(a, b);
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
