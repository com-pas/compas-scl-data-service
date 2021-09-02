// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;

import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.*;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.HEADER_NOT_FOUND_ERROR_CODE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasSclDataServiceTest {
    private static final Version initialVersion = new Version("1.0.0");

    @Mock
    private CompasSclDataRepository compasSclDataRepository;

    private CompasSclDataService compasSclDataService;

    private final ElementConverter converter = new ElementConverter();
    private final SclElementProcessor processor = new SclElementProcessor();

    @BeforeEach
    void beforeEach() {
        compasSclDataService = new CompasSclDataService(compasSclDataRepository, processor);
    }

    @Test
    void list_WhenCalled_ThenRepositoryIsCalled() {
        var type = SclType.SCD;
        when(compasSclDataRepository.list(type)).thenReturn(emptyList());

        var result = compasSclDataService.list(type);

        assertNotNull(result);
        verify(compasSclDataRepository, times(1)).list(type);
    }

    @Test
    void listVersionsByUUID_WhenCalled_ThenRepositoryIsCalled() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        when(compasSclDataRepository.listVersionsByUUID(type, uuid)).thenReturn(emptyList());

        var result = compasSclDataService.listVersionsByUUID(type, uuid);

        assertNotNull(result);
        verify(compasSclDataRepository, times(1)).listVersionsByUUID(type, uuid);
    }

    @Test
    void findByUUID_WhenCalledWithoutVersion_ThenRepositoryIsCalled() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        when(compasSclDataRepository.findByUUID(type, uuid)).thenReturn(readSCL());

        var result = compasSclDataService.findByUUID(type, uuid);

        assertNotNull(result);
        verify(compasSclDataRepository, times(1)).findByUUID(type, uuid);
    }

    @Test
    void findByUUID_WhenCalledWithVersion_ThenRepositoryIsCalled() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        when(compasSclDataRepository.findByUUID(type, uuid, version)).thenReturn(readSCL());

        var result = compasSclDataService.findByUUID(type, uuid, version);

        assertNotNull(result);
        verify(compasSclDataRepository, times(1)).findByUUID(type, uuid, version);
    }

    @Test
    void create_WhenCalledWithOutCompasExtension_ThenRepositoryIsCalledAndUUIDIsReturned() {
        var type = SclType.SCD;
        var name = "JUSTSOMENAME";
        var comment = "Some comments";
        var who = "User A";

        var scl = readSCL();

        doNothing().when(compasSclDataRepository).create(eq(type), any(UUID.class), eq(scl), eq(initialVersion));

        var uuid = compasSclDataService.create(type, name, who, comment, scl);

        assertNotNull(uuid);
        assertCompasExtenions(scl, name, type);
        assertHistoryItem(scl, initialVersion, comment);
        verify(compasSclDataRepository, times(1)).create(eq(type), any(UUID.class), eq(scl), eq(initialVersion));
    }

    @Test
    void create_WhenCalledWithCompasExtension_ThenRepositoryIsCalledAndUUIDIsReturned() {
        var type = SclType.SCD;
        var name = "JUSTSOMENAME";
        var comment = "";
        var who = "User A";

        var scl = readSCL();
        createCompasPrivate(scl, "JUSTANOTHERNAME", SclType.IID);

        doNothing().when(compasSclDataRepository).create(eq(type), any(UUID.class), eq(scl), eq(initialVersion));

        var uuid = compasSclDataService.create(type, name, who, comment, scl);

        assertNotNull(uuid);
        assertCompasExtenions(scl, name, type);
        assertHistoryItem(scl, initialVersion, comment);
        verify(compasSclDataRepository, times(1)).create(eq(type), any(UUID.class), eq(scl), eq(initialVersion));
    }

    @Test
    void update_WhenCalledWithCompasElements_ThenRepositoryIsCalled() {
        var type = SclType.SCD;
        var name = "JUSTSOMENAME";
        var uuid = UUID.randomUUID();
        var changeSet = ChangeSetType.MAJOR;
        var who = "User A";
        var nextVersion = initialVersion.getNextVersion(changeSet);

        var scl = readSCL();
        createCompasPrivate(scl, name, SclType.IID);
        var previousScl = readSCL();
        createCompasPrivate(previousScl, name, type);

        when(compasSclDataRepository.findByUUID(type, uuid)).thenReturn(previousScl);
        doNothing().when(compasSclDataRepository).create(type, uuid, scl, nextVersion);

        compasSclDataService.update(type, uuid, changeSet, who, null, scl);

        assertCompasExtenions(scl, name, type);
        assertHistoryItem(scl, nextVersion, null);
        verify(compasSclDataRepository, times(1)).create(type, uuid, scl, nextVersion);
        verify(compasSclDataRepository, times(1)).findByUUID(type, uuid);
    }

    @Test
    void update_WhenCalledPreviousSclDoesNotContainHeader_ThenExceptionIsThrown() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var changeSet = ChangeSetType.MAJOR;
        var comment = "Some comments";
        var who = "User A";

        var scl = readSCL();
        var previousScl = readSCL();
        // Remove the Header from the previous version.
        processor.getChildNodeByName(previousScl, SCL_HEADER_ELEMENT_NAME)
                .ifPresent(element -> previousScl.removeChild(element));
        when(compasSclDataRepository.findByUUID(type, uuid)).thenReturn(previousScl);

        var exception = assertThrows(CompasSclDataServiceException.class,
                () -> compasSclDataService.update(type, uuid, changeSet, who, comment, scl));
        assertEquals(HEADER_NOT_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void delete_WhenCalledWithoutVersion_ThenRepositoryIsCalled() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();

        doNothing().when(compasSclDataRepository).delete(type, uuid);

        compasSclDataService.delete(type, uuid);

        verify(compasSclDataRepository, times(1)).delete(type, uuid);
    }

    @Test
    void delete_WhenCalledWithVersion_ThenRepositoryIsCalled() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);

        doNothing().when(compasSclDataRepository).delete(type, uuid, version);

        compasSclDataService.delete(type, uuid, version);

        verify(compasSclDataRepository, times(1)).delete(type, uuid, version);
    }

    private void assertCompasExtenions(Element scl, String name, SclType type) {
        var compasPrivate = processor.getCompasPrivate(scl);
        assertTrue(compasPrivate.isPresent());

        var nameElement = processor.getChildNodeByName(compasPrivate.get(), COMPAS_SCL_NAME_EXTENSION);
        assertTrue(nameElement.isPresent());
        assertEquals(name, nameElement.get().getTextContent());

        var typeElement = processor.getChildNodeByName(compasPrivate.get(), COMPAS_SCL_FILE_TYPE_EXTENSION);
        assertTrue(typeElement.isPresent());
        assertEquals(type.toString(), typeElement.get().getTextContent());
    }

    private void assertHistoryItem(Element scl, Version version, String comment) {
        var header = processor.getSclHeader(scl);
        assertTrue(header.isPresent());

        var history = processor.getChildNodeByName(header.get(), SCL_HISTORY_ELEMENT_NAME);
        assertTrue(history.isPresent());

        var items = processor.getChildNodesByName(history.get(), SCL_HITEM_ELEMENT_NAME);
        assertFalse(items.isEmpty());
        // The last item should be the one added.
        var item = items.get(items.size() - 1);
        assertEquals(version.toString(), item.getAttribute(SCL_VERSION_ATTR));
        if (comment != null && !comment.isEmpty()) {
            assertTrue(item.getAttribute(SCL_WHAT_ATTR).contains(comment));
        }
    }

    private Element readSCL() {
        var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.scd");
        assert inputStream != null;

        var scl = converter.convertToElement(inputStream, SCL_ELEMENT_NAME, SCL_NS_URI);
        var header = processor.getSclHeader(scl).orElseGet(() -> processor.addSclHeader(scl));
        header.setAttribute(SCL_ID_ATTR, UUID.randomUUID().toString());
        header.setAttribute(SCL_VERSION_ATTR, initialVersion.toString());
        return scl;
    }

    private void createCompasPrivate(Element scl, String sclName, SclType sclType) {
        var compasPrivate = processor.addCompasPrivate(scl);
        processor.addCompasElement(compasPrivate, COMPAS_SCL_NAME_EXTENSION, sclName);
        processor.addCompasElement(compasPrivate, COMPAS_SCL_FILE_TYPE_EXTENSION, sclType.name());
    }
}