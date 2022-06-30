// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.scl.data.basex.repository;

import org.apache.commons.io.input.ReaderInputStream;
import org.lfenergy.compas.scl.data.basex.client.BaseXClient;
import org.lfenergy.compas.scl.data.basex.client.BaseXClientFactory;
import org.lfenergy.compas.scl.data.exception.CompasNoDataFoundException;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.HistoryItem;
import org.lfenergy.compas.scl.data.model.Item;
import org.lfenergy.compas.scl.data.model.SclMetaInfo;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.repository.CompasSclDataRepository;
import org.lfenergy.compas.scl.data.util.SclDataModelMarshaller;
import org.lfenergy.compas.scl.extensions.model.SclFileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.*;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.BASEX_COMMAND_ERROR_CODE;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.BASEX_QUERY_ERROR_CODE;

/**
 * This implementation of the repository will store the SCL XML Files in BaseX, this is a XML Database.
 * For more information see https://basex.org/.
 * <p>
 * For every type of SCL a separate database is created in which the SCL XML Files are stored.
 * every entries is stored under &lt;ID&gt;/&lt;Major version&gt;/&lt;Minor version&gt;/&lt;Patch version&gt;/scl.xml.
 * This combination is always unique and easy to use.
 */
public class CompasSclDataBaseXRepository implements CompasSclDataRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasSclDataBaseXRepository.class);

    private static final String DECLARE_SCL_NS_URI = "declare namespace scl=\"" + SCL_NS_URI + "\";\n";
    private static final String DECLARE_COMPAS_NAMESPACE = "declare namespace compas=\"" + COMPAS_EXTENSION_NS_URI + "\";\n";
    // This find method always searches for the latest version.
    // Retrieve all versions using db:list-details function.
    // Sort the result descending, this way the last version is the first.
    private static final String DECLARE_LATEST_VERSION_FUNC =
            "declare function local:latest-version($db as xs:string, $id as xs:string)\n" +
                    "   as document-node()? { \n" +
                    "      let $doc :=\n" +
                    "          (for $resource in db:open($db, $id)\n" +
                    "            let $parts := tokenize($resource/scl:SCL/scl:Header/@version, '\\.')\n" +
                    "            let $majorVersion := xs:int($parts[1])\n" +
                    "            let $minorVersion := xs:int($parts[2])\n" +
                    "            let $patchVersion := xs:int($parts[3])\n" +
                    "            order by $majorVersion descending, $minorVersion descending, $patchVersion descending\n" +
                    "            return $resource\n" +
                    "          )[1]\n" +
                    "      return $doc\n" +
                    "};\n";
    private static final String DECLARE_DB_VARIABLE = "declare variable $db := '%s';\n";
    private static final String DECLARE_ID_VARIABLE = "declare variable $id := '%s';\n";
    private static final String DECLARE_PATH_VARIABLE = "declare variable $path := '%s';\n";

    private static final String SCL_HEADER_ID_XPATH = "/scl:SCL/scl:Header/@id";
    private static final String SCL_HEADER_VERSION_XPATH = "/scl:SCL/scl:Header/@version";
    private static final String COMPAS_NAME_EXTENSION_XPATH = "/scl:SCL/scl:Private[@type='" + COMPAS_SCL_EXTENSION_TYPE + "']/compas:" + COMPAS_SCL_NAME_EXTENSION;

    private final BaseXClientFactory baseXClientFactory;
    private final SclDataModelMarshaller sclDataMarshaller;

    public CompasSclDataBaseXRepository(BaseXClientFactory baseXClientFactory,
                                        SclDataModelMarshaller sclDataMarshaller) {
        this.baseXClientFactory = baseXClientFactory;
        this.sclDataMarshaller = sclDataMarshaller;

        // At startup create all needed databases.
        Arrays.stream(SclFileType.values()).forEach(type ->
                executeCommand(client -> {
                    client.executeXQuery(
                            format(DECLARE_DB_VARIABLE, type) +
                                    "if (not(db:exists($db)))\n" +
                                    "   then db:create($db)");
                    return true;
                }));
    }

    @Override
    public List<Item> list(SclFileType type) {
        return executeQuery(type, DECLARE_SCL_NS_URI +
                        DECLARE_COMPAS_NAMESPACE +
                        DECLARE_LATEST_VERSION_FUNC +
                        format(DECLARE_DB_VARIABLE, type) +
                        "for $resource in db:open($db)\n" +
                        "   let $id := $resource" + SCL_HEADER_ID_XPATH + "\n" +
                        "   group by $id\n" +
                        "   let $latestScl := local:latest-version($db, $id)\n" +
                        "   let $version := $latestScl" + SCL_HEADER_VERSION_XPATH + "\n" +
                        "   let $name := $latestScl" + COMPAS_NAME_EXTENSION_XPATH + "\n" +
                        "   order by fn:lower-case($name)\n" +
                        "   return '<Item xmlns=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\"><Id>' || $id || '</Id><Name>' || $name || '</Name><Version>' || $version || '</Version></Item>'",
                sclDataMarshaller::unmarshalItem);
    }

    @Override
    public List<HistoryItem> listVersionsByUUID(SclFileType type, UUID id) {
        return executeQuery(type, DECLARE_SCL_NS_URI +
                        DECLARE_COMPAS_NAMESPACE +
                        format(DECLARE_DB_VARIABLE, type) +
                        format(DECLARE_ID_VARIABLE, id) +
                        "for $resource in db:open($db, $id)\n" +
                        "   let $id := $resource" + SCL_HEADER_ID_XPATH + "\n" +
                        "   let $version := $resource" + SCL_HEADER_VERSION_XPATH + "\n" +
                        "   let $name := $resource" + COMPAS_NAME_EXTENSION_XPATH + "\n" +
                        "   let $header := $resource/scl:SCL/scl:Header/scl:History/scl:Hitem[(not(@revision) or @revision=\"\") and @version=$version]\n" +
                        "   let $parts := tokenize($version, '\\.')\n" +
                        "   let $majorVersion := xs:int($parts[1])\n" +
                        "   let $minorVersion := xs:int($parts[2])\n" +
                        "   let $patchVersion := xs:int($parts[3])\n" +
                        "   order by $majorVersion, $minorVersion, $patchVersion\n" +
                        "   return '<HistoryItem xmlns=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\">'" +
                        "       || '  <Id>' || $id || '</Id>'" +
                        "       || '  <Name>' || $name || '</Name>'" +
                        "       || '  <Version>' || $version || '</Version>' " +
                        "       || '  <Who>' || $header/@who || '</Who>' " +
                        "       || '  <When>' || $header/@when || '</When>' " +
                        "       || '  <What>' || $header/@what || '</What>' " +
                        "       || '</HistoryItem>' ",
                sclDataMarshaller::unmarshalHistoryItem);
    }

    @Override
    public String findByUUID(SclFileType type, UUID id) {
        // This find method always searches for the latest version and returns this.
        var result = executeQuery(type, DECLARE_SCL_NS_URI +
                DECLARE_LATEST_VERSION_FUNC +
                format(DECLARE_DB_VARIABLE, type) +
                format(DECLARE_ID_VARIABLE, id) +
                "local:latest-version($db, $id)");

        if (result.isEmpty()) {
            var message = String.format("No record found for type '%s' with ID '%s'", type, id);
            throw new CompasNoDataFoundException(message);
        }
        return result.get(0);
    }

    @Override
    public String findByUUID(SclFileType type, UUID id, Version version) {
        // This find method searches for a specific version.
        var result = executeQuery(type, format(DECLARE_DB_VARIABLE, type) +
                format(DECLARE_PATH_VARIABLE, createDocumentPath(id, version)) +
                "db:open($db, $path)");

        if (result.isEmpty()) {
            var message = String.format("No record found for type '%s' with ID '%s' and version '%s'", type, id, version);
            throw new CompasNoDataFoundException(message);
        }
        return result.get(0);
    }

    @Override
    public boolean hasDuplicateSclName(SclFileType type, String name) {
        return false;
    }

    @Override
    public SclMetaInfo findMetaInfoByUUID(SclFileType type, UUID id) {
        // This find method always searches for the latest version.
        // Extracts the needed information from the document and returns this.
        var metaInfo = executeQuery(type, DECLARE_SCL_NS_URI +
                        DECLARE_COMPAS_NAMESPACE +
                        DECLARE_LATEST_VERSION_FUNC +
                        format(DECLARE_DB_VARIABLE, type) +
                        format(DECLARE_ID_VARIABLE, id) +
                        "let $resource := local:latest-version($db, $id)" +
                        "return if ($resource)\n" +
                        "then (\n" +
                        "  let $id := $resource" + SCL_HEADER_ID_XPATH + "\n" +
                        "  let $version := $resource" + SCL_HEADER_VERSION_XPATH + "\n" +
                        "  let $name := $resource" + COMPAS_NAME_EXTENSION_XPATH + "\n" +
                        "  return '<SclMetaInfo xmlns=\"" + SCL_DATA_SERVICE_V1_NS_URI + "\"><Id>' || $id || '</Id><Name>' || $name || '</Name><Version>' || $version || '</Version></SclMetaInfo>'\n" +
                        ")\n",
                sclDataMarshaller::unmarshalSclMetaInfo);

        if (metaInfo.isEmpty()) {
            var message = String.format("No meta info found for type '%s' with ID '%s'", type, id);
            throw new CompasNoDataFoundException(message);
        }
        return metaInfo.get(0);
    }

    @Override
    public void create(SclFileType type, UUID id, String name, String scl, Version version, String who) {
        // Who is ignored in the BaseX implementation.
        var inputStream = new ReaderInputStream(new StringReader(scl), StandardCharsets.UTF_8);
        executeCommand(client -> {
            openDatabase(client, type);
            client.add(createDocumentPath(id, version) + "/scl.xml", inputStream);
            closeDatabase(client);
            return true;
        });
    }

    @Override
    public void delete(SclFileType type, UUID id) {
        executeCommand(client -> {
            client.executeXQuery("db:delete('" + type + "', '" + id + "')");
            return true;
        });
    }

    @Override
    public void delete(SclFileType type, UUID id, Version version) {
        executeCommand(client -> {
            client.executeXQuery("db:delete('" + type + "', '" + createDocumentPath(id, version) + "')");
            return true;
        });
    }

    private String createDocumentPath(UUID uuid, Version version) {
        return uuid
                + "/" + version.getMajorVersion()
                + "/" + version.getMinorVersion()
                + "/" + version.getPatchVersion();
    }

    private List<String> executeQuery(SclFileType type, String query) {
        // When the Document (as String) is just returned without mapping.
        return executeQuery(type, query, xmlString -> xmlString);
    }

    private <R> List<R> executeQuery(SclFileType type, String query, ResultRowMapper<R> mapper) {
        return executeCommand(client -> {
            try {
                var response = new ArrayList<R>();
                openDatabase(client, type);
                LOGGER.debug("Executing Query:\n{}", query);
                try (var queryToRun = client.query(query)) {
                    while (queryToRun.more()) {
                        response.add(mapper.map(queryToRun.next()));
                    }
                }
                closeDatabase(client);
                return response;
            } catch (IOException exception) {
                final var exceptionMessage = exception.getLocalizedMessage();
                LOGGER.error("executeQuery: {}", exceptionMessage, exception);
                throw new CompasSclDataServiceException(BASEX_QUERY_ERROR_CODE, "Error executing query!");
            }
        });
    }

    private void openDatabase(BaseXClient client, SclFileType type) throws IOException {
        client.execute("OPEN " + type);
    }

    private void closeDatabase(BaseXClient client) throws IOException {
        client.execute("CLOSE");
    }

    private <R> R executeCommand(ClientExecutor<R> command) {
        try (var client = baseXClientFactory.createClient()) {
            return command.execute(client);
        } catch (IOException exception) {
            final var exceptionMessage = exception.getLocalizedMessage();
            LOGGER.error("executeCommand: {}", exceptionMessage, exception);
            throw new CompasSclDataServiceException(BASEX_COMMAND_ERROR_CODE, "Error executing command!");
        }
    }

    private interface ClientExecutor<R> {
        R execute(BaseXClient client) throws IOException;
    }

    private interface ResultRowMapper<R> {
        R map(String row);
    }
}
