package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lfenergy.compas.scl.data.model.IHistoryMetaItem;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.SUPPORTS;

@ApplicationScoped
public class CompasSclHistoryService {

    private final CompasSclDataRepository repository;

    @Inject
    public CompasSclHistoryService(CompasSclDataRepository repository) {
        this.repository = repository;
    }

    /**
     * List the latest version of all SCL File history entries.
     *
     * @return The List of Items found.
     */
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory() {
        return repository.listHistory();
    }

    /**
     * Get a specific version of a specific SCL XML File (using the UUID) for a specific type.
     *
     * @param id      The UUID of the record to search for.
     * @param version The version to search for.
     * @return The found version of the SCL XML Files.
     */
    @Transactional(SUPPORTS)
    public String findFileByIdAndVersion(UUID id, Version version) {
        return repository.findByUUID(id, version);
    }

    /**
     * List the history entries of an SCL File specified by an uuid.
     *
     * @return The List of Items found.
     */
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistoryVersionsForResource(UUID id) {
        return repository.listHistoryVersionsByUUID(id);
    }

    /**
     * List the latest version of all SCL File history entries.
     *
     * @return The List of Items found.
     */
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> searchResourcesHistoryVersions(SclFileType type, String name, String author, OffsetDateTime from, OffsetDateTime to) {
        return repository.listHistory(type, name, author, from, to);
    }
}
