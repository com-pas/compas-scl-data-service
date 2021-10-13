// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.lfenergy.compas.core.jaxrs.model.ErrorMessage;
import org.lfenergy.compas.core.jaxrs.model.ErrorResponse;
import org.lfenergy.compas.scl.data.model.SclMetaInfo;

/**
 * Configure class for Quarkus Native Build to be included.
 */
@RegisterForReflection(
        targets = {
                SclMetaInfo.class,
                ErrorMessage.class,
                ErrorResponse.class,
                org.lfenergy.compas.scl.data.model.ObjectFactory.class
        })
public class CompasReflectionConfiguration {
}
