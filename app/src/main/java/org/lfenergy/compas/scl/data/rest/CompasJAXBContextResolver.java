// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest;

import org.lfenergy.compas.scl.data.repository.SclDataException;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Provider
@Produces("application/xml")
public class CompasJAXBContextResolver implements ContextResolver<JAXBContext> {
    public JAXBContext getContext(Class<?> type) {
        try {
            return JAXBContext.newInstance(
                    org.lfenergy.compas.scl.data.rest.model.ObjectFactory.class,
                    org.lfenergy.compas.scl.data.model.ObjectFactory.class,
                    org.lfenergy.compas.scl.ObjectFactory.class,
                    org.lfenergy.compas.scl.extensions.ObjectFactory.class);
        } catch (JAXBException exp) {
            throw new SclDataException(exp);
        }
    }
}