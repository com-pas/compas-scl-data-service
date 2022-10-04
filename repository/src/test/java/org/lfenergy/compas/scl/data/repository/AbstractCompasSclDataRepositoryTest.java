// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.repository;

import org.junit.jupiter.api.Test;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.*;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.HEADER_NOT_FOUND_ERROR_CODE;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.NO_DATA_FOUND_ERROR_CODE;
import static org.lfenergy.compas.scl.extensions.commons.CompasExtensionsConstants.COMPAS_SCL_FILE_TYPE_EXTENSION;
import static org.lfenergy.compas.scl.extensions.commons.CompasExtensionsConstants.COMPAS_SCL_NAME_EXTENSION;

public abstract class AbstractCompasSclDataRepositoryTest {
    protected static final SclFileType TYPE = SclFileType.SCD;
    protected static final String NAME_1 = "SCL-NAME1";
    protected static final String NAME_2 = "SCL-NAME2";
    protected static final String WHO = "User 1";
    protected static final List<String> LABELS = List.of("Label-1", "Label-2");

    // Use different types, so tests don't conflict with each other.
    protected static final SclFileType LIST1_TYPE = SclFileType.CID;
    protected static final SclFileType LIST2_TYPE = SclFileType.ICD;
    protected static final SclFileType LIST3_TYPE = SclFileType.IID;
    protected static final SclFileType LIST4_TYPE = SclFileType.ISD;

    protected final ElementConverter converter = new ElementConverter();
    protected final SclElementProcessor processor = new SclElementProcessor();

    protected abstract CompasSclDataRepository getRepository();

    @Test
    void list_WhenCalledOnEmptyDatabase_ThenNoRecordsReturned() {
        var items = getRepository().list(LIST1_TYPE);

        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    void list_WhenRecordAdded_ThenRecordFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readCompasSCL(uuid, version, NAME_1);
        getRepository().create(LIST2_TYPE, uuid, NAME_1, scl, version, WHO, LABELS);

        var items = getRepository().list(LIST2_TYPE);

        assertNotNull(items);
        assertEquals(1, items.size());
        var item = items.get(0);
        assertEquals(uuid.toString(), item.getId());
        assertEquals(NAME_1, item.getName());
        assertNotNull(item.getLabels());
        assertEquals(2, item.getLabels().size());
        assertEquals("Label-1", item.getLabels().get(0));
        assertEquals("Label-2", item.getLabels().get(1));
    }

