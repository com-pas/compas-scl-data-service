// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.scl.data.entities.SclFileGroup;
import org.lfenergy.compas.scl.data.repository.SclFileGroupRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SclFileGroupServiceTest {
    @Mock
    private SclFileGroupRepository sclFileGroupRepository;

    private SclFileGroupService sclFileGroupService;

    @BeforeEach
    void setUp() {
        sclFileGroupService = new SclFileGroupService(sclFileGroupRepository);
    }

    @Test
    void listAll_WhenCalled_ThenReturn() {
        var group1 = new SclFileGroup();
        group1.name = "ABC";
        var group2 = new SclFileGroup();
        group2.name = "XYZ";

        doReturn(List.of(group1, group2)).when(sclFileGroupRepository).listAll();

        var result = sclFileGroupService.listAll();
        assertEquals(List.of(group1, group2), result);
    }
}
