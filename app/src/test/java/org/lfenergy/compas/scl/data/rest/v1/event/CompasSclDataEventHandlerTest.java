// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.core.commons.exception.CompasException;
import org.lfenergy.compas.core.commons.model.ErrorResponse;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.rest.v1.model.CreateResponse;
import org.lfenergy.compas.scl.data.rest.v1.model.UpdateResponse;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.core.commons.exception.CompasErrorCode.WEBSOCKET_GENERAL_ERROR_CODE;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.DUPLICATE_SCL_NAME_ERROR_CODE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompasSclDataEventHandlerTest {
    @Mock
    private CompasSclDataService service;

    @InjectMocks
    private CompasSclDataEventHandler eventHandler;

    @Test
    void createWebsocketsEvent_WhenCalled_ThenCreateResponseReturned() {
        var type = SclFileType.CID;
        var name = "Some name";
        var who = "Who executed it";
        var comment = "Some comment";
        var sclData = "Some SCL Data";

        var session = Mockito.mock(Session.class);
        var async = Mockito.mock(RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        var request = new CreateEventRequest(session, type, name, who, comment, sclData);

        when(service.create(type, name, who, comment, sclData)).thenReturn(sclData);

        eventHandler.createWebsocketsEvent(request);

        verify(session, times(1)).getAsyncRemote();
        ArgumentCaptor<CreateResponse> captor = ArgumentCaptor.forClass(CreateResponse.class);
        verify(async, times(1)).sendObject(captor.capture());
        var response = captor.getValue();
        assertEquals(sclData, response.getSclData());

        verify(service, times(1)).create(type, name, who, comment, sclData);
    }

    @Test
    void createWebsocketsEvent_WhenCalledAndCompasExceptionThrownByService_ThenErrorResponseReturned() {
        var type = SclFileType.CID;
        var name = "Some name";
        var who = "Who executed it";
        var comment = "Some comment";
        var sclData = "Some SCL Data";
        var errorMessage = "Some Error";

        var session = Mockito.mock(Session.class);
        var async = Mockito.mock(RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        var request = new CreateEventRequest(session, type, name, who, comment, sclData);

        when(service.create(type, name, who, comment, sclData))
                .thenThrow(new CompasException(DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage));

        eventHandler.createWebsocketsEvent(request);

        verify(session, times(1)).getAsyncRemote();
        ArgumentCaptor<ErrorResponse> captor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(async, times(1)).sendObject(captor.capture());
        var response = captor.getValue();
        assertEquals(1, response.getErrorMessages().size());
        var message = response.getErrorMessages().get(0);
        assertEquals(DUPLICATE_SCL_NAME_ERROR_CODE, message.getCode());
        assertEquals(errorMessage, message.getMessage());

        verify(service, times(1)).create(type, name, who, comment, sclData);
    }

    @Test
    void createWebsocketsEvent_WhenCalledAndRuntimeExceptionThrownByService_ThenErrorResponseReturned() {
        var type = SclFileType.CID;
        var name = "Some name";
        var who = "Who executed it";
        var comment = "Some comment";
        var sclData = "Some SCL Data";
        var errorMessage = "Some Error";

        var session = Mockito.mock(Session.class);
        var async = Mockito.mock(RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        var request = new CreateEventRequest(session, type, name, who, comment, sclData);

        when(service.create(type, name, who, comment, sclData)).thenThrow(new RuntimeException(errorMessage));

        eventHandler.createWebsocketsEvent(request);

        verify(session, times(1)).getAsyncRemote();
        ArgumentCaptor<ErrorResponse> captor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(async, times(1)).sendObject(captor.capture());
        var response = captor.getValue();
        assertEquals(1, response.getErrorMessages().size());
        var message = response.getErrorMessages().get(0);
        assertEquals(WEBSOCKET_GENERAL_ERROR_CODE, message.getCode());
        assertEquals(errorMessage, message.getMessage());

        verify(service, times(1)).create(type, name, who, comment, sclData);
    }

    @Test
    void updateWebsocketsEvent_WhenCalled_ThenUpdateResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var cst = ChangeSetType.MINOR;
        var who = "Who executed it";
        var comment = "Some comment";
        var sclData = "Some SCL Data";

        var session = Mockito.mock(Session.class);
        var async = Mockito.mock(RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        var request = new UpdateEventRequest(session, type, id, cst, who, comment, sclData);

        when(service.update(type, id, cst, who, comment, sclData)).thenReturn(sclData);

        eventHandler.updateWebsocketsEvent(request);

        verify(session, times(1)).getAsyncRemote();
        ArgumentCaptor<UpdateResponse> captor = ArgumentCaptor.forClass(UpdateResponse.class);
        verify(async, times(1)).sendObject(captor.capture());
        var response = captor.getValue();
        assertEquals(sclData, response.getSclData());

        verify(service, times(1)).update(type, id, cst, who, comment, sclData);
    }

    @Test
    void updateWebsocketsEvent_WhenCalledAndCompasExceptionThrownByService_ThenErrorResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var cst = ChangeSetType.MINOR;
        var who = "Who executed it";
        var comment = "Some comment";
        var sclData = "Some SCL Data";
        var errorMessage = "Some Error";

        var session = Mockito.mock(Session.class);
        var async = Mockito.mock(RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        var request = new UpdateEventRequest(session, type, id, cst, who, comment, sclData);

        when(service.update(type, id, cst, who, comment, sclData))
                .thenThrow(new CompasException(DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage));

        eventHandler.updateWebsocketsEvent(request);

        verify(session, times(1)).getAsyncRemote();
        ArgumentCaptor<ErrorResponse> captor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(async, times(1)).sendObject(captor.capture());
        var response = captor.getValue();
        assertEquals(1, response.getErrorMessages().size());
        var message = response.getErrorMessages().get(0);
        assertEquals(DUPLICATE_SCL_NAME_ERROR_CODE, message.getCode());
        assertEquals(errorMessage, message.getMessage());

        verify(service, times(1)).update(type, id, cst, who, comment, sclData);
    }

    @Test
    void updateWebsocketsEvent_WhenCalledAndRuntimeExceptionThrownByService_ThenErrorResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var cst = ChangeSetType.MINOR;
        var who = "Who executed it";
        var comment = "Some comment";
        var sclData = "Some SCL Data";
        var errorMessage = "Some Error";

        var session = Mockito.mock(Session.class);
        var async = Mockito.mock(RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        var request = new UpdateEventRequest(session, type, id, cst, who, comment, sclData);

        when(service.update(type, id, cst, who, comment, sclData)).thenThrow(new RuntimeException(errorMessage));

        eventHandler.updateWebsocketsEvent(request);

        verify(session, times(1)).getAsyncRemote();
        ArgumentCaptor<ErrorResponse> captor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(async, times(1)).sendObject(captor.capture());
        var response = captor.getValue();
        assertEquals(1, response.getErrorMessages().size());
        var message = response.getErrorMessages().get(0);
        assertEquals(WEBSOCKET_GENERAL_ERROR_CODE, message.getCode());
        assertEquals(errorMessage, message.getMessage());

        verify(service, times(1)).update(type, id, cst, who, comment, sclData);
    }
}
