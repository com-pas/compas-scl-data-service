// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.security.Authenticated;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.core.websocket.ErrorResponseEncoder;
import org.lfenergy.compas.scl.data.rest.UserInfoProperties;
import org.lfenergy.compas.scl.data.rest.v1.event.CreateEventRequest;
import org.lfenergy.compas.scl.data.rest.v1.model.CreateRequest;
import org.lfenergy.compas.scl.data.rest.v1.websocket.CreateRequestDecoder;
import org.lfenergy.compas.scl.data.rest.v1.websocket.CreateResponseEncoder;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import static org.lfenergy.compas.scl.data.rest.Constants.TYPE_PATH_PARAM;

@Authenticated
@ApplicationScoped
@ServerEndpoint(value = "/scl-ws/v1/{" + TYPE_PATH_PARAM + "}/create",
        decoders = {CreateRequestDecoder.class},
        encoders = {CreateResponseEncoder.class, ErrorResponseEncoder.class})
public class CompasSclCreateServerEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasSclCreateServerEndpoint.class);

    private final EventBus eventBus;

    @Inject
    JsonWebToken jsonWebToken;

    @Inject
    UserInfoProperties userInfoProperties;

    @Inject
    public CompasSclCreateServerEndpoint(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Starting session {} for type {}.", session.getId(), type);
    }

    @OnMessage
    public void onCreateMessage(Session session, CreateRequest request, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.info("Message from session {} for type {}.", session.getId(), type);

        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        eventBus.send("create-ws", new CreateEventRequest(
                session, SclFileType.valueOf(type), request.getName(), who, request.getComment(), request.getSclData()));
    }

    @OnError
    public void onError(Session session, @PathParam(TYPE_PATH_PARAM) String type, Throwable throwable) {
        LOGGER.warn("Error with session {} for type {}.", session.getId(), type, throwable);
    }

    @OnClose
    public void onClose(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Closing session {} for type {}.", session.getId(), type);
    }
}
