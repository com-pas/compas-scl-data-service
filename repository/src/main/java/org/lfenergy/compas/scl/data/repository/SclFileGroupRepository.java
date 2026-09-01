// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import io.quarkus.hibernate.panache.managed.blocking.PanacheManagedBlockingRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.lfenergy.compas.scl.data.entities.SclFileGroup;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SclFileGroupRepository implements PanacheManagedBlockingRepositoryBase<SclFileGroup, UUID> {

    public List<SclFileGroup> listAll() {
        return listAll(Sort.by("name"));
    }
}
