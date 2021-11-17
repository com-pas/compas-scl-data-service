// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgres;

import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclMetaInfo;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class CompasSclDataPostgreSQLRepository implements CompasSclDataRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasSclDataPostgreSQLRepository.class);

    public static final String NOT_IMPLEMENTED = "Not implemented!";

    private DataSource dataSource;

    @Inject
    public CompasSclDataPostgreSQLRepository(DataSource dataSource) {
        LOGGER.info("Starting PostgreSQL Repository.");
        this.dataSource = dataSource;
    }

    @Override
    @Transactional(SUPPORTS)
    public List<Item> list(SclType type) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    @Transactional(SUPPORTS)
    public List<Item> listVersionsByUUID(SclType type, UUID id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    @Transactional(SUPPORTS)
    public String findByUUID(SclType type, UUID id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    @Transactional(SUPPORTS)
    public String findByUUID(SclType type, UUID id, Version version) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    @Transactional(SUPPORTS)
    public SclMetaInfo findMetaInfoByUUID(SclType type, UUID id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    @Transactional(REQUIRED)
    public void create(SclType type, UUID id, String name, String scl, Version version) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    @Transactional(REQUIRED)
    public void delete(SclType type, UUID id) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    @Override
    @Transactional(REQUIRED)
    public void delete(SclType type, UUID id, Version version) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}
