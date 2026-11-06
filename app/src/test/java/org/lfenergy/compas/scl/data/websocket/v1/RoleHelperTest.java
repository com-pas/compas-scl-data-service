// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleHelperTest {
    @Test
    void getRequiredRoleCreate_shouldReturnWriteRoleForWriteTypes() {
        assertEquals("LNT_WRITE", RoleHelper.getRequiredRoleCreate("LNT"));
        assertEquals("BAY_WRITE", RoleHelper.getRequiredRoleCreate("BAY"));
    }

    @Test
    void getRequiredRoleCreate_shouldReturnCreateRoleForNonWriteTypes() {
        assertEquals("SCD_CREATE", RoleHelper.getRequiredRoleCreate("SCD"));
        assertEquals("ABC_CREATE", RoleHelper.getRequiredRoleCreate("ABC"));
    }

    @Test
    void getRequiredRoleUpdate_shouldReturnWriteRoleForWriteTypes() {
        assertEquals("LNT_WRITE", RoleHelper.getRequiredRoleUpdate("LNT"));
        assertEquals("BAY_WRITE", RoleHelper.getRequiredRoleUpdate("BAY"));
    }

    @Test
    void getRequiredRoleUpdate_shouldReturnUpdateRoleForNonWriteTypes() {
        assertEquals("SCD_UPDATE", RoleHelper.getRequiredRoleUpdate("SCD"));
        assertEquals("ABC_UPDATE", RoleHelper.getRequiredRoleUpdate("ABC"));
    }
}
