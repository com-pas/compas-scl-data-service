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
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.*;
import static org.lfenergy.compas.scl.data.basex.BaseXServerUtil.createClientFactory;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.HEADER_NOT_FOUND_ERROR_CODE;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.NO_DATA_FOUND_ERROR_CODE;

@ExtendWith({MockitoExtension.class, BaseXServerJUnitExtension.class})
class CompasSclDataBaseXRepositoryTest {

    private static final SclType TYPE = SclType.SCD;
    private static final String NAME_1 = "SCL-NAME1";
    private static final String NAME_2 = "SCL-NAME2";
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
        repository = new CompasSclDataBaseXRepository(factory, marshaller);
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
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);

        var items = repository.list(TYPE);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(uuid.toString(), items.get(0).getId());
    }

    @Test
    void list_WhenTwoRecordAdded_ThenBothRecordsFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);
        uuid = UUID.randomUUID();
        scl = readSCL(uuid, version, NAME_2);
        repository.create(TYPE, uuid, name, scl, version);

        var items = repository.list(TYPE);

        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    void list_WhenTwoVersionsOfARecordAdded_ThenLatestRecordFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);
        version = new Version(1, 1, 0);
        scl = readSCL(uuid, version, NAME_2);
        repository.create(TYPE, uuid, name, scl, version);

        var items = repository.list(TYPE);

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(uuid.toString(), items.get(0).getId());
        assertEquals(version.toString(), items.get(0).getVersion());
        assertEquals(NAME_2, items.get(0).getName());
    }

    @Test
    void listVersionsByUUID_WhenCalledWithUnknownID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        var exception = assertThrows(CompasNoDataFoundException.class, () -> {
            repository.listVersionsByUUID(TYPE, uuid);
        });
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void listVersionsByUUID_WhenTwoVersionsOfARecordAdded_ThenAllRecordAreFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);
        version = new Version(1, 1, 0);
        scl = readSCL(uuid, version, NAME_2);
        repository.create(TYPE, uuid, name, scl, version);

        var items = repository.listVersionsByUUID(TYPE, uuid);

        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals(uuid.toString(), items.get(1).getId());
        assertEquals(version.toString(), items.get(1).getVersion());
        assertEquals(NAME_2, items.get(1).getName());
    }

    @Test
    void findByUUID_WhenCalledWithUnknownUUID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        var exception = assertThrows(CompasNoDataFoundException.class, () -> {
            repository.findByUUID(TYPE, uuid);
        });
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void findByUUID_WhenTwoVersionsOfARecordAdded_ThenLastRecordIsFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);
        version = new Version(1, 1, 0);
        scl = readSCL(uuid, version, NAME_2);
        repository.create(TYPE, uuid, name, scl, version);

        var foundScl = repository.findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(version.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void findByUUIDWithVersion_WhenCalledWithUnknownVersion_ThenExceptionIsThrown() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);

        var unknownVersion = new Version(1, 1, 1);
        var exception = assertThrows(CompasNoDataFoundException.class, () -> {
            repository.findByUUID(TYPE, uuid, unknownVersion);
        });
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void findByUUIDWithVersion_WhenTwoVersionsOfARecordAdded_ThenCorrectRecordIsFound() {
        var expectedVersion = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, expectedVersion, NAME_1);
        repository.create(TYPE, uuid, name, scl, expectedVersion);
        var version = new Version(1, 1, 0);
        scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);

        var foundScl = repository.findByUUID(TYPE, uuid, expectedVersion);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(expectedVersion.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void findMetaInfoByUUID_WhenTwoVersionsOfARecordAdded_ThenLastRecordIsFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);
        version = new Version(1, 1, 0);
        scl = readSCL(uuid, version, NAME_2);
        repository.create(TYPE, uuid, name, scl, version);

        var metaInfo = repository.findMetaInfoByUUID(TYPE, uuid);

        assertNotNull(metaInfo);
        assertEquals(uuid.toString(), metaInfo.getId());
        assertEquals(version.toString(), metaInfo.getVersion());
        assertEquals(NAME_2, metaInfo.getName());
    }

    @Test
    void findMetaInfoByUUID_WhenCalledWithUnknownUUID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        var exception = assertThrows(CompasNoDataFoundException.class, () -> {
            repository.findMetaInfoByUUID(TYPE, uuid);
        });
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void createAndFind_WhenSclAdded_ThenScLStoredAndLastVersionCanBeFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);

        var foundScl = repository.findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(version.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void createAndFind_WhenMoreVersionOfSclAdded_ThenDefaultSCLLastVersionReturned() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readSCL(uuid, nextVersion, NAME_1);
        repository.create(TYPE, uuid, name, nextScl, nextVersion);

        var foundScl = repository.findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(nextVersion.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void createAndFind_WhenMoreVersionOfSCLAdded_ThenSCLOldVersionCanBeFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);
        repository.create(TYPE, uuid, name, scl, version);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readSCL(uuid, nextVersion, NAME_1);
        repository.create(TYPE, uuid, name, nextScl, nextVersion);

        var foundScl = repository.findByUUID(TYPE, uuid, version);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(version.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void createAndDelete_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);

        repository.create(TYPE, uuid, name, scl, version);
        var foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(getIdFromHeader(scl), getIdFromHeader(foundScl));

        repository.delete(TYPE, uuid, version);
        var exception = assertThrows(CompasNoDataFoundException.class, () -> {
            repository.findByUUID(TYPE, uuid);
        });
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void createAndDeleteAll_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var name = "SCL-Name";
        var scl = readSCL(uuid, version, NAME_1);

        repository.create(TYPE, uuid, name, scl, version);
        var foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(getIdFromHeader(scl), getIdFromHeader(foundScl));
        assertEquals(getVersionFromHeader(scl), getVersionFromHeader(foundScl));

        version = version.getNextVersion(ChangeSetType.MAJOR);
        repository.create(TYPE, uuid, name, scl, version);
        foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(getIdFromHeader(scl), getIdFromHeader(foundScl));
        assertEquals(getVersionFromHeader(scl), getVersionFromHeader(foundScl));

        repository.delete(TYPE, uuid);
        var exception = assertThrows(CompasNoDataFoundException.class, () -> {
            repository.findByUUID(TYPE, uuid);
        });
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    private String getIdFromHeader(String sclData) {
        var scl = converter.convertToElement(sclData, SCL_ELEMENT_NAME, SCL_NS_URI);
        var header = processor.getSclHeader(scl)
                .orElseThrow(() ->
                        new CompasSclDataServiceException(HEADER_NOT_FOUND_ERROR_CODE, "Header not found in SCL!"));
        return processor.getAttributeValue(header, SCL_ID_ATTR)
                .orElse("");
    }

    private String getVersionFromHeader(String sclData) {
        var scl = converter.convertToElement(sclData, SCL_ELEMENT_NAME, SCL_NS_URI);
        var header = processor.getSclHeader(scl)
                .orElseThrow(() ->
                        new CompasSclDataServiceException(HEADER_NOT_FOUND_ERROR_CODE, "Header not found in SCL!"));
        return processor.getAttributeValue(header, SCL_VERSION_ATTR)
                .orElse("");
    }

    private String readSCL(UUID uuid, Version version, String name) {
        var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.scd");
        assert inputStream != null;

        var scl = converter.convertToElement(inputStream, SCL_ELEMENT_NAME, SCL_NS_URI);
        var header = processor.getSclHeader(scl).orElseGet(() -> processor.addSclHeader(scl));
        header.setAttribute(SCL_ID_ATTR, uuid.toString());
        header.setAttribute(SCL_VERSION_ATTR, version.toString());
        var compasPrivate = processor.addCompasPrivate(scl);
        processor.addCompasElement(compasPrivate, COMPAS_SCL_NAME_EXTENSION, name);
        processor.addCompasElement(compasPrivate, COMPAS_SCL_FILE_TYPE_EXTENSION, TYPE.name());
        return converter.convertToString(scl);
    }
}