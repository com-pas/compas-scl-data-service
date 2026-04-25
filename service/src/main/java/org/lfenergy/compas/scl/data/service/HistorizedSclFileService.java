// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.lfenergy.compas.scl.data.model.IAbstractItem;
import org.lfenergy.compas.scl.data.model.IHistoryItem;
import org.lfenergy.compas.scl.data.model.IItem;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.repository.HistorizedSclFileRepository;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class HistorizedSclFileService {

    private final CompasSclDataRepository repository;
    private final HistorizedSclFileRepository historizedSclFileRepository;

    @Inject
    public HistorizedSclFileService(CompasSclDataRepository repository, HistorizedSclFileRepository historizedSclFileRepository) {
        this.repository = repository;
        this.historizedSclFileRepository = historizedSclFileRepository;
    }

    public void insertSclFileWithHistory(SclFileType type, UUID id, String name, String content, Version version,
                                                      String who, List<String> labels, String comment, String fileName) {
        var whoToUse = who != null ? who : "Unknown";
        repository.create(type, id, name, content, version, whoToUse, labels);
        historizedSclFileRepository.createEntry(id, version, "application/xml", fileName, comment);
    }

    public List<IItem> list(SclFileType type) {
        return repository.list(type);
    }

    public List<IHistoryItem> listVersionsByUUID(SclFileType type, UUID id) {
        return repository.listVersionsByUUID(type, id);
    }

    public String findByUUID(SclFileType type, UUID id) {
        return repository.findByUUID(type, id);
    }

    public IAbstractItem findMetaInfoByUUID(SclFileType type, UUID id) {
        return repository.findMetaInfoByUUID(type, id);
    }

    public String findByUUID(SclFileType type, UUID id, Version version) {
        return repository.findByUUID(type, id, version);
    }

    public boolean hasDuplicateSclName(SclFileType type, String name) {
        return repository.hasDuplicateSclName(type, name);
    }

    public void create(SclFileType type, UUID id, String name, String scl, Version version, String who, List<String> labels) {
        repository.create(type, id, name, scl, version, who, labels);
    }

    public void delete(SclFileType type, UUID id) {
        repository.delete(type, id);
    }

    public void delete(SclFileType type, UUID id, Version version) {
        repository.delete(type, id, version);
    }
}
