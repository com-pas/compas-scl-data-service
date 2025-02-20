// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.UniAssertSubscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.core.commons.exception.CompasException;
import org.lfenergy.compas.scl.data.dto.ResourceMetaData;
import org.lfenergy.compas.scl.data.dto.TypeEnum;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.*;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;
import org.lfenergy.compas.scl.data.xml.SclMetaInfo;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
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

    @Mock
    private ICompasSclDataArchivingService compasSclDataArchivingService;

    @Mock
    private CompasSclDataArchivingServiceImpl compasSclDataArchivingServiceImpl;

    private CompasSclDataService compasSclDataService;

    private final ElementConverter converter = new ElementConverter();
    private final SclElementProcessor processor = new SclElementProcessor();

    @BeforeEach
    void beforeEach() throws NoSuchFieldException, IllegalAccessException {
        compasSclDataService = new CompasSclDataService(compasSclDataRepository, converter, processor);
        Field archivingServiceField = compasSclDataService.getClass().getDeclaredField("archivingService");
        archivingServiceField.setAccessible(true);
        archivingServiceField.set(compasSclDataService, compasSclDataArchivingServiceImpl);
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
    void create_WhenCalledWithFeatureHistoryEnabled_ThenCreateHistoryVersionEntriesInRepository() throws IOException {
        var name = "JUSTSOMENAME";
        var comment = "Some comments";
        var who = "User A";

        var scl = readSCL("scl_test_file.scd");

        when(compasSclDataRepository.hasDuplicateSclName(SCL_TYPE, name)).thenReturn(false);
        doNothing().when(compasSclDataRepository).create(eq(SCL_TYPE), any(UUID.class), eq(name), anyString(), eq(INITIAL_VERSION), eq(who), eq(emptyList()));

        scl = compasSclDataService.create(SCL_TYPE, name, who, comment, scl, true);

        assertNotNull(scl);
        assertCompasExtension(scl, name);
        assertHistoryItem(scl, 2, INITIAL_VERSION, comment);
        verify(compasSclDataRepository).create(eq(SCL_TYPE), any(UUID.class), eq(name), anyString(), eq(INITIAL_VERSION), eq(who), eq(emptyList()));
        verify(compasSclDataRepository).hasDuplicateSclName(SCL_TYPE, name);
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

        compasSclDataService.delete(SCL_TYPE, uuid, false);

        verify(compasSclDataRepository).delete(SCL_TYPE, uuid);
    }

    @Test
    void deleteVersion_WhenCalledWithVersion_ThenRepositoryIsCalled() {
        var uuid = UUID.randomUUID();
        var version = new Version(1, 0, 0);

        doNothing().when(compasSclDataRepository).deleteVersion(SCL_TYPE, uuid, version);

        compasSclDataService.deleteVersion(SCL_TYPE, uuid, version, false);

        verify(compasSclDataRepository).deleteVersion(SCL_TYPE, uuid, version);
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

    @Test
    void listHistory_WhenCalled_ThenReturnHistoryItemWithLatestVersion() {
        String itemId = UUID.randomUUID().toString();
        IHistoryMetaItem historyItem = new HistoryMetaItem(
            itemId,
            "TestItem1",
            "1.0.1",
            "IID",
            "User1",
            "Comment after update",
            null,
            null,
            false,
            true,
            false
        );
        when(compasSclDataRepository.listHistory()).thenReturn(List.of(historyItem));
        List<IHistoryMetaItem> items = compasSclDataService.listHistory();

        verify(compasSclDataRepository).listHistory();
        assertEquals(1, items.size());
    }

    @Test
    void listHistory_WhenCalledWithId_ThenReturnHistoryItemWithLatestVersion() {
        UUID itemId = UUID.randomUUID();
        IHistoryMetaItem historyItem = new HistoryMetaItem(
            itemId.toString(),
            "TestItem1",
            "1.0.1",
            "IID",
            "User1",
            "Comment after update",
            null,
            null,
            false,
            true,
            false
        );
        when(compasSclDataRepository.listHistory(itemId)).thenReturn(List.of(historyItem));
        List<IHistoryMetaItem> items = compasSclDataService.listHistory(itemId);

        verify(compasSclDataRepository).listHistory(itemId);
        assertEquals(1, items.size());
    }

    @Test
    void listHistory_WhenCalledWithSearchParameters_ThenReturnHistoryItemWithLatestVersion() {
        UUID itemId = UUID.randomUUID();
        IHistoryMetaItem historyItem = new HistoryMetaItem(
            itemId.toString(),
            "TestItem1",
            "1.0.1",
            "IID",
            "User1",
            "Comment after update",
            null,
            null,
            false,
            true,
            false
        );
        SclFileType searchFileType = SclFileType.IID;
        when(compasSclDataRepository.listHistory(searchFileType, "TestItem1", "User1", null, null)).thenReturn(List.of(historyItem));
        List<IHistoryMetaItem> items = compasSclDataService.listHistory(searchFileType, "TestItem1", "User1", null, null);

        verify(compasSclDataRepository).listHistory(searchFileType, "TestItem1", "User1", null, null);
        assertEquals(1, items.size());
    }

    @Test
    void listHistoryVersionsByUUID_WhenCalledWithId_ThenReturnHistoryItems() {
        UUID itemId = UUID.randomUUID();
        IHistoryMetaItem historyItem = new HistoryMetaItem(
            itemId.toString(),
            "TestItem1",
            "1.0.0",
            "IID",
            "User1",
            null,
            null,
            null,
            false,
            true,
            false
        );
        IHistoryMetaItem historyItem1 = new HistoryMetaItem(
            itemId.toString(),
            "TestItem1",
            "1.0.1",
            "IID",
            "User1",
            "Comment after update",
            null,
            OffsetDateTime.now(),
            false,
            true,
            false
        );
        when(compasSclDataRepository.listHistoryVersionsByUUID(itemId)).thenReturn(List.of(historyItem, historyItem1));
        List<IHistoryMetaItem> items = compasSclDataService.listHistoryVersionsByUUID(itemId);

        verify(compasSclDataRepository).listHistoryVersionsByUUID(itemId);
        assertEquals(2, items.size());
    }

    @Test
    void findLocationByUUID_WhenCalledWithId_ThenReturnLocation() {
        UUID locationId = UUID.randomUUID();
        ILocationMetaItem expectedLocation = new LocationMetaItem(
            locationId.toString(),
            "locationKey",
            "locationName",
            "some description",
            0
        );
        when(compasSclDataRepository.findLocationByUUID(locationId)).thenReturn(expectedLocation);

        ILocationMetaItem actualLocation =  compasSclDataService.findLocationByUUID(locationId);

        verify(compasSclDataRepository).findLocationByUUID(locationId);
        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    void listLocations_WhenCalledWithPageAndPageSize_ThenReturnLocations() {
        ILocationMetaItem location1 = new LocationMetaItem(UUID.randomUUID().toString(), "locationKey1", "locationName1", "", 0);
        ILocationMetaItem location2 = new LocationMetaItem(UUID.randomUUID().toString(), "locationKey2", "locationName2", "", 0);
        ILocationMetaItem location3 = new LocationMetaItem(UUID.randomUUID().toString(), "locationKey3", "locationName3", "", 0);

        List<ILocationMetaItem> expectedLocations = List.of(location1, location2, location3);
        when(compasSclDataRepository.listLocations(0, 25)).thenReturn(expectedLocations);

        List<ILocationMetaItem> actualLocations = compasSclDataService.listLocations(0, 25);

        verify(compasSclDataRepository).listLocations(0, 25);
        assertEquals(expectedLocations.size(), actualLocations.size());
    }

    @Test
    void createLocation_WhenCalled_ThenReturnCreatedLocation() {
        ILocationMetaItem expectedLocation = new LocationMetaItem(UUID.randomUUID().toString(), "locationKey", "locationName", null, 0);
        when(compasSclDataRepository.createLocation("locationKey", "locationName", null)).thenReturn(expectedLocation);
        when(compasSclDataRepository.hasDuplicateLocationValues("locationKey", "locationName")).thenReturn(false);
        ILocationMetaItem actualLocation = compasSclDataService.createLocation("locationKey", "locationName", null);

        verify(compasSclDataRepository, times(1)).createLocation(any(), any(), any());
        verify(compasSclDataArchivingServiceImpl, times(1)).createLocation(any());
        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    void deleteLocation_WhenCalled_ThenLocationIsDeleted() {
        UUID locationId = UUID.randomUUID();
        ILocationMetaItem locationToRemove = new LocationMetaItem(locationId.toString(), "locationKey", "locationName", null, 0);
        when(compasSclDataRepository.findLocationByUUID(locationId)).thenReturn(locationToRemove);

        compasSclDataService.deleteLocation(locationId);

        verify(compasSclDataRepository).deleteLocationTags(locationToRemove);
        verify(compasSclDataRepository).deleteLocation(locationId);
    }

    @Test
    void deleteLocation_WhenResourceIsAssigned_ThenExceptionIsThrown() {
        UUID locationId = UUID.randomUUID();
        ILocationMetaItem locationToRemove = new LocationMetaItem(locationId.toString(), "locationKey", "locationName", null, 1);
        when(compasSclDataRepository.findLocationByUUID(locationId)).thenReturn(locationToRemove);

        assertThrows(CompasSclDataServiceException.class, () -> compasSclDataService.deleteLocation(locationId));

        verify(compasSclDataRepository, times(1)).findLocationByUUID(locationId);
        verify(compasSclDataRepository, times(0)).deleteLocationTags(locationToRemove);
        verify(compasSclDataRepository, times(0)).deleteLocation(locationId);
    }

    @Test
    void updateLocation_WhenCalled_ThenLocationIsUpdated() {
        UUID locationId = UUID.randomUUID();
        ILocationMetaItem expectedLocation = new LocationMetaItem(locationId.toString(), "locationKey", "locationName", "updatedDescription", 0);
        when(compasSclDataRepository.updateLocation(locationId, "locationKey", "locationName", "updatedDescription")).thenReturn(expectedLocation);

        ILocationMetaItem actualLocation = compasSclDataService.updateLocation(locationId, "locationKey", "locationName", "updatedDescription");

        verify(compasSclDataRepository, times(1)).updateLocation(locationId, "locationKey", "locationName", "updatedDescription");
        assertEquals(expectedLocation, actualLocation);
    }

    @Test
    void assignResourceToLocation_WhenCalled_ThenLocationIsAssigned() {
        UUID locationId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        ILocationMetaItem locationItem = new LocationMetaItem(locationId.toString(), "locationKey", "locationName", null, 0);
        HistoryMetaItem historyMetaItem = new HistoryMetaItem(
            resourceId.toString(),
            "sclDataName",
            "1.0.0",
            null,
            "someUser",
            "IID",
            "locationName",
            null,
            true,
            true,
            false
        );
        when(compasSclDataRepository.findLocationByUUID(locationId)).thenReturn(locationItem);
        when(compasSclDataRepository.listHistory(resourceId)).thenReturn(List.of(historyMetaItem));

        compasSclDataService.assignResourceToLocation(locationId, resourceId);
        verify(compasSclDataRepository, times(1)).assignResourceToLocation(locationId, resourceId);
        verify(compasSclDataRepository, times(1)).listHistory(resourceId);
    }

    @Test
    void assignResourceToLocation_WhenResourceHasLocationAssignedWithArchivedItems_ThenNewLocationIsAssignedAndArchivedItemsAreMoved() {
        UUID resourceId = UUID.randomUUID();
        UUID newLocationId = UUID.randomUUID();
        ILocationMetaItem newLocationItem = new LocationMetaItem(newLocationId.toString(), "newLocationKey", "newLocationName", null, 0);
        IHistoryMetaItem historyMetaItem = new HistoryMetaItem(
            resourceId.toString(),
            "sclDataName",
            "1.0.0",
            null,
            "someUser",
            "IID",
            "locationName",
            null,
            true,
            true,
            false
        );
        when(compasSclDataRepository.findLocationByUUID(newLocationId)).thenReturn(newLocationItem);
        when(compasSclDataRepository.listHistory(resourceId)).thenReturn(List.of(historyMetaItem));

        compasSclDataService.assignResourceToLocation(newLocationId, resourceId);

        verify(compasSclDataRepository, times(1)).assignResourceToLocation(newLocationId, resourceId);
        verify(compasSclDataRepository, times(1)).assignResourceToLocation(newLocationId, resourceId);
        verify(compasSclDataRepository, times(1)).assignResourceToLocation(newLocationId, resourceId);
    }

    @Test
    void unassignResourcesFromLocation_WhenCalled_ThenLocationIsUnassigned() {
        UUID locationId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();

        ILocationMetaItem assignedLocation = new LocationMetaItem(locationId.toString(), "locationKey", "locationName", null, 1);
        HistoryMetaItem historyMetaItem = new HistoryMetaItem(
            resourceId.toString(),
            "sclDataName",
            "1.0.0",
            null,
            "someUser",
            "IID",
            "locationName",
            null,
            true,
            true,
            false
        );

        when(compasSclDataRepository.listHistory(resourceId)).thenReturn(List.of(historyMetaItem));
        when(compasSclDataRepository.findLocationByUUID(locationId)).thenReturn(assignedLocation);

        compasSclDataService.unassignResourceFromLocation(locationId, resourceId);

        verify(compasSclDataRepository, times(1)).findLocationByUUID(locationId);
        verify(compasSclDataRepository, times(1)).listHistory(resourceId);
        verify(compasSclDataRepository, times(1)).unassignResourceFromLocation(locationId, resourceId);
    }

    @Test
    void unassignResourcesFromLocation_WhenLocationNotFound_ThenExceptionIsThrown() {
        UUID locationId = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();

        when(compasSclDataRepository.findLocationByUUID(locationId)).thenThrow(new CompasNoDataFoundException(String.format("Unable to find Location with id %s.", locationId)));

        assertThrows(CompasNoDataFoundException.class, () -> compasSclDataService.unassignResourceFromLocation(locationId, resourceId));

        verify(compasSclDataRepository, times(1)).findLocationByUUID(locationId);
        verify(compasSclDataRepository, times(0)).unassignResourceFromLocation(locationId, resourceId);
        verify(compasSclDataRepository, times(0)).searchArchivedResource(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void archiveResource_WhenCalled_ThenResourceIsArchived() {
        UUID resourceId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        String version = "1.0.0";
        String author = "someUser";
        String approver = "someOtherUser";
        String contentType = "application/pdf";
        String filename = "test.pdf";
        File file = new File("test.pdf");
        try {
            file.createNewFile();
            when(compasSclDataRepository.findLocationByUUID(locationId)).thenReturn(
                new LocationMetaItem(
                    locationId.toString(),
                    "locationKey",
                    "locationName",
                    null,
                    0
                )
            );
            when(compasSclDataRepository.listHistoryVersionsByUUID(resourceId)).thenReturn(
                List.of(
                    new HistoryMetaItem(
                        resourceId.toString(),
                        "someName",
                        version,
                        null,
                        author,
                        null,
                        locationId.toString(),
                        OffsetDateTime.now(),
                        false,
                        true,
                        false
                    )
                )
            );
            when(compasSclDataRepository.archiveResource(resourceId, new Version(version), author, approver, contentType, filename))
                .thenReturn(
                    new ArchivedSclResourceMetaItem(
                        UUID.randomUUID().toString(),
                        "someName",
                        version,
                        author,
                        approver,
                        null,
                        contentType,
                        locationId.toString(),
                        List.of(),
                        null,
                        OffsetDateTime.now(),
                        null,
                        null
                    )
                );

            when(compasSclDataArchivingServiceImpl.archiveData(any(), any(), any(), any(), any())).thenReturn(Uni.createFrom().item(
                new ResourceMetaData(
                    TypeEnum.RESOURCE,
                    UUID.randomUUID(),
                    locationId.toString(),
                    List.of()
                )
            ));

            UniAssertSubscriber<IAbstractArchivedResourceMetaItem> result = compasSclDataService.archiveResource(resourceId, version, author, approver, contentType, filename, file).subscribe().withSubscriber(UniAssertSubscriber.create());

            result.assertCompleted();
            verify(compasSclDataRepository, times(1)).archiveResource(resourceId, new Version(version), author, approver, contentType, filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
        }
    }

    @Test
    void archiveResource_WhenFileIsNull_ThenFileIsNotWritten() {
        UUID resourceId = UUID.randomUUID();
        String version = "1.0.0";
        String author = "someUser";
        String approver = "someOtherUser";
        String contentType = "application/pdf";
        String filename = "test.pdf";
        IHistoryMetaItem historyMetaItem = new HistoryMetaItem(
            resourceId.toString(),
            "sclDataName",
            version,
            null,
            "someUser",
            "IID",
            "locationName",
            null,
            true,
            true,
            false
        );

        UUID locationId = UUID.randomUUID();
        when(compasSclDataRepository.listHistoryVersionsByUUID(resourceId)).thenReturn(List.of(historyMetaItem));
        when(compasSclDataRepository.findLocationByUUID(UUID.fromString(locationId.toString()))).thenReturn(
            new LocationMetaItem(
                locationId.toString(),
                "someKey",
                "someName",
                "someDescription",
                1
            )
        );
        when(compasSclDataRepository.archiveResource(resourceId, new Version(version), author, approver, contentType, filename))
            .thenReturn(
                new ArchivedSclResourceMetaItem(
                    UUID.randomUUID().toString(),
                    "someName",
                    version,
                    author,
                    approver,
                    null,
                    contentType,
                    locationId.toString(),
                    List.of(),
                    null,
                    OffsetDateTime.now(),
                    null,
                    null
                )
            );

        when(compasSclDataArchivingServiceImpl.archiveData(any(), any(), any(), any(), any())).thenReturn(Uni.createFrom().item(
            new ResourceMetaData(
                TypeEnum.RESOURCE,
                UUID.randomUUID(),
                locationId.toString(),
                List.of()
            )
        ));

        UniAssertSubscriber<IAbstractArchivedResourceMetaItem> cut = compasSclDataService.archiveResource(resourceId, version, author, approver, contentType, filename, null).subscribe().withSubscriber(UniAssertSubscriber.create());

        cut.assertCompleted();

        verify(compasSclDataRepository, times(1)).archiveResource(resourceId, new Version(version), author, approver, contentType, filename);
        verify(compasSclDataArchivingService, times(0)).archiveSclData(any(), any(), any(), any());
    }

    @Test
    void archiveSclResource_WhenCalled_ThenResourceIsArchived() {
        UUID resourceId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        String version = "1.0.0";
        String approver = "someOtherUser";
        ArchivedSclResourceMetaItem archivedResource = new ArchivedSclResourceMetaItem(
            UUID.randomUUID().toString(),
            "someName",
            version,
            "someAuthor",
            approver,
            "IID",
            null,
            locationId.toString(),
            List.of(),
            null,
            OffsetDateTime.now(),
            null,
            null
        );
        String sclData = "someData";

        when(compasSclDataRepository.archiveSclResource(resourceId, new Version(version), approver)).thenReturn(archivedResource);
        when(compasSclDataRepository.findByUUID(resourceId, new Version(version))).thenReturn(sclData);
        when(compasSclDataRepository.findLocationByUUID(locationId)).thenReturn(
            new LocationMetaItem(
                locationId.toString(),
                "locationKey",
                "locationName",
                null,
                0
            )
        );

        when(compasSclDataArchivingServiceImpl.archiveSclData(any(), any(), any(), any())).thenReturn(Uni.createFrom().item(
            new ResourceMetaData(
                TypeEnum.RESOURCE,
                UUID.randomUUID(),
                locationId.toString(),
                List.of()
            )
        ));

        UniAssertSubscriber<IAbstractArchivedResourceMetaItem> result = compasSclDataService.archiveSclResource(resourceId, new Version(version), approver).subscribe().withSubscriber(UniAssertSubscriber.create());

        result.assertCompleted();

        verify(compasSclDataRepository, times(1)).archiveSclResource(resourceId, new Version(version), approver);
        verify(compasSclDataRepository, times(1)).findByUUID(resourceId, new Version(version));
        verify(compasSclDataArchivingServiceImpl, times(1)).archiveSclData(resourceId, archivedResource, "locationName", sclData);
    }

    @Test
    void getArchivedResourceHistory_whenCalled_ThenResourceHistoryIsReturned() {
        UUID archivedResourceId = UUID.randomUUID();
        IArchivedResourceVersion archivedResourceVersion = new ArchivedResourceVersion(
            archivedResourceId.toString(),
            "archivedResourceName",
            "1.0.0",
            "someLocation",
            "some note",
            "someAuthor",
            "someApprover",
            "someType",
            null,
            null,
            List.of(),
            null,
            OffsetDateTime.now(),
            null,
            true
        );
        IArchivedResourcesHistoryMetaItem archivedResourceHistory = new ArchivedResourcesHistoryMetaItem(List.of(archivedResourceVersion));

        when(compasSclDataRepository.searchArchivedResourceHistory(archivedResourceId)).thenReturn(archivedResourceHistory);

        IArchivedResourcesHistoryMetaItem actualMetaItems = compasSclDataService.getArchivedResourceHistory(archivedResourceId);

        assertFalse(actualMetaItems.getVersions().isEmpty());
        verify(compasSclDataRepository, times(1)).searchArchivedResourceHistory(archivedResourceId);
    }

    @Test
    void searchArchivedResources_whenCalled_ThenResourcesAreReturned() {
        UUID archivedResourceId = UUID.randomUUID();
        IAbstractArchivedResourceMetaItem archivedResourceVersion = new ArchivedSclResourceMetaItem(
            archivedResourceId.toString(),
            "archivedResourceName",
            "1.0.0",
            "someAuthor",
            "someApprover",
            "someType",
            null,
            "someLocation",
            List.of(),
            null,
            OffsetDateTime.now(),
            null,
            null
        );
        IArchivedResourcesMetaItem archivedResources = new ArchivedResourcesMetaItem(List.of(archivedResourceVersion));
        when(compasSclDataRepository.searchArchivedResource(archivedResourceId)).thenReturn(archivedResources);

        IArchivedResourcesMetaItem actualMetaItems = compasSclDataService.searchArchivedResources(archivedResourceId);

        assertFalse(actualMetaItems.getResources().isEmpty());
        verify(compasSclDataRepository, times(1)).searchArchivedResource(archivedResourceId);
    }

    @Test
    void searchArchivedResources_whenCalledWithMultipleParameters_ThenResourcesAreReturned() {
        UUID archivedResourceId = UUID.randomUUID();
        UUID archivedResourceId1 = UUID.randomUUID();
        IAbstractArchivedResourceMetaItem archivedResourceVersion = new ArchivedSclResourceMetaItem(
            archivedResourceId.toString(),
            "archivedResourceName",
            "1.0.0",
            "someAuthor",
            "someApprover",
            "someType",
            null,
            "someLocation",
            List.of(),
            null,
            OffsetDateTime.now(),
            null,
            null
        );
        IAbstractArchivedResourceMetaItem archivedResourceVersion1 = new ArchivedReferencedResourceMetaItem(
            archivedResourceId1.toString(),
            "test.pdf",
            "1.0.0",
            "someAuthor",
            "someApprover",
            "someType",
            "application/pdf",
            "someLocation",
            List.of(),
            null,
            OffsetDateTime.now(),
            null
        );
        IArchivedResourcesMetaItem archivedResources1 = new ArchivedResourcesMetaItem(List.of(archivedResourceVersion, archivedResourceVersion1));

        when(compasSclDataRepository.searchArchivedResource("someLocation", null, "someApprover", null, null, null, null, null))
            .thenReturn(archivedResources1);

        IArchivedResourcesMetaItem actualMetaItem = compasSclDataService.searchArchivedResources("someLocation", null, "someApprover", null, null, null, null, null);

        assertFalse(actualMetaItem.getResources().isEmpty());
        verify(compasSclDataRepository, times(1)).searchArchivedResource("someLocation", null, "someApprover", null, null, null, null, null);
    }

    @Test
    void searchArchivedResources_whenCalledWithWrongSclFileTypeParameter_ThenThrowsException() {
        assertThrows(CompasSclDataServiceException.class, () -> compasSclDataService.searchArchivedResources("someLocation", null, "someApprover", "asdf", null, null, null, null));
        verify(compasSclDataRepository, times(0)).searchArchivedResource("someLocation", null, "someApprover", "asdf", null, null, null, null);
    }

    @Test
    void searchArchivedResources_whenCalledValidSclFileTypeParameter_ThenResourcesAreReturned() {
        UUID archivedResourceId = UUID.randomUUID();
        IAbstractArchivedResourceMetaItem archivedResourceVersion = new ArchivedSclResourceMetaItem(
            archivedResourceId.toString(),
            "archivedResourceName",
            "1.0.0",
            "someAuthor",
            "someApprover",
            "IID",
            null,
            "someLocation",
            List.of(),
            null,
            OffsetDateTime.now(),
            null,
            null
        );
        IArchivedResourcesMetaItem archivedResources1 = new ArchivedResourcesMetaItem(List.of(archivedResourceVersion));

        when(compasSclDataRepository.searchArchivedResource("someLocation", null, "someApprover", "IID", null, null, null, null))
            .thenReturn(archivedResources1);

        IArchivedResourcesMetaItem actualMetaItem = compasSclDataService.searchArchivedResources("someLocation", null, "someApprover", "IID", null, null, null, null);

        assertFalse(actualMetaItem.getResources().isEmpty());
        verify(compasSclDataRepository, times(1)).searchArchivedResource("someLocation", null, "someApprover", "IID", null, null, null, null);
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