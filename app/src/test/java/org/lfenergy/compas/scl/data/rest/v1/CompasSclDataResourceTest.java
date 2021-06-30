// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.commons.MarshallerWrapper;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.model.CreateRequest;
import org.lfenergy.compas.scl.data.rest.model.UpdateRequest;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import java.util.Collections;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.data.model.Constants.SCL_NAMESPACE;
import static org.lfenergy.compas.scl.data.rest.Constants.*;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestHTTPEndpoint(CompasSclDataResource.class)
class CompasSclDataResourceTest {
    @InjectMock
    private CompasSclDataService compasSclDataService;

    @Test
    void list_WhenCalled_ThenItemResponseRetrieved() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var name = "name";
        var version = "1.0.0";

        when(compasSclDataService.list(type))
                .thenReturn(Collections.singletonList(new Item(uuid.toString(), name, version)));

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
        verify(compasSclDataService, times(1)).list(type);
    }

    @Test
    void listVersionsByUUID_WhenCalled_ThenItemResponseRetrieved() {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var name = "Name";
        var version = "1.0.0";

        when(compasSclDataService.listVersionsByUUID(type, uuid))
                .thenReturn(Collections.singletonList(new Item(uuid.toString(), name, version)));

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .when().get("/{" + ID_PATH_PARAM + "}/versions")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath();
        assertEquals(uuid.toString(), xmlPath.get("ListResponse.Item[0].Id"));
        assertEquals(name, xmlPath.get("ListResponse.Item[0].Name"));
        assertEquals(version, xmlPath.get("ListResponse.Item[0].Version"));
        verify(compasSclDataService, times(1)).listVersionsByUUID(type, uuid);
    }

    @Test
    void findByUUID_WhenCalled_ThenSCLResponseRetrieved() throws Exception {
        var type = SclType.SCD;
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
                .using(xmlPathConfig().declaredNamespace("scl", SCL_NAMESPACE));
        assertEquals("HeaderID", xmlPath.get("GetResponse.scl:SCL.scl:Header.@id"));
        verify(compasSclDataService, times(1)).findByUUID(type, uuid);
    }

    @Test
    void findByUUIDAndVersion_WhenCalled_ThenSCLResponseRetrieved() throws Exception {
        var type = SclType.SCD;
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
                .using(xmlPathConfig().declaredNamespace("scl", SCL_NAMESPACE));
        assertEquals("HeaderID", xmlPath.get("GetResponse.scl:SCL.scl:Header.@id"));
        verify(compasSclDataService, times(1)).findByUUID(type, uuid, version);
    }

    @Test
    void findRawSCLByUUID_WhenCalledOnlySCL_ThenSCLRetrieved() throws Exception {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL();

        when(compasSclDataService.findByUUID(type, uuid)).thenReturn(scl);

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .when().get("/{" + ID_PATH_PARAM + "}/scl")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath()
                .using(xmlPathConfig().declaredNamespace("scl", SCL_NAMESPACE));
        assertEquals("HeaderID", xmlPath.get("scl:SCL.scl:Header.@id"));
        verify(compasSclDataService, times(1)).findByUUID(type, uuid);
    }

    @Test
    void findRawSCLByUUIDAndVersion_WhenCalled_ThenSCLRetrieved() throws Exception {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL();
        var version = new Version(1, 2, 3);

        when(compasSclDataService.findByUUID(type, uuid, version)).thenReturn(scl);

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .pathParam(VERSION_PATH_PARAM, version.toString())
                .when().get("/{" + ID_PATH_PARAM + "}/{" + VERSION_PATH_PARAM + "}/scl")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath()
                .using(xmlPathConfig().declaredNamespace("scl", SCL_NAMESPACE));
        assertEquals("HeaderID", xmlPath.get("scl:SCL.scl:Header.@id"));
        verify(compasSclDataService, times(1)).findByUUID(type, uuid, version);
    }

    @Test
    void create_WhenCalled_ThenServiceCalledAndUUIDRetrieved() throws Exception {
        var uuid = UUID.randomUUID();
        var type = SclType.SCD;
        var name = "StationName";
        var scl = readSCL();

        var request = new CreateRequest();
        request.setScl(scl);
        request.setName(name);

        when(compasSclDataService.create(eq(type), eq(name), any(SCL.class))).thenReturn(uuid);

        var response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .contentType(ContentType.XML)
                .body(request)
                .when().post()
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertEquals(uuid.toString(), response.xmlPath().getString("CreateResponse.Id"));
        verify(compasSclDataService, times(1)).create(eq(type), eq(name), any(SCL.class));
    }

    @Test
    void update_WhenCalled_ThenServiceCalledAndNewUUIDRetrieved() throws Exception {
        var uuid = UUID.randomUUID();
        var type = SclType.SCD;
        var changeSetType = ChangeSetType.MAJOR;
        var scl = readSCL();

        var request = new UpdateRequest();
        request.setScl(scl);
        request.setChangeSetType(changeSetType);

        doNothing().when(compasSclDataService).update(eq(type), eq(uuid), eq(changeSetType), any(SCL.class));

        given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(ID_PATH_PARAM, uuid)
                .contentType(ContentType.XML)
                .body(request)
                .when().put("/{" + ID_PATH_PARAM + "}")
                .then()
                .statusCode(204);

        verify(compasSclDataService, times(1)).update(eq(type), eq(uuid), eq(changeSetType), any(SCL.class));
    }

    @Test
    void deleteAll_WhenCalled_ThenServiceCalled() {
        var uuid = UUID.randomUUID();
        var type = SclType.SCD;

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
        var type = SclType.SCD;
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

    private SCL readSCL() throws Exception {
        var inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.scd");
        assert inputStream != null;
        return new MarshallerWrapper.Builder().build().unmarshall(inputStream);
    }
}
