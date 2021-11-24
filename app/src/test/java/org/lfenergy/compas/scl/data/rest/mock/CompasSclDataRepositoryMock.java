// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.mock;

import io.quarkus.test.Mock;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclMetaInfo;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@Mock
@ApplicationScoped
public class CompasSclDataRepositoryMock implements CompasSclDataRepository {
    @Override
    public List<Item> list(SclType type) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public List<Item> listVersionsByUUID(SclType type, UUID id) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public String findByUUID(SclType type, UUID id) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public SclMetaInfo findMetaInfoByUUID(SclType type, UUID id) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public String findByUUID(SclType type, UUID id, Version version) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public void create(SclType type, UUID id, String name, String scl, Version version, String who) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public void delete(SclType type, UUID id) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }

    @Override
    public void delete(SclType type, UUID id, Version version) {
        throw new IllegalStateException("Mock method using Mockito. Only needed to startup.");
    }
}
