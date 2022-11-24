// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.security.Authenticated;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.core.websocket.ErrorResponseEncoder;
import org.lfenergy.compas.scl.data.rest.UserInfoProperties;
import org.lfenergy.compas.scl.data.websocket.event.model.CreateEventRequest;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.CreateWsRequestDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.CreateWsResponseEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.CreateWsRequest;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import static org.lfenergy.compas.core.websocket.WebsocketSupport.handleException;
import static org.lfenergy.compas.scl.data.rest.Constants.TYPE_PATH_PARAM;

@Authenticated
@ApplicationScoped
@ServerEndpoint(value = "/scl-ws/v1/{" + TYPE_PATH_PARAM + "}/create",
        decoders = {CreateWsRequestDecoder.class},
        encoders = {CreateWsResponseEncoder.class, ErrorResponseEncoder.class})
public class CompasSclCreateServerEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(CompasSclCreateServerEndpoint.class);

    private final EventBus eventBus;
    private final JsonWebToken jsonWebToken;
    private final UserInfoProperties userInfoProperties;

    @Inject
    public CompasSclCreateServerEndpoint(EventBus eventBus,
                                         JsonWebToken jsonWebToken,
                                         UserInfoProperties userInfoProperties) {
        this.eventBus = eventBus;
        this.jsonWebToken = jsonWebToken;
        this.userInfoProperties = userInfoProperties;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Starting (create) session {} for type {}.", session.getId(), type);
    }

    @OnMessage
    public void onCreateMessage(Session session,
                                @Valid CreateWsRequest request,
                                @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.info("Message (create) from session {} for type {}.", session.getId(), type);

        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        eventBus.send("create-ws", new CreateEventRequest(
                session, SclFileType.valueOf(type), request.getName(), who, request.getComment(), request.getSclData()));
    }

    @OnError
    public void onError(Session session, @PathParam(TYPE_PATH_PARAM) String type, Throwable throwable) {
        LOGGER.warn("Error (create) with session {} for type {}.", session.getId(), type, throwable);
        handleException(session, throwable);
    }

    @OnClose
    public void onClose(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Closing (create) session {} for type {}.", session.getId(), type);
    }
}
