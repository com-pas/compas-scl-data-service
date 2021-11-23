// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0

package org.lfenergy.compas.scl.data.repository.postgresql;

import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclMetaInfo;
import org.lfenergy.compas.scl.data.model.SclType;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.*;

public class CompasSclDataPostgreSQLRepository implements CompasSclDataRepository {
    private static final String ID_FIELD = "id";
    private static final String MAJOR_VERSION_FIELD = "major_version";
    private static final String MINOR_VERSION_FIELD = "minor_version";
    private static final String PATCH_VERSION_FIELD = "patch_version";
    private static final String NAME_FIELD = "name";
    private static final String SCL_DATA_FIELD = "scl_data";

    private final DataSource dataSource;

    public CompasSclDataPostgreSQLRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Transactional(SUPPORTS)
    public List<Item> list(SclType type) {
        var sql = "select id, name, major_version, minor_version, patch_version" +
                "    from scl_file outer_scl" +
                "   where type = ?" +
                "   and   (id, major_version, minor_version, patch_version) in (" +
                //    Last select the maximum patch version with the major/minor version per id.
                "     select id, major_version, minor_version, max(patch_version)" +
                "       from scl_file patch_scl" +
                "      where patch_scl.type = outer_scl.type" +
                "      and   (id, major_version, minor_version) in (" +
                //         Next select the maximum minor version with the major version per id.
                "          select id, major_version, max(minor_version)" +
                "            from scl_file minor_scl" +
                "           where minor_scl.type = outer_scl.type" +
                "           and   (id, major_version) in (" +
                //              First select the maximum major version per id.
                "               select id, max(major_version)" +
                "                 from scl_file major_scl" +
                "                where major_scl.type = outer_scl.type" +
                "                group by id" +
                "           )" +
                "           group by id, major_version" +
                "      )" +
                "      group by id, major_version, minor_version" +
                " )" +
                " order by id, major_version, minor_version, patch_version";

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
    @Transactional(SUPPORTS)
    public List<Item> listVersionsByUUID(SclType type, UUID id) {
        var sql = "select id, name, major_version, minor_version, patch_version"
                + "  from scl_file"
                + " where id   = ?"
                + " and   type = ?"
                + " order by major_version, minor_version, patch_version";

        var items = new ArrayList<Item>();
        try (var connection = dataSource.getConnection();
             var stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.setString(2, type.name());

            try (var resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    items.add(new Item(id.toString(),
                            resultSet.getString(NAME_FIELD),
                            createVersion(resultSet)));
                }
            }
        } catch (SQLException exp) {
            throw new CompasSclDataServiceException(POSTGRES_SELECT_ERROR_CODE, "Error selecting versions from database!", exp);
        }
        return items;
    }

    @Override
    @Transactional(SUPPORTS)
    public String findByUUID(SclType type, UUID id) {
        // Use the find meta info to retrieve info about the latest version.
        var metaInfo = findMetaInfoByUUID(type, id);
        // Next return the data using the meta info.
        return findByUUID(type, id, new Version(metaInfo.getVersion()));
    }

    @Override
    @Transactional(SUPPORTS)
    public String findByUUID(SclType type, UUID id, Version version) {
        var sql = "select scl_data "
                + "  from scl_file "
                + " where id            = ?"
                + " and   type          = ?"
                + " and   major_version = ? "
                + " and   minor_version = ? "
                + " and   patch_version = ? ";
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
    public SclMetaInfo findMetaInfoByUUID(SclType type, UUID id) {
        var sql = "select id, name, major_version, minor_version, patch_version"
                + "  from scl_file"
                + " where id   = ?"
                + " and   type = ?"
                + " order by major_version desc, minor_version desc, patch_version desc";
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
    public void create(SclType type, UUID id, String name, String scl, Version version, String who) {
        var sql = "insert into scl_file(id, major_version, minor_version, patch_version, type, name, created_by, scl_data)"
                + "     values (?, ?, ?, ?, ?, ?, ?, ?)";

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
    @Transactional(REQUIRED)
    public void delete(SclType type, UUID id) {
        var sql = "delete from scl_file "
                + " where id   = ?"
                + " and   type = ?";
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
    public void delete(SclType type, UUID id, Version version) {
        var sql = "delete from scl_file "
                + " where id            = ?"
                + " and   type          = ?"
                + " and   major_version = ? "
                + " and   minor_version = ? "
                + " and   patch_version = ? ";
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
