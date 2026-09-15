// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.lfenergy.compas.core.commons.ElementConverter;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 */
@RegisterForReflection(targets = {
        org.lfenergy.compas.core.commons.model.ErrorResponse.class,
        org.lfenergy.compas.core.commons.model.ErrorMessage.class,
        org.lfenergy.compas.scl.data.rest.api.plugins.resources.DataEntry.class,
        org.lfenergy.compas.scl.data.rest.api.plugins.resources.DataEntryWithContent.class,
        org.lfenergy.compas.scl.data.rest.api.plugins.resources.PagedDataEntryResponse.class,
        org.lfenergy.compas.scl.data.rest.api.plugins.resources.UploadDataResponse.class,
        org.lfenergy.compas.scl.data.rest.api.plugins.resources.Error.class,
        org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.CreatePluginResourceRequest.class,
        org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResource.class,
        org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.PluginResourceMeta.class,
        org.lfenergy.compas.scl.data.rest.api.plugins.resources.v2.Error.class
})
public class CompasSclDataServiceConfiguration {
    @Produces
    @ApplicationScoped
    public ElementConverter createElementConverter() {
        return new ElementConverter();
    }

    @Produces
    @ApplicationScoped
    public SclDataModelMarshaller createSclDataModelMarshaller() {
        return new SclDataModelMarshaller();
    }
}
