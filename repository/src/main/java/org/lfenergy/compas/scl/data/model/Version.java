// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Presenting the version logic used in CoMPAS.")
public class Version implements Comparable<Version> {
    public static final String PATTERN = "([1-9]\\d*)\\.(\\d+)\\.(\\d+)";

    @Schema(description = "The major version.", example = "2")
    private final int majorVersion;
    @Schema(description = "The minor version.", example = "1")
    private final int minorVersion;
    @Schema(description = "The patch version.", example = "3")
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
        if (!version.matches(PATTERN)) {
            throw new IllegalArgumentException("Version is in the wrong format. Must consist of 3 number separated by dot (1.3.5)");
        }
    }

    public Version getNextVersion(ChangeSetType changeSetType) {
        if (changeSetType == null) {
            throw new IllegalArgumentException("ChangeSetType can't be null or empty");
        }

        return switch (changeSetType) {
            case MAJOR -> new Version(majorVersion + 1, 0, 0);
            case MINOR -> new Version(majorVersion, minorVersion + 1, 0);
            case PATCH -> new Version(majorVersion, minorVersion, patchVersion + 1);
        };
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
        if (obj instanceof Version other) {
            return majorVersion == other.majorVersion
                    && minorVersion == other.minorVersion
                    && patchVersion == other.patchVersion;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Version otherVersion) {
        if (this.majorVersion == otherVersion.getMajorVersion()) {
            if (this.minorVersion == otherVersion.getMinorVersion()) {
                return Integer.compare(this.patchVersion, otherVersion.getPatchVersion());
            }
            return Integer.compare(this.minorVersion, otherVersion.getMinorVersion());
        }
        return Integer.compare(this.majorVersion, otherVersion.getMajorVersion());
    }

    @Override
    public String toString() {
        return majorVersion + "." + minorVersion + "." + patchVersion;
    }
}
