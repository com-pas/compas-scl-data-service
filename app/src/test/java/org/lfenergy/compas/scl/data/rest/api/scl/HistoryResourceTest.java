package org.lfenergy.compas.scl.data.rest.api.scl;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import org.lfenergy.compas.scl.data.model.IHistoryMetaItem;
import org.lfenergy.compas.scl.data.model.Version;
import org.lfenergy.compas.scl.data.rest.api.scl.model.DataResourceHistory;
import org.lfenergy.compas.scl.data.rest.api.scl.model.DataResourceSearch;
import org.lfenergy.compas.scl.data.rest.api.scl.model.DataResourcesResult;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
@TestHTTPEndpoint(HistoryResource.class)
@TestSecurity(user = "test-user")
class HistoryResourceTest {

    @InjectMock
    private CompasSclDataService compasSclDataService;

    @Test
    void searchForResources_WhenCalled_ThenReturnsDataResourcesResult() {
        var search = new DataResourceSearch(); // Populate with necessary data

        List<IHistoryMetaItem> testData = new ArrayList<>();
        testData.add(new HistoryResourceTestdataBuilder().build());

        when(compasSclDataService.listHistory()).thenReturn(testData);

        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(search)
                .when().post("/search")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var dataResourcesResult = response.as(DataResourcesResult.class);
        assertNotNull(dataResourcesResult);
    }

    @Test
    void searchForResourcesWithUUIDFilter_WhenCalled_ThenReturnsOnlyMatchingData() {
        var uuid = UUID.randomUUID();
        var search = new DataResourceSearch();
        search.setUuid(uuid.toString());

        List<IHistoryMetaItem> testData = new ArrayList<>();
        testData.add(new HistoryResourceTestdataBuilder().setId(uuid.toString()).build());

        when(compasSclDataService.listHistory(uuid)).thenReturn(testData);

        var response = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(search)
                .when().post("/search")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var dataResourcesResult = response.as(DataResourcesResult.class);
        assertNotNull(dataResourcesResult);
        assertEquals(1, dataResourcesResult.getResults().size(), "Expected exactly 1 matching result");
    }

    @Test
    void retrieveDataResourceHistory_WhenCalled_ThenReturnsEmptyDataResourceHistory() {
        var uuid = UUID.randomUUID();
        List<IHistoryMetaItem> testData = new ArrayList<>();
        testData.add(new HistoryResourceTestdataBuilder().setId(uuid.toString()).build());

        when(compasSclDataService.listHistoryVersionsByUUID(uuid)).thenReturn(testData);

        var response = given()
                .pathParam("id", uuid)
                .when().get("/{id}/versions")
                .then()
                .statusCode(200)
                .extract()
                .response();

        var dataResourceHistory = response.as(DataResourceHistory.class);
        assertNotNull(dataResourceHistory);
    }

    @Test
    void retrieveDataResourceByVersion_WhenCalled_ThenReturnsHelloWorldBinaryData() {
        var id = UUID.randomUUID();
        var version = "1.0.0";

        when(compasSclDataService.findByUUID(id, new Version(version))).thenReturn("hello world");

        var response = given()
                .pathParam("id", id)
                .pathParam("version", version)
                .when().get("/{id}/version/{version}")
                .then()
                .statusCode(200)
                .extract()
                .response();

        byte[] responseData = response.asByteArray();

        assertNotNull(responseData);
        String expectedContent = "hello world";
        assertArrayEquals(expectedContent.getBytes(), responseData);
    }
}
