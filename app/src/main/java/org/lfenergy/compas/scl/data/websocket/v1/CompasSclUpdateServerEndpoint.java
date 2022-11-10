// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.v1;

import io.quarkus.security.Authenticated;
import io.vertx.mutiny.core.eventbus.EventBus;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.lfenergy.compas.core.websocket.ErrorResponseEncoder;
import org.lfenergy.compas.scl.data.rest.UserInfoProperties;
import org.lfenergy.compas.scl.data.websocket.event.model.UpdateEventRequest;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.UpdateRequestDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.UpdateResponseEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateRequest;
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
@ServerEndpoint(value = "/scl-ws/v1/{" + TYPE_PATH_PARAM + "}/update",
        decoders = {UpdateRequestDecoder.class},
        encoders = {UpdateResponseEncoder.class, ErrorResponseEncoder.class})
public class CompasSclUpdateServerEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasSclUpdateServerEndpoint.class);

    private final EventBus eventBus;
    private final JsonWebToken jsonWebToken;
    private final UserInfoProperties userInfoProperties;

    @Inject
    public CompasSclUpdateServerEndpoint(EventBus eventBus,
                                         JsonWebToken jsonWebToken,
                                         UserInfoProperties userInfoProperties) {
        this.eventBus = eventBus;
        this.jsonWebToken = jsonWebToken;
        this.userInfoProperties = userInfoProperties;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Starting (update) session {} for type {}.", session.getId(), type);
    }

    @OnMessage
    public void onUpdateMessage(Session session, UpdateRequest request,
                                @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.info("Message (update) from session {} for type {}.", session.getId(), type);

        String who = jsonWebToken.getClaim(userInfoProperties.who());
        LOGGER.trace("Username used for Who {}", who);

        eventBus.send("update-ws", new UpdateEventRequest(
                session, SclFileType.valueOf(type), request.getId(), request.getChangeSetType(),
                who, request.getComment(), request.getSclData()));
    }

    @OnError
    public void onError(Session session, @PathParam(TYPE_PATH_PARAM) String type, Throwable throwable) {
        LOGGER.warn("Error (update) with session {} for type {}.", session.getId(), type, throwable);
        handleException(session, throwable);
    }

    @OnClose
    public void onClose(Session session, @PathParam(TYPE_PATH_PARAM) String type) {
        LOGGER.debug("Closing (update) session {} for type {}.", session.getId(), type);
    }
}
