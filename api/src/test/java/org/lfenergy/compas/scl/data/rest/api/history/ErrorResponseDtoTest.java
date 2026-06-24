// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.api.history;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.rest.api.errors.ErrorResponseDto;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseDtoTest {

    private ErrorResponseDto createSample() {
        return new ErrorResponseDto()
                .timestamp(OffsetDateTime.now())
                .code("ERR_CODE")
                .message("Something went wrong");
    }

    @Test
    void shouldSetAndGetProperties() {
        ErrorResponseDto dto = new ErrorResponseDto();
        OffsetDateTime now = OffsetDateTime.now();

        dto.setTimestamp(now);
        dto.setCode("ERR_CODE");
        dto.setMessage("Something went wrong");

        assertEquals(now, dto.getTimestamp());
        assertEquals("ERR_CODE", dto.getCode());
        assertEquals("Something went wrong", dto.getMessage());
    }

    @Test
    void shouldSupportFluentSetters() {
        OffsetDateTime now = OffsetDateTime.now();

        ErrorResponseDto dto = new ErrorResponseDto()
                .timestamp(now)
                .code("ERR_CODE")
                .message("Something went wrong");

        assertEquals(now, dto.getTimestamp());
        assertEquals("ERR_CODE", dto.getCode());
        assertEquals("Something went wrong", dto.getMessage());
    }

    @Test
    void equalsAndHashCodeShouldMatchForSameValues() {
        OffsetDateTime now = OffsetDateTime.now();

        ErrorResponseDto a = new ErrorResponseDto()
                .timestamp(now).code("ERR_CODE").message("Something went wrong");

        ErrorResponseDto b = new ErrorResponseDto()
                .timestamp(now).code("ERR_CODE").message("Something went wrong");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equalsShouldReturnFalseForDifferentValues() {
        OffsetDateTime now = OffsetDateTime.now();

        ErrorResponseDto a = new ErrorResponseDto().timestamp(now).code("CODE_A").message("MSG");
        ErrorResponseDto b = new ErrorResponseDto().timestamp(now).code("CODE_B").message("MSG");

        assertNotEquals(a, b);
    }

    @Test
    void equalsShouldReturnFalseForDifferentObject() {
        ErrorResponseDto dto = createSample();

        assertNotEquals(null, dto);
        assertNotEquals(new Object(), dto);
    }

    @Test
    void toStringShouldContainFields() {
        ErrorResponseDto dto = createSample();
        String result = dto.toString();

        assertNotNull(result);
        assertTrue(result.contains("class ErrorResponseDto"));
        assertTrue(result.contains("timestamp"));
        assertTrue(result.contains("code"));
        assertTrue(result.contains("message"));
    }
}
