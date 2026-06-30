// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.entities.PluginsCustomResource;
import org.lfenergy.compas.scl.data.service.CompasPluginsResourceService;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.lfenergy.compas.scl.data.rest.Constants.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


@QuarkusTest
@TestSecurity(user = "test-user", roles = {"SCD_" + READ_ROLE, "SCD_" + DELETE_ROLE, PLUGINS_RESOURCES + "_" + READ_ROLE, PLUGINS_RESOURCES + "_" + CREATE_ROLE, PLUGINS_RESOURCES + "_" + DELETE_ROLE})
@TestHTTPEndpoint(CompasPluginsResource.class)
class CompasPluginsResourceGetDataTest {

    private static final String PLUGIN = "engineering-wizard";
    private static final String TYPE = "process";

    @InjectMock
    private CompasPluginsResourceService compasPluginsResourceService;

    @Test
    void getPluginResourceById_WhenCalledWithValidUUID_ThenReturnsResource() {
        var resource = createTestResource();
        when(compasPluginsResourceService.findByIdAndType(resource.id, resource.type))
                .thenReturn(resource);

        given()
        .when()
            .get("/plugins/{plugin}/types/{type}/{id}", PLUGIN, TYPE, resource.id)
        .then()
            .statusCode(200)
            .body("id", equalTo(resource.id.toString()))
            .body("type", equalTo(TYPE))
            .body("tenant", equalTo(resource.tenant))
            .body("name", equalTo(resource.name))
            .body("description", equalTo(resource.description))
            .body("contentType", equalTo(resource.contentType))
            .body("version", equalTo(resource.version))
            .body("dataCompatibilityVersion", equalTo(resource.dataCompatibilityVersion))
            .body("uploadedAt", not(empty()));
    }

    @Test
    void getPluginResourceById_WhenCalledWithNonExistingUUID_ThenReturnsNotFoundError() {
        var nonExistingId = java.util.UUID.randomUUID();
        when(compasPluginsResourceService.findByIdAndType(nonExistingId, PLUGIN + "_" + TYPE))
                .thenThrow(CompasNoDataFoundException.class);

        given()
        .when()
            .get("/plugins/{plugin}/types/{type}/{id}", PLUGIN, TYPE, nonExistingId)
        .then()
            .statusCode(404);
    }

