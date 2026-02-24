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
import org.lfenergy.compas.scl.data.websocket.event.model.UpdateEventRequest;
import org.lfenergy.compas.scl.data.websocket.v1.decoder.UpdateWsRequestDecoder;
import org.lfenergy.compas.scl.data.websocket.v1.encoder.UpdateWsResponseEncoder;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateWsRequest;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;

import static org.lfenergy.compas.core.websocket.WebsocketSupport.handleException;
import static org.lfenergy.compas.scl.data.rest.Constants.TYPE_PATH_PARAM;
import static org.lfenergy.compas.scl.data.rest.Constants.UPDATE_ROLE;

@Authenticated
@ApplicationScoped
@ServerEndpoint(value = "/scl-ws/v1/{" + TYPE_PATH_PARAM + "}/update",
        decoders = {UpdateWsRequestDecoder.class},
        encoders = {UpdateWsResponseEncoder.class, ErrorResponseEncoder.class})
public class CompasSclUpdateServerEndpoint {
    private static final Logger LOGGER = LogManager.getLogger(CompasSclUpdateServerEndpoint.class);

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
        var requiredRole = type + "_" + UPDATE_ROLE;
        if (!jsonWebToken.getGroups().contains(requiredRole)) {
            LOGGER.warn("User lacks role {} for update session {}.", requiredRole, session.getId());
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Forbidden"));
            } catch (IOException e) {
                LOGGER.error("Error closing unauthorized session {}.", session.getId(), e);
            }
        }
    }

    @OnMessage
    public void onUpdateMessage(Session session,
                                @Valid UpdateWsRequest request,
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
