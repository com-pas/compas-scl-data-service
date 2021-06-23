// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    @XmlElementDecl(namespace = "", name = "CreateRequest")
    public JAXBElement<CreateRequest> createCreateRequest(CreateRequest value) {
        return new JAXBElement<>(new QName("", ""), CreateRequest.class, value);
    }

    @XmlElementDecl(namespace = "", name = "CreateResponse")
    public JAXBElement<CreateResponse> createCreateResponse(CreateResponse value) {
        return new JAXBElement<>(new QName("", ""), CreateResponse.class, value);
    }

    @XmlElementDecl(namespace = "", name = "GetResponse")
    public JAXBElement<GetResponse> createGetResponse(GetResponse value) {
        return new JAXBElement<>(new QName("", ""), GetResponse.class, value);
    }

    @XmlElementDecl(namespace = "", name = "ListResponse")
    public JAXBElement<ListResponse> createListResponse(ListResponse value) {
        return new JAXBElement<>(new QName("", ""), ListResponse.class, value);
    }

    @XmlElementDecl(namespace = "", name = "TypeListResponse")
    public JAXBElement<TypeListResponse> createTypeListResponse(TypeListResponse value) {
        return new JAXBElement<>(new QName("", ""), TypeListResponse.class, value);
    }

    @XmlElementDecl(namespace = "", name = "UpdateRequest")
    public JAXBElement<UpdateRequest> createUpdateRequest(UpdateRequest value) {
        return new JAXBElement<>(new QName("", ""), UpdateRequest.class, value);
    }

    @XmlElementDecl(namespace = "", name = "VersionsResponse")
    public JAXBElement<VersionsResponse> createVersionsResponse(VersionsResponse value) {
        return new JAXBElement<>(new QName("", ""), VersionsResponse.class, value);
    }
}
