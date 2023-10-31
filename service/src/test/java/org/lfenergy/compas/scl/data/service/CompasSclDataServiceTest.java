// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.core.commons.exception.CompasException;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.IHistoryItem;
import org.lfenergy.compas.scl.data.xml.HistoryItem;
import org.lfenergy.compas.scl.data.xml.SclMetaInfo;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.*;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.*;
import static org.lfenergy.compas.scl.extensions.commons.CompasExtensionsConstants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasSclDataServiceTest {
    private static final Version INITIAL_VERSION = new Version("1.0.0");
    private static final SclFileType SCL_TYPE = SclFileType.SCD;

    @Mock
    private CompasSclDataRepository compasSclDataRepository;

    private CompasSclDataService compasSclDataService;

    private final ElementConverter converter = new ElementConverter();
    private final SclElementProcessor processor = new SclElementProcessor();

    @BeforeEach
    void beforeEach() {
        compasSclDataService = new CompasSclDataService(compasSclDataRepository, converter, processor);
    }

    @Test
    void list_WhenCalled_ThenRepositoryIsCalled() {
        when(compasSclDataRepository.list(SCL_TYPE)).thenReturn(emptyList());

        var result = compasSclDataService.list(SCL_TYPE);

        assertNotNull(result);
        verify(compasSclDataRepository).list(SCL_TYPE);
    }

    @Test
    void listVersionsByUUID_WhenCalledAndRepositoryReturnItemList_ThenListIsReturned() {
        var uuid = UUID.randomUUID();
        var id = UUID.randomUUID().toString();

        var historyItem = mock(IHistoryItem.class);
        when(historyItem.getId()).thenReturn(id);
        List<IHistoryItem> expectedResult = List.of(historyItem);
        when(compasSclDataRepository.listVersionsByUUID(SCL_TYPE, uuid)).thenReturn(expectedResult);

        var result = compasSclDataService.listVersionsByUUID(SCL_TYPE, uuid);

        assertNotNull(result);
        assertEquals(expectedResult.size(), result.size());
        assertEquals(historyItem.getId(), result.get(0).getId());
        verify(compasSclDataRepository).listVersionsByUUID(SCL_TYPE, uuid);
    }

    @Test
    void listVersionsByUUID_WhenCalledAndRepositoryReturnsEmptyList_ThenExceptionIsThrown() {
        var uuid = UUID.randomUUID();
        when(compasSclDataRepository.listVersionsByUUID(SCL_TYPE, uuid)).thenReturn(emptyList());

        var exception = assertThrows(CompasNoDataFoundException.class, () -> {
            compasSclDataService.listVersionsByUUID(SCL_TYPE, uuid);
        });
        assertEquals(NO_DATA_FOUND_ERROR_CODE, exception.getErrorCode());
        verify(compasSclDataRepository).listVersionsByUUID(SCL_TYPE, uuid);
    }

    @Test
    void findByUUID_WhenCalledWithoutVersion_ThenRepositoryIsCalled() throws IOException {
        var uuid = UUID.randomUUID();
        when(compasSclDataRepository.findByUUID(SCL_TYPE, uuid)).thenReturn(readSCL("scl_test_file.scd"));

        var result = compasSclDataService.findByUUID(SCL_TYPE, uuid);

        assertNotNull(result);
        verify(compasSclDataRepository).findByUUID(SCL_TYPE, uuid);
    }

    @Test
    void findByUUID_WhenCalledWithVersion_ThenRepositoryIsCalled() throws IOException {
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);
        when(compasSclDataRepository.findByUUID(SCL_TYPE, uuid, version)).thenReturn(readSCL("scl_test_file.scd"));

        var result = compasSclDataService.findByUUID(SCL_TYPE, uuid, version);

        assertNotNull(result);
        verify(compasSclDataRepository).findByUUID(SCL_TYPE, uuid, version);
    }

    @Test
    void create_WhenCalledWithOutCompasExtension_ThenSCLReturnedWithCorrectCompasExtensionAndHistory() throws IOException {
        var name = "JUSTSOMENAME";
        var comment = "Some comments";
        var who = "User A";

        var scl = readSCL("scl_test_file.scd");

        when(compasSclDataRepository.hasDuplicateSclName(SCL_TYPE, name)).thenReturn(false);
        doNothing().when(compasSclDataRepository).create(eq(SCL_TYPE), any(UUID.class), eq(name), anyString(), eq(INITIAL_VERSION), eq(who), eq(emptyList()));

        scl = compasSclDataService.create(SCL_TYPE, name, who, comment, scl);

        assertNotNull(scl);
        assertCompasExtension(scl, name);
        assertHistoryItem(scl, 2, INITIAL_VERSION, comment);
        verify(compasSclDataRepository).create(eq(SCL_TYPE), any(UUID.class), eq(name), anyString(), eq(INITIAL_VERSION), eq(who), eq(emptyList()));
        verify(compasSclDataRepository).hasDuplicateSclName(SCL_TYPE, name);
    }

    @Test
    void create_WhenCalledWithCompasExtension_ThenSCLReturnedWithCorrectCompasExtensionAndHistory() throws IOException {
        var name = "JUSTSOMENAME";
        var comment = "";
        var who = "User A";

        var scl = readSCL("scl_test_file.scd");
        scl = createCompasPrivate(scl, "JUSTANOTHERNAME");

        when(compasSclDataRepository.hasDuplicateSclName(SCL_TYPE, name)).thenReturn(false);
        doNothing().when(compasSclDataRepository).create(eq(SCL_TYPE), any(UUID.class), eq(name), anyString(), eq(INITIAL_VERSION), eq(who), eq(emptyList()));

        scl = compasSclDataService.create(SCL_TYPE, name, who, comment, scl);

        assertNotNull(scl);
        assertCompasExtension(scl, name);
        assertHistoryItem(scl, 2, INITIAL_VERSION, comment);
        verify(compasSclDataRepository).create(eq(SCL_TYPE), any(UUID.class), eq(name), anyString(), eq(INITIAL_VERSION), eq(who), eq(emptyList()));
        verify(compasSclDataRepository).hasDuplicateSclName(SCL_TYPE, name);
    }

    @Test
    void create_WhenCalledWithDuplicateSclName_ThenCompasExceptionThrown() throws IOException {
        var name = "JUSTSOMENAME";
        var comment = "";
        var who = "User A";

        var scl = readSCL("scl_test_file.scd");

        when(compasSclDataRepository.hasDuplicateSclName(SCL_TYPE, name)).thenReturn(true);
        var exception = assertThrows(CompasException.class, () -> {
            compasSclDataService.create(SCL_TYPE, name, who, comment, scl);
        });
        assertEquals(DUPLICATE_SCL_NAME_ERROR_CODE, exception.getErrorCode());
        verify(compasSclDataRepository).hasDuplicateSclName(SCL_TYPE, name);
    }

    @Test
    void create_WhenCalledWithXMLStringWithoutSCL_ThenCompasExceptionThrown() {
        var name = "JUSTSOMENAME";
        var comment = "";
        var who = "User A";

        var scl = "<some-other-tag></some-other-tag>";

        var exception = assertThrows(CompasException.class, () -> {
            compasSclDataService.create(SCL_TYPE, name, who, comment, scl);
        });
        assertEquals(NO_SCL_ELEMENT_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void update_WhenCalledWithoutCompasElements_ThenSCLReturnedWithCorrectCompasExtensionAndHistory() throws IOException {
        var previousName = "Previous SCL Filename";
        var uuid = UUID.randomUUID();
        var changeSet = ChangeSetType.MAJOR;
        var who = "User A";
        var nextVersion = INITIAL_VERSION.getNextVersion(changeSet);

        var scl = readSCL("scl_test_file.scd");

        var sclMetaInfo = new SclMetaInfo(uuid.toString(), previousName, INITIAL_VERSION.toString());
        when(compasSclDataRepository.findMetaInfoByUUID(SCL_TYPE, uuid)).thenReturn(sclMetaInfo);
        doNothing().when(compasSclDataRepository).create(eq(SCL_TYPE), eq(uuid), eq(previousName), anyString(), eq(nextVersion), eq(who), eq(emptyList()));

        scl = compasSclDataService.update(SCL_TYPE, uuid, changeSet, who, null, scl);

        assertNotNull(scl);
        assertCompasExtension(scl, previousName);
        assertHistoryItem(scl, 4, nextVersion, null);
        verify(compasSclDataRepository).create(eq(SCL_TYPE), eq(uuid), eq(previousName), anyString(), eq(nextVersion), eq(who), eq(emptyList()));
        verify(compasSclDataRepository).findMetaInfoByUUID(SCL_TYPE, uuid);
        verify(compasSclDataRepository, never()).hasDuplicateSclName(SCL_TYPE, previousName);
    }

    @Test
    void update_WhenCalledWithCompasElementsAndNewName_ThenSCLReturnedWithCorrectCompasExtensionWithNewNameAndHistory() throws IOException {
        var previousName = "Previous SCL Filename";
        var newName = "New SCL Filename";
        var uuid = UUID.randomUUID();
        var changeSet = ChangeSetType.MAJOR;
        var who = "User A";
        var nextVersion = INITIAL_VERSION.getNextVersion(changeSet);

        var scl = readSCL("scl_test_file.scd");
        scl = createCompasPrivate(scl, newName);

        var sclMetaInfo = new SclMetaInfo(uuid.toString(), previousName, INITIAL_VERSION.toString());
        when(compasSclDataRepository.findMetaInfoByUUID(SCL_TYPE, uuid)).thenReturn(sclMetaInfo);
        doNothing().when(compasSclDataRepository).create(eq(SCL_TYPE), eq(uuid), eq(newName), anyString(), eq(nextVersion), eq(who), eq(emptyList()));
        when(compasSclDataRepository.hasDuplicateSclName(SCL_TYPE, newName)).thenReturn(false);

        scl = compasSclDataService.update(SCL_TYPE, uuid, changeSet, who, null, scl);

        assertNotNull(scl);
        assertCompasExtension(scl, newName);
        assertHistoryItem(scl, 4, nextVersion, null);
        verify(compasSclDataRepository).create(eq(SCL_TYPE), eq(uuid), eq(newName), anyString(), eq(nextVersion), eq(who), eq(emptyList()));
        verify(compasSclDataRepository).findMetaInfoByUUID(SCL_TYPE, uuid);
        verify(compasSclDataRepository).hasDuplicateSclName(SCL_TYPE, newName);
    }

    @Test
    void update_WhenCalledWithCompasElementsAndDuplicateNewName_ThenCompasExceptionThrown() throws IOException {
        var previousName = "Previous SCL Filename";
        var newName = "New SCL Filename";
        var uuid = UUID.randomUUID();
        var changeSet = ChangeSetType.MAJOR;
        var who = "User A";

        var scl = createCompasPrivate(readSCL("scl_test_file.scd"), newName);

        var sclMetaInfo = new SclMetaInfo(uuid.toString(), previousName, INITIAL_VERSION.toString());
        when(compasSclDataRepository.findMetaInfoByUUID(SCL_TYPE, uuid)).thenReturn(sclMetaInfo);
        when(compasSclDataRepository.hasDuplicateSclName(SCL_TYPE, newName)).thenReturn(true);

        var exception = assertThrows(CompasException.class, () -> {
            compasSclDataService.update(SCL_TYPE, uuid, changeSet, who, null, scl);
        });
        assertEquals(DUPLICATE_SCL_NAME_ERROR_CODE, exception.getErrorCode());
        verify(compasSclDataRepository).findMetaInfoByUUID(SCL_TYPE, uuid);
        verify(compasSclDataRepository).hasDuplicateSclName(SCL_TYPE, newName);
    }

    @Test
    void update_WhenCalledWithCompasElementsAndSameName_ThenSCLReturnedWithCorrectCompasExtensionWithSameNameAndHistory() throws IOException {
        var previousName = "Previous SCL Filename";
        var uuid = UUID.randomUUID();
        var changeSet = ChangeSetType.MAJOR;
        var who = "User A";
        var nextVersion = INITIAL_VERSION.getNextVersion(changeSet);

        var scl = readSCL("scl_test_file.scd");
        scl = createCompasPrivate(scl, previousName);

        var sclMetaInfo = new SclMetaInfo(uuid.toString(), previousName, INITIAL_VERSION.toString());
        when(compasSclDataRepository.findMetaInfoByUUID(SCL_TYPE, uuid)).thenReturn(sclMetaInfo);
        doNothing().when(compasSclDataRepository).create(eq(SCL_TYPE), eq(uuid), eq(previousName), anyString(), eq(nextVersion), eq(who), eq(emptyList()));

        scl = compasSclDataService.update(SCL_TYPE, uuid, changeSet, who, null, scl);

        assertNotNull(scl);
        assertCompasExtension(scl, previousName);
        assertHistoryItem(scl, 4, nextVersion, null);
        verify(compasSclDataRepository).create(eq(SCL_TYPE), eq(uuid), eq(previousName), anyString(), eq(nextVersion), eq(who), eq(emptyList()));
        verify(compasSclDataRepository).findMetaInfoByUUID(SCL_TYPE, uuid);
        verify(compasSclDataRepository, never()).hasDuplicateSclName(SCL_TYPE, previousName);
    }

    @Test
    void update_WhenCalledWithXMLStringWithoutSCL_ThenCompasExceptionThrown() {
        var uuid = UUID.randomUUID();
        var changeSet = ChangeSetType.MAJOR;
        var who = "User A";

        var scl = "<some-other-tag></some-other-tag>";

        var exception = assertThrows(CompasException.class, () -> {
            compasSclDataService.update(SCL_TYPE, uuid, changeSet, who, null, scl);
        });
        assertEquals(NO_SCL_ELEMENT_FOUND_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void delete_WhenCalledWithoutVersion_ThenRepositoryIsCalled() {
        var uuid = UUID.randomUUID();

        doNothing().when(compasSclDataRepository).delete(SCL_TYPE, uuid);

        compasSclDataService.delete(SCL_TYPE, uuid);

        verify(compasSclDataRepository).delete(SCL_TYPE, uuid);
    }

    @Test
    void delete_WhenCalledWithVersion_ThenRepositoryIsCalled() {
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);

        doNothing().when(compasSclDataRepository).delete(SCL_TYPE, uuid, version);

        compasSclDataService.delete(SCL_TYPE, uuid, version);

        verify(compasSclDataRepository).delete(SCL_TYPE, uuid, version);
    }

    @Test
    void validateLabels_WhenCalledWithoutLabels_ThenNoExceptionOccurs() throws IOException {
        var scl = converter.convertToElement(readSCL("scl_no_labels.scd"), SCL_ELEMENT_NAME, SCL_NS_URI);

        compasSclDataService.validateLabels(scl);
    }

    @Test
    void validateLabels_WhenCalledWithValidLabels_ThenNoExceptionOccurs() throws IOException {
        var scl = converter.convertToElement(readSCL("scl_valid_labels.scd"), SCL_ELEMENT_NAME, SCL_NS_URI);

        compasSclDataService.validateLabels(scl);
    }

    @Test
    void validateLabels_WhenCalledWithInvalidLabels_ThenNoExceptionOccurs() throws IOException {
        var scl = converter.convertToElement(readSCL("scl_invalid_labels.scd"), SCL_ELEMENT_NAME, SCL_NS_URI);

        var exception = assertThrows(CompasSclDataServiceException.class,
                () -> compasSclDataService.validateLabels(scl));
        assertEquals(INVALID_LABEL_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void validateLabels_WhenCalledWithTooManyLabels_ThenNoExceptionOccurs() throws IOException {
        var scl = converter.convertToElement(readSCL("scl_too_many_labels.scd"), SCL_ELEMENT_NAME, SCL_NS_URI);

        var exception = assertThrows(CompasSclDataServiceException.class,
                () -> compasSclDataService.validateLabels(scl));
        assertEquals(TOO_MANY_LABEL_ERROR_CODE, exception.getErrorCode());
    }

    @Test
    void validateLabel_WhenValidLabelPassed_TheReturnTrue() {
        assertTrue(compasSclDataService.validateLabel(createLabelElement("ValidLabel")));
        assertTrue(compasSclDataService.validateLabel(createLabelElement("ValidLabel-1")));
        assertTrue(compasSclDataService.validateLabel(createLabelElement("ValidLabel_1")));
    }

    @Test
    void validateLabel_WhenInvalidLabelPassed_TheReturnFalse() {
        assertFalse(compasSclDataService.validateLabel(createLabelElement("")));
        assertFalse(compasSclDataService.validateLabel(createLabelElement("1-Label")));
        assertFalse(compasSclDataService.validateLabel(createLabelElement("Label*")));
    }

    private Element createLabelElement(String validLabel) {
        var element = mock(Element.class);
        when(element.getTextContent()).thenReturn(validLabel);
        return element;
    }

    private void assertCompasExtension(String sclData, String name) {
        var scl = converter.convertToElement(sclData, SCL_ELEMENT_NAME, SCL_NS_URI);
        var compasPrivate = processor.getCompasPrivate(scl);
        assertTrue(compasPrivate.isPresent());

        var nameElement = processor.getChildNodeByName(compasPrivate.get(), COMPAS_SCL_NAME_EXTENSION,
                COMPAS_EXTENSION_NS_URI);
        assertTrue(nameElement.isPresent());
        assertEquals(name, nameElement.get().getTextContent());

        var typeElement = processor.getChildNodeByName(compasPrivate.get(), COMPAS_SCL_FILE_TYPE_EXTENSION,
                COMPAS_EXTENSION_NS_URI);
        assertTrue(typeElement.isPresent());
        assertEquals(SCL_TYPE.toString(), typeElement.get().getTextContent());
    }

    private void assertHistoryItem(String sclData, int expectedHItems, Version version, String comment) {
        var scl = converter.convertToElement(sclData, SCL_ELEMENT_NAME, SCL_NS_URI);
        var header = processor.getSclHeader(scl);
        assertTrue(header.isPresent());

        var history = processor.getChildNodeByName(header.get(), SCL_HISTORY_ELEMENT_NAME, SCL_NS_URI);
        assertTrue(history.isPresent());

        var items = processor.getChildNodesByName(history.get(), SCL_HITEM_ELEMENT_NAME, SCL_NS_URI);
        assertEquals(expectedHItems, items.size());
        // The last item should be the one added.
        var item = items.get(items.size() - 1);
        assertEquals(version.toString(), item.getAttribute(SCL_VERSION_ATTR));
        if (comment != null && !comment.isEmpty()) {
            assertTrue(item.getAttribute(SCL_WHAT_ATTR).contains(comment));
        }
    }

    private String createCompasPrivate(String sclData, String sclName) {
        var scl = converter.convertToElement(sclData, SCL_ELEMENT_NAME, SCL_NS_URI);
        var compasPrivate = processor.addCompasPrivate(scl);
        processor.addCompasElement(compasPrivate, COMPAS_SCL_NAME_EXTENSION, sclName);
        processor.addCompasElement(compasPrivate, COMPAS_SCL_FILE_TYPE_EXTENSION, SCL_TYPE.name());
        return converter.convertToString(scl);
    }

    private String readSCL(String filename) throws IOException {
        try (var inputStream = getClass().getResourceAsStream("/scl/" + filename)) {
            assert inputStream != null;
            return new String(inputStream.readAllBytes());
        }
    }
}