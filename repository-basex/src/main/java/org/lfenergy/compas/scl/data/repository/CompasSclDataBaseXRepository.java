// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.lfenergy.compas.commons.MarshallerWrapper;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.basex.BaseXClient;
import org.lfenergy.compas.scl.data.basex.BaseXClientFactory;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

@ApplicationScoped
public class CompasSclDataBaseXRepository implements CompasSclDataRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasSclDataBaseXRepository.class);

    private final MarshallerWrapper marshallerWrapper;
    private final BaseXClientFactory baseXClientFactory;

    @Inject
    public CompasSclDataBaseXRepository(BaseXClientFactory baseXClientFactory) throws Exception {
        this.baseXClientFactory = baseXClientFactory;
        this.marshallerWrapper = new MarshallerWrapper.Builder().build();

        // At startup create all needed databases.
        Arrays.stream(SclType.values()).forEach(type ->
                executeCommand(client -> {
                    client.executeXQuery(
                            "if (not(db:exists('" + type + "'))) " +
                                    " then db:create('" + type + "')");
                    return true;
                }));
    }

    @Override
    public SCL findSCLByUUID(SclType type, UUID uuid) {
        // This find method always searches for the latest version.
        // Retrieve all versions using db:list-details function.
        // Sort the result descending, this way the last version is the first.
        // Use this path to retrieve the document with the doc function.
        var result = executeQuery(type,
                "doc(concat('" + type + "/'," +
                        "  (for $resource in db:list-details('" + type + "', '" + uuid + "') " +
                        "     order by $resource/text() descending" +
                        "     return $resource" +
                        "  )[1]/text()" +
                        " ))");
        return marshallerWrapper.unmarshall(result.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public SCL findSCLByUUID(SclType type, UUID uuid, Version version) {
        // This find method always searches for a specific version.
        var result = executeQuery(type, "doc('" + type + "/" + createDocumentPath(uuid, version) + "')");
        return marshallerWrapper.unmarshall(result.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void create(SclType type, UUID uuid, SCL scl, Version version) {
        var inputStream = new ByteArrayInputStream(marshallerWrapper.marshall(scl).getBytes(StandardCharsets.UTF_8));
        executeCommand(client -> {
            openDatabase(client, type);
            client.add(createDocumentPath(uuid, version) + "/scl.xml", inputStream);
            closeDatabase(client);
            return true;
        });
    }

    @Override
    public void delete(SclType type, UUID uuid) {
        executeCommand(client -> {
            client.executeXQuery("db:delete('" + type + "', '" + uuid + "')");
            return true;
        });
    }

    @Override
    public void delete(SclType type, UUID uuid, Version version) {
        executeCommand(client -> {
            client.executeXQuery("db:delete('" + type + "', '" + createDocumentPath(uuid, version) + "')");
            return true;
        });
    }

    private String createDocumentPath(UUID uuid, Version version) {
        return uuid
                + "/" + version.getMajorVersion()
                + "/" + version.getMinorVersion()
                + "/" + version.getPatchVersion();
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
        client.execute("OPEN " + type);
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
