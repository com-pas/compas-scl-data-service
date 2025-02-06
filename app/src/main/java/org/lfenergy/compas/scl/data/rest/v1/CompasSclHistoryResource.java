package org.lfenergy.compas.scl.data.rest.v1;

import io.quarkus.security.Authenticated;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.lfenergy.compas.scl.data.model.IHistoryMetaItem;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.service.CompasSclHistoryService;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.lfenergy.compas.scl.rest.api.HistoryResource;
import org.lfenergy.compas.scl.rest.api.beans.DataResourceHistory;
import org.lfenergy.compas.scl.rest.api.beans.DataResourceSearch;
import org.lfenergy.compas.scl.rest.api.beans.DataResourcesResult;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Authenticated
@RequestScoped
@Path("/api")
public class CompasSclHistoryResource implements HistoryResource {

    private final CompasSclHistoryService compasSclHistoryService;

    @Inject
    public CompasSclHistoryResource(CompasSclHistoryService compasSclHistoryService) {
        this.compasSclHistoryService = compasSclHistoryService;
    }

    @Override
    public DataResourcesResult searchForResources(DataResourceSearch searchQuery) {
        List<IHistoryMetaItem> historyItems = fetchHistoryItems(searchQuery);
        DataResourcesResult result = new DataResourcesResult();
        result.setResults(historyItems.stream().map(HistoryMapper::convertToDataResource).toList());
        return result;
    }

    @Override
    public DataResourceHistory retrieveDataResourceHistory(String id) {
        List<IHistoryMetaItem> historyItems = compasSclHistoryService.listHistoryVersionsForResource(UUID.fromString(id));
        DataResourceHistory resourcesHistories = new DataResourceHistory();
        resourcesHistories.setVersions(historyItems.stream().map(HistoryMapper::convertToDataResourceVersion).toList());
        return resourcesHistories;
    }

    @Override
    public Response retrieveDataResourceByVersion(String id, String version) {
        String fetchedData = compasSclHistoryService.findFileByIdAndVersion(UUID.fromString(id), new Version(version));
        return Response.status(Response.Status.OK).entity(fetchedData).type(MediaType.APPLICATION_XML).build();
    }

    private List<IHistoryMetaItem> fetchHistoryItems(DataResourceSearch searchQuery) {
        String uuid = searchQuery.getUuid();

        if (uuid != null) {
            return compasSclHistoryService.listHistoryVersionsForResource(UUID.fromString(uuid));
        }

        SclFileType type = searchQuery.getType() != null ? SclFileType.valueOf(searchQuery.getType()) : null;
        String name = searchQuery.getName();
        String author = searchQuery.getAuthor();
        OffsetDateTime from = DateUtil.convertToOffsetDateTime(searchQuery.getFrom());
        OffsetDateTime to = DateUtil.convertToOffsetDateTime(searchQuery.getTo());

        if (type != null || name != null || author != null || from != null || to != null) {
            return compasSclHistoryService.searchResourcesHistoryVersions(type, name, author, from, to);
        }
        return compasSclHistoryService.listHistory();
    }
}
