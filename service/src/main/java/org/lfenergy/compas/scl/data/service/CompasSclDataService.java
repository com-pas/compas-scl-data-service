// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

public interface CompasSclDataService {
    @Transactional(SUPPORTS)
    List<Item> list(SclType type);

    @Transactional(SUPPORTS)
    List<Item> listVersionsByUUID(SclType type, UUID id);

    @Transactional(SUPPORTS)
    String findByUUID(SclType type, UUID id);

    @Transactional(SUPPORTS)
    String findByUUID(SclType type, UUID id, Version version);

    @Transactional(REQUIRED)
    String create(SclType type, String name, String who, String comment, String sclData);

    @Transactional(REQUIRED)
    String update(SclType type, UUID id, ChangeSetType changeSetType, String who, String comment, String sclData);

    @Transactional(REQUIRED)
    void delete(SclType type, UUID id);

    @Transactional(REQUIRED)
    void delete(SclType type, UUID id, Version version);
}
