# SPDX-FileCopyrightText: 2026 BearingPoint GmbH
#
# SPDX-License-Identifier: Apache-2.0

@scl-data-service @location-resources
Feature: Location API

  Background:
    Given I am authenticated as "scl-data-editor"
    And I POST "/compas-scl-data-service/api/locations" with JSON body:
      """
      {
        "key": "location-{{UUID}}",
        "name": "Location {{UUID}}",
        "description": "Location API service test"
      }
      """
    Then the response status should be 200
    And I save the response field "uuid" as "locationId"
    And I save the response field "key" as "locationKey"
    And I save the response field "name" as "locationName"
    And I schedule a DELETE for "/compas-scl-data-service/api/locations/{locationId}"

  Scenario: Create a new location
    Then the response field "uuid" should not be empty
    And the response field "key" should equal "location-{__UUID__}"
    And the response field "name" should equal "Location {__UUID__}"
    And the response field "description" should equal "Location API service test"
    And the response field "assignedResources" should equal "0"

  Scenario: Get list of available locations with setup
    When I GET "/compas-scl-data-service/api/locations"
    Then the response status should be 200
    And the response should be an array
    And the response array should have at least 1 items
    And each item in response should have field "uuid"
    And each item in response should have field "key"
    And each item in response should have field "name"
    And each item in response should have field "assignedResources"

  Scenario: Get a page of locations
    When I GET "/compas-scl-data-service/api/locations?page=0&pageSize=1"
    Then the response status should be 200
    And the response should be an array
    And the response array should have at least 1 items
    And the response field "length" should equal "1"
    And each item in response should have field "uuid"

  Scenario: Get a location by its ID
    When I GET "/compas-scl-data-service/api/locations/{locationId}"
    Then the response status should be 200
    And the response field "uuid" should equal "{locationId}"
    And the response field "key" should equal "{locationKey}"
    And the response field "name" should equal "{locationName}"
    And the response field "description" should equal "Location API service test"
    And the response field "assignedResources" should equal "0"

  Scenario: Update a location
    When I PUT "/compas-scl-data-service/api/locations/{locationId}" with JSON body:
      """
      {
        "uuid": "{{UUID}}",
        "key": "updated-location-{{UUID}}",
        "name": "Updated Location {{UUID}}",
        "description": "Updated description"
      }
      """
    Then the response status should be 200
    And the response field "uuid" should equal "{locationId}"
    And the response field "key" should equal "updated-location-{__UUID__}"
    And the response field "name" should equal "Updated Location {__UUID__}"
    And I save the response field "key" as "updatedKey"
    And I save the response field "name" as "updatedName"
    And the response field "description" should equal "Updated description"

    When I GET "/compas-scl-data-service/api/locations/{locationId}"
    Then the response status should be 200
    And the response field "uuid" should equal "{locationId}"
    And the response field "key" should equal "{updatedKey}"
    And the response field "name" should equal "{updatedName}"
    And the response field "description" should equal "Updated description"
    And the response field "assignedResources" should equal "0"

  Scenario: Delete a location without assigned resources
    When I DELETE "/compas-scl-data-service/api/locations/{locationId}"
    Then the response status should be 204

    When I GET "/compas-scl-data-service/api/locations/{locationId}"
    Then the response status should be 404

  Scenario Outline: Reject creation of a location with missing required fields
    When I POST "/compas-scl-data-service/api/locations" with JSON body:
      """
      <body>
      """
    Then the response status should be 400

    Examples:
      | body                              |
      | {"name": "Missing key {{UUID}}"}   |
      | {"key": "missing-name-{{UUID}}"}   |

  Scenario Outline: Reject an update with missing required fields
    When I PUT "/compas-scl-data-service/api/locations/{locationId}" with JSON body:
      """
      <body>
      """
    Then the response status should be 400

    When I GET "/compas-scl-data-service/api/locations/{locationId}"
    Then the response status should be 200
    And the response field "key" should equal "{locationKey}"
    And the response field "name" should equal "{locationName}"

    Examples:
      | body                              |
      | {"name": "Missing key {{UUID}}"}   |
      | {"key": "missing-name-{{UUID}}"}   |

  Scenario: Get a non-existing location
    When I GET "/compas-scl-data-service/api/locations/{{UUID}}"
    Then the response status should be 404

  Scenario: Update a non-existing location
    When I PUT "/compas-scl-data-service/api/locations/{{UUID}}" with JSON body:
      """
      {
        "key": "missing-location-{{UUID}}",
        "name": "Missing Location {{UUID}}",
        "description": "This location does not exist"
      }
      """
    Then the response status should be 404

  Scenario: Delete a non-existing location
    When I DELETE "/compas-scl-data-service/api/locations/{{UUID}}"
    Then the response status should be 404

  Scenario Outline: Reject assignment operations for a non-existing resource
    When I POST "/compas-scl-data-service/api/locations/{locationId}/resources/{{UUID}}/<operation>" with JSON body:
      """
      {}
      """
    Then the response status should be 404

    When I GET "/compas-scl-data-service/api/locations/{locationId}"
    Then the response status should be 200
    And the response field "assignedResources" should equal "0"

    Examples:
      | operation |
      | assign    |
      | unassign  |

  Rule: Manage assignments of existing SCL resources

    Background:
      Given I POST "/compas-scl-data-service/scl/v1/SCD" with XML body:
        """
        <CreateRequest xmlns="https://www.lfenergy.org/compas/SclDataService/v1">
          <Name>location-resource-{{UUID}}</Name>
          <Comment>Location API service test</Comment>
          <SclData><![CDATA[<SCL xmlns="http://www.iec.ch/61850/2003/SCL"/>]]></SclData>
        </CreateRequest>
        """
      Then the response status should be 200
      And I save the XML response element "//sds:SclData" text as "sclData"
      And I save the element "//scl:Header" attribute "id" from saved XML "sclData" as "resourceId"
      And I schedule a DELETE for "/compas-scl-data-service/scl/v1/SCD/{resourceId}"
      And I schedule a POST for "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/unassign"

    Scenario: Assign a resource and find it by location
      When I POST "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/assign" with JSON body:
        """
        {}
        """
      Then the response status should be 204

      When I GET "/compas-scl-data-service/api/locations/{locationId}"
      Then the response status should be 200
      And the response field "assignedResources" should equal "1"

      When I POST "/compas-scl-data-service/api/scl/search" with JSON body:
        """
        {
          "location": "{locationId}"
        }
        """
      Then the response status should be 200
      And the response field "results.length" should equal "1"
      And the response field "results.0.uuid" should equal "{resourceId}"

      When I POST "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/assign" with JSON body:
        """
        {}
        """
      Then the response status should be 204

      When I GET "/compas-scl-data-service/api/locations/{locationId}"
      Then the response status should be 200
      And the response field "assignedResources" should equal "1"

    Scenario: Unassign a resource and delete the now empty location
      Given I POST "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/assign" with JSON body:
        """
        {}
        """
      Then the response status should be 204

      When I POST "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/unassign" with JSON body:
        """
        {}
        """
      Then the response status should be 204

      When I GET "/compas-scl-data-service/api/locations/{locationId}"
      Then the response status should be 200
      And the response field "assignedResources" should equal "0"

      When I POST "/compas-scl-data-service/api/scl/search" with JSON body:
        """
        {
          "location": "{locationId}"
        }
        """
      Then the response status should be 200
      And the response field "results.length" should equal "0"

      When I DELETE "/compas-scl-data-service/api/locations/{locationId}"
      Then the response status should be 204

      When I GET "/compas-scl-data-service/api/locations/{locationId}"
      Then the response status should be 404

    Scenario: Reject deleting a location with an assigned resource
      Given I POST "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/assign" with JSON body:
        """
        {}
        """
      Then the response status should be 204

      When I DELETE "/compas-scl-data-service/api/locations/{locationId}"
      Then the response status should be 409

      When I GET "/compas-scl-data-service/api/locations/{locationId}"
      Then the response status should be 200
      And the response field "assignedResources" should equal "1"

    Scenario Outline: Reject assignment operations for a non-existing location
      When I POST "/compas-scl-data-service/api/locations/{{UUID}}/resources/{resourceId}/<operation>" with JSON body:
        """
        {}
        """
      Then the response status should be 404

      When I GET "/compas-scl-data-service/api/locations/{locationId}"
      Then the response status should be 200
      And the response field "assignedResources" should equal "0"

      Examples:
        | operation |
        | assign    |
        | unassign  |