// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.mock;

import io.quarkus.test.Mock;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.ItemHistory;
import org.lfenergy.compas.scl.data.model.SclMetaInfo;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@Mock
@ApplicationScoped
public class CompasSclDataRepositoryMock implements CompasSclDataRepository {
    @Override
    public List<Item> list(SclFileType type) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public List<ItemHistory> listVersionsByUUID(SclFileType type, UUID id) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public String findByUUID(SclFileType type, UUID id) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public SclMetaInfo findMetaInfoByUUID(SclFileType type, UUID id) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public String findByUUID(SclFileType type, UUID id, Version version) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public boolean hasDuplicateSclName(SclFileType type, String name) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public void create(SclFileType type, UUID id, String name, String scl, Version version, String who) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public void delete(SclFileType type, UUID id) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public void delete(SclFileType type, UUID id, Version version) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }
}
