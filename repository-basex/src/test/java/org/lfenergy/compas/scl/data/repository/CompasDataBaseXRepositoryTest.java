// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.basex.BaseXServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.commons.MarshallerWrapper;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.basex.BaseXClientFactory;
import org.lfenergy.compas.scl.data.model.SclType;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.basex.BaseXServerUtil.createServer;
import static org.lfenergy.compas.scl.data.basex.BaseXServerUtil.getFreePortNumber;

@ExtendWith(MockitoExtension.class)
class CompasDataBaseXRepositoryTest {
    private static BaseXServer server;
    private static BaseXClientFactory factory;

    @BeforeAll
    static void beforeAll() throws Exception {
        var portNumber = getFreePortNumber();
        server = createServer(portNumber);
        factory = new BaseXClientFactory("localhost", portNumber, "admin", "admin");
    }

    @AfterAll
    static void afterAll() {
        server.stop();
    }

    @Test
    void find_WhenCalledWithUnknownUUID_ThenExceptionIsThrown() throws Exception {
        var repository = new CompasDataBaseXRepository(factory);
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();

        assertThrows(SclDataException.class, () -> {
            repository.findSCLByUUID(type, uuid);
        });
    }

    @Test
    void createAndFind_WhenSclAdded_ThenScLStoredAndCanBeFound() throws Exception {
        var repository = new CompasDataBaseXRepository(factory);
        var type = SclType.SCD;
        var scl = readSCL();

        var uuid = repository.create(type, scl);
        assertNotNull(uuid);

        var foundScl = repository.findSCLByUUID(type, uuid);
        assertNotNull(foundScl);
        assertEquals(scl.getHeader().getId(), foundScl.getHeader().getId());
    }

    @Test
    void createFindAndDelete_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() throws Exception {
        var repository = new CompasDataBaseXRepository(factory);
        var type = SclType.SCD;
        var scl = readSCL();

        var uuid = repository.create(type, scl);
        assertNotNull(uuid);

        var foundScl = repository.findSCLByUUID(type, uuid);
        assertNotNull(foundScl);
        assertEquals(scl.getHeader().getId(), foundScl.getHeader().getId());

        repository.delete(type, uuid);
        assertThrows(SclDataException.class, () -> {
            repository.findSCLByUUID(type, uuid);
        });
    }

    private SCL readSCL() throws Exception {
        var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.xml");
        assert inputStream != null;
        return new MarshallerWrapper.Builder().build().unmarshall(inputStream);
    }
}