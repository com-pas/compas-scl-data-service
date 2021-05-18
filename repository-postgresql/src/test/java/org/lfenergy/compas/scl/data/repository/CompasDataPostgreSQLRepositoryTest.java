package org.lfenergy.compas.scl.data.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.SclType;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CompasDataPostgreSQLRepositoryTest {
    @InjectMocks
    private CompasDataPostgreSQLRepository repository;

    @Test
    void findSCLByUUID_WhenCalled_ThenUnsupportedExceptionThrown() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.findSCLByUUID(SclType.SCD, UUID.randomUUID());
        });
    }

    @Test
    void create_WhenCalled_ThenUnsupportedExceptionThrown() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.create(SclType.SCD, new SCL());
        });
    }

    @Test
    void delete_WhenCalled_ThenUnsupportedExceptionThrown() {
        assertThrows(UnsupportedOperationException.class, () -> {
            repository.delete(SclType.SCD, UUID.randomUUID());
        });
    }
}