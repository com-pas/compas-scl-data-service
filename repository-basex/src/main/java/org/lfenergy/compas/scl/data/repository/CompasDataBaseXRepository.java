// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.SclType;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class CompasDataBaseXRepository implements CompasDataRepository {
    @Override
    public SCL findSCLByUUID(SclType type, UUID uuid) {
        return new SCL();
    }

    @Override
    public UUID create(SclType type, String name, SCL scl) {
        return UUID.randomUUID();
    }

    @Override
    public void delete(SclType type, UUID uuid) {
    }
}
