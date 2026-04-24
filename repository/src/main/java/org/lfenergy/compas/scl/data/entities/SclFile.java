// SPDX-FileCopyrightText: 2026 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Read-only JPA entity mapping the {@code scl_file} table.
 * Accessed only via the eager {@code @ManyToOne} relationship on {@link HistorizedSclFile}.
 */
@Entity
@Table(name = "scl_file")
public class SclFile {

    @EmbeddedId
    public SclFileId id;

    public String name;

    public String type;

    @Column(name = "is_deleted")
    public boolean isDeleted;

    @Column(name = "created_by")
    public String createdBy;

    @Column(name = "creation_date")
    public LocalDateTime creationDate;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SclFile other = (SclFile) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
