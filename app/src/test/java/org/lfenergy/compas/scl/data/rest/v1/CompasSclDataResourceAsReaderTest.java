// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.v1.model.CreateRequest;
import org.lfenergy.compas.scl.data.rest.v1.model.UpdateRequest;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.xml.HistoryItem;
import org.lfenergy.compas.scl.data.xml.Item;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_NS_URI;
import static org.lfenergy.compas.scl.data.rest.Constants.*;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestHTTPEndpoint(CompasSclDataResource.class)
@TestSecurity(user = "test-reader", roles = {"SCD_" + READ_ROLE})
class CompasSclDataResourceAsReaderTest {

    @InjectMock
    private CompasSclDataService compasSclDataService;

    @Test
    void list_WhenCalled_ThenItemResponseRetrieved() {
        var type = SclFileType.SCD;
        var uuid = UUID.randomUUID();
        var name = "name";
        var version = "1.0.0";
        var labels = List.of("Label1");

        when(compasSclDataService.list(type))
                .thenReturn(Collections.singletonList(new Item(uuid.toString(), name, version, labels)));

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .when().get("/list")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath();
        assertEquals(uuid.toString(), xmlPath.get("ListResponse.Item[0].Id"));
        assertEquals(name, xmlPath.get("ListResponse.Item[0].Name"));
        assertEquals(version, xmlPath.get("ListResponse.Item[0].Version"));
        assertEquals(labels.get(0), xmlPath.get("ListResponse.Item[0].Label"));
        verify(compasSclDataService).list(type);
    }

    @Test
    void listVersionsByUUID_WhenCalled_ThenItemResponseRetrieved() {
        var type = SclFileType.SCD;
        var uuid = UUID.randomUUID();
        var name = "Name";
        var version = "1.0.0";

        when(compasSclDataService.listVersionsByUUID(type, uuid))
                .thenReturn(Collections.singletonList(new HistoryItem(uuid.toString(), name, version, null, null, null)));

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .when().get("/{" + ID_PATH_PARAM + "}/versions")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath();
        assertEquals(uuid.toString(), xmlPath.get("VersionsResponse.HistoryItem[0].Id"));
        assertEquals(name, xmlPath.get("VersionsResponse.HistoryItem[0].Name"));
        assertEquals(version, xmlPath.get("VersionsResponse.HistoryItem[0].Version"));
        verify(compasSclDataService).listVersionsByUUID(type, uuid);
    }

    @Test
    void findByUUID_WhenCalled_ThenSCLResponseRetrieved() throws IOException {
        var type = SclFileType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL();

        when(compasSclDataService.findByUUID(type, uuid)).thenReturn(scl);

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .when().get("/{" + ID_PATH_PARAM + "}")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath()
                .using(xmlPathConfig().declaredNamespace("scl", SCL_NS_URI));
        assertEquals(scl, xmlPath.get("GetWsResponse.SclData"));
        verify(compasSclDataService).findByUUID(type, uuid);
    }

    @Test
    void findByUUIDAndVersion_WhenCalled_ThenSCLResponseRetrieved() throws IOException {
        var type = SclFileType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL();
        var version = new Version(1, 2, 3);

        when(compasSclDataService.findByUUID(type, uuid, version)).thenReturn(scl);

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .pathParam(VERSION_PATH_PARAM, version.toString())
                .when().get("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath()
                .using(xmlPathConfig().declaredNamespace("scl", SCL_NS_URI));
        assertEquals(scl, xmlPath.get("GetWsResponse.SclData"));
        verify(compasSclDataService).findByUUID(type, uuid, version);
    }

    @Test
    void create_WhenCalled_ThenServiceCalledAndUUIDRetrieved() throws IOException {
        var type = SclFileType.SCD;
        var name = "StationName";
        var comment = "Some comments";
        var scl = readSCL();

        var request = new CreateRequest();
        request.setName(name);
        request.setComment(comment);
        request.setSclData(scl);

        given()
                .pathParam(TYPE_PATH_PARAM, type)
                .contentType(ContentType.XML)
                .body(request)
                .when().post()
                .then()
                .statusCode(403);

        verifyNoInteractions(compasSclDataService);
    }

    @Test
    void update_WhenCalled_ThenServiceCalledAndNewUUIDRetrieved() throws IOException {
        var uuid = UUID.randomUUID();
        var type = SclFileType.SCD;
        var changeSetType = ChangeSetType.MAJOR;
        var comment = "Some comments";
        var scl = readSCL();

        var request = new UpdateRequest();
        request.setChangeSetType(changeSetType);
        request.setComment(comment);
        request.setSclData(scl);

        given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .contentType(ContentType.XML)
                .body(request)
                .when().put("/{" + ID_PATH_PARAM + "}")
                .then()
                .statusCode(403);

        verifyNoInteractions(compasSclDataService);
    }

    @Test
    void deleteAll_WhenCalled_ThenServiceCalled() {
        var uuid = UUID.randomUUID();
        var type = SclFileType.SCD;

        doNothing().when(compasSclDataService).delete(type, uuid, false);

        given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .when().delete("/{" + ID_PATH_PARAM + "}")
                .then()
                .statusCode(403);

        verifyNoInteractions(compasSclDataService);
    }

    @Test
    void deleteVersion_WhenCalled_ThenServiceCalled() {
        var uuid = UUID.randomUUID();
        var type = SclFileType.SCD;
        var version = new Version(1, 2, 3);

        doNothing().when(compasSclDataService).deleteVersion(type, uuid, version, false);

        given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .pathParam(VERSION_PATH_PARAM, version.toString())
                .when().delete("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
                .then()
                .statusCode(403);

        verifyNoInteractions(compasSclDataService);
    }

    private String readSCL() throws IOException {
        try (var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.scd")) {
            assert inputStream != null;

            return new String(inputStream.readAllBytes());
        }
    }
}
