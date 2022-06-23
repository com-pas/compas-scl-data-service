// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.ItemHistory;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

public interface CompasSclDataService {
    @Transactional(SUPPORTS)
    List<Item> list(SclFileType type);

    @Transactional(SUPPORTS)
    List<ItemHistory> listVersionsByUUID(SclFileType type, UUID id);

    @Transactional(SUPPORTS)
    String findByUUID(SclFileType type, UUID id);

    @Transactional(SUPPORTS)
    String findByUUID(SclFileType type, UUID id, Version version);

    @Transactional(REQUIRED)
    String create(SclFileType type, String name, String who, String comment, String sclData);

    @Transactional(REQUIRED)
    String update(SclFileType type, UUID id, ChangeSetType changeSetType, String who, String comment, String sclData);

    @Transactional(REQUIRED)
    void delete(SclFileType type, UUID id);

    @Transactional(REQUIRED)
    void delete(SclFileType type, UUID id, Version version);
}
