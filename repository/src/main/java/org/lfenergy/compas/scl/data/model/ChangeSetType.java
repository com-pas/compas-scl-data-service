// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = """
        The type of change used to determine the next version value.
        Major change updating the first digit, for example from 1.2.4 to 2.0.0.
        Minor change updating the second digit, for example from 1.2.4 to 1.3.0.
        Patch change updating the third digit, for example from 1.2.4 to 1.2.5.
        """)
public enum ChangeSetType {
    MAJOR,
    MINOR,
    PATCH
}
