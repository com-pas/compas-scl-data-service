// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;

import java.util.UUID;

public interface CompasSclDataRepository {
    
    SCL findSCLByUUID(SclType type, UUID uuid);

    SCL findSCLByUUID(SclType type, UUID uuid, Version version);

    void create(SclType type, UUID uuid, SCL scl, Version version);

    void delete(SclType type, UUID uuid);

    void delete(SclType type, UUID uuid, Version version);
}
