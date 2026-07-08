// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

public record CreatePluginResourceData(
        String plugin,
        String type,
        String name,
        String contentType,
        String content,
        String dataCompatibilityVersion,
        String description,
        String version,
        String nextVersionType
) {
}
