// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {
    @Test
    void constructor_WhenCalledWithNullOrEmptyValue_ThenExceptionThrown() {
        var expectedMessage = "Version can't be null or empty";

        assertThrows(IllegalArgumentException.class, () -> {
            new Version(null);
        }, expectedMessage);

        assertThrows(IllegalArgumentException.class, () -> {
            new Version("");
        }, expectedMessage);
    }

    @Test
    void constructor_WhenCalledWithInvalidValues_ThenExceptionThrown() {
        var expectedMessage = "Version is in the wrong format. Must consist of 3 number separated by dot (1.3.5)";

        assertThrows(IllegalArgumentException.class, () -> {
            new Version("AS.ER.AD");
        }, expectedMessage);
        assertThrows(IllegalArgumentException.class, () -> {
            new Version("JANSEN");
        }, expectedMessage);
        assertThrows(IllegalArgumentException.class, () -> {
            new Version("1.0");
        }, expectedMessage);
        assertThrows(IllegalArgumentException.class, () -> {
            new Version("1");
        }, expectedMessage);
        assertThrows(IllegalArgumentException.class, () -> {
            new Version("0.0.0");
        }, expectedMessage);
    }

    @Test
    void constructor_WhenCalledWithValidValues_ThenObjectCreated() {
        assertCheckVersion(new Version("1.3.5"), 1, 3, 5, "1.3.5");
        assertCheckVersion(new Version("1234.3456.5678"), 1234, 3456, 5678, "1234.3456.5678");
        assertCheckVersion(new Version(1, 3, 5), 1, 3, 5, "1.3.5");
    }

    private void assertCheckVersion(Version version, int majorVersion, int minorVersion, int patchVersion, String result) {
        assertEquals(majorVersion, version.getMajorVersion());
        assertEquals(minorVersion, version.getMinorVersion());
        assertEquals(patchVersion, version.getPatchVersion());
        assertEquals(result, version.toString());
    }

    @Test
    void getNextVersion_WhenCalledWithDiffentChangeSetTypes_ThenTheCorrectNextVersionIsReturned() {
        var currentVersion = new Version(1, 3, 5);

        assertEquals("2.0.0", currentVersion.getNextVersion(ChangeSetType.MAJOR).toString());
        assertEquals("1.4.0", currentVersion.getNextVersion(ChangeSetType.MINOR).toString());
        assertEquals("1.3.6", currentVersion.getNextVersion(ChangeSetType.PATCH).toString());
    }

    @Test
    void getNextVersion_WhenCalledWithNoChangeSetTypes_ThenExceptionThrown() {
        var currentVersion = new Version(1, 3, 5);

        assertThrows(IllegalArgumentException.class, () -> {
            currentVersion.getNextVersion(null);
        });
    }

    @Test
    void hashCode_WhenTwoObjectHaveSameVersion_ThenHashcodeShouldAlsoBeTheSame() {
        assertEquals(new Version(1, 5, 8).hashCode(), new Version(1, 5, 8).hashCode());
    }

    @Test
    void equals_WhenTwoObjectHaveSameVersion_ThenEqualsShouldAlsoBeTheSame() {
        assertEquals(new Version(1, 5, 8), new Version(1, 5, 8));
    }

    @Test
    void equals_WhenTwoObjectHaveDifferentMajorVersion_ThenEqualsShouldNotBeTheSame() {
        assertNotEquals(new Version(1, 5, 8), new Version(2, 5, 8));
    }

    @Test
    void equals_WhenTwoObjectHaveDifferentMinorVersion_ThenEqualsShouldNotBeTheSame() {
        assertNotEquals(new Version(1, 5, 8), new Version(1, 6, 8));
    }

    @Test
    void equals_WhenTwoObjectHaveDifferentPatchVersion_ThenEqualsShouldNotBeTheSame() {
        assertNotEquals(new Version(1, 5, 8), new Version(1, 5, 9));
    }

    @Test
    void equals_WhenTwoSameObject_ThenEqualsShouldAlsoBeTheSame() {
        var version = new Version(1, 5, 8);
        assertEquals(version, version);
    }

    @Test
    void toString_WhenCalled_ThenCorrectStringReturned() {
        assertEquals("1.5.8", new Version(1, 5, 8).toString());
    }
}