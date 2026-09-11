// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository;

import io.quarkus.panache.common.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.SclFileGroup;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SclFileGroupRepositoryTest {
    private SclFileGroupRepository repository;

    @BeforeEach
    void setUp() {
        repository = spy(new SclFileGroupRepository() {
        });
    }

    @Test
    void codeExists_WhenSclFileGroupExists_ThenReturnsTrue() {
        doReturn(1L).when(repository).count(anyString(), eq("SCD"));

        assertTrue(repository.doesCodeExist("SCD"));
    }

    @Test
    void listAll_WhenCalled_ThenReturnSorted() {
        var group1 = new SclFileGroup();
        group1.name = "ABC";
        var group2 = new SclFileGroup();
        group2.name = "XYZ";

        doReturn(List.of(group1, group2)).when(repository).listAll(any(Sort.class));

        var result = repository.listAll();
        assertEquals(List.of(group1, group2), result);
    }
}
