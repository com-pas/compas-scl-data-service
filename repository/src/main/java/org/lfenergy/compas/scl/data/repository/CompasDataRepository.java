package org.lfenergy.compas.scl.data.repository;

import org.lfenergy.compas.scl.SCL;
import org.lfenergy.compas.scl.data.model.SclType;

import java.util.UUID;

public interface CompasDataRepository {
    SCL findSCLByUUID(SclType type, UUID uuid);

    UUID create(SclType type, String name, SCL scl);

    void delete(SclType type, UUID uuid);
}
