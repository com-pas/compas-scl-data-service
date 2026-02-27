// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class SoftDeleteEnabledTestProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of("compas.scl-data-service.features.soft-delete-enabled", "true");
    }
}
