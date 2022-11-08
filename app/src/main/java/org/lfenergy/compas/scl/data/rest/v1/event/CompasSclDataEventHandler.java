// SPDX-FileCopyrightText: 2022 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.rest.v1.event;

import io.quarkus.vertx.ConsumeEvent;
import org.lfenergy.compas.core.websocket.WebsocketHandler;
import org.lfenergy.compas.scl.data.rest.v1.model.CreateResponse;
import org.lfenergy.compas.scl.data.rest.v1.model.UpdateResponse;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
        new WebsocketHandler<CreateResponse>().execute(request.getSession(), () -> {
            var response = new CreateResponse();
            response.setSclData(compasSclDataService.create(request.getType(), request.getName(), request.getWho(),
                    request.getComment(), request.getSclData()));
            return response;
        });
    }

    @ConsumeEvent(value = "update-ws", blocking = true)
    public void updateWebsocketsEvent(UpdateEventRequest request) {
        new WebsocketHandler<UpdateResponse>().execute(request.getSession(), () -> {
            var response = new UpdateResponse();
            response.setSclData(compasSclDataService.update(request.getType(), request.getId(), request.getChangeSetType(),
                    request.getWho(), request.getComment(), request.getSclData()));
            return response;
        });
    }
}
