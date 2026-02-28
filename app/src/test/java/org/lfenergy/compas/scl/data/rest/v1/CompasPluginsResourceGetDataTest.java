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
import org.lfenergy.compas.scl.data.model.PluginsCustomResource;
import org.lfenergy.compas.scl.data.service.CompasPluginsResourceService;

import java.time.OffsetDateTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@QuarkusTest
@TestSecurity(user = "test-user")
@TestHTTPEndpoint(CompasPluginsResource.class)
class CompasPluginsResourceGetDataTest {


    @InjectMock
    private CompasPluginsResourceService compasPluginsResourceService;

    @Test
    void getDataById_WhenCalledWithValidUUID_ThenReturnsResource() {

        var resource = createTestResource();
        when(compasPluginsResourceService.findById(resource.id))
                .thenReturn(resource);

        given()
        .when()
            .get("/{id}", resource.id)
        .then()
            .statusCode(200)
            .body("id", equalTo(resource.id.toString()))
            .body("type", equalTo(resource.type))
            .body("tenant", equalTo(resource.tenant))
            .body("name", equalTo(resource.name))
            .body("description", equalTo(resource.description))
            .body("contentType", equalTo(resource.contentType))
            .body("version", equalTo(resource.version))
            .body("dataCompatibilityVersion", equalTo(resource.dataCompatibilityVersion))
            .body("uploadedAt", not(empty()));
    }

    @Test
    void getDataById_WhenCalledWithNonExistingUUID_ThenReturnsNotFoundError() {

        var nonExistingId = java.util.UUID.randomUUID();
        when(compasPluginsResourceService.findById(nonExistingId))
                .thenThrow(CompasNoDataFoundException.class);

        given()
        .when()
            .get("/{id}", nonExistingId)
        .then()
            .statusCode(404);
    }

    @Test
    void getAllData_WhenCalledWithValidTypeAndNoFilters_ThenReturnsListOfResources() {
        String type = "xml";
        int page = 0;
        int size = 20;

        var resource1 = createTestResource();
        var resource2 = createTestResource();
        resource2.name = "another-resource";

        when(compasPluginsResourceService.list(eq(type), any(), any(), any(), eq(page), eq(size)))
                .thenReturn(List.of(resource1, resource2));
        when(compasPluginsResourceService.count(eq(type), any(), any(), any()))
                .thenReturn(2L);

        given()
            .queryParam("type", type)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("page", equalTo(page))
            .body("size", equalTo(size))
            .body("totalElements", equalTo(2))
            .body("totalPages", equalTo(1))
            .body("content", hasSize(2))
            .body("content[0].id", equalTo(resource1.id.toString()))
            .body("content[0].name", equalTo(resource1.name))
            .body("content[1].id", equalTo(resource2.id.toString()))
            .body("content[1].name", equalTo(resource2.name));
    }

    @Test
    void getAllData_WhenCalledWithPaginationParameters_ThenReturnsPagedResponse() {
        String type = "xml";
        int page = 1;
        int size = 5;

        var resource = createTestResource();

        when(compasPluginsResourceService.list(eq(type), any(), any(), any(), eq(page), eq(size)))
                .thenReturn(List.of(resource));
        when(compasPluginsResourceService.count(eq(type), any(), any(), any()))
                .thenReturn(6L); // e.g. 6 total entries -> 2 pages of 5

        given()
            .queryParam("type", type)
            .queryParam("page", page)
            .queryParam("size", size)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("page", equalTo(page))
            .body("size", equalTo(size))
            .body("totalElements", equalTo(6))
            .body("totalPages", equalTo(2))
            .body("content", hasSize(1));
    }

    @Test
    void getAllData_WhenCalledWithNonMatchingFilters_ThenReturnsEmptyContent() {
        String type = "non-existing-type";
        int page = 0;
        int size = 20;

        when(compasPluginsResourceService.list(eq(type), any(), any(), any(), eq(page), eq(size)))
                .thenReturn(List.of());
        when(compasPluginsResourceService.count(eq(type), any(), any(), any()))
                .thenReturn(0L);

        given()
            .queryParam("type", type)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("content", hasSize(0))
            .body("totalElements", equalTo(0))
            .body("totalPages", equalTo(0))
            .body("page", equalTo(page))
            .body("size", equalTo(size));
    }



    private PluginsCustomResource createTestResource() {
        var resource = new PluginsCustomResource();
        resource.id = java.util.UUID.randomUUID();
        resource.type = "xml";
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
