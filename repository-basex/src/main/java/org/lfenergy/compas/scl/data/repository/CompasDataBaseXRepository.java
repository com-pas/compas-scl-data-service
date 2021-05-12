// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.basex.BaseXClient;
import org.lfenergy.compas.scl.data.model.SclType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.UUID;

@ApplicationScoped
public class CompasDataBaseXRepository implements CompasDataRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasDataBaseXRepository.class);

    private final String baseXHost;
    private Integer baseXPort;
    private String baseXUsername;
    private String baseXPassword;

    public CompasDataBaseXRepository(@ConfigProperty(name = "basex.host") String baseXHost,
                                     @ConfigProperty(name = "basex.port") Integer baseXPort,
                                     @ConfigProperty(name = "basex.username") String baseXUsername,
                                     @ConfigProperty(name = "basex.password") String baseXPassword) {
        this.baseXHost = baseXHost;
        this.baseXPort = baseXPort;
        this.baseXUsername = baseXUsername;
        this.baseXPassword = baseXPassword;

        // At startup create all needed databases.
        for (SclType type: SclType.values()) {
            executeCommand("CREATE DB " + type.name());
        }
    }

    @Override
    public SCL findSCLByUUID(SclType type, UUID uuid) {
        return new SCL();
    }

    @Override
    public UUID create(SclType type, String name, SCL scl) {
        return UUID.randomUUID();
    }

    @Override
    public void delete(SclType type, UUID uuid) {
    }

    private String executeCommand(String command) {
        try (BaseXClient client = new BaseXClient(baseXHost, baseXPort, baseXUsername, baseXPassword)) {
            return client.execute(command);
        } catch (IOException exception) {
            final String exceptionMessage = exception.getLocalizedMessage();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("executeCommand: {}", exceptionMessage, exception);
            } else {
                LOGGER.error("executeCommand: {}", exceptionMessage);
            }
            return exceptionMessage;
        }
    }

    private String executeQuery(String database, String query) {
        try (BaseXClient client = new BaseXClient(baseXHost, baseXPort, baseXUsername, baseXPassword)) {
            StringBuilder response = new StringBuilder();
            client.execute("open ".concat(database));
            try (BaseXClient.Query queryToRun = client.query(query)) {
                while (queryToRun.more()) {
                    response.append(queryToRun.next());
                }
            }
            client.execute("close");
            return response.toString();
        } catch (IOException exception) {
            final String exceptionMessage = exception.getLocalizedMessage();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("executeQuery: {}", exceptionMessage, exception);
            } else {
                LOGGER.error("executeQuery: {}", exceptionMessage);
            }
            return exceptionMessage;
        }
    }
}
