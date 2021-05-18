package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.repository.CompasDataRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasDataServiceTest {
    @Mock
    private CompasDataRepository compasDataRepository;

    @InjectMocks
    private CompasDataService compasDataService;

    @Test
    public void findSCLByUUID_WhenCalled_ThenRepositoryIsCalled() {
        SclType type = SclType.SCD;
        UUID uuid = UUID.randomUUID();
        when(compasDataRepository.findSCLByUUID(eq(type), eq(uuid))).thenReturn(new SCL());

        SCL result = compasDataService.findSCLByUUID(type, uuid);

        assertNotNull(result);
        verify(compasDataRepository, times(1)).findSCLByUUID(type, uuid);
    }

    @Test
    public void create_WhenCalled_ThenRepositoryIsCalledAndUUIDIsReturned() {
        SclType type = SclType.SCD;
        String name = "JUSTSOMENAME";
        SCL scl = new SCL();

        when(compasDataRepository.create(eq(type), eq(scl))).thenReturn(UUID.randomUUID());

        UUID uuid = compasDataService.create(type, name, scl);

        assertNotNull(uuid);
        verify(compasDataRepository, times(1)).create(type, scl);
    }

    @Test
    public void update_WhenCalled_ThenRepositoryIsCalledAndNewUUIDIsReturned() {
        SclType type = SclType.SCD;
        UUID uuid = UUID.randomUUID();
        SCL scl = new SCL();

        when(compasDataRepository.findSCLByUUID(eq(type), eq(uuid))).thenReturn(new SCL());
        when(compasDataRepository.create(eq(type), eq(scl))).thenReturn(UUID.randomUUID());

        UUID newUuid = compasDataService.update(type, uuid, ChangeSetType.MAJOR, scl);

        assertNotNull(newUuid);
        assertFalse(uuid.equals(newUuid));
        verify(compasDataRepository, times(1)).create(type, scl);
        verify(compasDataRepository, times(1)).findSCLByUUID(type, uuid);
    }

    @Test
    public void delete_WhenCalled_ThenRepositoryIsCalled() {
        SclType type = SclType.SCD;
        UUID uuid = UUID.randomUUID();

        doNothing().when(compasDataRepository).delete(eq(type), eq(uuid));

        compasDataService.delete(type, uuid);

        verify(compasDataRepository, times(1)).delete(type, uuid);
    }
}