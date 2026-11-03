# SPDX-FileCopyrightText: 2026 BearingPoint GmbH
#
# SPDX-License-Identifier: Apache-2.0

@scl-data-service @history-resources
Feature: History API

  Background:
    Given I am authenticated as "scl-data-editor"
    And I POST "/compas-scl-data-service/scl/v1/SCD" with XML body:
      """
      <CreateRequest xmlns="https://www.lfenergy.org/compas/SclDataService/v1">
        <Name>history-resource-{{UUID}}</Name>
        <Comment>History API initial version</Comment>
        <SclData><![CDATA[<SCL xmlns="http://www.iec.ch/61850/2003/SCL"/>]]></SclData>
      </CreateRequest>
      """
    Then the response status should be 200
    And I save the XML response element "//sds:SclData" text as "sclData"
    And I save the element "//scl:Header" attribute "id" from saved XML "sclData" as "resourceId"
    And I schedule a DELETE for "/compas-scl-data-service/scl/v1/SCD/{resourceId}"

  Scenario Outline: Search for a resource by UUID or name and type
    When I POST "/compas-scl-data-service/api/scl/search" with JSON body:
      """
      <body>
      """
    Then the response status should be 200
    And the response field "results.length" should equal "1"
    And the response field "results.0.uuid" should equal "{resourceId}"
    And the response field "results.0.name" should equal "history-resource-{__UUID__}"
    And the response field "results.0.type" should equal "SCD"
    And the response field "results.0.version" should equal "1.0.0"
    And the response field "results.0.available" should equal "true"
    And the response field "results.0.deleted" should equal "false"
    And the response field "results.0.author" should not be empty
    And the response field "results.0.changedAt" should not be empty

    Examples:
      | body                                                           |
      | {"uuid": "{resourceId}"}                                        |
      | {"name": "history-resource-{{UUID}}", "type": "SCD"}             |

  Scenario: Search for resources assigned to a location
    Given I POST "/compas-scl-data-service/api/locations" with JSON body:
      """
      {
        "key": "history-location-{{UUID}}",
        "name": "History Location {{UUID}}"
      }
      """
    Then the response status should be 200
    And I save the response field "uuid" as "locationId"
    And I schedule a DELETE for "/compas-scl-data-service/api/locations/{locationId}"
    And I schedule a POST for "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/unassign"
    Given I POST "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/assign" with JSON body:
      """
      {}
      """
    Then the response status should be 204

    When I POST "/compas-scl-data-service/api/scl/search" with JSON body:
      """
      {
        "location": "{locationId}"
      }
      """
    Then the response status should be 200
    And the response field "results.length" should equal "1"
    And the response field "results.0.uuid" should equal "{resourceId}"
    And the response field "results.0.location" should equal "{locationId}"

  Scenario: Search for a non-existing resource
    When I POST "/compas-scl-data-service/api/scl/search" with JSON body:
      """
      {
        "uuid": "{{UUID}}"
      }
      """
    Then the response status should be 200
    And the response field "results.length" should equal "0"

  Scenario: Reject an invalid search date
    When I POST "/compas-scl-data-service/api/scl/search" with JSON body:
      """
      {
        "from": "not-a-date"
      }
      """
    Then the response status should be 400

  Scenario: Retrieve the initial resource history
    When I GET "/compas-scl-data-service/api/scl/{resourceId}/versions"
    Then the response status should be 200
    And the response field "versions.length" should equal "1"
    And the response field "versions.0.uuid" should equal "{resourceId}"
    And the response field "versions.0.name" should equal "history-resource-{__UUID__}"
    And the response field "versions.0.type" should equal "SCD"
    And the response field "versions.0.version" should equal "1.0.0"
    And the response field "versions.0.archived" should equal "false"
    And the response field "versions.0.available" should equal "true"
    And the response field "versions.0.deleted" should equal "false"
    And the response field "versions.0.comment" should not be empty

  Scenario: Retrieve history for a non-existing resource
    When I GET "/compas-scl-data-service/api/scl/{{UUID}}/versions"
    Then the response status should be 404

  Scenario: Download a non-existing resource
    When I GET "/compas-scl-data-service/api/scl/{{UUID}}/version/1.0.0"
    Then the response status should be 404

  Scenario: Download a non-existing resource version
    When I GET "/compas-scl-data-service/api/scl/{resourceId}/version/9.9.9"
    Then the response status should be 404

  Rule: Retrieve resources with multiple versions

    Background:
      Given I PUT "/compas-scl-data-service/scl/v1/SCD/{resourceId}" with XML body:
        """
        <UpdateRequest xmlns="https://www.lfenergy.org/compas/SclDataService/v1">
          <ChangeSet>PATCH</ChangeSet>
          <Comment>History API updated version</Comment>
          <SclData><![CDATA[{sclData}]]></SclData>
        </UpdateRequest>
        """
      Then the response status should be 200

    Scenario: Retrieve all versions newest first
      When I GET "/compas-scl-data-service/api/scl/{resourceId}/versions"
      Then the response status should be 200
      And the response field "versions.length" should equal "2"
      And the response field "versions.0.uuid" should equal "{resourceId}"
      And the response field "versions.0.version" should equal "1.0.1"
      And the response field "versions.0.archived" should equal "false"
      And the response field "versions.1.uuid" should equal "{resourceId}"
      And the response field "versions.1.version" should equal "1.0.0"
      And the response field "versions.1.archived" should equal "false"

    Scenario: Search returns only the latest resource version
      When I POST "/compas-scl-data-service/api/scl/search" with JSON body:
        """
        {
          "uuid": "{resourceId}"
        }
        """
      Then the response status should be 200
      And the response field "results.length" should equal "1"
      And the response field "results.0.uuid" should equal "{resourceId}"
      And the response field "results.0.version" should equal "1.0.1"

    Scenario Outline: Download a specific resource version
      When I GET "/compas-scl-data-service/api/scl/{resourceId}/version/<version>"
      Then the response status should be 200
      And the XML response element "//scl:Header" attribute "id" should be "{resourceId}"
      And the XML response element "//scl:Header" attribute "version" should be "<version>"

      Examples:
        | version |
        | 1.0.0   |
        | 1.0.1   |