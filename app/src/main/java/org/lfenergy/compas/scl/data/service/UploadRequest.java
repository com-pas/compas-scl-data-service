// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

public record UploadRequest(
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
