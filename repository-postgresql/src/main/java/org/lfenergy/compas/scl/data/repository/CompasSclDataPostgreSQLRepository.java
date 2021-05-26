// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class CompasSclDataPostgreSQLRepository implements CompasSclDataRepository {

    public static final String NOT_IMPLEMENTED = "Not implemented!";

    @Override
    public List<Item> list(SclType type) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public List<Item> listVersionsByUUID(SclType type, UUID id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public SCL findByUUID(SclType type, UUID id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public SCL findByUUID(SclType type, UUID id, Version version) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void create(SclType type, UUID id, SCL scl, Version version) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void delete(SclType type, UUID id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    public void delete(SclType type, UUID id, Version version) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}
