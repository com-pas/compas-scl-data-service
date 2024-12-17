package org.lfenergy.compas.scl.data.rest.api.archive;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.model.ArchivedResourcesMetaItem;
import org.lfenergy.compas.scl.data.model.IAbstractArchivedResourceMetaItem;
import org.lfenergy.compas.scl.data.model.IArchivedResourcesMetaItem;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ArchivedResource;
import org.lfenergy.compas.scl.data.rest.api.archive.model.ArchivedResources;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(ArchiveResource.class)
@TestSecurity(user = "test-user")
class ArchiveResourceTest {

    @InjectMock
    private CompasSclDataService compasSclDataService;
    @InjectMock
    private JsonWebToken jwt;

    @Test
    void archiveSclResource_WhenCalled_ThenReturnsArchivedResource() {
        UUID uuid = UUID.randomUUID();
        String name = "Name";
        String version = "1.0.0";
        IAbstractArchivedResourceMetaItem testData = new ArchivedSclResourceTestDataBuilder().setId(uuid.toString()).build();
        when(jwt.getClaim("name")).thenReturn("");
        when(compasSclDataService.archiveSclResource(uuid, new Version(version), "")).thenReturn(testData);
        Response response = given()
            .contentType(MediaType.APPLICATION_JSON)
            .when().post("/scl/" + uuid + "/versions/" + version)
            .then()
            .statusCode(200)
            .extract()
            .response();

        ArchivedResource result = response.as(ArchivedResource.class);
        assertEquals(uuid, UUID.fromString(result.getUuid()));
        assertEquals(name, result.getName());
        assertEquals(version, result.getVersion());
    }

//    @Test
//    void archiveResource_WhenCalled_ThenReturnsArchivedResource() {
//        UUID uuid = UUID.randomUUID();
//        String name = "Name";
//        String version = "1.0.0";
//        IAbstractArchivedResourceMetaItem testData = new ArchivedReferencedResourceTestDataBuilder().setId(uuid.toString()).build();
//        File f = Paths.get("src","test","resources","scl", "icd_import_ied_test.scd").toFile();
//        //ToDo cgutmann: fix this test -> file not the same as in test function -> therefore request not mocked
//        when(compasSclDataService.archiveResource(uuid, version, null, null, "application/json", null, f)).thenReturn(testData);
//        Response response = given()
//            .contentType(MediaType.APPLICATION_JSON)
//            .body(f)
//            .when().post("/referenced-resource/" + uuid + "/versions/" + version)
//            .then()
//            .statusCode(200)
//            .extract()
//            .response();
//
//        ArchivedResource result = response.as(ArchivedResource.class);
//        assertEquals(uuid, UUID.fromString(result.getUuid()));
//        assertEquals(name, result.getName());
//        assertEquals(version, result.getVersion());
//    }

    @Test
    void searchArchivedResources_WhenCalledWithUuid_ThenReturnsMatchingArchivedResources() {
        UUID uuid = UUID.randomUUID();
        String name = "Name";
        String version = "1.0.0";
        IAbstractArchivedResourceMetaItem testData1 = new ArchivedSclResourceTestDataBuilder().setId(uuid.toString()).setName(name).setVersion(version).build();
        IAbstractArchivedResourceMetaItem testData2 = new ArchivedSclResourceTestDataBuilder().setId(uuid.toString()).setName(name).setVersion("1.0.1").build();
        IArchivedResourcesMetaItem archivedResources = new ArchivedResourcesMetaItem(List.of(testData1, testData2));

        when(compasSclDataService.searchArchivedResources(uuid)).thenReturn(archivedResources);
        Response response = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"uuid\": \"" + uuid + "\"}")
            .when().post("/resources/search")
            .then()
            .statusCode(200)
            .extract()
            .response();

        ArchivedResources result = response.as(ArchivedResources.class);
        assertEquals(uuid, UUID.fromString(result.getResources().get(0).getUuid()));
        assertEquals(name, result.getResources().get(0).getName());
        assertEquals(version, result.getResources().get(0).getVersion());
    }

    @Test
    void searchArchivedResources_WhenCalledWithoutUuid_ThenReturnsMatchingArchivedResources() {
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        String name = "Name";
        String version = "1.0.0";
        IAbstractArchivedResourceMetaItem testData1 = new ArchivedSclResourceTestDataBuilder().setId(uuid.toString()).setName(name).setVersion(version).build();
        IAbstractArchivedResourceMetaItem testData2 = new ArchivedSclResourceTestDataBuilder().setId(uuid2.toString()).setName(name).setVersion(version).build();
        IArchivedResourcesMetaItem archivedResources = new ArchivedResourcesMetaItem(List.of(testData1, testData2));

        when(compasSclDataService.searchArchivedResources(null, name, null,null,null,null,null, null)).thenReturn(archivedResources);
        Response response = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"name\": \"Name\"}")
            .when().post("/resources/search")
            .then()
            .statusCode(200)
            .extract()
            .response();

        ArchivedResources result = response.as(ArchivedResources.class);
        assertEquals(uuid, UUID.fromString(result.getResources().get(0).getUuid()));
        assertEquals(name, result.getResources().get(0).getName());
        assertEquals(version, result.getResources().get(0).getVersion());
        assertEquals(uuid2, UUID.fromString(result.getResources().get(1).getUuid()));
        assertEquals(name, result.getResources().get(1).getName());
        assertEquals(version, result.getResources().get(1).getVersion());
    }

}