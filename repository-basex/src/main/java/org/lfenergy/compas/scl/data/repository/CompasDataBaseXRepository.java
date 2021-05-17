// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lfenergy.compas.commons.MarshallerWrapper;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.basex.BaseXClient;
import org.lfenergy.compas.scl.data.model.SclType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ApplicationScoped
public class CompasDataBaseXRepository implements CompasDataRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasDataBaseXRepository.class);

    private final MarshallerWrapper marshallerWrapper;

    private final String baseXHost;
    private final Integer baseXPort;
    private final String baseXUsername;
    private final String baseXPassword;

    @Inject
    public CompasDataBaseXRepository(@ConfigProperty(name = "basex.host") String baseXHost,
                                     @ConfigProperty(name = "basex.port") Integer baseXPort,
                                     @ConfigProperty(name = "basex.username") String baseXUsername,
                                     @ConfigProperty(name = "basex.password") String baseXPassword
                                    ) throws Exception {
        this.baseXHost = baseXHost;
        this.baseXPort = baseXPort;
        this.baseXUsername = baseXUsername;
        this.baseXPassword = baseXPassword;

        this.marshallerWrapper = new MarshallerWrapper.Builder().build();

        // At startup create all needed databases.
        for (SclType type: SclType.values()) {
            executeCommand((client) -> {
                client.execute("CREATE DB " + type.name());
                return true;
            });
        }
    }

    @Override
    public SCL findSCLByUUID(SclType type, UUID uuid) {
        String result = executeQuery(type, "doc('".concat(type.name()).concat("/").concat(uuid.toString()).concat(".xml')"));
        return marshallerWrapper.unmarshall(result.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public UUID create(SclType type, SCL scl) {
        UUID uuid = UUID.randomUUID();
        InputStream inputStream = new ByteArrayInputStream(marshallerWrapper.marshall(scl).getBytes(StandardCharsets.UTF_8));
        executeCommand((client) -> {
            client.execute("OPEN ".concat(type.name()));
            client.add(uuid.toString().concat(".xml"), inputStream);
            return true;
        });
        return uuid;
    }

    @Override
    public void delete(SclType type, UUID uuid) {
        executeCommand((client) -> {
            client.execute("OPEN ".concat(type.name()));
            client.execute("DELETE ".concat(uuid.toString().concat(".xml")));
            return true;
        });
    }

    private String executeQuery(SclType type, String query) {
        return executeCommand((client) -> {
            StringBuilder response = new StringBuilder();
            client.execute("OPEN ".concat(type.name()));
            try (BaseXClient.Query queryToRun = client.query(query)) {
                while (queryToRun.more()) {
                    response.append(queryToRun.next());
                }
            }
            client.execute("CLOSE");
            return response.toString();
        });
    }

    private <R> R executeCommand(ClientCommand<R> command) {
        try (BaseXClient client = new BaseXClient(baseXHost, baseXPort, baseXUsername, baseXPassword)) {
            return command.execute(client);
        } catch (IOException exception) {
            final String exceptionMessage = exception.getLocalizedMessage();
            LOGGER.error("executeCommand: {}", exceptionMessage);
            throw new SclDataException("Error executing command!", exception);
        }
    }

    private interface ClientCommand<R> {
        R execute(BaseXClient client) throws IOException;
    }
}
