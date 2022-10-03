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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static javax.transaction.Transactional.TxType.REQUIRED;
import static javax.transaction.Transactional.TxType.SUPPORTS;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_DATA_SERVICE_V1_NS_URI;
import static org.lfenergy.compas.scl.data.SclDataServiceConstants.SCL_NS_URI;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.BASEX_COMMAND_ERROR_CODE;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.BASEX_QUERY_ERROR_CODE;
import static org.lfenergy.compas.scl.extensions.commons.CompasExtensionsConstants.COMPAS_EXTENSION_NS_URI;
import static org.lfenergy.compas.scl.extensions.commons.CompasExtensionsConstants.COMPAS_SCL_EXTENSION_TYPE;

/**
 * This implementation of the repository will store the SCL XML Files in BaseX, this is a XML Database.
 * For more information see https://basex.org/.
 * <p>
 * For every type of SCL a separate database is created in which the SCL XML Files are stored.
 * Every entry is stored under &lt;ID&gt;/&lt;Major version&gt;/&lt;Minor version&gt;/&lt;Patch version&gt;/scl.xml.
 * This combination is always unique and easy to use.
 */
@ApplicationScoped
public class CompasSclDataBaseXRepository implements CompasSclDataRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompasSclDataBaseXRepository.class);

    private static final String DECLARE_NS_AND_VARS = """
            declare namespace scl    = "%s";
            declare namespace compas = "%s";

            declare variable $compasSclExtensionType     := '%s';
            declare variable $compasDataServiceNamespace := '%s';
            """.formatted(SCL_NS_URI, COMPAS_EXTENSION_NS_URI, COMPAS_SCL_EXTENSION_TYPE, SCL_DATA_SERVICE_V1_NS_URI);

    // This find method always searches for the latest version. Retrieve all versions using db:list-details function.
    // Sort the result descending, this way the last version is the first.
    private static final String DECLARE_LATEST_VERSION_FUNC = """
            declare function local:latest-version($db as xs:string, $id as xs:string) as document-node()? {
              let $doc :=
                (for $resource in db:open($db, $id)
                  let $parts := tokenize($resource/scl:SCL/scl:Header/@version, '\\.')
                  let $majorVersion := xs:int($parts[1])
                  let $minorVersion := xs:int($parts[2])
                  let $patchVersion := xs:int($parts[3])
                  order by $majorVersion descending, $minorVersion descending, $patchVersion descending
                  return $resource
                )[1]
                return $doc
            };
            """;

    // Retrieve the Labels as XML Label elements from the XML. The result can be returned by the List functions.
    private static final String DECLARE_LABELS_FUNC = """
            declare function local:createLabelsResponse($latestScl as document-node()) as xs:string* {
              let $labels := distinct-values($latestScl/scl:SCL/scl:Private[@type=$compasSclExtensionType]/compas:Labels/compas:Label)
              for $label in $labels
                return ' <Label>' || $label || '</Label>'
            };
            """;

    private final BaseXClientFactory baseXClientFactory;
    private final SclDataModelMarshaller sclDataMarshaller;

    @Inject
    public CompasSclDataBaseXRepository(BaseXClientFactory baseXClientFactory,
                                        SclDataModelMarshaller sclDataMarshaller) {
        this.baseXClientFactory = baseXClientFactory;
        this.sclDataMarshaller = sclDataMarshaller;

        var command = """
                declare variable $db := '%s';
                if (not(db:exists($db)))
                then
                  db:create($db)
                """;
        // At startup create all needed databases.
        Arrays.stream(SclFileType.values()).forEach(type ->
                executeCommand(client -> {
                    client.executeXQuery(command.formatted(type));
                    return true;
                }));
    }

    @Override
    @Transactional(SUPPORTS)
    public List<Item> list(SclFileType type) {
        return executeQuery(type, """
                        %s
                        %s
                        %s
                        declare variable $db := '%s';
                        for $resource in db:open($db)
                          let $id := $resource/scl:SCL/scl:Header/@id
                          group by $id
                          let $latestScl := local:latest-version($db, $id)
                          let $version := $latestScl/scl:SCL/scl:Header/@version
                          let $name := ($latestScl/scl:SCL/scl:Private[@type=$compasSclExtensionType]/compas:SclName)[1]
                          let $labels := fn:string-join(local:createLabelsResponse($latestScl))
                          order by fn:lower-case($name)
                          return '<Item xmlns="' || $compasDataServiceNamespace || '">'
                              || ' <Id>'      || $id      || '</Id>'
                              || ' <Name>'    || $name    || '</Name>'
                              || ' <Version>' || $version || '</Version>'
                              || $labels
                              || '</Item>'
                        """.formatted(DECLARE_NS_AND_VARS, DECLARE_LATEST_VERSION_FUNC, DECLARE_LABELS_FUNC, type)
                ,
                sclDataMarshaller::unmarshalItem);
    }

    @Override
    @Transactional(SUPPORTS)
    public List<HistoryItem> listVersionsByUUID(SclFileType type, UUID id) {
        return executeQuery(type, """
                        %s
                        declare variable $db := '%s';
                        declare variable $id := '%s';
                        for $resource in db:open($db, $id)
                           let $version := $resource/scl:SCL/scl:Header/@version
                           let $name := $resource/scl:SCL/scl:Private[@type=$compasSclExtensionType]/compas:SclName
                           let $header := ($resource/scl:SCL/scl:Header/scl:History/scl:Hitem[(not(@revision) or @revision="") and @version=$version])[1]
                           let $parts := tokenize($version, '\\.')
                           let $majorVersion := xs:int($parts[1])
                           let $minorVersion := xs:int($parts[2])
                           let $patchVersion := xs:int($parts[3])
                           order by $majorVersion, $minorVersion, $patchVersion
                           return '<HistoryItem xmlns="' || $compasDataServiceNamespace || '">'
                               || ' <Id>'      || $id           || '</Id>'
                               || ' <Name>'    || $name         || '</Name>'
                               || ' <Version>' || $version      || '</Version>'
                               || ' <Who>'     || $header/@who  || '</Who>'
                               || ' <When>'    || $header/@when || '</When>'
                               || ' <What>'    || $header/@what || '</What>'
                               || '</HistoryItem>'
                        """.formatted(DECLARE_NS_AND_VARS, type, id),
                sclDataMarshaller::unmarshalHistoryItem);
    }

    @Override
    @Transactional(SUPPORTS)
    public String findByUUID(SclFileType type, UUID id) {
        // This find method always searches for the latest version and returns this.
        var result = executeQuery(type, """
                %s
                %s
                declare variable $db := '%s';
                declare variable $id := '%s';
                local:latest-version($db, $id)
                """.formatted(DECLARE_NS_AND_VARS, DECLARE_LATEST_VERSION_FUNC, type, id)
        );

        if (result.isEmpty()) {
            var message = String.format("No record found for type '%s' with ID '%s'", type, id);
            throw new CompasNoDataFoundException(message);
        }
        return result.get(0);
    }

    @Override
    @Transactional(SUPPORTS)
    public String findByUUID(SclFileType type, UUID id, Version version) {
        // This find method searches for a specific version.
        var result = executeQuery(type, """
                declare variable $db   := '%s';
                declare variable $path := '%s';
                db:open($db, $path)
                """.formatted(type, createDocumentPath(id, version))
        );

        if (result.isEmpty()) {
            var message = String.format("No record found for type '%s' with ID '%s' and version '%s'", type, id, version);
            throw new CompasNoDataFoundException(message);
        }
        return result.get(0);
    }

    @Override
    @Transactional(SUPPORTS)
    public boolean hasDuplicateSclName(SclFileType type, String name) {
        return false;
    }

    @Override
    @Transactional(SUPPORTS)
    public SclMetaInfo findMetaInfoByUUID(SclFileType type, UUID id) {
        // This find method always searches for the latest version.
        // Extracts the needed information from the document and returns this.
        var metaInfo = executeQuery(type, """
                                %s
                                %s
                                declare variable $db := '%s';
                                declare variable $id := '%s';
                                let $resource := local:latest-version($db, $id)
                                return if ($resource)
                                then (
                                  let $version := $resource/scl:SCL/scl:Header/@version
                                  let $name := $resource/scl:SCL/scl:Private[@type=$compasSclExtensionType]/compas:SclName
                                  return '<SclMetaInfo xmlns="' || $compasDataServiceNamespace || '">'
                                      || ' <Id>'      || $id      || '</Id>'
                                      || ' <Name>'    || $name    || '</Name>'
                                      || ' <Version>' || $version || '</Version>'
                                      || '</SclMetaInfo>'
                                )
                        """.formatted(DECLARE_NS_AND_VARS, DECLARE_LATEST_VERSION_FUNC, type, id),
                sclDataMarshaller::unmarshalSclMetaInfo);

        if (metaInfo.isEmpty()) {
            var message = String.format("No meta info found for type '%s' with ID '%s'", type, id);
            throw new CompasNoDataFoundException(message);
        }
        return metaInfo.get(0);
    }

    @Override
    @Transactional(REQUIRED)
    public void create(SclFileType type, UUID id, String name, String scl, Version version, String who, List<String> labels) {
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
    @Transactional(REQUIRED)
    public void delete(SclFileType type, UUID id) {
        executeCommand(client -> {
            client.executeXQuery("db:delete('%s', '%s')".formatted(type, id));
            return true;
        });
    }

    @Override
    @Transactional(REQUIRED)
    public void delete(SclFileType type, UUID id, Version version) {
        executeCommand(client -> {
            client.executeXQuery("db:delete('%s', '%s')".formatted(type, createDocumentPath(id, version)));
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
        client.execute("OPEN %s".formatted(type));
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
