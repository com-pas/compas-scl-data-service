// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.util;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.Constants.*;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.HEADER_NOT_FOUND_ERROR_CODE;

class SclElementProcessorTest {
    private SclElementProcessor processor = new SclElementProcessor();
    private ElementConverter converter = new ElementConverter();

    @Test
    void fixDefaultPrefix_WhenCalledWithDifferentSclPrefix_ThenPrefixIsSetToDefaultAndAttributeRemoved() {
        var scl = readSCL("scl_with_prefix.scd");

        assertTrue(scl.hasAttribute("xmlns:scl"));
        assertEquals("scl", scl.getPrefix());
        var header = processor.getSclHeader(scl);
        assertEquals("scl", header.get().getPrefix());

        /// Fix the namespace prefix
        processor.fixDefaultPrefix(scl);

        assertFalse(scl.hasAttribute("xmlns:scl"));
        assertNull(scl.getPrefix());
        header = processor.getSclHeader(scl);
        assertNull(header.get().getPrefix());
    }

    @Test
    void fixDefaultPrefix_WhenCalledWithDefaultSclPrefix_ThenNothingIsDone() {
        var scl = readSCL("scl_with_default_prefix.scd");

        assertFalse(scl.hasAttribute("xmlns:scl"));
        assertNull(scl.getPrefix());
        var header = processor.getSclHeader(scl);
        assertNull(header.get().getPrefix());

        /// Fix the namespace prefix
        processor.fixDefaultPrefix(scl);

        assertFalse(scl.hasAttribute("xmlns:scl"));
        assertNull(scl.getPrefix());
        header = processor.getSclHeader(scl);
        assertNull(header.get().getPrefix());
    }

    @Test
    void getSclHeader_WhenCalledWithSclContainingAHeader_ThenHeaderReturned() {
        var scl = readSCL("scl_with_header.scd");

        var result = processor.getSclHeader(scl);

        assertTrue(result.isPresent());
        var header = result.get();
        assertEquals("1.0.0", header.getAttribute(SCL_HEADER_VERSION_ATTR));
    }

    @Test
    void getSclHeader_WhenCalledWithoutSclContainingAHeader_ThenEmptyOptionalReturned() {
        var scl = readSCL("scl_without_header.scd");

        var result = processor.getSclHeader(scl);

        assertFalse(result.isPresent());
    }

    @Test
    void addSclHeader_WhenCalled_ThenHeaderIsAddedAsFirstChild() {
        var scl = readSCL("scl_without_header.scd");

        var header = processor.addSclHeader(scl);

        assertNotNull(header);
        assertEquals(1, scl.getChildNodes().getLength());
        assertEquals(header, scl.getFirstChild());
    }

    @Test
    void getCompasPrivate_WhenCalledWithCompasPrivate_ThenPrivateElementReturned() {
        var scl = readSCL("scl_with_compas_private.scd");

        var result = processor.getCompasPrivate(scl);

        assertTrue(result.isPresent());
        var compasPrivate = result.get();
        assertEquals(COMPAS_SCL_EXTENSION_TYPE, compasPrivate.getAttribute(SCL_PRIVATE_TYPE_ATTR));
    }

    @Test
    void getCompasPrivate_WhenCalledWithoutCompasPrivate_ThenEmptyOptionalReturned() {
        var scl = readSCL("scl_without_compas_private.scd");

        var result = processor.getCompasPrivate(scl);

        assertFalse(result.isPresent());
    }

    @Test
    void addCompasPrivate_WhenCalledWithoutCompasPrivate_ThenNewElementIsAdded() {
        var scl = readSCL("scl_without_compas_private.scd");

        var foundElement = processor.getCompasPrivate(scl);
        assertFalse(foundElement.isPresent());

        var result = processor.addCompasPrivate(scl);

        assertNotNull(result);
        assertEquals(SCL_PRIVATE_ELEMENT_NAME, result.getLocalName());
        assertEquals(COMPAS_SCL_EXTENSION_TYPE, result.getAttribute(SCL_PRIVATE_TYPE_ATTR));
        foundElement = processor.getCompasPrivate(scl);
        assertTrue(foundElement.isPresent());
        assertEquals(result, foundElement.get());
    }

    @Test
    void addCompasPrivate_WhenCalledWithoutSclHeader_ThenExceptionIsThrown() {
        var scl = readSCL("scl_without_header.scd");

        var foundElement = processor.getCompasPrivate(scl);
        assertFalse(foundElement.isPresent());

        var exception = assertThrows(CompasSclDataServiceException.class, () -> {
            processor.addCompasPrivate(scl);
        });
        assertEquals(HEADER_NOT_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void addCompasElement_WhenCalledWithoutCompasElement_ThenNewElementIsAdded() {
        var value = "test-station";
        var scl = readSCL("scl_without_sclname_compas_private.scd");

        var compasPrivate = processor.getCompasPrivate(scl);
        assertTrue(compasPrivate.isPresent());
        var foundElement = processor.getChildNodeByName(compasPrivate.get(), COMPAS_SCL_NAME_EXTENSION);
        assertFalse(foundElement.isPresent());

        var result = processor.addCompasElement(compasPrivate.get(), COMPAS_SCL_NAME_EXTENSION, value);

        assertNotNull(result);
        assertEquals(COMPAS_SCL_NAME_EXTENSION, result.getLocalName());
        assertEquals(value, result.getTextContent());
        foundElement = processor.getChildNodeByName(compasPrivate.get(), COMPAS_SCL_NAME_EXTENSION);
        assertTrue(foundElement.isPresent());
        assertEquals(foundElement.get(), result);
    }

    @Test
    void getAttributeValue_WhenCalledForExistingAttribute_ThenValueIsReturnedAsOptional() {
        var scl = readSCL("scl_with_compas_private.scd");
        var compasPrivate = processor.getCompasPrivate(scl);

        var result = processor.getAttributeValue(compasPrivate.get(), SCL_PRIVATE_TYPE_ATTR);

        assertTrue(result.isPresent());
        assertEquals(COMPAS_SCL_EXTENSION_TYPE, result.get());
    }

    @Test
    void getAttributeValue_WhenCalledForNonExistingAttribute_ThenOptionalEmptyReturned() {
        var scl = readSCL("scl_with_compas_private.scd");
        var compasPrivate = processor.getCompasPrivate(scl);

        var result = processor.getAttributeValue(compasPrivate.get(), "unknown");

        assertFalse(result.isPresent());
    }

    private Element readSCL(String sclFilename) {
        var inputStream = getClass().getResourceAsStream("/scl/" + sclFilename);
        assert inputStream != null;

        return converter.convertToElement(inputStream, SCL_ELEMENT_NAME, SCL_NS_URI);
    }
}
