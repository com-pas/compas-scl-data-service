// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;



@QuarkusTest
@TestSecurity(user = "test-user")
@TestHTTPEndpoint(CompasPluginsResource.class)
public class CompasPluginsResourceUploadDataTest {

    private static final String UUID_PATTERN =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89aAbB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";


    @Test
    void uploadData_WhenCalledWithValidNonSclJsonResource_ThenUploadDataResponseIsRetrieved() {

        given()
            .contentType("multipart/form-data")
            .multiPart("type", "json")
            .multiPart("name", "json-resource")
            .multiPart("description", "Description for JSON resource")
            .multiPart("content-type", "application/json")
            .multiPart("content", "{\"key\":\"value\"}")
            .multiPart("data-compatibility-version", "1.1.0")
            .multiPart("version", "1.0.0")
            .multiPart("nextVersionType", "something")
        .when()
            .post()
        .then()
            .statusCode(200)
            .body("id", matchesPattern(UUID_PATTERN))
            .body("name", equalTo("json-resource"))
            .body("uploadedAt", not(empty()));
    }

    @Test
    void uploadData_WhenCalledWithValidNonSclXmlResource_ThenUploadDataResponseIsRetrieved() {

        given()
            .contentType("multipart/form-data")
            .multiPart("type", "xml")
            .multiPart("name", "xml-resource")
            .multiPart("description", "Description for XML resource")
            .multiPart("content-type", "application/xml")
            .multiPart("content", "<root>Test content</root>")
            .multiPart("data-compatibility-version", "1.1.0")
            .multiPart("version", "1.0.0")
            .multiPart("nextVersionType", "something")
        .when()
            .post()
        .then()
            .statusCode(200)
            .body("id", matchesPattern(UUID_PATTERN))
            .body("name", equalTo("xml-resource"))
            .body("uploadedAt", not(empty()));
    }

    @Test
    void uploadData_WhenCalledWithInvalidResourceType_ThenReturnsBadRequestError() {

        given()
            .contentType("multipart/form-data")
            .multiPart("type", "text")
            .multiPart("name", "text-resource")
            .multiPart("description", "Description for Text resource")
            .multiPart("content-type", "*/*")
            .multiPart("content", "text")
            .multiPart("data-compatibility-version", "1.1.0")
            .multiPart("version", "1.0.0")
            .multiPart("nextVersionType", "something")
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    void uploadData_WhenCalledWithIncompletePayload_ThenReturnsBadRequestError() {

        given()
            .contentType("multipart/form-data")
            .multiPart("type", "xml")
            .multiPart("name", "xml-resource")
            .multiPart("description", "Description for XML resource")
            .multiPart("content-type", "application/xml")
            .multiPart("content", "<root>Test content</root>")
            .multiPart("data-compatibility-version", "1.1.0")
        .when()
            .post()
        .then()
            .statusCode(400);
    }

}
