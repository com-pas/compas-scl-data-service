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
        repository = new CompasSclDataBaseXRepository(factory);
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @Test
    void find_WhenCalledWithUnknownUUID_ThenExceptionIsThrown() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();

        assertThrows(SclDataException.class, () -> {
            repository.findSCLByUUID(type, uuid);
        });
    }

    @Test
    void createAndFind_WhenSclAdded_ThenScLStoredAndLastVersionCanBeFound() throws Exception {
        var version = new Version(1, 0, 0);
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(type, uuid, scl, version);

        var foundScl = repository.findSCLByUUID(type, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), foundScl.getHeader().getId());
        assertEquals(version.toString(), foundScl.getHeader().getVersion());
    }

    @Test
    void createAndFind_WhenMoreVersionOfSclAdded_ThenDefaultSCLLastVersionReturned() throws Exception {
        var version = new Version(1, 0, 0);
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(type, uuid, scl, version);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readSCL(uuid, nextVersion);
        repository.create(type, uuid, nextScl, nextVersion);

        var foundScl = repository.findSCLByUUID(type, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), foundScl.getHeader().getId());
        assertEquals(nextVersion.toString(), foundScl.getHeader().getVersion());
    }

    @Test
    void createAndFind_WhenMoreVersionOfSCLAdded_ThenSCLOldVersionCanBeFound() throws Exception {
        var version = new Version(1, 0, 0);
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(type, uuid, scl, version);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readSCL(uuid, nextVersion);
        repository.create(type, uuid, nextScl, nextVersion);

        var foundScl = repository.findSCLByUUID(type, uuid, version);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), foundScl.getHeader().getId());
        assertEquals(version.toString(), foundScl.getHeader().getVersion());
    }

    @Test
    void createAndDelete_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() throws Exception {
        var version = new Version(1, 0, 0);
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);

        repository.create(type, uuid, scl, version);
        var foundScl = repository.findSCLByUUID(type, uuid);
        assertNotNull(foundScl);
        assertEquals(scl.getHeader().getId(), foundScl.getHeader().getId());

        repository.delete(type, uuid, version);
        assertThrows(SclDataException.class, () -> {
            repository.findSCLByUUID(type, uuid);
        });
    }

    @Test
    void createAndDeleteAll_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() throws Exception {
        var version = new Version(1, 0, 0);
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);

        repository.create(type, uuid, scl, version);
        var foundScl = repository.findSCLByUUID(type, uuid);
        assertNotNull(foundScl);
        assertEquals(scl.getHeader().getId(), foundScl.getHeader().getId());
        assertEquals(scl.getHeader().getVersion(), foundScl.getHeader().getVersion());

        version = version.getNextVersion(ChangeSetType.MAJOR);
        repository.create(type, uuid, scl, version);
        foundScl = repository.findSCLByUUID(type, uuid);
        assertNotNull(foundScl);
        assertEquals(scl.getHeader().getId(), foundScl.getHeader().getId());
        assertEquals(scl.getHeader().getVersion(), foundScl.getHeader().getVersion());

        repository.delete(type, uuid);
        assertThrows(SclDataException.class, () -> {
            repository.findSCLByUUID(type, uuid);
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