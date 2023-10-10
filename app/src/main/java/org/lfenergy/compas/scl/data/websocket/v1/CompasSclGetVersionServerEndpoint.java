// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.security.Authenticated;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lfenergy.compas.core.websocket.ErrorResponseEncoder;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.websocket.event.model.GetVersionEventRequest;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.GetVersionWsRequestDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.GetWsResponseEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetVersionWsRequest;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import static org.lfenergy.compas.core.websocket.WebsocketSupport.handleException;
import static org.lfenergy.compas.scl.data.rest.Constants.TYPE_PATH_PARAM;

@Authenticated
@ApplicationScoped
@ServerEndpoint(value = "/scl-ws/v1/{" + TYPE_PATH_PARAM + "}/get-version",
        decoders = {GetVersionWsRequestDecoder.class},
        encoders = {GetWsResponseEncoder.class, ErrorResponseEncoder.class})
public class CompasSclGetVersionServerEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(CompasSclGetVersionServerEndpoint.class);

    private final EventBus eventBus;

    @Inject
    public CompasSclGetVersionServerEndpoint(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Starting session {} for type {}.", session.getId(), type);
    }

    @OnMessage
    public void onGetVersionMessage(Session session,
                                    @Valid GetVersionWsRequest request,
                                    @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.info("Message from session {} for type {}.", session.getId(), type);

        eventBus.send("get-version-ws", new GetVersionEventRequest(session, SclFileType.valueOf(type),
                request.getId(), new Version(request.getVersion())));
    }

    @OnError
    public void onError(Session session, @PathParam(TYPE_PATH_PARAM) String type, Throwable throwable) {
        LOGGER.warn("Error with session {} for type {}.", session.getId(), type, throwable);
        handleException(session, throwable);
    }

    @OnClose
    public void onClose(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Closing session {} for type {}.", session.getId(), type);
    }
}
