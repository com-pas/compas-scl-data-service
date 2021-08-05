// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.basex.BaseXClientFactory;
import org.lfenergy.compas.scl.data.basex.BaseXServerJUnitExtension;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.*;
import static org.lfenergy.compas.scl.data.basex.BaseXServerUtil.createClientFactory;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.BASEX_QUERY_ERROR_CODE;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.HEADER_NOT_FOUND_ERROR_CODE;

@ExtendWith({MockitoExtension.class, BaseXServerJUnitExtension.class})
class CompasSclDataBaseXRepositoryTest {

    private static final SclType TYPE = SclType.SCD;
    private static BaseXClientFactory factory;

    private CompasSclDataBaseXRepository repository;

    private final ElementConverter converter = new ElementConverter();
    private final SclElementProcessor processor = new SclElementProcessor();
    private final SclDataModelMarshaller marshaller = new SclDataModelMarshaller();

    @BeforeAll
    static void beforeAll() {
        factory = createClientFactory(BaseXServerJUnitExtension.getPortNumber());
    }

    @BeforeEach
    void beforeEach() throws Exception {
        factory.createClient().executeXQuery("db:create('" + TYPE + "')");
        repository = new CompasSclDataBaseXRepository(factory, converter, marshaller);
    }

    @Test
    void list_WhenCalledOnEmptyDatabase_ThenNoRecordsReturned() {
        var items = repository.list(TYPE);

        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    void list_WhenRecordAdded_ThenRecordFound() {
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
    void list_WhenTwoRecordAdded_ThenBothRecordsFound() {
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
    void list_WhenTwoVersionsOfARecordAdded_ThenLatestRecordFound() {
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
    void listVersionsByUUID_WhenTwoVersionsOfARecordAdded_ThenAllRecordAreFound() {
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

        var exception = assertThrows(CompasSclDataServiceException.class, () -> {
            repository.findByUUID(TYPE, uuid);
        });
        assertEquals(BASEX_QUERY_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void createAndFind_WhenSclAdded_ThenScLStoredAndLastVersionCanBeFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var foundScl = repository.findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(version.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void createAndFind_WhenMoreVersionOfSclAdded_ThenDefaultSCLLastVersionReturned() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readSCL(uuid, nextVersion);
        repository.create(TYPE, uuid, nextScl, nextVersion);

        var foundScl = repository.findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(nextVersion.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void createAndFind_WhenMoreVersionOfSCLAdded_ThenSCLOldVersionCanBeFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);
        repository.create(TYPE, uuid, scl, version);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readSCL(uuid, nextVersion);
        repository.create(TYPE, uuid, nextScl, nextVersion);

        var foundScl = repository.findByUUID(TYPE, uuid, version);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(version.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void createAndDelete_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);

        repository.create(TYPE, uuid, scl, version);
        var foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(getIdFromHeader(scl), getIdFromHeader(foundScl));

        repository.delete(TYPE, uuid, version);
        var exception = assertThrows(CompasSclDataServiceException.class, () -> {
            repository.findByUUID(TYPE, uuid);
        });
        assertEquals(BASEX_QUERY_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void createAndDeleteAll_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readSCL(uuid, version);

        repository.create(TYPE, uuid, scl, version);
        var foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(getIdFromHeader(scl), getIdFromHeader(foundScl));
        assertEquals(getVersionFromHeader(scl), getVersionFromHeader(foundScl));

        version = version.getNextVersion(ChangeSetType.MAJOR);
        repository.create(TYPE, uuid, scl, version);
        foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(getIdFromHeader(scl), getIdFromHeader(foundScl));
        assertEquals(getVersionFromHeader(scl), getVersionFromHeader(foundScl));

        repository.delete(TYPE, uuid);
        var exception = assertThrows(CompasSclDataServiceException.class, () -> {
            repository.findByUUID(TYPE, uuid);
        });
        assertEquals(BASEX_QUERY_ERROR_CODE, exception.getErrorCode());
    }

    private String getIdFromHeader(Element scl) {
        var header = processor.getSclHeader(scl)
                .orElseThrow(() ->
                        new CompasSclDataServiceException(HEADER_NOT_FOUND_ERROR_CODE, "Header not found in SCL!"));
        return processor.getAttributeValue(header, SCL_HEADER_ID_ATTR)
                .orElse("");
    }

    private String getVersionFromHeader(Element scl) {
        var header = processor.getSclHeader(scl)
                .orElseThrow(() ->
                        new CompasSclDataServiceException(HEADER_NOT_FOUND_ERROR_CODE, "Header not found in SCL!"));
        return processor.getAttributeValue(header, SCL_HEADER_VERSION_ATTR)
                .orElse("");
    }

    private Element readSCL(UUID uuid, Version version) {
        var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.scd");
        assert inputStream != null;

        var scl = converter.convertToElement(inputStream, SCL_ELEMENT_NAME, SCL_NS_URI);
        var header = processor.getSclHeader(scl).orElseGet(() -> processor.addSclHeader(scl));
        header.setAttribute(SCL_HEADER_ID_ATTR, uuid.toString());
        header.setAttribute(SCL_HEADER_VERSION_ATTR, version.toString());
        return scl;
    }
}