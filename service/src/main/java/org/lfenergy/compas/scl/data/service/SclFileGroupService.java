// SPDX-FileCopyrightText: 2026 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lfenergy.compas.scl.data.repository.SclFileGroupRepository;
import org.lfenergy.compas.scl.data.entities.SclFileGroup;

import java.util.List;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class SclFileGroupService {
    private final SclFileGroupRepository sclFileGroupRepository;

    @Inject
    public SclFileGroupService(SclFileGroupRepository sclFileGroupRepository) {
        this.sclFileGroupRepository = sclFileGroupRepository;
    }

    @Transactional(SUPPORTS)
    public List<SclFileGroup> listAll() {
        return sclFileGroupRepository.listAll();
    }
}
