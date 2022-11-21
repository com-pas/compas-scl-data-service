// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.security.Authenticated;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.core.websocket.ErrorResponseEncoder;
import org.lfenergy.compas.scl.data.websocket.event.model.GetEventRequest;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.GetWsRequestDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.GetWsResponseEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetWsRequest;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import static org.lfenergy.compas.core.websocket.WebsocketSupport.handleException;
import static org.lfenergy.compas.scl.data.rest.Constants.TYPE_PATH_PARAM;

@Authenticated
@ApplicationScoped
@ServerEndpoint(value = "/scl-ws/v1/{" + TYPE_PATH_PARAM + "}/get",
        decoders = {GetWsRequestDecoder.class},
        encoders = {GetWsResponseEncoder.class, ErrorResponseEncoder.class})
public class CompasSclGetServerEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(CompasSclGetServerEndpoint.class);

    private final EventBus eventBus;

    @Inject
    public CompasSclGetServerEndpoint(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Starting (get) session {} for type {}.", session.getId(), type);
    }

    @OnMessage
    public void onGetMessage(Session session,
                             GetWsRequest request,
                             @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.info("Message (get) from session {} for type {}.", session.getId(), type);

        eventBus.send("get-ws", new GetEventRequest(session, SclFileType.valueOf(type), request.getId()));
    }

    @OnError
    public void onError(Session session, @PathParam(TYPE_PATH_PARAM) String type, Throwable throwable) {
        LOGGER.warn("Error (get) with session {} for type {}.", session.getId(), type, throwable);
        handleException(session, throwable);
    }

    @OnClose
    public void onClose(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Closing (get) session {} for type {}.", session.getId(), type);
    }
}
