// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import io.quarkus.arc.profile.IfBuildProfile;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.lfenergy.compas.scl.data.basex.client.BaseXClientFactory;
import org.lfenergy.compas.scl.data.basex.repository.CompasSclDataBaseXRepository;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Create Beans from other dependencies that are used in the application.
 */
public class CompasSclDataServiceBaseXConfiguration {
    @Produces
    @ApplicationScoped
    @IfBuildProfile("prod-basex")
    public BaseXClientFactory createBaseXClientFactoryProduction(@ConfigProperty(name = "basex.host") String baseXHost,
                                                                 @ConfigProperty(name = "basex.port") Integer baseXPort,
                                                                 @ConfigProperty(name = "basex.username") String baseXUsername,
                                                                 @ConfigProperty(name = "basex.password") String baseXPassword) {
        return new BaseXClientFactory(baseXHost, baseXPort, baseXUsername, baseXPassword);
    }

    @Produces
    @ApplicationScoped
    @IfBuildProfile("prod-basex")
    public CompasSclDataRepository creatCompasSclDataRepositoryProduction(BaseXClientFactory baseXClientFactory,
                                                                          SclDataModelMarshaller sclDataModelMarshaller) {
        return new CompasSclDataBaseXRepository(baseXClientFactory, sclDataModelMarshaller);
    }

    @Produces
    @ApplicationScoped
    @IfBuildProfile("dev-basex")
    public BaseXClientFactory createBaseXClientFactoryDevelopment(@ConfigProperty(name = "basex.host") String baseXHost,
                                                                  @ConfigProperty(name = "basex.port") Integer baseXPort,
                                                                  @ConfigProperty(name = "basex.username") String baseXUsername,
                                                                  @ConfigProperty(name = "basex.password") String baseXPassword) {
        return createBaseXClientFactoryProduction(baseXHost, baseXPort, baseXUsername, baseXPassword);
    }

    @Produces
    @ApplicationScoped
    @IfBuildProfile("dev-basex")
    public CompasSclDataRepository creatCompasSclDataRepositoryDevelopment(BaseXClientFactory baseXClientFactory,
                                                                           SclDataModelMarshaller sclDataModelMarshaller) {
        return creatCompasSclDataRepositoryProduction(baseXClientFactory, sclDataModelMarshaller);
    }
}
