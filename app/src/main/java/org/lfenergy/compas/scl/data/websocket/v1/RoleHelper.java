// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import static org.lfenergy.compas.scl.data.rest.Constants.CREATE_ROLE;
import static org.lfenergy.compas.scl.data.rest.Constants.UPDATE_ROLE;

import java.util.List;

public class RoleHelper {
    private final static List<String> WRITE_TYPES = List.of("LNT", "BAY");

    public static String getRequiredRoleCreate(String type) {
        if (WRITE_TYPES.contains(type)) {
            return type + "_WRITE";
        }
        return type + "_" + CREATE_ROLE;
    }

    public static String getRequiredRoleUpdate(String type) {
        if (WRITE_TYPES.contains(type)) {
            return type + "_WRITE";
        }
        return type + "_" + UPDATE_ROLE;
    }
}
