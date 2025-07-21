// SPDX-FileCopyrightText: 2025 BearingPoint GmbH
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import javax.sql.DataSource;

public class SoftDeleteCompasSclDataPostgreSQLRepository extends CompasSclDataPostgreSQLRepository {

    private static final String SOFT_DELETE_SCL_FILE_SQL = """
        update scl_file
         set is_deleted = true
         where scl_file.id   = ?
         and   scl_file.type = ?
        """;

    private static final String SOFT_DELETE_SCL_FILE_SQL_BY_VERSION = """
        delete from scl_file
         where scl_file.id   = ?
         and   scl_file.type = ?
         and   scl_file.major_version = ?
         and   scl_file.minor_version = ?
         and   scl_file.patch_version = ?
        """;

    public SoftDeleteCompasSclDataPostgreSQLRepository(DataSource dataSource) {
        super(dataSource);
        DELETE_SCL_FILE_SQL = SOFT_DELETE_SCL_FILE_SQL;
        DELETE_SCL_FILE_SQL_BY_VERSION = SOFT_DELETE_SCL_FILE_SQL_BY_VERSION;
    }
}
