// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.SclType;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@QuarkusTest
@TestHTTPEndpoint(CompasCommonResource.class)
class CompasCommonResourceTest {
    @Test
    void list_WhenCalled_ThenItemResponseRetrieved() {
        var response = given()
                .when().get("/type/list")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath();
        assertEquals(SclType.values().length, xmlPath.getList("TypeListResponse.Type").size());
    }
}
