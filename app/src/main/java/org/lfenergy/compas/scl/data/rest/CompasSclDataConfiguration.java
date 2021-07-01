// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.lfenergy.compas.scl.commons.CompasExtensionsManager;

import javax.enterprise.inject.Produces;

public class CompasSclDataConfiguration {
    @Produces
    public CompasExtensionsManager createCompasExtensionsManager() {
        return new CompasExtensionsManager();
    }
}
