// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.commons.CompasExtensionsManager;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.THeader;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.extensions.ObjectFactory;
import org.lfenergy.compas.scl.extensions.TSclFileType;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.extensions.common.CompasExtensionsField.SCL_FILETYPE_EXTENSION;
import static org.lfenergy.compas.scl.extensions.common.CompasExtensionsField.SCL_NAME_EXTENSION;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasSclDataServiceTest {
    private CompasExtensionsManager compasExtensionsManager = new CompasExtensionsManager();

    @Mock
    private CompasSclDataRepository compasSclDataRepository;

    private CompasSclDataService compasSclDataService;

    @BeforeEach
    void beforeEach() {
        compasSclDataService = new CompasSclDataService(compasSclDataRepository, compasExtensionsManager);
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
        when(compasSclDataRepository.findByUUID(type, uuid)).thenReturn(createBasicSCL());

        var result = compasSclDataService.findByUUID(type, uuid);

        assertNotNull(result);
        verify(compasSclDataRepository, times(1)).findByUUID(type, uuid);
    }

    @Test
    void findByUUID_WhenCalledWithVersion_ThenRepositoryIsCalled() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        when(compasSclDataRepository.findByUUID(type, uuid, version)).thenReturn(createBasicSCL());

        var result = compasSclDataService.findByUUID(type, uuid, version);

        assertNotNull(result);
        verify(compasSclDataRepository, times(1)).findByUUID(type, uuid, version);
    }

    @Test
    void create_WhenCalled_ThenRepositoryIsCalledAndUUIDIsReturned() {
        var type = SclType.SCD;
        var name = "JUSTSOMENAME";
        var scl = createBasicSCL();

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
        var scl = createBasicSCL();
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
        var scl = createBasicSCL();
        createCompasPrivate(scl, name, SclType.ISD);

        when(compasSclDataRepository.findByUUID(type, uuid)).thenReturn(createBasicSCL());
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

    private void assertCompasExtenions(SCL scl, String name, SclType type) {
        var compasPrivate = compasExtensionsManager.getCompasPrivate(scl);
        assertTrue(compasPrivate.isPresent());

        var nameElement = compasExtensionsManager.getCompasElement(compasPrivate.get(), SCL_NAME_EXTENSION);
        assertTrue(nameElement.isPresent());
        assertEquals(name, nameElement.get().getValue().toString());

        var typeElement = compasExtensionsManager.getCompasElement(compasPrivate.get(), SCL_FILETYPE_EXTENSION);
        assertTrue(typeElement.isPresent());
        assertEquals(type.toString(), typeElement.get().getValue().toString());
    }

    private SCL createBasicSCL() {
        var scl = new SCL();
        scl.setHeader(new THeader());
        scl.getHeader().setVersion("1.0.0");
        return scl;
    }

    private void createCompasPrivate(SCL scl, String name, SclType type) {
        var compasPrivateElementFactory = new ObjectFactory();
        var compasPrivate = compasExtensionsManager.createCompasPrivate();
        compasPrivate.getContent().add(compasPrivateElementFactory.createSclName(name));
        TSclFileType sclFileType = TSclFileType.valueOf(type.toString());
        compasPrivate.getContent().add(compasPrivateElementFactory.createSclFileType(sclFileType));
        scl.getPrivate().add(compasPrivate);
    }
}