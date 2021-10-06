// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.Claim;
import io.quarkus.test.security.jwt.JwtSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.data.rest.Constants.READ_ROLE;
import static org.lfenergy.compas.scl.data.rest.Constants.UPDATE_ROLE;

@QuarkusTest
@TestHTTPEndpoint(CompasCommonResource.class)
class CompasCommonResourceTest {
    @Test
    @TestSecurity(user = "test-user", roles = {"IID_" + READ_ROLE, "SCD_" + READ_ROLE, "SED_" + UPDATE_ROLE})
    void list_WhenCalledWithMultipleReadRights_ThenMultipleItemResponseRetrieved() {
        var response = given()
                .when().get("/type/list")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath();
        // User has read rights for 2 types, so these types are returned.
        var sclTypes = xmlPath.getList("TypeListResponse.Type.Code");
        assertEquals(2, sclTypes.size());
        assertEquals("IID", sclTypes.get(0));
        assertEquals("SCD", sclTypes.get(1));
    }

    @Test
    @TestSecurity(user = "test-user", roles = {"SCD_" + READ_ROLE})
    void list_WhenCalledWithOneReadRights_ThenOneItemResponseRetrieved() {
        var response = given()
                .when().get("/type/list")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath();
        // User has read rights for one type, so this type is returned.
        var sclTypes = xmlPath.getList("TypeListResponse.Type.Code");
        assertEquals(1, sclTypes.size());
        assertEquals("SCD", sclTypes.get(0));
    }

    @Test
    @TestSecurity(user = "test-user")
    void list_WhenCalledWithNoReadRights_ThenNoItemResponseRetrieved() {
        var response = given()
                .when().get("/type/list")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath();
        // User has read rights for no types, so empty list is returned.
        var sclTypes = xmlPath.getList("TypeListResponse.Type.Code");
        assertEquals(0, sclTypes.size());
    }

    @Test
    @TestSecurity(user = "test-user")
    @JwtSecurity(claims = {
            // Default the claim "name" is configured, so we will set this claim for the test.
            @Claim(key = "name", value = "Test User")
    })
    void getUserInfo_WhenCalled_ThenUserInfoResponseRetrieved() {
        var response = given()
                .when().get("/userinfo")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var xmlPath = response.xmlPath();
        assertEquals("Test User", xmlPath.get("UserInfoResponse.Name"));
        assertEquals(10, xmlPath.getInt("UserInfoResponse.SessionWarning"));
        assertEquals(15, xmlPath.getInt("UserInfoResponse.SessionExpires"));
    }
}
