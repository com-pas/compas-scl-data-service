// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.compas.core.commons.exception.CompasException;
import org.lfenergy.compas.core.commons.model.ErrorResponse;
import org.lfenergy.compas.scl.data.model.ChangeSetType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.websocket.event.model.CreateEventRequest;
import org.lfenergy.compas.scl.data.websocket.event.model.GetEventRequest;
import org.lfenergy.compas.scl.data.websocket.event.model.GetVersionEventRequest;
import org.lfenergy.compas.scl.data.websocket.event.model.UpdateEventRequest;
import org.lfenergy.compas.scl.data.websocket.v1.model.CreateWsResponse;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetWsResponse;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateWsResponse;
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

        var session = mockSession();
        var request = new CreateEventRequest(session, type, name, who, comment, sclData);
        when(service.create(type, name, who, comment, sclData)).thenReturn(sclData);

        eventHandler.createWebsocketsEvent(request);

        var response = verifyResponse(session, CreateWsResponse.class);
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

        var session = mockSession();
        var request = new CreateEventRequest(session, type, name, who, comment, sclData);
        when(service.create(type, name, who, comment, sclData))
                .thenThrow(new CompasException(DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage));

        eventHandler.createWebsocketsEvent(request);

        verifyErrorResponse(session, DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage);
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

        var session = mockSession();
        var request = new CreateEventRequest(session, type, name, who, comment, sclData);
        when(service.create(type, name, who, comment, sclData)).thenThrow(new RuntimeException(errorMessage));

        eventHandler.createWebsocketsEvent(request);

        verifyErrorResponse(session, WEBSOCKET_GENERAL_ERROR_CODE, errorMessage);
        verify(service, times(1)).create(type, name, who, comment, sclData);
    }

    @Test
    void getWebsocketsEvent_WhenCalled_ThenGetResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var sclData = "Some SCL Data";

        var session = mockSession();
        var request = new GetEventRequest(session, type, id);
        when(service.findByUUID(type, id)).thenReturn(sclData);

        eventHandler.getWebsocketsEvent(request);

        var response = verifyResponse(session, GetWsResponse.class);
        assertEquals(sclData, response.getSclData());
        verify(service, times(1)).findByUUID(type, id);
    }

    @Test
    void getWebsocketsEvent_WhenCalledAndCompasExceptionThrownByService_ThenErrorResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var errorMessage = "Some Error";

        var session = mockSession();
        var request = new GetEventRequest(session, type, id);
        when(service.findByUUID(type, id))
                .thenThrow(new CompasException(DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage));

        eventHandler.getWebsocketsEvent(request);

        verifyErrorResponse(session, DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage);
        verify(service, times(1)).findByUUID(type, id);
    }

    @Test
    void getWebsocketsEvent_WhenCalledAndRuntimeExceptionThrownByService_ThenErrorResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var errorMessage = "Some Error";

        var session = mockSession();
        var request = new GetEventRequest(session, type, id);
        when(service.findByUUID(type, id)).thenThrow(new RuntimeException(errorMessage));

        eventHandler.getWebsocketsEvent(request);

        verifyErrorResponse(session, WEBSOCKET_GENERAL_ERROR_CODE, errorMessage);
        verify(service, times(1)).findByUUID(type, id);
    }

    @Test
    void getVersionWebsocketsEvent_WhenCalled_ThenGetResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var sclData = "Some SCL Data";
        var version = new Version("1.2.3");

        var session = mockSession();
        var request = new GetVersionEventRequest(session, type, id, version);
        when(service.findByUUID(type, id, version)).thenReturn(sclData);

        eventHandler.getVersionWebsocketsEvent(request);

        var response = verifyResponse(session, GetWsResponse.class);
        assertEquals(sclData, response.getSclData());
        verify(service, times(1)).findByUUID(type, id, version);
    }

    @Test
    void getVersionWebsocketsEvent_WhenCalledAndCompasExceptionThrownByService_ThenErrorResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var version = new Version("1.2.3");
        var errorMessage = "Some Error";

        var session = mockSession();
        var request = new GetVersionEventRequest(session, type, id, version);
        when(service.findByUUID(type, id, version))
                .thenThrow(new CompasException(DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage));

        eventHandler.getVersionWebsocketsEvent(request);

        verifyErrorResponse(session, DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage);
        verify(service, times(1)).findByUUID(type, id, version);
    }

    @Test
    void getVersionWebsocketsEvent_WhenCalledAndRuntimeExceptionThrownByService_ThenErrorResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var version = new Version("1.2.3");
        var errorMessage = "Some Error";

        var session = mockSession();
        var request = new GetVersionEventRequest(session, type, id, version);
        when(service.findByUUID(type, id, version)).thenThrow(new RuntimeException(errorMessage));

        eventHandler.getVersionWebsocketsEvent(request);

        verifyErrorResponse(session, WEBSOCKET_GENERAL_ERROR_CODE, errorMessage);
        verify(service, times(1)).findByUUID(type, id, version);
    }

    @Test
    void updateWebsocketsEvent_WhenCalled_ThenUpdateResponseReturned() {
        var type = SclFileType.CID;
        var id = UUID.randomUUID();
        var cst = ChangeSetType.MINOR;
        var who = "Who executed it";
        var comment = "Some comment";
        var sclData = "Some SCL Data";

        var session = mockSession();
        var request = new UpdateEventRequest(session, type, id, cst, who, comment, sclData);
        when(service.update(type, id, cst, who, comment, sclData)).thenReturn(sclData);

        eventHandler.updateWebsocketsEvent(request);

        var response = verifyResponse(session, UpdateWsResponse.class);
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

        var session = mockSession();
        var request = new UpdateEventRequest(session, type, id, cst, who, comment, sclData);
        when(service.update(type, id, cst, who, comment, sclData))
                .thenThrow(new CompasException(DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage));

        eventHandler.updateWebsocketsEvent(request);

        verifyErrorResponse(session, DUPLICATE_SCL_NAME_ERROR_CODE, errorMessage);
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

        var session = mockSession();
        var request = new UpdateEventRequest(session, type, id, cst, who, comment, sclData);
        when(service.update(type, id, cst, who, comment, sclData)).thenThrow(new RuntimeException(errorMessage));

        eventHandler.updateWebsocketsEvent(request);

        verifyErrorResponse(session, WEBSOCKET_GENERAL_ERROR_CODE, errorMessage);
        verify(service, times(1)).update(type, id, cst, who, comment, sclData);
    }

    private Session mockSession() {
        var session = Mockito.mock(Session.class);
        var async = Mockito.mock(RemoteEndpoint.Async.class);
        when(session.getAsyncRemote()).thenReturn(async);
        return session;
    }

    private <T> T verifyResponse(Session session, Class<T> responseClass) {
        verify(session, times(1)).getAsyncRemote();
        ArgumentCaptor<T> captor = ArgumentCaptor.forClass(responseClass);
        verify(session.getAsyncRemote(), times(1)).sendObject(captor.capture());
        return captor.getValue();
    }

    private void verifyErrorResponse(Session session, String errorCode, String errorMessage) {
        verify(session, times(1)).getAsyncRemote();
        ArgumentCaptor<ErrorResponse> captor = ArgumentCaptor.forClass(ErrorResponse.class);
        verify(session.getAsyncRemote(), times(1)).sendObject(captor.capture());
        var response = captor.getValue();
        assertEquals(1, response.getErrorMessages().size());
        var message = response.getErrorMessages().get(0);
        assertEquals(errorCode, message.getCode());
        assertEquals(errorMessage, message.getMessage());
    }
}
