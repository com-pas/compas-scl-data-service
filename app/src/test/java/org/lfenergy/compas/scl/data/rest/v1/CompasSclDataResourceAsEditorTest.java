// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.HistoryItem;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.v1.model.CreateRequest;
import org.lfenergy.compas.scl.data.rest.v1.model.UpdateRequest;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
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
@TestSecurity(user = "test-editor", roles = {"SCD_" + READ_ROLE, "SCD_" + CREATE_ROLE, "SCD_" + UPDATE_ROLE, "SCD_" + DELETE_ROLE})
@JwtSecurity(claims = {
        // Default the claim "name" is configured for Who, so we will set this claim for the test.
        @Claim(key = "name", value = CompasSclDataResourceAsEditorTest.USERNAME)
})
class CompasSclDataResourceAsEditorTest {
    public static final String USERNAME = "Test Editor";

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
        verify(compasSclDataService, times(1)).list(type);
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
        verify(compasSclDataService, times(1)).listVersionsByUUID(type, uuid);
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
        verify(compasSclDataService, times(1)).findByUUID(type, uuid);
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
        verify(compasSclDataService, times(1)).findByUUID(type, uuid, version);
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

        when(compasSclDataService.create(type, name, USERNAME, comment, scl)).thenReturn(scl);

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .contentType(ContentType.XML)
                .body(request)
                .when().post()
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertEquals(scl, response.xmlPath().getString("CreateWsResponse.SclData"));
        verify(compasSclDataService, times(1)).create(type, name, USERNAME, comment, scl);
    }

    @Test
    void create_WhenCalledWithInvalidName_ThenErrorReturned() throws IOException {
        var type = SclFileType.SCD;
        var invalidName = "tes/*.ssd.";
        var comment = "Some comments";
        var scl = readSCL();

        var request = new CreateRequest();
        request.setName(invalidName);
        request.setComment(comment);
        request.setSclData(scl);

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .contentType(ContentType.XML)
                .body(request)
                .when().post()
                .then()
                .statusCode(400)
                .extract()
                .response();

        var xpath = response.xmlPath();
        assertEquals("CORE-8000", xpath.getString("ErrorResponse.ErrorMessage.Code"));
        assertEquals("create.request.name", xpath.getString("ErrorResponse.ErrorMessage.Property"));
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

        when(compasSclDataService.update(type, uuid, changeSetType, USERNAME, comment, scl)).thenReturn(scl);

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .contentType(ContentType.XML)
                .body(request)
                .when().put("/{" + ID_PATH_PARAM + "}")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertEquals(scl, response.xmlPath().getString("CreateWsResponse.SclData"));
        verify(compasSclDataService, times(1)).update(type, uuid, changeSetType, USERNAME, comment, scl);
    }

    @Test
    void deleteAll_WhenCalled_ThenServiceCalled() {
        var uuid = UUID.randomUUID();
        var type = SclFileType.SCD;

        doNothing().when(compasSclDataService).delete(type, uuid);

        given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .when().delete("/{" + ID_PATH_PARAM + "}")
                .then()
                .statusCode(204);

        verify(compasSclDataService, times(1)).delete(type, uuid);
    }

    @Test
    void deleteVersion_WhenCalled_ThenServiceCalled() {
        var uuid = UUID.randomUUID();
        var type = SclFileType.SCD;
        var version = new Version(1, 2, 3);

        doNothing().when(compasSclDataService).delete(type, uuid, version);

        given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .pathParam(VERSION_PATH_PARAM, version.toString())
                .when().delete("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}")
                .then()
                .statusCode(204);

        verify(compasSclDataService, times(1)).delete(type, uuid, version);
    }

    private String readSCL() throws IOException {
        try (var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.scd")) {
            assert inputStream != null;

            return new String(inputStream.readAllBytes());
        }
    }
}