    @Test
    void list_WhenTwoRecordAdded_ThenBothRecordsFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, version, NAME_1);
        getRepository().create(LIST3_TYPE, uuid, NAME_1, scl, version, WHO, LABELS);
        uuid = UUID.randomUUID();
        scl = readCompasSCL(uuid, version, NAME_2);
        getRepository().create(LIST3_TYPE, uuid, NAME_2, scl, version, WHO, LABELS);

        var items = getRepository().list(LIST3_TYPE);

        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    void list_WhenTwoVersionsOfARecordAdded_ThenLatestRecordFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readCompasSCL(uuid, version, NAME_1);
        getRepository().create(LIST4_TYPE, uuid, NAME_1, scl, version, WHO, LABELS);
        version = new Version(1, 1, 0);
        scl = readCompasSCL(uuid, version, NAME_2);
        getRepository().create(LIST4_TYPE, uuid, NAME_2, scl, version, WHO, LABELS);

        var items = getRepository().list(LIST4_TYPE);

        assertNotNull(items);
        assertEquals(1, items.size());
        var item = items.get(0);
        assertEquals(uuid.toString(), item.getId());
        assertEquals(version.toString(), item.getVersion());
        assertEquals(NAME_2, item.getName());
        assertNotNull(item.getLabels());
        assertEquals(2, item.getLabels().size());
        assertEquals("Label-1", item.getLabels().get(0));
        assertEquals("Label-2", item.getLabels().get(1));
    }

    @Test
    void listVersionsByUUID_WhenCalledWithUnknownID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        var items = getRepository().listVersionsByUUID(TYPE, uuid);

        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    void listVersionsByUUID_WhenTwoVersionsOfARecordAdded_ThenAllRecordAreFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, version, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);
        version = new Version(1, 1, 0);
        scl = readStandardSCL(uuid, version, NAME_2);
        getRepository().create(TYPE, uuid, NAME_2, scl, version, WHO, LABELS);

        var items = getRepository().listVersionsByUUID(TYPE, uuid);

        assertNotNull(items);
        assertEquals(2, items.size());
        var item = items.get(1);
        assertEquals(uuid.toString(), item.getId());
        assertEquals(version.toString(), item.getVersion());
        assertEquals(NAME_2, item.getName());

        // Hardcoded Header from SCD File, so not related to parameters passed to create.
        assertEquals("User 2", item.getWho());
        assertEquals("2021-12-15", item.getWhen());
        assertEquals("Some change 2", item.getWhat());
    }

    @Test
    void findByUUID_WhenCalledWithUnknownUUID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        var repository = getRepository();
        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> repository.findByUUID(TYPE, uuid));
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void findByUUID_WhenTwoVersionsOfARecordAdded_ThenLastRecordIsFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, version, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);
        version = new Version(1, 1, 0);
        scl = readStandardSCL(uuid, version, NAME_2);
        getRepository().create(TYPE, uuid, NAME_2, scl, version, WHO, LABELS);

        var foundScl = getRepository().findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(version.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void findByUUIDWithVersion_WhenCalledWithUnknownVersion_ThenExceptionIsThrown() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, version, NAME_1);

        var repository = getRepository();
        repository.create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);

        var unknownVersion = new Version(1, 1, 1);
        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> repository.findByUUID(TYPE, uuid, unknownVersion));
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void findByUUIDWithVersion_WhenTwoVersionsOfARecordAdded_ThenCorrectRecordIsFound() {
        var expectedVersion = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, expectedVersion, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, expectedVersion, WHO, LABELS);
        var version = new Version(1, 1, 0);
        scl = readStandardSCL(uuid, version, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);

        var foundScl = getRepository().findByUUID(TYPE, uuid, expectedVersion);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(expectedVersion.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void hasDuplicateSclName_WhenUsingSclNameThatHasNotBeenUsedYet_ThenNoDuplicateIsFound() {
        var expectedVersion = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, expectedVersion, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, expectedVersion, WHO, LABELS);

        assertFalse(getRepository().hasDuplicateSclName(TYPE, "Some other name"));
    }

    @Test
    void findMetaInfoByUUID_WhenTwoVersionsOfARecordAdded_ThenLastRecordIsFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, version, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);
        version = new Version(1, 1, 0);
        scl = readStandardSCL(uuid, version, NAME_2);
        getRepository().create(TYPE, uuid, NAME_2, scl, version, WHO, LABELS);

        var metaInfo = getRepository().findMetaInfoByUUID(TYPE, uuid);

        assertNotNull(metaInfo);
        assertEquals(uuid.toString(), metaInfo.getId());
        assertEquals(version.toString(), metaInfo.getVersion());
        assertEquals(NAME_2, metaInfo.getName());
    }

    @Test
    void findMetaInfoByUUID_WhenCalledWithUnknownUUID_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();

        var repository = getRepository();
        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> repository.findMetaInfoByUUID(TYPE, uuid));
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void createAndFind_WhenSclAdded_ThenScLStoredAndLastVersionCanBeFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, version, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);

        var foundScl = getRepository().findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(version.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void createAndFind_WhenMoreVersionOfSclAdded_ThenDefaultSCLLastVersionReturned() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, version, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readStandardSCL(uuid, nextVersion, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, nextScl, nextVersion, WHO, LABELS);

        var foundScl = getRepository().findByUUID(TYPE, uuid);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(nextVersion.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void createAndFind_WhenMoreVersionOfSCLAdded_ThenSCLOldVersionCanBeFound() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readStandardSCL(uuid, version, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);

        var nextVersion = version.getNextVersion(ChangeSetType.MAJOR);
        var nextScl = readStandardSCL(uuid, nextVersion, NAME_1);
        getRepository().create(TYPE, uuid, NAME_1, nextScl, nextVersion, WHO, LABELS);

        var foundScl = getRepository().findByUUID(TYPE, uuid, version);

        assertNotNull(foundScl);
        assertEquals(uuid.toString(), getIdFromHeader(foundScl));
        assertEquals(version.toString(), getVersionFromHeader(foundScl));
    }

    @Test
    void createAndDelete_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readCompasSCL(uuid, version, NAME_1);

        var repository = getRepository();
        repository.create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);
        var foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(getIdFromHeader(scl), getIdFromHeader(foundScl));

        repository.delete(TYPE, uuid, version);
        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> repository.findByUUID(TYPE, uuid));
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void createAndDeleteAll_WhenSclAddedAndDelete_ThenScLStoredAndRemoved() {
        var version = new Version(1, 0, 0);
        var uuid = UUID.randomUUID();
        var scl = readCompasSCL(uuid, version, NAME_1);

        var repository = getRepository();
        repository.create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);
        var foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(getIdFromHeader(scl), getIdFromHeader(foundScl));
        assertEquals(getVersionFromHeader(scl), getVersionFromHeader(foundScl));

        version = version.getNextVersion(ChangeSetType.MAJOR);
        repository.create(TYPE, uuid, NAME_1, scl, version, WHO, LABELS);
        foundScl = repository.findByUUID(TYPE, uuid);
        assertNotNull(foundScl);
        assertEquals(getIdFromHeader(scl), getIdFromHeader(foundScl));
        assertEquals(getVersionFromHeader(scl), getVersionFromHeader(foundScl));

        repository.delete(TYPE, uuid);
        var exception = assertThrows(CompasNoDataFoundException.class,
                () -> repository.findByUUID(TYPE, uuid));
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

    protected String readStandardSCL(UUID uuid, Version version, String name) {
        return readSCL("scl_standard.scd", uuid, version, name);
    }

    protected String readCompasSCL(UUID uuid, Version version, String name) {
        return readSCL("scl_compas.scd", uuid, version, name);
    }

    private String readSCL(String filename, UUID uuid, Version version, String name) {
        var inputStream = getClass().getResourceAsStream("/scl/" + filename);
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
