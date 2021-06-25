// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Item;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ObjectFactoryTest {
    private static ObjectFactory objectFactory;

    @BeforeAll
    static void beforeAll() {
        objectFactory = new ObjectFactory();
    }

    @Test
    void createCreateRequest_WhenMethodCalled_ThenObjectsAreCreated() {
        var requestName = "REQUEST";
        var scl = new SCL();

        var request = new CreateRequest();
        request.setName(requestName);
        request.setScl(scl);

        var createRequest = objectFactory.createCreateRequest(request);

        assertNotNull(createRequest);
        assertEquals(CreateRequest.class, createRequest.getDeclaredType());
        assertEquals(requestName, createRequest.getValue().getName());
        assertEquals(scl, createRequest.getValue().getScl());
    }

    @Test
    void createCreateResponse_WhenMethodCalled_ThenObjectsAreCreated() {
        var id = UUID.randomUUID();

        var response = new CreateResponse();
        response.setId(id);

        var createResponse = objectFactory.createCreateResponse(response);

        assertNotNull(createResponse);
        assertEquals(CreateResponse.class, createResponse.getDeclaredType());
        assertEquals(id, createResponse.getValue().getId());
    }

    @Test
    void createGetResponse_WhenMethodCalled_ThenObjectsAreCreated() {
        var scl = new SCL();

        var response = new GetResponse();
        response.setScl(scl);

        var getResponse = objectFactory.createGetResponse(response);

        assertNotNull(getResponse);
        assertEquals(GetResponse.class, getResponse.getDeclaredType());
        assertEquals(scl, getResponse.getValue().getScl());
    }

    @Test
    void createListResponse_WhenMethodCalled_ThenObjectsAreCreated() {
        var id = "1234";
        var name = "NAME";
        var version = "1.0.0";

        var item = new Item(id, name, version);
        var listOfItems = Collections.singletonList(item);

        var response = new ListResponse();
        response.setItems(listOfItems);

        var listResponse = objectFactory.createListResponse(response);

        assertNotNull(listResponse);
        assertEquals(ListResponse.class, listResponse.getDeclaredType());
        assertEquals(id, listResponse.getValue().getItems().get(0).getId());
        assertEquals(name, listResponse.getValue().getItems().get(0).getName());
        assertEquals(version, listResponse.getValue().getItems().get(0).getVersion());
    }

    @Test
    void createTypeListResponse_WhenMethodCalled_ThenObjectsAreCreated() {
        var code = "CODE";
        var description = "DESCRIPTION";

        var type = new Type(code, description);
        var listOfTypes = Collections.singletonList(type);

        var response = new TypeListResponse();
        response.setTypes(listOfTypes);

        var typeListResponse = objectFactory.createTypeListResponse(response);

        assertNotNull(typeListResponse);
        assertEquals(TypeListResponse.class, typeListResponse.getDeclaredType());
        assertEquals(code, typeListResponse.getValue().getTypes().get(0).getCode());
        assertEquals(description, typeListResponse.getValue().getTypes().get(0).getDescription());
    }

    @Test
    void createUpdateRequest_WhenMethodCalled_ThenObjectsAreCreated() {
        var scl = new SCL();
        var changeSetType = ChangeSetType.PATCH;

        var request = new UpdateRequest();
        request.setScl(scl);
        request.setChangeSetType(changeSetType);

        var updateRequest = objectFactory.createUpdateRequest(request);

        assertNotNull(updateRequest);
        assertEquals(UpdateRequest.class, updateRequest.getDeclaredType());
        assertEquals(scl, updateRequest.getValue().getScl());
        assertEquals(changeSetType, updateRequest.getValue().getChangeSetType());
    }

    @Test
    void createVersionsResponse_WhenMethodCalled_ThenObjectsAreCreated() {
        var id = "1234";
        var name = "NAME";
        var version = "1.0.0";

        var item = new Item(id, name, version);
        var listOfItems = Collections.singletonList(item);

        var response = new VersionsResponse();
        response.setItems(listOfItems);

        var versionsResponse = objectFactory.createVersionsResponse(response);

        assertNotNull(versionsResponse);
        assertEquals(VersionsResponse.class, versionsResponse.getDeclaredType());
        assertEquals(id, versionsResponse.getValue().getItems().get(0).getId());
        assertEquals(name, versionsResponse.getValue().getItems().get(0).getName());
        assertEquals(version, versionsResponse.getValue().getItems().get(0).getVersion());
    }
}
