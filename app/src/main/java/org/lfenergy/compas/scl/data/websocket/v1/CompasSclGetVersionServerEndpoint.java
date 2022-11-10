// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.security.Authenticated;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.lfenergy.compas.core.websocket.ErrorResponseEncoder;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.websocket.event.model.GetVersionEventRequest;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.GetVersionRequestDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.GetResponseEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetVersionRequest;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import static org.lfenergy.compas.core.websocket.WebsocketSupport.handleException;
import static org.lfenergy.compas.scl.data.rest.Constants.TYPE_PATH_PARAM;

@Authenticated
@ApplicationScoped
@ServerEndpoint(value = "/scl-ws/v1/{" + TYPE_PATH_PARAM + "}/get-version",
        decoders = {GetVersionRequestDecoder.class},
        encoders = {GetResponseEncoder.class, ErrorResponseEncoder.class})
public class CompasSclGetVersionServerEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasSclGetVersionServerEndpoint.class);

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
                                    GetVersionRequest request,
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
