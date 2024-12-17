package org.lfenergy.compas.scl.data.rest.api.locations;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.response.Response;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Test;
import org.lfenergy.compas.scl.data.exception.CompasSclDataServiceException;
import org.lfenergy.compas.scl.data.model.ILocationMetaItem;
import org.lfenergy.compas.scl.data.rest.api.locations.model.Location;
import org.lfenergy.compas.scl.data.service.CompasSclDataService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.lfenergy.compas.scl.data.exception.CompasSclDataServiceErrorCode.POSTGRES_INSERT_ERROR_CODE;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(LocationsResource.class)
@TestSecurity(user = "test-user")
class LocationsResourceTest {

    @InjectMock
    private CompasSclDataService compasSclDataService;
    @InjectMock
    private JsonWebToken jwt;

    @Test
    void createLocation_WhenCalled_ThenReturnsCreatedLocation() {
        UUID uuid = UUID.randomUUID();
        String key = "Key";
        String name = "Name";
        String description = "Description";
        Location location = new Location();
        location.setUuid(uuid.toString());
        location.setKey(key);
        location.setName(name);
        location.setDescription(description);
        ILocationMetaItem testData = new LocationResourceTestDataBuilder().setId(uuid.toString()).build();

        when(jwt.getClaim("name")).thenReturn("test user");
        when(compasSclDataService.createLocation(key, name, description, "test user")).thenReturn(testData);
        Response response = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(location)
            .when().post("/")
            .then()
            .statusCode(200)
            .extract()
            .response();

        Location result = response.as(Location.class);
        assertEquals(uuid, UUID.fromString(result.getUuid()));
        assertEquals(key, result.getKey());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
    }

    @Test
    void deleteLocation_WhenCalled_ThenDeletesLocation() {
        UUID uuid = UUID.randomUUID();
        ILocationMetaItem testData = new LocationResourceTestDataBuilder().setId(uuid.toString()).build();
        when(compasSclDataService.findLocationByUUID(uuid)).thenReturn(testData);
        given()
            .contentType(MediaType.APPLICATION_JSON)
            .when().delete("/" + uuid)
            .then()
            .statusCode(204)
            .extract()
            .response();
    }

    @Test
    void getLocation_WhenCalled_ThenReturnsLocation() {
        UUID uuid = UUID.randomUUID();
        String key = "Key";
        String name = "Name";
        String description = "Description";
        Location location = new Location();
        location.setUuid(uuid.toString());
        location.setKey(key);
        location.setName(name);
        location.setDescription(description);
        ILocationMetaItem testData = new LocationResourceTestDataBuilder().setId(uuid.toString()).build();

        when(compasSclDataService.findLocationByUUID(uuid)).thenReturn(testData);

        Response response = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(location)
            .when().get("/" + uuid)
            .then()
            .statusCode(200)
            .extract()
            .response();

        Location result = response.as(Location.class);
        assertEquals(uuid, UUID.fromString(result.getUuid()));
        assertEquals(key, result.getKey());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
    }

    @Test
    void getLocations_WhenCalled_ThenReturnsLocations() {
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        String key = "Key";
        String name = "Name";
        String description = "Description";
        Location location = new Location();
        location.setUuid(uuid.toString());
        location.setKey(key);
        location.setName(name);
        location.setDescription(description);
        ILocationMetaItem testData = new LocationResourceTestDataBuilder().setId(uuid.toString()).build();
        ILocationMetaItem testData2 = new LocationResourceTestDataBuilder().setId(uuid2.toString()).build();

        when(compasSclDataService.listLocations(0, 25)).thenReturn(List.of(testData, testData2));

        Response response = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(location)
            .when().get("/")
            .then()
            .statusCode(200)
            .extract()
            .response();

        List<LinkedHashMap> result = response.as(List.class);
        List<Location> mappedResult = new ArrayList<>();
        result.stream().map(entry ->
                new Location()
                    .uuid((String) entry.get("uuid"))
                    .key((String) entry.get("key"))
                    .name((String) entry.get("name"))
                    .description((String) entry.get("description")))
            .forEach(mappedResult::add);
        assertEquals(uuid, UUID.fromString(mappedResult.get(0).getUuid()));
        assertEquals(key, mappedResult.get(0).getKey());
        assertEquals(name, mappedResult.get(0).getName());
        assertEquals(description, mappedResult.get(0).getDescription());
        assertEquals(uuid2, UUID.fromString(mappedResult.get(1).getUuid()));
        assertEquals(key, mappedResult.get(1).getKey());
        assertEquals(name, mappedResult.get(1).getName());
        assertEquals(description, mappedResult.get(1).getDescription());
    }

    @Test
    void unassignResourceFromLocation_WhenSqlExceptionOccurs_ThenReturns500StatusCode() {
        UUID locationUuid = UUID.randomUUID();
        UUID resourceUuid = UUID.randomUUID();

        doThrow(new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error unassigning SCL Resource from Location in database!"))
            .when(compasSclDataService).unassignResourceFromLocation(locationUuid, resourceUuid);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .when().post("/"+locationUuid+"/resources/"+resourceUuid+"/unassign")
            .then()
            .statusCode(500);
    }

    @Test
    void assignResourceToLocation_WhenSqlExceptionOccurs_ThenReturns500StatusCode() {
        UUID locationUuid = UUID.randomUUID();
        UUID resourceUuid = UUID.randomUUID();

        doThrow(new CompasSclDataServiceException(POSTGRES_INSERT_ERROR_CODE, "Error assigning SCL Resource from Location in database!"))
            .when(compasSclDataService).assignResourceToLocation(locationUuid, resourceUuid);

        given()
            .contentType(MediaType.APPLICATION_JSON)
            .when().post("/"+locationUuid+"/resources/"+resourceUuid+"/assign")
            .then()
            .statusCode(500);
    }

    @Test
    void updateLocation_WhenCalled_ThenReturnsUpdatedLocation() {
        UUID uuid = UUID.randomUUID();
        String key = "Key";
        String name = "Name";
        String description = "Description";
        Location location = new Location();
        location.setUuid(uuid.toString());
        location.setKey(key);
        location.setName(name);
        location.setDescription(description);
        ILocationMetaItem testData = new LocationResourceTestDataBuilder().setId(uuid.toString()).build();

        when(compasSclDataService.updateLocation(uuid, key, name, description)).thenReturn(testData);
        Response response = given()
            .contentType(MediaType.APPLICATION_JSON)
            .body(location)
            .when().put("/" + uuid)
            .then()
            .statusCode(200)
            .extract()
            .response();

        Location result = response.as(Location.class);
        assertEquals(uuid, UUID.fromString(result.getUuid()));
        assertEquals(key, result.getKey());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
    }
}