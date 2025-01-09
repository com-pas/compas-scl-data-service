// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.*;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static jakarta.transaction.Transactional.TxType.REQUIRED;
import static jakarta.transaction.Transactional.TxType.SUPPORTS;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.*;

@ApplicationScoped
public class CompasSclDataPostgreSQLRepository implements CompasSclDataRepository {
    private static final String ID_FIELD = "id";
    private static final String MAJOR_VERSION_FIELD = "major_version";
    private static final String MINOR_VERSION_FIELD = "minor_version";
    private static final String PATCH_VERSION_FIELD = "patch_version";
    private static final String NAME_FIELD = "name";
    private static final String KEY_FIELD = "key";
    private static final String VALUE_FIELD = "value";
    private static final String LOCATIONMETAITEM_DESCRIPTION_FIELD = "description";
    private static final String SCL_DATA_FIELD = "scl_data";
    private static final String HITEM_WHO_FIELD = "hitem_who";
    private static final String HITEM_WHEN_FIELD = "hitem_when";
    private static final String HITEM_WHAT_FIELD = "hitem_what";
    private static final String HISTORYMETAITEM_TYPE_FIELD = "type";
    private static final String HISTORYMETAITEM_AUTHOR_FIELD = "author";
    private static final String HISTORYMETAITEM_COMMENT_FIELD = "comment";
    private static final String HISTORYMETAITEM_CHANGEDAT_FIELD = "changedAt";
    private static final String HISTORYMETAITEM_AVAILABLE_FIELD = "available";
    private static final String HISTORYMETAITEM_ARCHIVED_FIELD = "archived";
    private static final String HISTORYMETAITEM_IS_DELETED_FIELD = "is_deleted";
    private static final String ARCHIVEMETAITEM_LOCATION_FIELD = "location";
    private static final String ARCHIVEMETAITEM_AUTHOR_FIELD = "author";
    private static final String ARCHIVEMETAITEM_APPROVER_FIELD = "approver";
    private static final String ARCHIVEMETAITEM_TYPE_FIELD = "type";
    private static final String ARCHIVEMETAITEM_CONTENT_TYPE_FIELD = "content_type";
    private static final String ARCHIVEMETAITEM_VOLTAGE_FIELD = "voltage";
    private static final String ARCHIVEMETAITEM_MODIFIED_AT_FIELD = "modified_at";
    private static final String ARCHIVEMETAITEM_ARCHIVED_AT_FIELD = "archived_at";

    private final DataSource dataSource;

    @Inject
    public CompasSclDataPostgreSQLRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Transactional(SUPPORTS)
    public List<IItem> list(SclFileType type) {
        var sql = """
                select scl_file.id, scl_file.name,
                       scl_file.major_version, scl_file.minor_version, scl_file.patch_version,
                       scl_labels.label_values as labels
                  from (select distinct on (scl_file.id) *
                          from scl_file
                         where scl_file.type = ?
                         and scl_file.is_deleted = false
                         order by scl_file.id
                                , scl_file.major_version desc
                                , scl_file.minor_version desc
                                , scl_file.patch_version desc
                  ) scl_file
                  left outer join (
                    select scl_label.scl_id, scl_label.major_version, scl_label.minor_version, scl_label.patch_version,
                           array_agg(scl_label.label_value) AS label_values
                              from scl_label
                          group by scl_label.scl_id, scl_label.major_version, scl_label.minor_version, scl_label.patch_version) scl_labels
                        on scl_labels.scl_id        = scl_file.id
                       and scl_labels.major_version = scl_file.major_version
                       and scl_labels.minor_version = scl_file.minor_version
                       and scl_labels.patch_version = scl_file.patch_version
                  order by scl_file.name, scl_file.major_version, scl_file.minor_version, scl_file.patch_version
                """;

        List<IItem> items = new ArrayList<>();
        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.name());

