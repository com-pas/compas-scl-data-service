// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.persistence;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.entities.ArchivedResource;
import org.lfenergy.compas.scl.data.entities.ResourceTag;
import org.lfenergy.compas.scl.data.repository.ArchivedResourceRepository;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ArchivedResourcePersistenceTest {

    @Inject
    ArchivedResourceRepository archivedResourceRepository;

    @Inject
    EntityManager entityManager;

    @Test
    @Transactional
    void persistAndLoadArchivedResource_WithTags_UsesArchivedResourceTables() {
        entityManager.createNativeQuery("DELETE FROM archived_resource_tag").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM archived_resource").executeUpdate();

        UUID resourceId = UUID.randomUUID();

        ArchivedResource entity = new ArchivedResource();
        entity.resourceId = resourceId;
        entity.name = "archive.pdf";
        entity.version = "1.0.0";
        entity.author = "alice";
        entity.type = "SCD";
        entity.contentType = "application/pdf";
        entity.modifiedAt = OffsetDateTime.now().minusDays(1);
        entity.archivedAt = OffsetDateTime.now();
        entity.archivingComment = "retention copy";

        ResourceTag tag = new ResourceTag();
        tag.key = "documentType";
        tag.value = "manual";
        entity.fields.add(tag);

        archivedResourceRepository.persist(entity);
        entityManager.flush();
        entityManager.clear();

        var results = archivedResourceRepository.findAllByResourceId(resourceId);

        assertEquals(1, results.size());
        ArchivedResource persisted = results.get(0);
        assertNotNull(persisted.id);
        assertEquals("archive.pdf", persisted.name);
        assertEquals("retention copy", persisted.archivingComment);
        assertEquals(1, persisted.fields.size());
        assertEquals("documentType", persisted.fields.get(0).key);
        assertEquals("manual", persisted.fields.get(0).value);
    }
}