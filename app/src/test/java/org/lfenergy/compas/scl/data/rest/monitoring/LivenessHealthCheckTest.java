// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.monitoring;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@QuarkusTest
class LivenessHealthCheckTest {

    @Test
    void testLivenessEndpoint() {
        given()
                .when().get("/q/health/live")
                .then()
                .statusCode(200);
    }

}