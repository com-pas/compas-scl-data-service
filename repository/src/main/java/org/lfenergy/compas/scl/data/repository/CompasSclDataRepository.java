// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.w3c.dom.Element;

import java.util.List;
import java.util.UUID;

public interface CompasSclDataRepository {
    List<Item> list(SclType type);

    List<Item> listVersionsByUUID(SclType type, UUID id);

    Element findByUUID(SclType type, UUID id);

    Element findByUUID(SclType type, UUID id, Version version);

    void create(SclType type, UUID id, Element scl, Version version);

    void delete(SclType type, UUID id);

    void delete(SclType type, UUID id, Version version);
}
