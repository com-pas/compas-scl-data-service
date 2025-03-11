// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.monitoring;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.config.FeatureFlagService;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@QuarkusTest
class ReadinessHealthCheckTest {

    @InjectMock
    private FeatureFlagService featureFlagService;

    @Test
    void testReadinessEndpoint() {
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200);
    }

    @Test
    void testReadinessEndpoint_whenCalledWithHistoryFeatureEnabled_ThenRetrieveIsHistoryEnabledTrue() {
        // Mock the feature flag to be enabled
        when(featureFlagService.isHistoryEnabled()).thenReturn(true);

        var response = given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertNotNull(response);
        assertEquals(response.jsonPath().getString("checks.find { it.name == 'System Ready' }.name"), "System Ready");
        assertEquals(response.jsonPath().getBoolean("checks.find { it.name == 'System Ready' }.data.isHistoryEnabled"), true);
    }

    @Test
    void testReadinessEndpoint_whenCalledWithHistoryFeatureDisabled_ThenRetrieveIsHistoryEnabledFalse() {
        // Mock the feature flag to be disabled
        when(featureFlagService.isHistoryEnabled()).thenReturn(false);

        var response = given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertNotNull(response);
        assertEquals(response.jsonPath().getString("checks.find { it.name == 'System Ready' }.name"), "System Ready");
        assertEquals(response.jsonPath().getBoolean("checks.find { it.name == 'System Ready' }.data.isHistoryEnabled"), false);
    }
}