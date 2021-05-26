// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.basex.BaseXServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.commons.MarshallerWrapper;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.basex.BaseXClientFactory;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.basex.BaseXServerUtil.createServer;
import static org.lfenergy.compas.scl.data.basex.BaseXServerUtil.getFreePortNumber;

@ExtendWith(MockitoExtension.class)
class CompasSclDataBaseXRepositoryTest {
    private static final SclType TYPE = SclType.SCD;
    private static BaseXServer server;
    private static BaseXClientFactory factory;
    private CompasSclDataBaseXRepository repository;

    @BeforeAll
    static void beforeAll() throws Exception {
        var portNumber = getFreePortNumber();
        server = createServer(portNumber);
        factory = new BaseXClientFactory("localhost", portNumber, "admin", "admin");
    }

    @BeforeEach
    void beforeEach() throws Exception {
        factory.createClient().executeXQuery("db:create('" + TYPE + "')");
        repository = new CompasSclDataBaseXRepository(factory);
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @Test
    void list_WhenCalledOnEmptyDatabase_ThenNoRecordsReturned() {
        var items = repository.list(TYPE);

        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    void list_WhenRecordAdded_ThenRecordFound() throws Exception {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var items = repository.list(TYPE);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(uuid.toString(), items.get(0).getId());
    }

    @Test
    void list_WhenTwoRecordAdded_ThenBothRecordsFound() throws Exception {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);
        uuid = UUID.randomUUID();
        scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var items = repository.list(TYPE);

        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    void list_WhenTwoVersionsOfARecordAdded_ThenLatestRecordFound() throws Exception {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);
        version = new Version(1, 1, 0);
        scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var items = repository.list(TYPE);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(uuid.toString(), items.get(0).getId());
        assertEquals(version.toString(), items.get(0).getVersion());
    }

    @Test
    void listVersionsByUUID_WhenTwoVersionsOfARecordAdded_ThenAllRecordAreFound() throws Exception {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);
        version = new Version(1, 1, 0);
        scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var items = repository.listVersionsByUUID(TYPE, uuid);

        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals(uuid.toString(), items.get(1).getId());
        assertEquals(version.toString(), items.get(1).getVersion());
    }

    @Test
    void find_WhenCalledWithUnknownUUID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        assertThrows(SclDataException.class, () -> {
            repository.findByUUID(TYPE, uuid);
        });
    }

    @Test
    void createAndFind_WhenSclAdded_ThenScLStoredAndLastVersionCanBeFound() throws Exception {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var foundScl = repository.findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), foundScl.getHeader().getId());
        assertEquals(version.toString(), foundScl.getHeader().getVersion());
    }

    @Test
    void createAndFind_WhenMoreVersionOfSclAdded_ThenDefaultSCLLastVersionReturned() throws Exception {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readSCL(uuid, nextVersion);
        repository.create(TYPE, uuid, nextScl, nextVersion);

        var foundScl = repository.findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), foundScl.getHeader().getId());
        assertEquals(nextVersion.toString(), foundScl.getHeader().getVersion());
    }

    @Test
    void createAndFind_WhenMoreVersionOfSCLAdded_ThenSCLOldVersionCanBeFound() throws Exception {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readSCL(uuid, nextVersion);
        repository.create(TYPE, uuid, nextScl, nextVersion);

        var foundScl = repository.findByUUID(TYPE, uuid, version);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), foundScl.getHeader().getId());
        assertEquals(version.toString(), foundScl.getHeader().getVersion());
    }

    @Test
    void createAndDelete_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() throws Exception {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);

        repository.create(TYPE, uuid, scl, version);
        var foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(scl.getHeader().getId(), foundScl.getHeader().getId());

        repository.delete(TYPE, uuid, version);
        assertThrows(SclDataException.class, () -> {
            repository.findByUUID(TYPE, uuid);
        });
    }

    @Test
    void createAndDeleteAll_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() throws Exception {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);

        repository.create(TYPE, uuid, scl, version);
        var foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(scl.getHeader().getId(), foundScl.getHeader().getId());
        assertEquals(scl.getHeader().getVersion(), foundScl.getHeader().getVersion());

        version = version.getNextVersion(ChangeSetType.MAJOR);
        repository.create(TYPE, uuid, scl, version);
        foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(scl.getHeader().getId(), foundScl.getHeader().getId());
        assertEquals(scl.getHeader().getVersion(), foundScl.getHeader().getVersion());

        repository.delete(TYPE, uuid);
        assertThrows(SclDataException.class, () -> {
            repository.findByUUID(TYPE, uuid);
        });
    }

    private SCL readSCL(UUID uuid, Version version) throws Exception {
        var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.xml");
        assert inputStream != null;
        var scl = new MarshallerWrapper.Builder().build().unmarshall(inputStream);
        scl.getHeader().setId(uuid.toString());
        scl.getHeader().setVersion(version.toString());
        return scl;
    }
}