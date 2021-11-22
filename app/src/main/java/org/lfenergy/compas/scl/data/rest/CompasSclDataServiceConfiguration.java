// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;

import javax.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 */
@RegisterForReflection(
        targets = {
                org.lfenergy.compas.core.jaxrs.model.ErrorMessage.class,
                org.lfenergy.compas.core.jaxrs.model.ErrorResponse.class,
                org.lfenergy.compas.scl.data.model.SclMetaInfo.class,
                org.lfenergy.compas.scl.data.model.ObjectFactory.class
        })
public class CompasSclDataServiceConfiguration {
    @Produces
    public ElementConverter createElementConverter() {
        return new ElementConverter();
    }

    @Produces
    public SclElementProcessor creatSclElementProcessor() {
        return new SclElementProcessor();
    }

    @Produces
    public SclDataModelMarshaller createSclDataModelMarshaller() {
        return new SclDataModelMarshaller();
    }
}
