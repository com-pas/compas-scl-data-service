// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.commons.MarshallerWrapper;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.rest.model.CreateRequest;
import org.lfenergy.compas.scl.data.rest.model.UpdateRequest;
import org.lfenergy.compas.scl.data.service.CompasDataService;

import java.io.InputStream;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.data.rest.Constants.*;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestHTTPEndpoint(CompasSclDataResource.class)
class CompasSclDataResourceTest {
    @InjectMock
    private CompasDataService compasDataService;

    @Test
    void findSCLByUUID_WhenCalled_ThenSCLResponseRetrieved() throws Exception {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL();

        when(compasDataService.findSCLByUUID(type, uuid)).thenReturn(scl);

        Response response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(UUID_PATH_PARAM, uuid)
                .when().get("/{" + UUID_PATH_PARAM + "}")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath()
                .using(xmlPathConfig().declaredNamespace("scl", SCL_NAMESPACE));
        assertEquals("HeaderID", xmlPath.get("GetResponse.scl:SCL.scl:Header.@id"));
        verify(compasDataService, times(1)).findSCLByUUID(type, uuid);
    }

    @Test
    void findSCLByUUID_WhenCalledOnlySCL_ThenSCLRetrieved() throws Exception {
        var type = SclType.SCD;
        var uuid = UUID.randomUUID();
        var scl = readSCL();

        when(compasDataService.findSCLByUUID(type, uuid)).thenReturn(scl);

        Response response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(UUID_PATH_PARAM, uuid)
                .when().get("/{" + UUID_PATH_PARAM + "}/scl")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath()
                .using(xmlPathConfig().declaredNamespace("scl", SCL_NAMESPACE));
        assertEquals("HeaderID", xmlPath.get("scl:SCL.scl:Header.@id"));
        verify(compasDataService, times(1)).findSCLByUUID(type, uuid);
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

        when(compasDataService.create(eq(type), eq(name), any(SCL.class))).thenReturn(uuid);

        Response response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .contentType(ContentType.XML)
                .body(request)
                .when().post()
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertEquals(uuid.toString(), response.xmlPath().getString("CreateResponse.Uuid"));
        verify(compasDataService, times(1)).create(eq(type), eq(name), any(SCL.class));
    }

    @Test
    void update_WhenCalled_ThenServiceCalledAndNewUUIDRetrieved() throws Exception {
        var uuid = UUID.randomUUID();
        var newUuid = UUID.randomUUID();
        var type = SclType.SCD;
        var changeSetType = ChangeSetType.MAJOR;
        var scl = readSCL();

        var request = new UpdateRequest();
        request.setScl(scl);
        request.setChangeSetType(changeSetType);

        when(compasDataService.update(eq(type), eq(uuid), eq(changeSetType), any(SCL.class))).thenReturn(newUuid);

        Response response = given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(UUID_PATH_PARAM, uuid)
                .contentType(ContentType.XML)
                .body(request)
                .when().put("/{" + UUID_PATH_PARAM + "}")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertEquals(newUuid.toString(), response.xmlPath().getString("UpdateResponse.Uuid"));
        verify(compasDataService, times(1)).update(eq(type), eq(uuid), eq(changeSetType), any(SCL.class));
    }

    @Test
    void delete_WhenCalled_ThenServiceCalled() {
        var uuid = UUID.randomUUID();
        var type = SclType.SCD;

        doNothing().when(compasDataService).delete(type, uuid);

        given()
                .pathParam(TYPE_PATH_PARAM, type)
                .pathParam(UUID_PATH_PARAM, uuid)
                .when().delete("/{" + UUID_PATH_PARAM + "}")
                .then()
                .statusCode(204);

        verify(compasDataService, times(1)).delete(type, uuid);
    }

    private SCL readSCL() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/scl/icd_import_ied_test.xml");
        assert inputStream != null;
        return new MarshallerWrapper.Builder().build().unmarshall(inputStream);
    }
}
