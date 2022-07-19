// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.HistoryItem;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclMetaInfo;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.extensions.model.SclFileType;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.*;

public class CompasSclDataPostgreSQLRepository implements CompasSclDataRepository {
    private static final String ID_FIELD = "id";
    private static final String MAJOR_VERSION_FIELD = "major_version";
    private static final String MINOR_VERSION_FIELD = "minor_version";
    private static final String PATCH_VERSION_FIELD = "patch_version";
    private static final String NAME_FIELD = "name";
    private static final String SCL_DATA_FIELD = "scl_data";
    private static final String HITEM_WHO_FIELD = "hitem_who";
    private static final String HITEM_WHEN_FIELD = "hitem_when";
    private static final String HITEM_WHAT_FIELD = "hitem_what";

    private final DataSource dataSource;

    public CompasSclDataPostgreSQLRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Item> list(SclFileType type) {
        var sql = """
                select scl_file.id, scl_file.name, scl_file.major_version, scl_file.minor_version, scl_file.patch_version
                  from scl_file
                 where scl_file.type = ?
                 and   (scl_file.id, scl_file.major_version, scl_file.minor_version, scl_file.patch_version) in (
                        -- Last select the maximum patch version with the major/minor version per id.
                        select id, major_version, minor_version, max(patch_version)
                          from scl_file patch_scl
                         where patch_scl.type = scl_file.type
                         and   (id, major_version, minor_version) in (
                               -- Next select the maximum minor version with the major version per id.
                               select id, major_version, max(minor_version)
                                 from scl_file minor_scl
                                where minor_scl.type = scl_file.type
                                and   (id, major_version) in (
                                      -- First select the maximum major version per id.
                                      select id, max(major_version)
                                        from scl_file major_scl
                                       where major_scl.type = scl_file.type
                                       group by id
                                )
                                group by id, major_version
                         )
                         group by id, major_version, minor_version
                 )
                 order by scl_file.name, scl_file.major_version, scl_file.minor_version, scl_file.patch_version
                """;

        var items = new ArrayList<Item>();
        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.name());

            try (var resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new Item(resultSet.getString(ID_FIELD),
                            resultSet.getString(NAME_FIELD),
                            createVersion(resultSet)));
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error listing scl entries from database!", exp);
        }
        return items;
    }

    @Override
    public List<HistoryItem> listVersionsByUUID(SclFileType type, UUID id) {
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
                 order by scl_file.major_version
                        , scl_file.minor_version
                        , scl_file.patch_version
                """;

        var items = new ArrayList<HistoryItem>();
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
    public String findByUUID(SclFileType type, UUID id) {
        // Use the find meta info to retrieve info about the latest version.
        var metaInfo = findMetaInfoByUUID(type, id);
        // Next return the data using the meta info.
        return findByUUID(type, id, new Version(metaInfo.getVersion()));
    }

    @Override
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
    public boolean hasDuplicateSclName(SclFileType type, String name) {
        var sql = """
                select distinct on (scl_file.id) *
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
    public SclMetaInfo findMetaInfoByUUID(SclFileType type, UUID id) {
        var sql = """
                select scl_file.id, scl_file.name, scl_file.major_version, scl_file.minor_version, scl_file.patch_version
                  from scl_file
                 where scl_file.id   = ?
                 and   scl_file.type = ?
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
    public void create(SclFileType type, UUID id, String name, String scl, Version version, String who) {
        var sql = """
                insert into scl_file(id, major_version, minor_version, patch_version, type, name, created_by, scl_data)
                     values (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setInt(2, version.getMajorVersion());
            stmt.setInt(3, version.getMinorVersion());
            stmt.setInt(4, version.getPatchVersion());
            stmt.setString(5, type.name());
            stmt.setString(6, name);
            stmt.setString(7, who);
            stmt.setString(8, scl);
            stmt.executeUpdate();
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error inserting SCL to database!", exp);
        }
    }

    @Override
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
    public void delete(SclFileType type, UUID id, Version version) {
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

    private String createVersion(ResultSet resultSet) throws SQLException {
        var version = new Version(resultSet.getInt(MAJOR_VERSION_FIELD),
                resultSet.getInt(MINOR_VERSION_FIELD),
                resultSet.getInt(PATCH_VERSION_FIELD));
        return version.toString();
    }
}
