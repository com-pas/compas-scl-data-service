// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import jakarta.transaction.Transactional;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.*;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.*;

public class SoftDeleteCompasSclDataPostgreSQLRepository extends CompasSclDataPostgreSQLRepository {

    public SoftDeleteCompasSclDataPostgreSQLRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(REQUIRED)
    public void delete(SclFileType type, UUID id) {
        var sql = """
                UPDATE scl_file
                 SET is_deleted = true
                 where scl_file.id   = ?
                 and   scl_file.type = ?
                """;

        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setString(2, type.name());
            stmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_DELETE_ERROR_CODE, "Error removing SCL from database!", exp);
        }
    }

    @Override
    @Transactional(REQUIRED)
    public void delete(SclFileType type, UUID id, Version version) {
        var sql = """
                UPDATE scl_file
                 SET is_deleted = true
                 where scl_file.id   = ?
                 and   scl_file.type = ?
                 and   scl_file.major_version = ?
                 and   scl_file.minor_version = ?
                 and   scl_file.patch_version = ?
                """;

        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setString(2, type.name());
            stmt.setInt(3, version.getMajorVersion());
            stmt.setInt(4, version.getMinorVersion());
            stmt.setInt(5, version.getPatchVersion());
            stmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_DELETE_ERROR_CODE, "Error removing SCL (version) from database!", exp);
        }
    }
}
