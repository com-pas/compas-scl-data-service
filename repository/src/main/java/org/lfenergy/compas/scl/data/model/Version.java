// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.lfenergy.compas.scl.data.repository.SclDataRepositoryException;

import java.util.Objects;

public class Version {
    private final int majorVersion;
    private final int minorVersion;
    private final int patchVersion;

    public Version(String version) {
        // Verify if format is meet.
        validate(version);

        // Split the string into separate parts.
        String[] elements = version.split("\\.");
        this.majorVersion = Integer.parseInt(elements[0]);
        this.minorVersion = Integer.parseInt(elements[1]);
        this.patchVersion = Integer.parseInt(elements[2]);
    }

    public Version(int majorVersion, int minorVersion, int patchVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
    }

    private void validate(String version) {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Version can't be null or empty");
        }
        if (!version.matches("([1-9]\\d*)\\.(\\d+)\\.(\\d+)")) {
            throw new IllegalArgumentException("Version is in the wrong format. Must consist of 3 number separated by dot (1.3.5)");
        }
    }

    public Version getNextVersion(ChangeSetType changeSetType) {
        if (changeSetType == null) {
            throw new IllegalArgumentException("ChangeSetType can't be null or empty");
        }

        switch (changeSetType) {
            case MAJOR:
                return new Version(majorVersion + 1, 0, 0);
            case MINOR:
                return new Version(majorVersion, minorVersion + 1, 0);
            case PATCH:
                return new Version(majorVersion, minorVersion, patchVersion + 1);
            default:
                throw new SclDataRepositoryException("Unhandled ChangeSetType " + changeSetType);
        }
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(majorVersion, minorVersion, patchVersion);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj instanceof Version) {
            final Version other = (Version) obj;
            return majorVersion == other.majorVersion
                    && minorVersion == other.minorVersion
                    && patchVersion == other.patchVersion;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return majorVersion + "." + minorVersion + "." + patchVersion;
    }
}
