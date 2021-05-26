// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.THeader;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasSclDataServiceTest {
    @Mock
    private CompasSclDataRepository compasSclDataRepository;

    @InjectMocks
    private CompasSclDataService compasSclDataService;

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
        verify(compasSclDataRepository, times(1)).create(eq(type), any(UUID.class), eq(scl), any(Version.class));
    }

    @Test
    void update_WhenCalled_ThenRepositoryIsCalledAndNewUUIDIsReturned() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = createBasicSCL();

        when(compasSclDataRepository.findByUUID(type, uuid)).thenReturn(createBasicSCL());
        doNothing().when(compasSclDataRepository).create(eq(type), eq(uuid), eq(scl), any(Version.class));

        compasSclDataService.update(type, uuid, ChangeSetType.MAJOR, scl);

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

    private SCL createBasicSCL() {
        var scl = new SCL();
        scl.setHeader(new THeader());
        scl.getHeader().setVersion("1.0.0");
        return scl;
    }
}