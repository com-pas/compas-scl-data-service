// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository;

import org.lfenergy.compas.commons.MarshallerWrapper;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.basex.BaseXClient;
import org.lfenergy.compas.scl.data.basex.BaseXClientFactory;
import org.lfenergy.compas.scl.data.model.SclType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ApplicationScoped
public class CompasDataBaseXRepository implements CompasDataRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasDataBaseXRepository.class);

    private final MarshallerWrapper marshallerWrapper;
    private final BaseXClientFactory baseXClientFactory;

    @Inject
    public CompasDataBaseXRepository(BaseXClientFactory baseXClientFactory) throws Exception {
        this.baseXClientFactory = baseXClientFactory;
        this.marshallerWrapper = new MarshallerWrapper.Builder().build();

        // At startup create all needed databases.
        for (SclType type : SclType.values()) {
            executeCommand(client -> {
                client.execute("CREATE DB " + type.name());
                return true;
            });
        }
    }

    @Override
    public SCL findSCLByUUID(SclType type, UUID uuid) {
        var result = executeQuery(type, "doc('".concat(type.name()).concat("/").concat(uuid.toString()).concat(".xml')"));
        return marshallerWrapper.unmarshall(result.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public UUID create(SclType type, SCL scl) {
        var uuid = UUID.randomUUID();
        var inputStream = new ByteArrayInputStream(marshallerWrapper.marshall(scl).getBytes(StandardCharsets.UTF_8));
        executeCommand(client -> {
            openDatabase(client, type);
            client.add(uuid.toString().concat(".xml"), inputStream);
            return true;
        });
        return uuid;
    }

    @Override
    public void delete(SclType type, UUID uuid) {
        executeCommand(client -> {
            openDatabase(client, type);
            client.execute("DELETE ".concat(uuid.toString().concat(".xml")));
            return true;
        });
    }

    private String executeQuery(SclType type, String query) {
        return executeCommand(client -> {
            var response = new StringBuilder();
            openDatabase(client, type);
            try (var queryToRun = client.query(query)) {
                while (queryToRun.more()) {
                    response.append(queryToRun.next());
                }
            }
            closeDatabase(client);
            return response.toString();
        });
    }

    private void openDatabase(BaseXClient client, SclType type) throws IOException {
        client.execute("OPEN ".concat(type.name()));
    }

    private void closeDatabase(BaseXClient client) throws IOException {
        client.execute("CLOSE");
    }

    private <R> R executeCommand(ClientExecutor<R> command) {
        try (var client = baseXClientFactory.createClient()) {
            return command.execute(client);
        } catch (IOException exception) {
            final var exceptionMessage = exception.getLocalizedMessage();
            LOGGER.error("executeCommand: {}", exceptionMessage);
            throw new SclDataException("Error executing command!", exception);
        }
    }

    private interface ClientExecutor<R> {
        R execute(BaseXClient client) throws IOException;
    }
}
