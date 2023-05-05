// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.websocket.event;

import io.quarkus.vertx.ConsumeEvent;
import org.lfenergy.compas.core.websocket.WebsocketHandler;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;
import org.lfenergy.compas.scl.data.websocket.event.model.CreateEventRequest;
import org.lfenergy.compas.scl.data.websocket.event.model.GetEventRequest;
import org.lfenergy.compas.scl.data.websocket.event.model.GetVersionEventRequest;
import org.lfenergy.compas.scl.data.websocket.event.model.UpdateEventRequest;
import org.lfenergy.compas.scl.data.websocket.v1.model.CreateWsResponse;
import org.lfenergy.compas.scl.data.websocket.v1.model.GetWsResponse;
import org.lfenergy.compas.scl.data.websocket.v1.model.UpdateWsResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Event Handler used to execute the validation asynchronized.
 */
@ApplicationScoped
public class CompasSclDataEventHandler {
    private final CompasSclDataService compasSclDataService;

    @Inject
    public CompasSclDataEventHandler(CompasSclDataService compasSclDataService) {
        this.compasSclDataService = compasSclDataService;
    }

    @ConsumeEvent(value = "create-ws", blocking = true)
    public void createWebsocketsEvent(CreateEventRequest request) {
        new WebsocketHandler<CreateWsResponse>().execute(request.getSession(), () -> {
            var response = new CreateWsResponse();
            response.setSclData(compasSclDataService.create(request.getType(), request.getName(), request.getWho(),
                    request.getComment(), request.getSclData()));
            return response;
        });
    }

    @ConsumeEvent(value = "get-ws")
    public void getWebsocketsEvent(GetEventRequest request) {
        new WebsocketHandler<GetWsResponse>().execute(request.getSession(), () -> {
            var response = new GetWsResponse();
            response.setSclData(compasSclDataService.findByUUID(request.getType(), request.getId()));
            return response;
        });
    }

    @ConsumeEvent(value = "get-version-ws")
    public void getVersionWebsocketsEvent(GetVersionEventRequest request) {
        new WebsocketHandler<GetWsResponse>().execute(request.getSession(), () -> {
            var response = new GetWsResponse();
            response.setSclData(compasSclDataService.findByUUID(request.getType(), request.getId(), request.getVersion()));
            return response;
        });
    }

    @ConsumeEvent(value = "update-ws", blocking = true)
    public void updateWebsocketsEvent(UpdateEventRequest request) {
        new WebsocketHandler<UpdateWsResponse>().execute(request.getSession(), () -> {
            var response = new UpdateWsResponse();
            response.setSclData(compasSclDataService.update(request.getType(), request.getId(), request.getChangeSetType(),
                    request.getWho(), request.getComment(), request.getSclData()));
            return response;
        });
    }
}
