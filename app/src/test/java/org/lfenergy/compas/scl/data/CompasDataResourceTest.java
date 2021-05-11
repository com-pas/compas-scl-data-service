// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class CompasDataResourceTest {
    @Test
    void testApiEndpoint() {
        given()
                .when().get("/scl/v1/SCD/f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")
                .then()
                .statusCode(200);
    }
}