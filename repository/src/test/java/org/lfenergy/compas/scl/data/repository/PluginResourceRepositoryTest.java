// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.blocking.PanacheBlockingQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.PluginResource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PluginResourceRepositoryTest {

    private static final String PLUGIN = "engineering_wizard";
    private static final String TYPE = "processes";
    private static final String NAME = "default-process";
    private static final String TENANT = "default";

    private PluginResourceRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new PluginResourceRepository());
    }

    // ---- findByIdForPluginAndType ------------------------------------------

    @Test
    void findByIdForPluginAndType_WhenEntityFound_ThenReturnsOptionalOfEntity() {
        var id = UUID.randomUUID();
        var entity = new PluginResource();
        var query = mock(PanacheBlockingQuery.class);
        doReturn(query).when(repository).find(anyString(), eq(PLUGIN), eq(TYPE), eq(id));
        when(query.firstResultOptional()).thenReturn(Optional.of(entity));

        var result = repository.findByIdForPluginAndType(PLUGIN, TYPE, id);

        assertTrue(result.isPresent());
        assertSame(entity, result.get());
    }

    @Test
    void findByIdForPluginAndType_WhenNoEntity_ThenReturnsEmpty() {
        var id = UUID.randomUUID();
        var query = mock(PanacheBlockingQuery.class);
        doReturn(query).when(repository).find(anyString(), eq(PLUGIN), eq(TYPE), eq(id));
        when(query.firstResultOptional()).thenReturn(Optional.empty());

        var result = repository.findByIdForPluginAndType(PLUGIN, TYPE, id);

        assertFalse(result.isPresent());
    }

    // ---- findAllByPluginAndType --------------------------------------------

    @Test
    void findAllByPluginAndType_WhenCalled_ThenDelegatesToList() {
        var e1 = new PluginResource();
        var e2 = new PluginResource();
        doReturn(List.of(e1, e2)).when(repository).list(anyString(), eq(PLUGIN), eq(TYPE));

        var result = repository.findAllByPluginAndType(PLUGIN, TYPE);

        assertEquals(2, result.size());
    }

    // ---- findAllByPluginTypeAndName ----------------------------------------

    @Test
    void findAllByPluginTypeAndName_WhenCalled_ThenDelegatesToList() {
        var e1 = new PluginResource();
        doReturn(List.of(e1)).when(repository).list(anyString(), eq(PLUGIN), eq(TYPE), eq(NAME));

        var result = repository.findAllByPluginTypeAndName(PLUGIN, TYPE, NAME);

        assertEquals(1, result.size());
    }

    @Test
    void findAllByPluginTypeAndName_WhenCalled_ThenQueryOrdersByUploadedAtDesc() {
        doReturn(List.of()).when(repository).list(anyString(), eq(PLUGIN), eq(TYPE), eq(NAME));

        repository.findAllByPluginTypeAndName(PLUGIN, TYPE, NAME);

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(repository).list(captor.capture(), eq(PLUGIN), eq(TYPE), eq(NAME));
        assertTrue(captor.getValue().toLowerCase().contains("order by uploadedat desc"),
                "Query must order by uploadedAt desc");
    }

    // ---- existsByPluginTypeTenantNameAndVersion ----------------------------

    @Test
    void existsByPluginTypeTenantNameAndVersion_WhenCountPositive_ThenReturnsTrue() {
        doReturn(1L).when(repository).count(anyString(), eq(PLUGIN), eq(TYPE),
                eq(TENANT), eq(NAME), eq("1.0.0"));

        assertTrue(repository.existsByPluginTypeTenantNameAndVersion(
                PLUGIN, TYPE, TENANT, NAME, "1.0.0"));
    }

    @Test
    void existsByPluginTypeTenantNameAndVersion_WhenCountZero_ThenReturnsFalse() {
        doReturn(0L).when(repository).count(anyString(), eq(PLUGIN), eq(TYPE),
                eq(TENANT), eq(NAME), eq("1.0.0"));

        assertFalse(repository.existsByPluginTypeTenantNameAndVersion(
                PLUGIN, TYPE, TENANT, NAME, "1.0.0"));
    }

    // ---- deleteAllByPluginAndType ------------------------------------------

    @Test
    void deleteAllByPluginAndType_WhenCalled_ThenReturnsRowsDeleted() {
        doReturn(3L).when(repository).delete(anyString(), eq(PLUGIN), eq(TYPE));

        assertEquals(3L, repository.deleteAllByPluginAndType(PLUGIN, TYPE));
    }

    @Test
    void deleteAllByPluginAndType_WhenNothingDeleted_ThenReturnsZero() {
        doReturn(0L).when(repository).delete(anyString(), eq(PLUGIN), eq(TYPE));

        assertEquals(0L, repository.deleteAllByPluginAndType(PLUGIN, TYPE));
    }

    // ---- deleteAllByPluginTypeAndName --------------------------------------

    @Test
    void deleteAllByPluginTypeAndName_WhenCalled_ThenReturnsRowsDeleted() {
        doReturn(2L).when(repository).delete(anyString(), eq(PLUGIN), eq(TYPE), eq(NAME));

        assertEquals(2L, repository.deleteAllByPluginTypeAndName(PLUGIN, TYPE, NAME));
    }

    @Test
    void deleteAllByPluginTypeAndName_WhenNothingDeleted_ThenReturnsZero() {
        doReturn(0L).when(repository).delete(anyString(), eq(PLUGIN), eq(TYPE), eq(NAME));

        assertEquals(0L, repository.deleteAllByPluginTypeAndName(PLUGIN, TYPE, NAME));
    }
}
