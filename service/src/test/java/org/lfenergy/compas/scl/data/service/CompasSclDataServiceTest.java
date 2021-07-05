// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclElementConverter;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;

import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.Constants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasSclDataServiceTest {
    @Mock
    private CompasSclDataRepository compasSclDataRepository;

    private CompasSclDataService compasSclDataService;

    private final SclElementConverter converter = new SclElementConverter();
    private final SclElementProcessor processor = new SclElementProcessor();

    @BeforeEach
    void beforeEach() {
        compasSclDataService = new CompasSclDataService(compasSclDataRepository);
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
    void create_WhenCalled_ThenRepositoryIsCalledAndUUIDIsReturned() {
        var type = SclType.SCD;
        var name = "JUSTSOMENAME";
        var scl = readSCL();

        doNothing().when(compasSclDataRepository).create(eq(type), any(UUID.class), eq(scl), any(Version.class));

        var uuid = compasSclDataService.create(type, name, scl);

        assertNotNull(uuid);
        assertCompasExtenions(scl, name, type);
        verify(compasSclDataRepository, times(1)).create(eq(type), any(UUID.class), eq(scl), any(Version.class));
    }

    @Test
    void create_WhenCalledWithCompasExtension_ThenRepositoryIsCalledAndUUIDIsReturned() {
        var type = SclType.SCD;
        var name = "JUSTSOMENAME";
        var scl = readSCL();
        createCompasPrivate(scl, "JUSTANOTHERNAME", SclType.IID);

        doNothing().when(compasSclDataRepository).create(eq(type), any(UUID.class), eq(scl), any(Version.class));

        var uuid = compasSclDataService.create(type, name, scl);

        assertNotNull(uuid);
        assertCompasExtenions(scl, name, type);
        verify(compasSclDataRepository, times(1)).create(eq(type), any(UUID.class), eq(scl), any(Version.class));
    }

    @Test
    void update_WhenCalled_ThenRepositoryIsCalledAndNewUUIDIsReturned() {
        var type = SclType.SCD;
        var name = "JUSTANOTHERNAME";
        var uuid = UUID.randomUUID();
        var scl = readSCL();
        createCompasPrivate(scl, name, SclType.ISD);

        when(compasSclDataRepository.findByUUID(type, uuid)).thenReturn(readSCL());
        doNothing().when(compasSclDataRepository).create(eq(type), eq(uuid), eq(scl), any(Version.class));

        compasSclDataService.update(type, uuid, ChangeSetType.MAJOR, scl);

        assertCompasExtenions(scl, name, type);
        verify(compasSclDataRepository, times(1)).create(eq(type), eq(uuid), eq(scl), any(Version.class));
        verify(compasSclDataRepository, times(1)).findByUUID(type, uuid);
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

        var nameElement = processor.getCompasElement(compasPrivate.get(), COMPAS_SCL_NAME_EXTENSION);
        assertTrue(nameElement.isPresent());
        assertEquals(name, nameElement.get().getTextContent());

        var typeElement = processor.getCompasElement(compasPrivate.get(), COMPAS_SCL_FILE_TYPE_EXTENSION);
        assertTrue(typeElement.isPresent());
        assertEquals(type.toString(), typeElement.get().getTextContent());
    }

    private Element readSCL() {
        var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.scd");
        assert inputStream != null;

        Element scl = converter.convertToElement(inputStream);
        Element header = processor.getHeader(scl).orElseGet(() -> processor.addHeader(scl));
        header.setAttribute(SCL_HEADER_ID_ATTR, UUID.randomUUID().toString());
        header.setAttribute(SCL_HEADER_VERSION_ATTR, "1.0.0");
        return scl;
    }

    private void createCompasPrivate(Element scl, String sclName, SclType sclType) {
        Element compasPrivate = processor.addCompasPrivate(scl);
        processor.addCompasElement(compasPrivate, COMPAS_SCL_NAME_EXTENSION, sclName);
        processor.addCompasElement(compasPrivate, COMPAS_SCL_FILE_TYPE_EXTENSION, sclType.name());
    }
}