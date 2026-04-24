// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class SclFileId implements Serializable {

    @Column(name = "id", nullable = false)
    public UUID id;

    @Column(name = "major_version", nullable = false)
    public short majorVersion;

    @Column(name = "minor_version", nullable = false)
    public short minorVersion;

    @Column(name = "patch_version", nullable = false)
    public short patchVersion;

    /**
     * Returns the version as a "major.minor.patch" string.
     */
    public String version() {
        return majorVersion + "." + minorVersion + "." + patchVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SclFileId other = (SclFileId) o;
        return majorVersion == other.majorVersion
                && minorVersion == other.minorVersion
                && patchVersion == other.patchVersion
                && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, majorVersion, minorVersion, patchVersion);
    }
}