            try (var resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new Item(resultSet.getString(ID_FIELD),
                            resultSet.getString(NAME_FIELD),
                            createVersion(resultSet),
                            createLabelList(resultSet.getArray("labels"))));
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error listing scl entries from database!", exp);
        }
        return items;
    }

    @Override
    @Transactional(SUPPORTS)
    public List<IHistoryItem> listVersionsByUUID(SclFileType type, UUID id) {
        var sql = """
                select scl_file.id, scl_file.name
                     , scl_file.major_version, scl_file.minor_version, scl_file.patch_version
                     , (xpath('/scl:Hitem/@who', scl_data.header, ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1] hitem_who
                     , (xpath('/scl:Hitem/@when', scl_data.header, ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1] hitem_when
                     , (xpath('/scl:Hitem/@what', scl_data.header, ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1] hitem_what
                 from scl_file
                 left outer join (
                    select id, major_version, minor_version, patch_version,
                           unnest(
                              xpath( '(/scl:SCL/scl:Header//scl:Hitem[(not(@revision) or @revision="") and @version="' || major_version || '.' || minor_version || '.' || patch_version || '"])[1]'
                                   , scl_data::xml
                                   , ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL']])) as header
                              from scl_file) scl_data
                        on scl_data.id            = scl_file.id
                       and scl_data.major_version = scl_file.major_version
                       and scl_data.minor_version = scl_file.minor_version
                       and scl_data.patch_version = scl_file.patch_version
                 where scl_file.id   = ?
                 and   scl_file.type = ?
                 and   scl_file.is_deleted = false
                 order by scl_file.major_version
                        , scl_file.minor_version
                        , scl_file.patch_version
                """;

        List<IHistoryItem> items = new ArrayList<>();
        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setString(2, type.name());

            try (var resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new HistoryItem(id.toString(),
                            resultSet.getString(NAME_FIELD),
                            createVersion(resultSet),
                            resultSet.getString(HITEM_WHO_FIELD),
                            resultSet.getString(HITEM_WHEN_FIELD),
                            resultSet.getString(HITEM_WHAT_FIELD)));
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error selecting versions from database!", exp);
        }
        return items;
    }

    @Override
    @Transactional(SUPPORTS)
    public String findByUUID(SclFileType type, UUID id) {
        // Use the find meta info to retrieve info about the latest version.
        var metaInfo = findMetaInfoByUUID(type, id);
        // Next return the data using the meta info.
        return findByUUID(type, id, new Version(metaInfo.getVersion()));
    }

    @Override
    @Transactional(SUPPORTS)
    public String findByUUID(SclFileType type, UUID id, Version version) {
        var sql = """
                select scl_file.scl_data
                  from scl_file
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

            try (var resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(SCL_DATA_FIELD);
                }
                var message = String.format("No record found for type '%s' with ID '%s' and version '%s'", type, id, version);
                throw new CompasNoDataFoundException(message);
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error select scl data from database!", exp);
        }
    }

    @Override
    @Transactional(SUPPORTS)
    public String findByUUID(UUID id, Version version) {
        var sql = """
                select scl_file.scl_data
                  from scl_file
                 where scl_file.id   = ?
                 and   scl_file.major_version = ?
                 and   scl_file.minor_version = ?
                 and   scl_file.patch_version = ?
                """;

        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setInt(2, version.getMajorVersion());
            stmt.setInt(3, version.getMinorVersion());
            stmt.setInt(4, version.getPatchVersion());

            try (var resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(SCL_DATA_FIELD);
                }
                var message = String.format("No record found with ID '%s' and version '%s'", id, version);
                throw new CompasNoDataFoundException(message);
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error select scl data from database!", exp);
        }
    }

    @Override
    @Transactional(SUPPORTS)
    public boolean hasDuplicateSclName(SclFileType type, String name) {
        var sql = """
                select distinct on (scl_file.id) scl_file.name
                  from scl_file
                 where scl_file.type = ?
                 order by scl_file.id
                        , scl_file.major_version desc
                        , scl_file.minor_version desc
                        , scl_file.patch_version desc
                """;

        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.name());

            try (var resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    var usedName = resultSet.getString(NAME_FIELD);
                    if (usedName.equals(name)) return true;
                }
            }
            return false;
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error selecting latest versions from database!", exp);
        }
    }

    @Override
    @Transactional(SUPPORTS)
    public IAbstractItem findMetaInfoByUUID(SclFileType type, UUID id) {
        var sql = """
                select scl_file.id, scl_file.name, scl_file.major_version, scl_file.minor_version, scl_file.patch_version, scl_file.location_id
                  from scl_file
                 where scl_file.id   = ?
                 and   scl_file.type = ?
                 and   scl_file.is_deleted = false
                 order by scl_file.major_version desc, scl_file.minor_version desc, scl_file.patch_version desc
                """;

        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setString(2, type.name());

            try (var resultSet = stmt.executeQuery()) {
                // We need to only retrieve the first row, because that's the latest version.
                if (resultSet.next()) {
                    return new SclMetaInfo(resultSet.getString(ID_FIELD),
                            resultSet.getString(NAME_FIELD),
                            createVersion(resultSet),
                            resultSet.getString("location_id"));
                }
                var message = String.format("No meta info found for type '%s' with ID '%s'", type, id);
                throw new CompasNoDataFoundException(message);
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error select meta info from database!", exp);
        }
    }

    @Override
    @Transactional(REQUIRED)
    public void create(SclFileType type, UUID id, String name, String scl, Version version, String who, List<String> labels) {
        var createSclSQL = """
                insert into scl_file(id, major_version, minor_version, patch_version, type, name, created_by, scl_data)
                     values (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(createSclSQL)) {
            // First add the SCL XML File to the SCL_FILE Table.
            sclStmt.setObject(1, id);
            sclStmt.setInt(2, version.getMajorVersion());
            sclStmt.setInt(3, version.getMinorVersion());
            sclStmt.setInt(4, version.getPatchVersion());
            sclStmt.setString(5, type.name());
            sclStmt.setString(6, name);
            sclStmt.setString(7, who);
            sclStmt.setString(8, scl);
            sclStmt.executeUpdate();

            // Add the label to the database, if there are any.
            createLabels(connection, id, version, labels);
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding SCL to database!", exp);
        }
    }

    void createLabels(Connection connection, UUID id, Version version, List<String> labels) {
        if (labels != null && !labels.isEmpty()) {
            // Now add the extracted labels from the header to the SCL_LABEL table (in batch)
            var createLabelSQL = """
                    insert into scl_label(scl_id, major_version, minor_version, patch_version, label_value)
                         values (?, ?, ?, ?, ?)
                    """;
            try (var labelsStmt = connection.prepareStatement(createLabelSQL)) {
                labels.stream().distinct().forEach(label -> {
                    try {
                        labelsStmt.setObject(1, id);
                        labelsStmt.setInt(2, version.getMajorVersion());
                        labelsStmt.setInt(3, version.getMinorVersion());
                        labelsStmt.setInt(4, version.getPatchVersion());
                        labelsStmt.setString(5, label);
                        labelsStmt.addBatch();
                        labelsStmt.clearParameters();
                    } catch (SQLException exp) {
                        throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding Label to database!", exp);
                    }
                });
                // Execute the insert commands in batch now.
                labelsStmt.executeBatch();
            } catch (SQLException exp) {
                throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding Labels to database!", exp);
            }
        }
    }

    @Override
    @Transactional(REQUIRED)
    public void delete(SclFileType type, UUID id) {
        var sql = """
                delete from scl_file
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
    public void softDelete(SclFileType type, UUID id) {
        var sql = """
                UPDATE scl_file
                SET   is_deleted = true
                WHERE scl_file.id = ?
                AND   scl_file.type = ?
                """;
        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(sql)) {
            sclStmt.setObject(1, id);
            sclStmt.setObject(2, type.name());
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error marking SCL as deleted in database!", exp);
        }
    }

    @Override
    @Transactional(REQUIRED)
    public void deleteVersion(SclFileType type, UUID id, Version version) {
        var sql = """
                delete from scl_file
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

    @Override
    @Transactional(REQUIRED)
    public void softDeleteVersion(SclFileType type, UUID id, Version version) {
        var sql = """
                UPDATE scl_file
                SET    is_deleted = true
                WHERE  scl_file.id = ?
                AND    scl_file.type = ?
                AND    scl_file.major_version = ?
                AND    scl_file.minor_version = ?
                AND    scl_file.patch_version = ?
                """;

        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(sql)) {
            sclStmt.setObject(1, id);
            sclStmt.setString(2, type.name());
            sclStmt.setInt(3, version.getMajorVersion());
            sclStmt.setInt(4, version.getMinorVersion());
            sclStmt.setInt(5, version.getPatchVersion());
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error marking SCL version as deleted in database!", exp);
        }
    }

    private String createVersion(ResultSet resultSet) throws SQLException {
        var version = new Version(resultSet.getInt(MAJOR_VERSION_FIELD),
                resultSet.getInt(MINOR_VERSION_FIELD),
                resultSet.getInt(PATCH_VERSION_FIELD));
        return version.toString();
    }

    private List<String> createLabelList(Array sqlArray) throws SQLException {
        var labelsList = new ArrayList<String>();
        // Sadly no generics in JDBC so we need to check what the Array() method returns.
        if (sqlArray != null && sqlArray.getArray() instanceof Object[] objectArray) {
            Arrays.stream(objectArray)
                    .forEach(arrayObject ->
                            // Just use toString() to return the value of the PostgreSQL Object.
                            labelsList.add(arrayObject.toString())
                    );
        }
        return labelsList;
    }

    @Override
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory() {
        String sql = """
            SELECT subquery.id
                 , subquery.major_version
                 , subquery.minor_version
                 , subquery.patch_version
                 , subquery.type
                 , subquery.name
                 , subquery.creation_date                                                         as changedAt
                 , subquery.created_by                                                            as author
                 , subquery.id IN (SELECT ar.scl_file_id FROM archived_resource ar)               as archived
                 , true                                                                           as available
                 , subquery.is_deleted
                 , l.name                                                                         as location
                 , (XPATH('/scl:Hitem/@what', subquery.header,
                          ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1]::varchar as comment
            FROM (SELECT DISTINCT ON (scl_file.id) scl_file.*,
                                       UNNEST(
                                           XPATH(
                                               '(/scl:SCL/scl:Header//scl:Hitem[(not(@revision) or @revision="") and @version="' ||
                                               scl_file.major_version || '.' || scl_file.minor_version || '.' ||
                                               scl_file.patch_version || '"])[1]'
                                               , scl_file.scl_data::xml
                                               , ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]))
                                           as header
                  FROM scl_file
                  ORDER BY scl_file.id,
                           scl_file.major_version DESC,
                           scl_file.minor_version DESC,
                           scl_file.patch_version DESC) subquery
                     LEFT JOIN location l
                               ON location_id = l.id
            ORDER BY subquery.name;
            """;
        return executeHistoryQuery(sql, Collections.emptyList());
    }

    @Override
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory(UUID id) {
        String sql = """
            SELECT subquery.id
                 , subquery.major_version
                 , subquery.minor_version
                 , subquery.patch_version
                 , subquery.type
                 , subquery.name
                 , subquery.creation_date                                                         as changedAt
                 , subquery.created_by                                                            as author
                 , subquery.id IN (SELECT ar.scl_file_id FROM archived_resource ar)               as archived
                 , true                                                                           as available
                 , subquery.is_deleted
                 , l.name                                                                         as location
                 , (XPATH('/scl:Hitem/@what', subquery.header,
                          ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1]::varchar as comment
            FROM (SELECT DISTINCT ON (scl_file.id) scl_file.*,
                                       UNNEST(
                                           XPATH(
                                               '(/scl:SCL/scl:Header//scl:Hitem[(not(@revision) or @revision="") and @version="' ||
                                               scl_file.major_version || '.' || scl_file.minor_version || '.' ||
                                               scl_file.patch_version || '"])[1]'
                                               , scl_file.scl_data::xml
                                               , ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]))
                                           as header
                  FROM scl_file
                  WHERE scl_file.id = ?
                  ORDER BY scl_file.id,
                           scl_file.major_version DESC,
                           scl_file.minor_version DESC,
                           scl_file.patch_version DESC) subquery
                     LEFT JOIN location l
                               ON location_id = l.id
            ORDER BY subquery.name;
            """;
        return executeHistoryQuery(sql, Collections.singletonList(id));
    }

    @Override
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory(SclFileType type, String name, String author, OffsetDateTime from, OffsetDateTime to) {
        StringBuilder sqlBuilder = new StringBuilder("""
            SELECT subquery.id
                 , subquery.major_version
                 , subquery.minor_version
                 , subquery.patch_version
                 , subquery.type
                 , subquery.name
                 , subquery.creation_date                                                         as changedAt
                 , subquery.created_by                                                            as author
                 , subquery.id IN (SELECT ar.scl_file_id FROM archived_resource ar)               as archived
                 , true                                                                           as available
                 , subquery.is_deleted
                 , l.name                                                                         as location
                 , (XPATH('/scl:Hitem/@what', subquery.header,
                          ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1]::varchar as comment
            FROM (SELECT DISTINCT ON (scl_file.id) scl_file.*,
                                       UNNEST(
                                           XPATH(
                                               '(/scl:SCL/scl:Header//scl:Hitem[(not(@revision) or @revision="") and @version="' ||
                                               scl_file.major_version || '.' || scl_file.minor_version || '.' ||
                                               scl_file.patch_version || '"])[1]'
                                               , scl_file.scl_data::xml
                                               , ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]))
                                           as header
                  FROM scl_file
                  ORDER BY scl_file.id,
                           scl_file.major_version DESC,
                           scl_file.minor_version DESC,
                           scl_file.patch_version DESC) subquery
                     LEFT JOIN location l
                               ON location_id = l.id
            WHERE 1=1
            """);

        List<Object> parameters = new ArrayList<>();

        if (type != null) {
            sqlBuilder.append(" AND subquery.type = ?");
            parameters.add(type.toString());
        }

        if (name != null) {
            sqlBuilder.append(" AND subquery.name ILIKE ?");
            parameters.add("%" + name + "%");
        }

        if (author != null) {
            sqlBuilder.append(" AND subquery.created_by = ?");
            parameters.add(author);
        }

        if (from != null) {
            sqlBuilder.append(" AND subquery.creation_date >= ?");
            parameters.add(from);
        }

        if (to != null) {
            sqlBuilder.append(" AND subquery.creation_date <= ?");
            parameters.add(to);
        }

        sqlBuilder.append(System.lineSeparator());
        sqlBuilder.append("ORDER BY subquery.name;");

        return executeHistoryQuery(sqlBuilder.toString(), parameters);
    }


    @Override
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistoryVersionsByUUID(UUID id) {
        String sql = """
            SELECT sf.id
                 , sf.major_version
                 , sf.minor_version
                 , sf.patch_version
                 , sf.type
                 , sf.name
                 , sf.creation_date as changedAt
                 , sf.created_by as author
                 , sf.id IN (SELECT ar.scl_file_id
                             FROM   archived_resource ar
                             WHERE  ar.scl_file_id            = sf.id
                               AND  ar.scl_file_major_version = sf.major_version
                               AND  ar.scl_file_minor_version = sf.minor_version
                               AND  ar.scl_file_patch_version = sf.patch_version) as archived
                 , true as available
                 , sf.is_deleted
                 , l.name as location
                 , (XPATH('/scl:Hitem/@what', scl_data.header, ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1]::varchar as comment
            FROM scl_file sf
                     INNER JOIN (
                SELECT id, major_version, minor_version, patch_version,
                       UNNEST(
                               XPATH( '(/scl:SCL/scl:Header//scl:Hitem[(not(@revision) or @revision="") and @version="' || major_version || '.' || minor_version || '.' || patch_version || '"])[1]'
                                   , scl_data::xml
                                   , ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL']])) as header
                FROM scl_file) scl_data
                                ON scl_data.id                 = sf.id
                                    AND scl_data.major_version = sf.major_version
                                    AND scl_data.minor_version = sf.minor_version
                                    AND scl_data.patch_version = sf.patch_version
                     LEFT JOIN location l
                               ON sf.location_id = l.id
            WHERE sf.id = ?
            ORDER BY
                sf.name,
                sf.major_version,
                sf.minor_version,
                sf.patch_version;
            """;
        return executeHistoryQuery(sql, Collections.singletonList(id));
    }

    @Override
    @Transactional(REQUIRED)
    public ILocationMetaItem createLocation(UUID id, String key, String name, String description) {
        String sql = """
            INSERT INTO location (id, key, name, description)
            VALUES      (?, ?, ?, ?);
            """;

        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(sql)) {
            sclStmt.setObject(1, id);
            sclStmt.setString(2, key);
            sclStmt.setString(3, name);
            sclStmt.setString(4, description);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding Location to database!", exp);
        }

        return findLocationByUUID(id);
    }

    @Override
    public void addLocationTags(ILocationMetaItem location, String author) {
        String locationName = location.getName();
        ResourceTagItem locationNameTag = getResourceTag("LOCATION", locationName);
        ResourceTagItem locationAuthorTag = getResourceTag("AUTHOR", author);

        if (locationNameTag == null) {
            createResourceTag("LOCATION", locationName);
            locationNameTag = getResourceTag("LOCATION", locationName);
        }
        if (locationAuthorTag == null) {
            createResourceTag("AUTHOR", author);
            locationAuthorTag = getResourceTag("AUTHOR", author);
        }
        UUID locationUuid = UUID.fromString(location.getId());
        updateTagMappingForLocation(locationUuid, List.of(locationNameTag, locationAuthorTag));
    }

    @Override
    public void deleteLocationTags(ILocationMetaItem location) {
        String selectLocationResourceTagQuery = """
            SELECT DISTINCT lrt.resource_tag_id as resource_tag_id
            FROM   location_resource_tag lrt
                LEFT OUTER JOIN archived_resource_resource_tag arrt ON lrt.resource_tag_id = arrt.resource_tag_id
            WHERE lrt.location_id = ?
                AND COALESCE(CAST(arrt.resource_tag_id AS varchar), '' ) <> CAST(lrt.resource_tag_id AS varchar);
            """;

        String deleteResourceTagQuery = """
            DELETE FROM resource_tag
            WHERE       id = ?;
            """;

        List<String> locationTagId = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sclStmt = connection.prepareStatement(selectLocationResourceTagQuery)) {
            sclStmt.setObject(1, UUID.fromString(location.getId()));
            try (ResultSet resultSet = sclStmt.executeQuery()) {
                while (resultSet.next()) {
                    locationTagId.add(resultSet.getString("resource_tag_id"));
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_DELETE_ERROR_CODE, "Error removing Location from database", exp);
        }
        if (!locationTagId.isEmpty()) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement sclStmt = connection.prepareStatement(deleteResourceTagQuery)) {
                for (String id : locationTagId) {
                    sclStmt.setObject(1, UUID.fromString(id));
                    sclStmt.addBatch();
                    sclStmt.clearParameters();
                }
                sclStmt.executeBatch();
            } catch (SQLException exp) {
                throw new CompasSclDataServiceException(POSTGRES_DELETE_ERROR_CODE, "Error removing Location from database", exp);
            }
        }
    }

    @Override
    @Transactional(SUPPORTS)
    public List<ILocationMetaItem> listLocations(int page, int pageSize) {
        String sql = """
            SELECT   *, (SELECT COUNT(sf.id) FROM scl_file sf WHERE sf.location_id = l.id) as assigned_resources
            FROM     location l
            ORDER BY name
            OFFSET   ?
            LIMIT    ?;
            """;
        int offset = 0;
        if (page > 1) {
            offset = (page - 1) * pageSize;
        }
        return executeLocationQuery(sql, List.of(offset, pageSize));
    }

    @Override
    @Transactional(SUPPORTS)
    public ILocationMetaItem findLocationByUUID(UUID locationId) {
        String sql = """
            SELECT   *, (SELECT COUNT(sf.id) FROM scl_file sf WHERE sf.location_id = ?) as assigned_resources
            FROM     location l
            WHERE    id = ?
            ORDER BY l.name;
            """;
        List<ILocationMetaItem> retrievedLocation = executeLocationQuery(sql, List.of(locationId, locationId));
        if (retrievedLocation.isEmpty()) {
            throw new CompasNoDataFoundException(String.format("Unable to find Location with id %s.", locationId));
        }
        return retrievedLocation.get(0);
    }

    @Override
    @Transactional(REQUIRED)
    public void deleteLocation(UUID locationId) {
        String deleteLocationQuery = """
            DELETE FROM location
            WHERE       id = ?;
            """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement sclStmt = connection.prepareStatement(deleteLocationQuery)) {
            sclStmt.setObject(1, locationId);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_DELETE_ERROR_CODE, "Error removing Location from database", exp);
        }
    }

    @Override
    @Transactional(REQUIRED)
    public ILocationMetaItem updateLocation(UUID locationId, String key, String name, String description) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("""
            UPDATE location
            SET    key = ?, name = ?
            """);
        if (description != null && !description.isBlank()) {
            sqlBuilder.append(", description = ?");
            sqlBuilder.append(System.lineSeparator());
        }
        sqlBuilder.append("WHERE id = ?;");
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sclStmt = connection.prepareStatement(sqlBuilder.toString())) {
            sclStmt.setString(1, key);
            sclStmt.setString(2, name);
            if (description == null || description.isBlank()) {
                sclStmt.setObject(3, locationId);
            } else {
                sclStmt.setString(3, description);
                sclStmt.setObject(4, locationId);
            }
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error updating Location in database!", exp);
        }
        return findLocationByUUID(locationId);
    }

    @Override
    @Transactional(REQUIRED)
    public void assignResourceToLocation(UUID locationId, UUID resourceId) {
        String archivedResourceSql = """
        UPDATE scl_file
        SET    location_id = ?
        WHERE  id = ?;
        """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sclStmt = connection.prepareStatement(archivedResourceSql)) {
            sclStmt.setObject(1, locationId);
            sclStmt.setObject(2, resourceId);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error assigning SCL Resource to Location in database!", exp);
        }
        String referencedResourceSql = """
            UPDATE referenced_resource
            SET    location_id = ?
            WHERE  scl_file_id = ?;
            """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sclStmt = connection.prepareStatement(referencedResourceSql)) {
            sclStmt.setObject(1, locationId);
            sclStmt.setObject(2, resourceId);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error assigning Referenced Resource to Location in database!", exp);
        }
        String locationKey = findLocationByUUID(locationId).getName();
        updateLocationKeyMappingForSclResource(resourceId, locationKey);
        updateLocationKeyMappingForReferencedResource(resourceId, locationKey);
    }

    @Override
    @Transactional(REQUIRED)
    public void unassignResourceFromLocation(UUID locationId, UUID resourceId) {
        String archivedResourceSql = """
            UPDATE scl_file
            SET    location_id = NULL
            WHERE  id = ? AND location_id = ?;
            """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sclStmt = connection.prepareStatement(archivedResourceSql)) {
            sclStmt.setObject(1, resourceId);
            sclStmt.setObject(2, locationId);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error unassigning SCL Resource from Location in database!", exp);
        }
        String referencedResourceSql = """
            UPDATE referenced_resource
            SET    location_id = NULL
            WHERE  scl_file_id = ? AND location_id = ?;
            """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement sclStmt = connection.prepareStatement(referencedResourceSql)) {
            sclStmt.setObject(1, resourceId);
            sclStmt.setObject(2, locationId);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error unassigning Referenced Resource from Location in database!", exp);
        }
        updateLocationKeyMappingForSclResource(resourceId, null);
        updateLocationKeyMappingForReferencedResource(resourceId, null);
    }

    private void updateTagMappingForLocation(UUID locationId, List<IResourceTagItem> resourceTags) {
        List<IResourceTagItem> newMappingEntries = resourceTags.stream().filter(entry ->
            !existsLocationResourceTagMapping(locationId, UUID.fromString(entry.getId()))
        ).toList();

        String insertStatement = """
            INSERT INTO location_resource_tag(location_id, resource_tag_id)
            VALUES (?, ?);
            """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement mappingStmt = connection.prepareStatement(insertStatement)) {
            newMappingEntries.forEach(entry -> {
                try {
                    mappingStmt.setObject(1, locationId);
                    mappingStmt.setObject(2, UUID.fromString(entry.getId()));
                    mappingStmt.executeUpdate();
                } catch (SQLException exp) {
                    throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding location to resource tag mapping entry to database!", exp);
                }
            });
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding location to resource tag mapping entries to database!", exp);
        }
    }

    private void updateLocationKeyMappingForSclResource(UUID resourceId, String locationKey) {
        ResourceTagItem locationTag = getResourceTag("LOCATION", locationKey);
        if (locationTag == null) {
            createResourceTag("LOCATION", locationKey);
            locationTag = getResourceTag("LOCATION", locationKey);
        }
        for (IAbstractArchivedResourceMetaItem resource : searchArchivedResourceBySclFile(resourceId)) {
            updateLocationResourceTag(locationTag, resource);
        }
    }

    private void updateLocationKeyMappingForReferencedResource(UUID resourceId, String locationKey) {
        ResourceTagItem locationTag = getResourceTag("LOCATION", locationKey);
        if (locationTag == null) {
            createResourceTag("LOCATION", locationKey);
            locationTag = getResourceTag("LOCATION", locationKey);
        }
        for (IAbstractArchivedResourceMetaItem resource : searchArchivedReferencedResources(resourceId)) {
            updateLocationResourceTag(locationTag, resource);
        }
    }

    private void updateLocationResourceTag(ResourceTagItem locationTag, IAbstractArchivedResourceMetaItem resource) {
        List<String> locationFieldIds = resource.getFields()
            .stream()
            .filter(field ->
                field.getKey().equals("LOCATION")
            )
            .map(IResourceTagItem::getId)
            .toList();
        if (!locationFieldIds.isEmpty()) {
            removeLocationTagsFromResource(resource.getId(), locationFieldIds);
        }
        updateArchivedResourceToResourceTagMappingTable(
            UUID.fromString(resource.getId()),
            List.of(locationTag)
        );
    }

    private void removeLocationTagsFromResource(String resourceId, List<String> locationFieldIds) {
        String sql = String.format("""
            DELETE FROM archived_resource_resource_tag
            WHERE archived_resource_id = ? AND resource_tag_id IN (%s);
            """, locationFieldIds.stream()
            .map(fieldIds -> "?")
            .collect(Collectors.joining(",")));
        try (Connection connection = dataSource.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(sql)) {
            deleteStatement.setObject(1, UUID.fromString(resourceId));
            for (int i = 1; i <= locationFieldIds.size(); i++) {
                deleteStatement.setObject(i + 1, UUID.fromString(locationFieldIds.get(i - 1)));
            }
            deleteStatement.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_DELETE_ERROR_CODE, "Error deleting archived resource to resource tag mapping entries in database!", exp);
        }
    }

    @Override
    public IAbstractArchivedResourceMetaItem archiveResource(UUID id, Version version, String author, String approver, String contentType, String filename) {
        ArchivedSclResourceMetaItem sclResourceMetaItem = getSclFileAsArchivedSclResourceMetaItem(id, version, approver);
        String location = sclResourceMetaItem.getLocation();

        String locationIdQuery = """
            SELECT l.*, (SELECT COUNT(DISTINCT(sf.id)) FROM scl_file sf WHERE sf.location_id = l.id) as assigned_resources
            FROM   scl_file sf INNER JOIN location l ON sf.location_id = l.id
            WHERE  sf.id = ?
            AND sf.major_version = ?
            AND sf.minor_version = ?
            AND sf.patch_version = ?;
            """;

        List<ILocationMetaItem> locationItems = executeLocationQuery(locationIdQuery, List.of(id, version.getMajorVersion(), version.getMinorVersion(), version.getPatchVersion()));

        UUID assignedResourceId = UUID.randomUUID();

        if (!locationItems.isEmpty() && locationItems.get(0).getId() != null) {
            String locationId = locationItems.get(0).getId();
            String sql = """
            INSERT INTO referenced_resource (id, content_type, filename, author, approver, location_id, scl_file_id, scl_file_major_version, scl_file_minor_version, scl_file_patch_version)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
            """;
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setObject(1, assignedResourceId);
                stmt.setObject(2, contentType);
                stmt.setObject(3, filename);
                stmt.setObject(4, author);
                stmt.setObject(5, approver);
                stmt.setObject(6, UUID.fromString(locationId));
                stmt.setObject(7, id);
                stmt.setObject(8, version.getMajorVersion());
                stmt.setObject(9, version.getMinorVersion());
                stmt.setObject(10, version.getPatchVersion());
                stmt.executeUpdate();
            } catch (SQLException exp) {
                throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error inserting Referenced Resources!", exp);
            }
        }

        List<IResourceTagItem> resourceTags = generateFields(location, id.toString(), author, approver);
        ArchivedReferencedResourceMetaItem archivedResourcesMetaItem = new ArchivedReferencedResourceMetaItem(
            assignedResourceId.toString(),
            filename,
            version.toString(),
            author,
            approver,
            null,
            contentType,
            location,
            resourceTags,
            null,
            convertToOffsetDateTime(Timestamp.from(Instant.now())),
            null
        );
        UUID archivedResourceId = UUID.randomUUID();
        insertIntoArchivedResourceTable(archivedResourceId, archivedResourcesMetaItem, version);
        updateArchivedResourceToResourceTagMappingTable(
            archivedResourceId,
            archivedResourcesMetaItem.getFields()
        );
        return new ArchivedReferencedResourceMetaItem(
            archivedResourceId.toString(),
            archivedResourcesMetaItem.getName(),
            archivedResourcesMetaItem.getVersion(),
            archivedResourcesMetaItem.getAuthor(),
            archivedResourcesMetaItem.getApprover(),
            archivedResourcesMetaItem.getType(),
            archivedResourcesMetaItem.getContentType(),
            archivedResourcesMetaItem.getLocation(),
            archivedResourcesMetaItem.getFields(),
            archivedResourcesMetaItem.getModifiedAt(),
            archivedResourcesMetaItem.getArchivedAt(),
            archivedResourcesMetaItem.getComment()
        );
    }

    @Override
    public IAbstractArchivedResourceMetaItem archiveSclResource(UUID id, Version version, String approver) {
        ArchivedSclResourceMetaItem convertedArchivedResourceMetaItem = getSclFileAsArchivedSclResourceMetaItem(id, version, approver);
        if (convertedArchivedResourceMetaItem != null) {
            if (convertedArchivedResourceMetaItem.getLocation() == null) {
                throw new CompasSclDataServiceException(NO_LOCATION_ASSIGNED_TO_SCL_DATA_ERROR_CODE,
                    String.format("Unable to archive scl_file %s with version %s, no location assigned!", id, version));
            }
            UUID archivedResourceId = UUID.randomUUID();
            insertIntoArchivedResourceTable(archivedResourceId, convertedArchivedResourceMetaItem, version);
            updateArchivedResourceToResourceTagMappingTable(
                archivedResourceId,
                convertedArchivedResourceMetaItem.getFields()
            );
            return new ArchivedSclResourceMetaItem(
              archivedResourceId.toString(),
              convertedArchivedResourceMetaItem.getName(),
              convertedArchivedResourceMetaItem.getVersion(),
              convertedArchivedResourceMetaItem.getAuthor(),
              convertedArchivedResourceMetaItem.getApprover(),
              convertedArchivedResourceMetaItem.getType(),
              convertedArchivedResourceMetaItem.getContentType(),
              convertedArchivedResourceMetaItem.getLocation(),
              convertedArchivedResourceMetaItem.getFields(),
              convertedArchivedResourceMetaItem.getModifiedAt(),
              convertedArchivedResourceMetaItem.getArchivedAt(),
              convertedArchivedResourceMetaItem.getNote(),
              convertedArchivedResourceMetaItem.getVoltage()
            );
        }
        return null;
    }

    private ArchivedSclResourceMetaItem getSclFileAsArchivedSclResourceMetaItem(UUID id, Version version, String approver) {
        String sql = """
            SELECT scl_file.*,
                   l.name as location,
                   (xpath('/scl:Hitem/@who', scl_data.header, ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1] hitem_who,
                   (xpath('/scl:Hitem/@what', scl_data.header, ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1] hitem_what
            FROM   scl_file
                   LEFT OUTER JOIN (
                      SELECT id, major_version, minor_version, patch_version,
                             unnest(
                                 xpath('(/scl:SCL/scl:Header//scl:Hitem[(not(@revision) or @revision="") and @version="' || major_version || '.' || minor_version || '.' || patch_version || '"])[1]'
                                      , scl_data::xml
                                      , ARRAY[ARRAY['scl', 'http://www.iec.ch/61850/2003/SCL']])) AS header
                      FROM   scl_file) scl_data
                   ON     scl_data.id            = scl_file.id
                   AND    scl_data.major_version = scl_file.major_version
                   AND    scl_data.minor_version = scl_file.minor_version
                   AND    scl_data.patch_version = scl_file.patch_version
                   INNER JOIN location l
                   ON     scl_file.location_id   = l.id
            WHERE  scl_file.id            = ?
            AND    scl_file.major_version = ?
            AND    scl_file.minor_version = ?
            AND    scl_file.patch_version = ?;
            """;
        ArchivedSclResourceMetaItem archivedResourceMetaItem;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setInt(2, version.getMajorVersion());
            stmt.setInt(3, version.getMinorVersion());
            stmt.setInt(4, version.getPatchVersion());

            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    List<IResourceTagItem> fieldList = generateFieldsFromResultSet(resultSet, approver);
                    archivedResourceMetaItem = mapResultSetToArchivedSclResource(approver, resultSet, fieldList);
                } else {
                    String message = String.format("No SCL Resource with ID '%s' and version '%s' found", id, version);
                    throw new CompasNoDataFoundException(message);
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error select SCL Resource from database!", exp);
        }
        return archivedResourceMetaItem;
    }

    private ArchivedSclResourceMetaItem mapResultSetToArchivedSclResource(String approver, ResultSet resultSet, List<IResourceTagItem> fieldList) throws SQLException {
        return new ArchivedSclResourceMetaItem(
            resultSet.getString(ID_FIELD),
            resultSet.getString(NAME_FIELD),
            createVersion(resultSet),
            resultSet.getString("created_by"),
            approver,
            null,
            resultSet.getString("type"),
            resultSet.getString("location"),
            fieldList,
            convertToOffsetDateTime(Timestamp.from(Instant.now())),
            convertToOffsetDateTime(resultSet.getTimestamp("creation_date")),
            resultSet.getString(HITEM_WHAT_FIELD),
            null
        );
    }

    private List<IResourceTagItem> generateFields(String location, String sourceResourceId, String author, String examiner) {
        List<IResourceTagItem> fieldList = new ArrayList<>();

        ResourceTagItem locationTag = getResourceTag("LOCATION", location);
        ResourceTagItem resourceIdTag = getResourceTag("SOURCE_RESOURCE_ID", sourceResourceId);
        ResourceTagItem authorTag = getResourceTag("AUTHOR", author);
        ResourceTagItem examinerTag = getResourceTag("EXAMINER", examiner);

        if (locationTag == null) {
            createResourceTag("LOCATION", location);
            locationTag = getResourceTag("LOCATION", location);
        }
        if (resourceIdTag == null) {
            createResourceTag("SOURCE_RESOURCE_ID", sourceResourceId);
            resourceIdTag = getResourceTag("SOURCE_RESOURCE_ID", sourceResourceId);
        }
        if (authorTag == null) {
            createResourceTag("AUTHOR", author);
            authorTag = getResourceTag("AUTHOR", author);
        }
        if (examinerTag == null) {
            createResourceTag("EXAMINER", examiner);
            examinerTag = getResourceTag("EXAMINER", examiner);
        }

        fieldList.add(locationTag);
        fieldList.add(resourceIdTag);
        fieldList.add(authorTag);
        fieldList.add(examinerTag);

        return fieldList;
    }

    private List<IResourceTagItem> generateFieldsFromResultSet(ResultSet resultSet, String examiner) throws SQLException {
        return generateFields(
            resultSet.getString(ARCHIVEMETAITEM_LOCATION_FIELD),
            resultSet.getString(ID_FIELD),
            resultSet.getString("created_by"),
            examiner
        );
    }

    private void createResourceTag(String key, String value) {
        String insertIntoResourceTagSql = """
            INSERT INTO resource_tag (id, key, value)
            VALUES (?, ?, ?);
            """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(insertIntoResourceTagSql)) {
            stmt.setObject(1, UUID.randomUUID());
            stmt.setString(2, key);
            if (value == null) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, value);
            }
            stmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding SCL Resource to Archived Resources!", exp);
        }
    }

    private ResourceTagItem getResourceTag(String key, String value) {
        StringBuilder sb = new StringBuilder();
        String sql = """
            SELECT *
            FROM   resource_tag
            """;
        sb.append(sql);
        if (value == null) {
            sb.append("WHERE key = ? AND value IS NULL;");
        } else {
            sb.append("WHERE key = ? AND value = ?;");
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sb.toString())) {
            stmt.setObject(1, key);
            if (value != null) {
                stmt.setObject(2, value);
            }
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new ResourceTagItem(
                        resultSet.getString(ID_FIELD),
                        resultSet.getString(KEY_FIELD),
                        resultSet.getString(VALUE_FIELD)
                    );
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error retrieving Resource Tag entry from database!", exp);
        }

        return null;
    }

    private void insertIntoArchivedResourceTable(UUID archivedResourceId, AbstractArchivedResourceMetaItem archivedResource, Version version) {
        String insertSclResourceIntoArchiveSql = """
        INSERT INTO archived_resource(id, archived_at, scl_file_id, scl_file_major_version, scl_file_minor_version, scl_file_patch_version)
        VALUES (?, ?, ?, ?, ?, ?);
        """;

        String insertReferencedResourceIntoArchiveSql = """
        INSERT INTO archived_resource(id, archived_at, referenced_resource_id, referenced_resource_major_version, referenced_resource_minor_version, referenced_resource_patch_version)
        VALUES (?, ?, ?, ?, ?, ?);
        """;

        if (archivedResource instanceof ArchivedSclResourceMetaItem) {
            executeArchivedResourceInsertStatement(archivedResourceId, archivedResource, version, insertSclResourceIntoArchiveSql);
        } else {
            executeArchivedResourceInsertStatement(archivedResourceId, archivedResource, version, insertReferencedResourceIntoArchiveSql);
        }
    }

    private void executeArchivedResourceInsertStatement(UUID archivedResourceId, AbstractArchivedResourceMetaItem archivedResource, Version version, String query) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, archivedResourceId);
            stmt.setObject(2, OffsetDateTime.now());
            stmt.setObject(3, UUID.fromString(archivedResource.getId()));
            stmt.setInt(4, version.getMajorVersion());
            stmt.setInt(5, version.getMinorVersion());
            stmt.setInt(6, version.getPatchVersion());
            stmt.executeUpdate();
        } catch (SQLException exp) {
            String message = String.format("Error adding %s resource to archived resources!",
                archivedResource instanceof ArchivedSclResourceMetaItem ? "SCL" : "Referenced");
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, message, exp);
        }
    }

    private List<IAbstractArchivedResourceMetaItem> searchArchivedReferencedResources(UUID archivedResourceUuid) {
            String archivedResourcesSql = """
            SELECT ar.*,
                   COALESCE(sf.name, rr.filename)                                         as name,
                   COALESCE(sf.created_by, rr.author)                                     as author,
                   rr.approver                                                            as approver,
                   rr.content_type                                                        as content_type,
                   COALESCE(sf.type, rr.type)                                             as type,
                   sf.creation_date                                                       as modified_at,
                   ar.archived_at                                                         as archived_at,
                   null                                                                   as comment,
                   null                                                                   as voltage,
                   ARRAY_AGG(rt.id || ';' || rt.key || ';' || COALESCE(rt.value, 'null')) AS tags,
                   l.name                                                                 as location
            FROM archived_resource ar
                     INNER JOIN archived_resource_resource_tag arrt
                                ON ar.id = arrt.archived_resource_id
                     INNER JOIN resource_tag rt
                                ON arrt.resource_tag_id = rt.id
                     LEFT JOIN scl_file sf
                               ON sf.id = ar.scl_file_id
                                   AND sf.major_version = ar.scl_file_major_version
                                   AND sf.minor_version = ar.scl_file_minor_version
                                   AND sf.patch_version = ar.scl_file_patch_version
                     LEFT JOIN referenced_resource rr
                               ON ar.referenced_resource_id = rr.id
                     LEFT JOIN location l
                               ON sf.location_id = l.id OR rr.location_id = l.id
            WHERE rr.scl_file_id = ?
            GROUP BY ar.id, sf.name, rr.filename, sf.created_by, rr.author, l.name, rr.content_type, rr.approver, sf.type, rr.type, sf.creation_date, ar.archived_at;
            """;

        return executeArchivedResourceQuery(archivedResourcesSql, Collections.singletonList(archivedResourceUuid));
    }

    private List<IAbstractArchivedResourceMetaItem> searchArchivedResourceBySclFile(UUID archivedResourceUuid) {
        String archivedResourcesSql = """
            SELECT ar.*,
                   COALESCE(sf.name, rr.filename)                                         as name,
                   COALESCE(sf.created_by, rr.author)                                     as author,
                   rr.approver                                                            as approver,
                   rr.content_type                                                        as content_type,
                   COALESCE(sf.type, rr.type)                                             as type,
                   sf.creation_date                                                       as modified_at,
                   ar.archived_at                                                         as archived_at,
                   null                                                                   as comment,
                   null                                                                   as voltage,
                   ARRAY_AGG(rt.id || ';' || rt.key || ';' || COALESCE(rt.value, 'null')) AS tags,
                   l.name                                                                 as location
            FROM archived_resource ar
                     INNER JOIN archived_resource_resource_tag arrt
                                ON ar.id = arrt.archived_resource_id
                     INNER JOIN resource_tag rt
                                ON arrt.resource_tag_id = rt.id
                     LEFT JOIN scl_file sf
                               ON sf.id = ar.scl_file_id
                                   AND sf.major_version = ar.scl_file_major_version
                                   AND sf.minor_version = ar.scl_file_minor_version
                                   AND sf.patch_version = ar.scl_file_patch_version
                     LEFT JOIN referenced_resource rr
                               ON ar.referenced_resource_id = rr.id
                     LEFT JOIN location l
                               ON sf.location_id = l.id OR rr.location_id = l.id
            WHERE sf.id = ?
            GROUP BY ar.id, sf.name, rr.filename, sf.created_by, rr.author, l.name, rr.content_type, rr.approver, sf.type, rr.type, sf.creation_date, ar.archived_at;
            """;

        return executeArchivedResourceQuery(archivedResourcesSql, Collections.singletonList(archivedResourceUuid));
    }

    @Override
    public IArchivedResourcesMetaItem searchArchivedResource(UUID id) {
        String archivedResourcesSql = """
            SELECT ar.*,
                   COALESCE(sf.name, rr.filename)                                         as name,
                   COALESCE(sf.created_by, rr.author)                                     as author,
                   rr.approver                                                            as approver,
                   rr.content_type                                                        as content_type,
                   COALESCE(sf.type, rr.type)                                             as type,
                   sf.creation_date                                                       as modified_at,
                   ar.archived_at                                                         as archived_at,
                   null                                                                   as comment,
                   null                                                                   as voltage,
                   ARRAY_AGG(rt.id || ';' || rt.key || ';' || COALESCE(rt.value, 'null')) AS tags,
                   l.name                                                                 as location
            FROM archived_resource ar
                     INNER JOIN archived_resource_resource_tag arrt
                                ON ar.id = arrt.archived_resource_id
                     INNER JOIN resource_tag rt
                                ON arrt.resource_tag_id = rt.id
                     LEFT JOIN scl_file sf
                               ON sf.id = ar.scl_file_id
                                   AND sf.major_version = ar.scl_file_major_version
                                   AND sf.minor_version = ar.scl_file_minor_version
                                   AND sf.patch_version = ar.scl_file_patch_version
                     LEFT JOIN referenced_resource rr
                               ON ar.referenced_resource_id = rr.id
                     LEFT JOIN location l
                               ON sf.location_id = l.id OR rr.location_id = l.id
            WHERE ar.id = ?
            GROUP BY ar.id, sf.name, rr.filename, sf.created_by, rr.author, l.name, rr.content_type, rr.approver, sf.type, rr.type, sf.creation_date, ar.archived_at;
            """;
        return new ArchivedResourcesMetaItem(executeArchivedResourceQuery(archivedResourcesSql, Collections.singletonList(id)));
    }

    @Override
    public IArchivedResourcesMetaItem searchArchivedResource(String location, String name, String approver, String contentType, String type, String voltage, OffsetDateTime from, OffsetDateTime to) {
        List<Object> parameters = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("""
            SELECT ar.*,
                   COALESCE(sf.name, rr.filename)                                         AS name,
                   COALESCE(sf.created_by, rr.author)                                     AS author,
                   COALESCE(xml_data.hitem_who::varchar, rr.approver)                     AS approver,
                   rr.content_type                                                        AS content_type,
                   COALESCE(sf.type, rr.type)                                             AS type,
                   sf.creation_date                                                       AS modified_at,
                   ar.archived_at                                                         AS archived_at,
                   null                                                                   AS comment,
                   null                                                                   AS voltage,
                   ARRAY_AGG(rt.id || ';' || rt.key || ';' || COALESCE(rt.value, 'null')) AS tags,
                   l.name                                                                 AS location
            FROM archived_resource ar
                     LEFT JOIN scl_file sf
                         ON sf.id = ar.scl_file_id
                             AND sf.major_version = ar.scl_file_major_version
                             AND sf.minor_version = ar.scl_file_minor_version
                             AND sf.patch_version = ar.scl_file_patch_version
                     LEFT JOIN
                         (SELECT scl_file.id, scl_file.major_version, scl_file.minor_version, scl_file.patch_version,
                                  (xpath('/scl:Hitem/@who', scl_data.header,
                                         ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1] hitem_who
                           FROM scl_file
                               INNER JOIN (SELECT id, major_version, minor_version, patch_version,
                                                       unnest(
                                                           xpath(
                                                               '(/scl:SCL/scl:Header//scl:Hitem[(not(@revision) or @revision="") and @version="' || major_version || '.' || minor_version || '.' || patch_version || '"])[1]'
                                                               , scl_data::xml
                                                               , ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]
                                                           )
                                                       ) AS header
                                            FROM scl_file) scl_data
                                                ON scl_data.id = scl_file.id
                                                    AND scl_data.major_version = scl_file.major_version
                                                    AND scl_data.minor_version = scl_file.minor_version
                                                    AND scl_data.patch_version = scl_file.patch_version) xml_data
                          ON xml_data.id = sf.id
                              AND xml_data.major_version = sf.major_version
                              AND xml_data.minor_version = sf.minor_version
                              AND xml_data.patch_version = sf.patch_version
                     INNER JOIN archived_resource_resource_tag arrt
                                ON ar.id = arrt.archived_resource_id
                     INNER JOIN resource_tag rt
                                ON arrt.resource_tag_id = rt.id
                     LEFT JOIN referenced_resource rr
                               ON ar.referenced_resource_id = rr.id
                     LEFT JOIN location l
                               ON sf.location_id = l.id OR rr.location_id = l.id
            WHERE 1 = 1
            """);

        if (location != null && !location.isBlank()) {
            parameters.add(location);
            sb.append(" AND l.name = ?");
        }
        if (name != null && !name.isBlank()) {
            parameters.add("%"+name+"%");
            parameters.add("%"+name+"%");
            sb.append(" AND (rr.filename ILIKE ? OR sf.name ILIKE ?)");
        }
        if (approver != null && !approver.isBlank()) {
            parameters.add(approver);
            parameters.add(approver);
            sb.append(" AND (rr.approver = ? OR xml_data.hitem_who::varchar = ?)");
        }
        if (contentType != null && !contentType.isBlank()) {
            parameters.add(contentType);
            sb.append(" AND rr.content_type = ?");
        }
        if (type != null && !type.isBlank()) {
            parameters.add(type);
            parameters.add(type);
            sb.append(" AND (rr.type = ? OR sf.type = ?)");
        }
        if (voltage != null && !voltage.isBlank()) {
//            ToDo cgutmann: find out how to retrieve voltage
        }
        if (from != null) {
            parameters.add(from);
            sb.append(" AND ar.archived_at >= ?");
        }
        if (to != null) {
            parameters.add(to);
            sb.append(" AND ar.archived_at <= ?");
        }
        sb.append(System.lineSeparator());
        sb.append("GROUP BY ar.id, sf.name, rr.filename, sf.created_by, rr.author, l.name, rr.content_type, rr.approver, sf.type, rr.type, sf.creation_date, ar.archived_at, sf.scl_data, xml_data.hitem_who::varchar;");
        return new ArchivedResourcesMetaItem(executeArchivedResourceQuery(sb.toString(), parameters));
    }

    @Override
    public IArchivedResourcesHistoryMetaItem searchArchivedResourceHistory(UUID uuid) {
        String sql = """
            SELECT ar.*,
                   COALESCE(sf.name, rr.filename)                                         AS name,
                   COALESCE(sf.created_by, rr.author)                                     AS author,
                   COALESCE(xml_data.hitem_who::varchar, rr.approver)                     AS approver,
                   rr.content_type                                                        AS content_type,
                   COALESCE(sf.type, rr.type)                                             AS type,
                   sf.creation_date                                                       AS modified_at,
                   ar.archived_at                                                         AS archived_at,
                   xml_data.hitem_what::varchar                                           AS comment,
                   null                                                                   AS voltage,
                   ARRAY_AGG(rt.id || ';' || rt.key || ';' || COALESCE(rt.value, 'null')) AS tags,
                   l.name                                                                 AS location,
                   ar.scl_file_id IS NOT NULL OR ar.referenced_resource_id IS NOT NULL    AS is_archived
            FROM archived_resource ar
                     LEFT JOIN scl_file sf
                         ON sf.id = ar.scl_file_id
                             AND sf.major_version = ar.scl_file_major_version
                             AND sf.minor_version = ar.scl_file_minor_version
                             AND sf.patch_version = ar.scl_file_patch_version
                     LEFT JOIN
                         (SELECT scl_file.id, scl_file.major_version, scl_file.minor_version, scl_file.patch_version
                               , (xpath('/scl:Hitem/@who', scl_data.header, ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1] hitem_who
                               , (xpath('/scl:Hitem/@what', scl_data.header, ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]))[1] hitem_what
                           FROM scl_file
                               INNER JOIN (SELECT id, major_version, minor_version, patch_version,
                                                       unnest(
                                                           xpath(
                                                               '(/scl:SCL/scl:Header//scl:Hitem[(not(@revision) or @revision="") and @version="' || major_version || '.' || minor_version || '.' || patch_version || '"])[1]'
                                                               , scl_data::xml
                                                               , ARRAY [ARRAY ['scl', 'http://www.iec.ch/61850/2003/SCL']]
                                                           )
                                                       ) AS header
                                            FROM scl_file) scl_data
                                                ON scl_data.id = scl_file.id
                                                    AND scl_data.major_version = scl_file.major_version
                                                    AND scl_data.minor_version = scl_file.minor_version
                                                    AND scl_data.patch_version = scl_file.patch_version) xml_data
                          ON xml_data.id = sf.id
                              AND xml_data.major_version = sf.major_version
                              AND xml_data.minor_version = sf.minor_version
                              AND xml_data.patch_version = sf.patch_version
                     INNER JOIN archived_resource_resource_tag arrt
                                ON ar.id = arrt.archived_resource_id
                     INNER JOIN resource_tag rt
                                ON arrt.resource_tag_id = rt.id
                     LEFT JOIN referenced_resource rr
                               ON ar.referenced_resource_id = rr.id
                     LEFT JOIN location l
                               ON sf.location_id = l.id OR rr.location_id = l.id
            WHERE ar.id = ?
            GROUP BY ar.id, sf.name, rr.filename, sf.created_by, rr.author, l.name, rr.content_type, rr.approver, sf.type, rr.type, sf.creation_date, ar.archived_at, sf.scl_data, xml_data.hitem_who::varchar, xml_data.hitem_what::varchar;
            """;

        List<IArchivedResourceVersion> versions = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, uuid);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    List<IResourceTagItem> resourceTags = getResourceTagItems(resultSet);
                    versions.add(mapToArchivedResourceVersion(resultSet, resourceTags));
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error retrieving Archived Resource History entries from database!", exp);
        }

        return new ArchivedResourcesHistoryMetaItem(versions);
    }

    private ArchivedResourceVersion mapToArchivedResourceVersion(ResultSet resultSet, List<IResourceTagItem> resourceTags) throws SQLException {
        Version version;
        if (resultSet.getObject("scl_file_id") != null) {
            version = new Version(
                resultSet.getInt("scl_file_major_version"),
                resultSet.getInt("scl_file_minor_version"),
                resultSet.getInt("scl_file_patch_version")
            );
        } else {
            version = new Version(
                resultSet.getInt("referenced_resource_major_version"),
                resultSet.getInt("referenced_resource_minor_version"),
                resultSet.getInt("referenced_resource_patch_version")
            );
        }

        return new ArchivedResourceVersion(
            resultSet.getString(ID_FIELD),
            resultSet.getString(NAME_FIELD),
            version.toString(),
            resultSet.getString(ARCHIVEMETAITEM_LOCATION_FIELD),
            resultSet.getString(HISTORYMETAITEM_COMMENT_FIELD),
            resultSet.getString(ARCHIVEMETAITEM_AUTHOR_FIELD),
            resultSet.getString(ARCHIVEMETAITEM_APPROVER_FIELD),
            resultSet.getString(ARCHIVEMETAITEM_TYPE_FIELD),
            resultSet.getString(ARCHIVEMETAITEM_CONTENT_TYPE_FIELD),
            resultSet.getString(ARCHIVEMETAITEM_VOLTAGE_FIELD),
            resourceTags,
            convertToOffsetDateTime(resultSet.getTimestamp(ARCHIVEMETAITEM_MODIFIED_AT_FIELD)),
            convertToOffsetDateTime(resultSet.getTimestamp(ARCHIVEMETAITEM_ARCHIVED_AT_FIELD)),
            resultSet.getString(HISTORYMETAITEM_COMMENT_FIELD),
            resultSet.getBoolean("is_archived")
        );
    }

    private List<IHistoryMetaItem> executeHistoryQuery(String sql, List<Object> parameters) {
        List<IHistoryMetaItem> items = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapResultSetToHistoryMetaItem(resultSet));
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(
                    POSTGRES_SELECT_ERROR_CODE,
                    "Error listing scl entries from database!", exp
            );
        }
        return items;
    }

    private void updateArchivedResourceToResourceTagMappingTable(UUID id, List<IResourceTagItem> resourceTags) {
        List<IResourceTagItem> newMappingEntries = resourceTags.stream().filter(entry ->
            !existsResourceTagMapping(id, UUID.fromString(entry.getId()))
        ).toList();

        String insertStatement = """
            INSERT INTO archived_resource_resource_tag(archived_resource_id, resource_tag_id)
            VALUES (?, ?);
            """;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement mappingStmt = connection.prepareStatement(insertStatement)) {
            newMappingEntries.forEach(entry -> {
                try {
                    mappingStmt.setObject(1, id);
                    mappingStmt.setObject(2, UUID.fromString(entry.getId()));
                    mappingStmt.addBatch();
                    mappingStmt.clearParameters();
                } catch (SQLException exp) {
                    throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding archived resource to resource tag mapping entry to database!", exp);
                }
            });
            mappingStmt.executeBatch();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding archived resource to resource tag mapping entries to database!", exp);
        }
    }

    private boolean existsLocationResourceTagMapping(UUID id, UUID tagId) {
        String query = """
            SELECT *
            FROM   location_resource_tag
            WHERE  location_id = ?
            AND    resource_tag_id = ?;
            """;
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)
        ) {
            stmt.setObject(1, id);
            stmt.setObject(2, tagId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(
                POSTGRES_SELECT_ERROR_CODE,
                "Error listing scl entries from database!", exp
            );
        }
    }

    private boolean existsResourceTagMapping(UUID id, UUID tagId) {
        String query = """
            SELECT *
            FROM   archived_resource_resource_tag
            WHERE  archived_resource_id = ?
            AND    resource_tag_id = ?;
            """;
        try (
            Connection connection = dataSource.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)
        ) {
            stmt.setObject(1, id);
            stmt.setObject(2, tagId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(
                POSTGRES_SELECT_ERROR_CODE,
                "Error listing scl entries from database!", exp
            );
        }
    }

    private HistoryMetaItem mapResultSetToHistoryMetaItem(ResultSet resultSet) throws SQLException {
        return new HistoryMetaItem(
                resultSet.getString(ID_FIELD),
                resultSet.getString(NAME_FIELD),
                createVersion(resultSet),
                resultSet.getString(HISTORYMETAITEM_TYPE_FIELD),
                resultSet.getString(HISTORYMETAITEM_AUTHOR_FIELD),
                resultSet.getString(HISTORYMETAITEM_COMMENT_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_LOCATION_FIELD),
                convertToOffsetDateTime(resultSet.getTimestamp(HISTORYMETAITEM_CHANGEDAT_FIELD)),
                resultSet.getBoolean(HISTORYMETAITEM_ARCHIVED_FIELD),
                resultSet.getBoolean(HISTORYMETAITEM_AVAILABLE_FIELD),
                resultSet.getBoolean(HISTORYMETAITEM_IS_DELETED_FIELD)
        );
    }

    private OffsetDateTime convertToOffsetDateTime(Timestamp sqlTimestamp) {
        return sqlTimestamp != null
                ? sqlTimestamp.toInstant().atOffset(ZoneOffset.UTC)
                : null;
    }

    private List<ILocationMetaItem> executeLocationQuery(String sql, List<Object> parameters) {
        List<ILocationMetaItem> items = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    items.add(mapResultSetToLocationMetaItem(resultSet));
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error listing Location entries from database!", exp);
        }
        return items;
    }

    private LocationMetaItem mapResultSetToLocationMetaItem(ResultSet resultSet) throws SQLException {
        return new LocationMetaItem(
            resultSet.getString(ID_FIELD),
            resultSet.getString(KEY_FIELD),
            resultSet.getString(NAME_FIELD),
            resultSet.getString(LOCATIONMETAITEM_DESCRIPTION_FIELD) == null ? "" : resultSet.getString(LOCATIONMETAITEM_DESCRIPTION_FIELD),
            Integer.parseInt(resultSet.getString("assigned_resources"))
        );
    }

    private List<IAbstractArchivedResourceMetaItem> executeArchivedResourceQuery(String sql, List<Object> parameters) {
        List<IAbstractArchivedResourceMetaItem> items = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    List<IResourceTagItem> resourceTags = getResourceTagItems(resultSet);
                    items.add(mapResultSetToArchivedResourceMetaItem(resultSet, resourceTags));
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(
                POSTGRES_SELECT_ERROR_CODE,
                "Error listing Archived Resource entries from database!", exp
            );
        }
        return items;
    }

    private List<IResourceTagItem> getResourceTagItems(ResultSet resultSet) throws SQLException {
        Array tags = resultSet.getArray("tags");
        List<IResourceTagItem> resourceTags = new ArrayList<>();
        if (tags != null) {
            Arrays.stream((String[]) tags.getArray())
                .filter(Objects::nonNull)
                .map(entry ->
                    new ResourceTagItem(
                        entry.split(";")[0],
                        entry.split(";")[1],
                        entry.split(";")[2].equalsIgnoreCase("null") ? null : entry.split(";")[2]
                    )
                ).forEach(resourceTags::add);
        }
        return resourceTags;
    }

    private AbstractArchivedResourceMetaItem mapResultSetToArchivedResourceMetaItem(ResultSet resultSet, List<IResourceTagItem> resourceTags) throws SQLException {
        String sclFileId = resultSet.getString("scl_file_id");
        if (sclFileId != null) {
            Version version = new Version(
                resultSet.getInt("scl_file_major_version"),
                resultSet.getInt("scl_file_minor_version"),
                resultSet.getInt("scl_file_patch_version")
            );
            return new ArchivedSclResourceMetaItem(
                resultSet.getString(ID_FIELD),
                resultSet.getString(NAME_FIELD),
                version.toString(),
                resultSet.getString(ARCHIVEMETAITEM_AUTHOR_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_APPROVER_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_TYPE_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_CONTENT_TYPE_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_LOCATION_FIELD),
                resourceTags,
                convertToOffsetDateTime(resultSet.getTimestamp(ARCHIVEMETAITEM_MODIFIED_AT_FIELD)),
                convertToOffsetDateTime(resultSet.getTimestamp(ARCHIVEMETAITEM_ARCHIVED_AT_FIELD)),
                resultSet.getString(HISTORYMETAITEM_COMMENT_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_VOLTAGE_FIELD)
            );
        } else {
            Version version = new Version(
                resultSet.getInt("referenced_resource_major_version"),
                resultSet.getInt("referenced_resource_minor_version"),
                resultSet.getInt("referenced_resource_patch_version")
            );
            return new ArchivedReferencedResourceMetaItem(
                resultSet.getString(ID_FIELD),
                resultSet.getString(NAME_FIELD),
                version.toString(),
                resultSet.getString(ARCHIVEMETAITEM_AUTHOR_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_APPROVER_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_TYPE_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_CONTENT_TYPE_FIELD),
                resultSet.getString(ARCHIVEMETAITEM_LOCATION_FIELD),
                resourceTags,
                convertToOffsetDateTime(resultSet.getTimestamp(ARCHIVEMETAITEM_MODIFIED_AT_FIELD)),
                convertToOffsetDateTime(resultSet.getTimestamp(ARCHIVEMETAITEM_ARCHIVED_AT_FIELD)),
                resultSet.getString(HISTORYMETAITEM_COMMENT_FIELD)
            );
        }
    }
}