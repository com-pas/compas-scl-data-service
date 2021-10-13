// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Configure class for Quarkus Native Build to be included.
 */
@RegisterForReflection(
        targets = {
                org.lfenergy.compas.core.jaxrs.model.ErrorMessage.class,
                org.lfenergy.compas.core.jaxrs.model.ErrorResponse.class,
                org.lfenergy.compas.scl.data.model.SclMetaInfo.class,
                org.lfenergy.compas.scl.data.model.ObjectFactory.class
        })
public class CompasReflectionConfiguration {
}
