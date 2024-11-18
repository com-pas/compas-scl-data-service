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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;

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
    private static final String LOCATIONMETAITEM_KEY_FIELD = "key";
    private static final String LOCATIONMETAITEM_DESCRIPTION_FIELD = "description";
    private static final String LOCATIONMETAITEM_RESOURCE_ID_FIELD = "resource_id";
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
                select scl_file.id, scl_file.name, scl_file.major_version, scl_file.minor_version, scl_file.patch_version
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
                            createVersion(resultSet));
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
    @Transactional(REQUIRED)
    public void createHistoryVersion(UUID id, String name, Version version, SclFileType type, String author, String comment, OffsetDateTime changedAt, Boolean archived, Boolean available) {
        var sql = """
                INSERT INTO scl_history(id, name, major_version, minor_version, patch_version, type, author, comment, changedAt, archived, available)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;

        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(sql)) {
            sclStmt.setObject(1, id);
            sclStmt.setString(2, name);
            sclStmt.setInt(3, version.getMajorVersion());
            sclStmt.setInt(4, version.getMinorVersion());
            sclStmt.setInt(5, version.getPatchVersion());
            sclStmt.setString(6, type.toString());
            sclStmt.setString(7, author);
            sclStmt.setString(8, comment);
            sclStmt.setObject(9, changedAt);
            sclStmt.setBoolean(10, archived);
            sclStmt.setBoolean(11, available);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding SCL History to database!", exp);
        }
    }

    @Override
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory() {
        var sql = """
                SELECT *
                FROM (
                    SELECT DISTINCT ON (scl_history.id) scl_history.*, scl_file.is_deleted
                    FROM scl_history
                    JOIN scl_file ON scl_history.id = scl_file.id
                        AND scl_history.major_version = scl_file.major_version
                        AND scl_history.minor_version = scl_file.minor_version
                        AND scl_history.patch_version = scl_file.patch_version
                    ORDER BY
                        scl_history.id,
                        scl_history.major_version DESC,
                        scl_history.minor_version DESC,
                        scl_history.patch_version DESC
                ) subquery
                ORDER BY subquery.name
                """;
        return executeHistoryQuery(sql, Collections.emptyList());
    }

    @Override
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory(UUID id) {
        var sql = """
                SELECT *
                FROM (
                    SELECT DISTINCT ON (scl_history.id) scl_history.*, scl_file.is_deleted
                    FROM scl_history
                    JOIN scl_file
                        ON  scl_history.id            = scl_file.id
                        AND scl_history.major_version = scl_file.major_version
                        AND scl_history.minor_version = scl_file.minor_version
                        AND scl_history.patch_version = scl_file.patch_version
                    WHERE scl_history.id  = ?
                    ORDER BY
                        scl_history.id,
                        scl_history.major_version DESC,
                        scl_history.minor_version DESC,
                        scl_history.patch_version DESC
                ) subquery
                ORDER BY subquery.name
                """;
        return executeHistoryQuery(sql, Collections.singletonList(id));
    }

    @Override
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistory(SclFileType type, String name, String author, OffsetDateTime from, OffsetDateTime to) {
        StringBuilder sqlBuilder = new StringBuilder("""
                SELECT *
                FROM (
                    SELECT DISTINCT ON (scl_history.id) scl_history.*, scl_file.is_deleted
                    FROM scl_history
                    JOIN scl_file
                        ON  scl_history.id            = scl_file.id
                        AND scl_history.major_version = scl_file.major_version
                        AND scl_history.minor_version = scl_file.minor_version
                        AND scl_history.patch_version = scl_file.patch_version
                    ORDER BY
                        scl_history.id,
                        scl_history.major_version DESC,
                        scl_history.minor_version DESC,
                        scl_history.patch_version DESC
                ) subquery
                ORDER BY subquery.name
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
            sqlBuilder.append(" AND subquery.author = ?");
            parameters.add(author);
        }

        if (from != null) {
            sqlBuilder.append(" AND subquery.changedAt >= ?");
            parameters.add(from);
        }

        if (to != null) {
            sqlBuilder.append(" AND subquery.changedAt <= ?");
            parameters.add(to);
        }

        sqlBuilder.append(System.lineSeparator());
        sqlBuilder.append("""
                ORDER BY
                    scl_history.name,
                    scl_history.major_version,
                    scl_history.minor_version,
                    scl_history.patch_version
                """);

        return executeHistoryQuery(sqlBuilder.toString(), parameters);
    }


    @Override
    @Transactional(SUPPORTS)
    public List<IHistoryMetaItem> listHistoryVersionsByUUID(UUID id) {
        var sql = """
                SELECT scl_history.*, scl_file.is_deleted
                FROM scl_history
                JOIN scl_file
                    ON  scl_history.id            = scl_file.id
                    AND scl_history.major_version = scl_file.major_version
                    AND scl_history.minor_version = scl_file.minor_version
                    AND scl_history.patch_version = scl_file.patch_version
                WHERE scl_history.id = ?
                ORDER BY
                    scl_history.name,
                    scl_history.major_version,
                    scl_history.minor_version,
                    scl_history.patch_version
                """;
        return executeHistoryQuery(sql, Collections.singletonList(id));
    }

    @Override
    @Transactional(REQUIRED)
    public ILocationMetaItem createLocation(UUID id, String key, String name, String description) {
        String sql = """
            INSERT INTO location (id, key, name, description, resource_id)
            VALUES      (?, ?, ?, ?, null);
            """;

        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(sql)) {
            sclStmt.setObject(1, id);
            sclStmt.setString(2, key);
            sclStmt.setString(3, name);
            sclStmt.setString(4, description == null ? "" : description);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error adding Location to database!", exp);
        }

        return findLocationByUUID(id);
    }

    @Override
    @Transactional(SUPPORTS)
    public List<ILocationMetaItem> listLocations(int page, int pageSize) {
        String sql = """
            SELECT   *
            FROM     location
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
            SELECT *
            FROM   location
            WHERE  id = ?
            ORDER BY name;
            """;
        List<ILocationMetaItem> retrievedLocation = executeLocationQuery(sql, Collections.singletonList(locationId));
        if (retrievedLocation.isEmpty()) {
            throw new CompasNoDataFoundException(String.format("Unable to find Location with id %s.", locationId));
        }
        return retrievedLocation.get(0);
    }

    @Override
    @Transactional(REQUIRED)
    public void deleteLocation(UUID locationId) {
        String sql = """
            DELETE FROM location
            WHERE       id = ?;
            """;
        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(sql)) {
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
            sqlBuilder.append("\n");
        }
        sqlBuilder.append("WHERE id = ?;");
        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(sqlBuilder.toString())) {
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
        String sql = """
            UPDATE location
            SET    resource_id = ?
            WHERE  id = ?;
            """;
        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(sql)) {
            sclStmt.setObject(1, resourceId);
            sclStmt.setObject(2, locationId);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error assigning SCL Resource to Location in database!", exp);
        }
    }

    @Override
    @Transactional(REQUIRED)
    public void unassignResourceFromLocation(UUID locationId, UUID resourceId) {
        String sql = """
            UPDATE location
            SET    resource_id = NULL
            WHERE  id = ? AND resource_id = ?;
            """;
        try (var connection = dataSource.getConnection();
             var sclStmt = connection.prepareStatement(sql)) {
            sclStmt.setObject(1, locationId);
            sclStmt.setObject(2, resourceId);
            sclStmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error unassigning SCL Resource from Location in database!", exp);
        }
    }

    private List<IHistoryMetaItem> executeHistoryQuery(String sql, List<Object> parameters) {
        List<IHistoryMetaItem> items = new ArrayList<>();
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement stmt = connection.prepareStatement(sql);
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

    private HistoryMetaItem mapResultSetToHistoryMetaItem(ResultSet resultSet) throws SQLException {
        return new HistoryMetaItem(
                resultSet.getString(ID_FIELD),
                resultSet.getString(NAME_FIELD),
                createVersion(resultSet),
                resultSet.getString(HISTORYMETAITEM_TYPE_FIELD),
                resultSet.getString(HISTORYMETAITEM_AUTHOR_FIELD),
                resultSet.getString(HISTORYMETAITEM_COMMENT_FIELD),
                convertToOffsetDateTime(resultSet.getTimestamp(HISTORYMETAITEM_CHANGEDAT_FIELD)),
                resultSet.getBoolean(HISTORYMETAITEM_ARCHIVED_FIELD),
                resultSet.getBoolean(HISTORYMETAITEM_AVAILABLE_FIELD),
                resultSet.getBoolean(HISTORYMETAITEM_IS_DELETED_FIELD)
        );
    }

    private OffsetDateTime convertToOffsetDateTime(Timestamp sqlTimestamp) {
        return sqlTimestamp != null
                ? sqlTimestamp.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime()
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
        UUID resourceId = resultSet.getObject(LOCATIONMETAITEM_RESOURCE_ID_FIELD, UUID.class);
        int resourceCount = resourceId == null ? 0 : 1;
        return new LocationMetaItem(
            resultSet.getString(ID_FIELD),
            resultSet.getString(LOCATIONMETAITEM_KEY_FIELD),
            resultSet.getString(NAME_FIELD),
            resultSet.getString(LOCATIONMETAITEM_DESCRIPTION_FIELD),
            resourceCount
        );
    }
}