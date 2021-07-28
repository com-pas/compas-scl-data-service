// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;

import javax.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 */
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