    @Test
    void getLatestPluginResourcesByType_WhenCalledWithValidType_ThenReturnsLatestResources() {
        var resource = createTestResource();
        var secondResource = createTestResource();
        secondResource.name = "another-resource";
        when(compasPluginsResourceService.findLatestByType(resource.type))
                .thenReturn(List.of(resource, secondResource));

        given()
        .when()
            .get("/plugins/{plugin}/types/{type}/latest", PLUGIN, TYPE)
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].id", equalTo(resource.id.toString()))
            .body("[0].type", equalTo(TYPE))
            .body("[0].tenant", equalTo(resource.tenant))
            .body("[0].name", equalTo(resource.name))
            .body("[0].description", equalTo(resource.description))
            .body("[0].contentType", equalTo(resource.contentType))
            .body("[0].version", equalTo(resource.version))
            .body("[0].dataCompatibilityVersion", equalTo(resource.dataCompatibilityVersion))
            .body("[0].uploadedAt", not(empty()))
            .body("[1].id", equalTo(secondResource.id.toString()))
            .body("[1].name", equalTo(secondResource.name));
    }

    @Test
    void getLatestPluginResourcesByType_WhenCalledWithNonExistingType_ThenReturnsNotFoundError() {
        when(compasPluginsResourceService.findLatestByType(PLUGIN + "_" + TYPE))
                .thenThrow(CompasNoDataFoundException.class);

        given()
        .when()
            .get("/plugins/{plugin}/types/{type}/latest", PLUGIN, TYPE)
        .then()
            .statusCode(404);
    }

    @Test
    void deletePluginResourcesByType_WhenCalledWithExistingType_ThenReturnsNoContent() {
        given()
        .when()
            .delete("/plugins/{plugin}/types/{type}", PLUGIN, TYPE)
        .then()
            .statusCode(204);
    }

    @Test
    void deletePluginResourcesByType_WhenCalledWithNonExistingType_ThenReturnsNotFoundError() {
        doThrow(CompasNoDataFoundException.class)
                .when(compasPluginsResourceService).deleteByType(PLUGIN + "_" + TYPE);

        given()
        .when()
            .delete("/plugins/{plugin}/types/{type}", PLUGIN, TYPE)
        .then()
            .statusCode(404);
    }

    @Test
    void getLatestPluginResourceByName_WhenCalledWithValidArguments_ThenReturnsResource() {
        var resource = createTestResource();
        when(compasPluginsResourceService.findLatestByTypeAndName(resource.type, resource.name))
                .thenReturn(resource);

        given()
        .when()
            .get("/plugins/{plugin}/types/{type}/resources/{name}/latest", PLUGIN, TYPE, resource.name)
        .then()
            .statusCode(200)
            .body("id", equalTo(resource.id.toString()))
            .body("type", equalTo(TYPE))
            .body("name", equalTo(resource.name))
            .body("version", equalTo(resource.version));
    }

    @Test
    void getLatestPluginResourceByName_WhenCalledWithUnknownArguments_ThenReturnsNotFoundError() {
        var name = "unknown";
        when(compasPluginsResourceService.findLatestByTypeAndName(PLUGIN + "_" + TYPE, name))
                .thenThrow(CompasNoDataFoundException.class);

        given()
        .when()
            .get("/plugins/{plugin}/types/{type}/resources/{name}/latest", PLUGIN, TYPE, name)
        .then()
            .statusCode(404);
    }

    @Test
    void deletePluginResourceByName_WhenCalledWithExistingArguments_ThenReturnsNoContent() {
        var resource = createTestResource();

        given()
        .when()
            .delete("/plugins/{plugin}/types/{type}/resources/{name}", PLUGIN, TYPE, resource.name)
        .then()
            .statusCode(204);
    }

    @Test
    void deletePluginResourceByName_WhenCalledWithUnknownArguments_ThenReturnsNotFoundError() {
        var name = "unknown";
        doThrow(CompasNoDataFoundException.class)
                .when(compasPluginsResourceService).deleteByTypeAndName(PLUGIN + "_" + TYPE, name);

        given()
        .when()
            .delete("/plugins/{plugin}/types/{type}/resources/{name}", PLUGIN, TYPE, name)
        .then()
            .statusCode(404);
    }

    @Test
    void getPluginResourceVersionsByName_WhenCalledWithValidArguments_ThenReturnsVersions() {
        var older = createTestResource();
        older.version = "1.0.0";
        var newer = createTestResource();
        newer.version = "2.0.0";

        when(compasPluginsResourceService.findVersionsByTypeAndName(eq(PLUGIN + "_" + TYPE), eq(older.name)))
                .thenReturn(List.of(newer, older));

        given()
        .when()
            .get("/plugins/{plugin}/types/{type}/resources/{name}/versions", PLUGIN, TYPE, older.name)
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("[0].type", equalTo(TYPE))
            .body("[0].version", equalTo("2.0.0"))
            .body("[1].version", equalTo("1.0.0"));
    }

    @Test
    void getPluginResourceVersionsByName_WhenCalledWithUnknownArguments_ThenReturnsNotFoundError() {
        var name = "unknown";
        when(compasPluginsResourceService.findVersionsByTypeAndName(PLUGIN + "_" + TYPE, name))
                .thenThrow(CompasNoDataFoundException.class);

        given()
        .when()
            .get("/plugins/{plugin}/types/{type}/resources/{name}/versions", PLUGIN, TYPE, name)
        .then()
            .statusCode(404);
    }

    @Test
    void getPluginsWithTypes_WhenCalled_ThenReturnsPluginsAndTypes() {
        Map<String, List<String>> pluginsWithTypes = new LinkedHashMap<>();
        pluginsWithTypes.put("engineering-wizard", List.of("config", "process"));
        pluginsWithTypes.put("switching", List.of("report"));
        when(compasPluginsResourceService.listPluginsWithTypes()).thenReturn(pluginsWithTypes);

        given()
        .when()
            .get("/plugins")
        .then()
            .statusCode(200)
            .body("", hasSize(2))
            .body("[0].plugin", equalTo("engineering-wizard"))
            .body("[0].types", hasSize(2))
            .body("[0].types[0]", equalTo("config"))
            .body("[0].types[1]", equalTo("process"))
            .body("[1].plugin", equalTo("switching"))
            .body("[1].types[0]", equalTo("report"));
    }

    private PluginsCustomResource createTestResource() {
        var resource = new PluginsCustomResource();
        resource.id = java.util.UUID.randomUUID();
        resource.type = PLUGIN + "_" + TYPE;
        resource.tenant = "default";
        resource.name = "xml-resource";
        resource.description = "Description for XML resource";
        resource.contentType = "application/xml";
        resource.content = "<root>Test content</root>";
        resource.version = "1.0.0";
        resource.uploadedAt = OffsetDateTime.now();
        resource.dataCompatibilityVersion = "1.1.0";
        return resource;
    }
}
