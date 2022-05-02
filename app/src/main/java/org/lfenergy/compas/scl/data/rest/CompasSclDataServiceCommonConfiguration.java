// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.service.impl.CompasSclDataServiceImpl;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;
import org.lfenergy.compas.scl.data.util.SclElementProcessor;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 */
@RegisterForReflection(targets = {
        org.lfenergy.compas.core.jaxrs.model.ErrorResponse.class,
        org.lfenergy.compas.core.jaxrs.model.ErrorMessage.class
})
public class CompasSclDataServiceCommonConfiguration {
    @Produces
    @ApplicationScoped
    public ElementConverter createElementConverter() {
        return new ElementConverter();
    }

    @Produces
    @ApplicationScoped
    public SclElementProcessor creatSclElementProcessor() {
        return new SclElementProcessor();
    }

    @Produces
    @ApplicationScoped
    public SclDataModelMarshaller createSclDataModelMarshaller() {
        return new SclDataModelMarshaller();
    }

    @Produces
    @ApplicationScoped
    public CompasSclDataService createCompasSclDataService(CompasSclDataRepository repository,
                                                           ElementConverter converter,
                                                           SclElementProcessor sclElementProcessor) {
        return new CompasSclDataServiceImpl(repository, converter, sclElementProcessor);
    }
}
