# SPDX-FileCopyrightText: 2026 BearingPoint GmbH
#
# SPDX-License-Identifier: Apache-2.0

@scl-data-service @archiving-resources
Feature: Archiving API

  # The API has no archive deletion endpoint; source resources and locations are cleaned up.
  Background:
    Given I am authenticated as "scl-data-editor"
    And I POST "/compas-scl-data-service/api/locations" with JSON body:
      """
      {
        "key": "archive-location-{{UUID}}",
        "name": "Archive Location {{UUID}}"
      }
      """
    Then the response status should be 200
    And I save the response field "uuid" as "locationId"
    And I save the response field "key" as "locationKey"
    And I schedule a DELETE for "/compas-scl-data-service/api/locations/{locationId}"
    Given I POST "/compas-scl-data-service/scl/v1/SCD" with XML body:
      """
      <CreateRequest xmlns="https://www.lfenergy.org/compas/SclDataService/v1">
        <Name>archive-resource-{{UUID}}</Name>
        <Comment>Archiving API initial version</Comment>
        <SclData><![CDATA[<SCL xmlns="http://www.iec.ch/61850/2003/SCL"/>]]></SclData>
      </CreateRequest>
      """
    Then the response status should be 200
    And I save the XML response element "//sds:SclData" text as "sclData"
    And I save the element "//scl:Header" attribute "id" from saved XML "sclData" as "resourceId"
    And I schedule a DELETE for "/compas-scl-data-service/scl/v1/SCD/{resourceId}"
    And I schedule a POST for "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/unassign"
    Given I POST "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/assign" with JSON body:
      """
      {}
      """
    Then the response status should be 204

  Scenario: Archive an existing SCL resource version
    When I POST "/compas-scl-data-service/api/archive/scl/{resourceId}/versions/1.0.0" with JSON body:
      """
      {}
      """
    Then the response status should be 200
    And the response field "uuid" should equal "{resourceId}"
    And the response field "name" should equal "archive-resource-{__UUID__}"
    And the response field "location" should equal "{locationKey}"
    And the response field "type" should equal "SCD"
    And the response field "version" should equal "1.0.0"
    And the response field "author" should not be empty
    And the response field "modifiedAt" should not be empty
    And the response field "archivedAt" should not be empty

    When I GET "/compas-scl-data-service/api/scl/{resourceId}/versions"
    Then the response status should be 200
    And the response field "versions.length" should equal "1"
    And the response field "versions.0.version" should equal "1.0.0"
    And the response field "versions.0.archived" should equal "true"

  Scenario: Archive an XML attachment linked to an existing resource version
    When I POST "/compas-scl-data-service/api/archive/referenced-resource/{resourceId}/versions/1.0.0" with XML body:
      """
      <Attachment>
        <Description>Archive attachment {{UUID}}</Description>
      </Attachment>
      """
    Then the response status should be 200
    And the response field "uuid" should equal "{resourceId}"
    And the response field "name" should equal "archive-resource-{__UUID__}"
    And the response field "location" should equal "{locationKey}"
    And the response field "type" should equal "SCD"
    And the response field "contentType" should equal "application/xml"
    And the response field "author" should equal "service-tests"
    And the response field "version" should equal "1.0.0"
    And the response field "archivedAt" should not be empty

    When I GET "/compas-scl-data-service/api/archive/resources/{resourceId}/versions"
    Then the response status should be 200
    And the response field "versions.length" should equal "1"
    And the response field "versions.0.uuid" should equal "{resourceId}"
    And the response field "versions.0.contentType" should equal "application/xml"
    And the response field "versions.0.version" should equal "1.0.0"

  Scenario: Retrieve multiple archived versions newest first
    Given I POST "/compas-scl-data-service/api/archive/scl/{resourceId}/versions/1.0.0" with JSON body:
      """
      {}
      """
    Then the response status should be 200
    Given I PUT "/compas-scl-data-service/scl/v1/SCD/{resourceId}" with XML body:
      """
      <UpdateRequest xmlns="https://www.lfenergy.org/compas/SclDataService/v1">
        <ChangeSet>PATCH</ChangeSet>
        <Comment>Archiving API updated version</Comment>
        <SclData><![CDATA[{sclData}]]></SclData>
      </UpdateRequest>
      """
    Then the response status should be 200
    Given I POST "/compas-scl-data-service/api/locations/{locationId}/resources/{resourceId}/assign" with JSON body:
      """
      {}
      """
    Then the response status should be 204
    Given I POST "/compas-scl-data-service/api/archive/scl/{resourceId}/versions/1.0.1" with JSON body:
      """
      {}
      """
    Then the response status should be 200

    When I GET "/compas-scl-data-service/api/archive/resources/{resourceId}/versions"
    Then the response status should be 200
    And the response field "versions.length" should equal "2"
    And the response field "versions.0.uuid" should equal "{resourceId}"
    And the response field "versions.0.version" should equal "1.0.1"
    And the response field "versions.0.location" should equal "{locationKey}"
    And the response field "versions.1.uuid" should equal "{resourceId}"
    And the response field "versions.1.version" should equal "1.0.0"
    And the response field "versions.1.location" should equal "{locationKey}"

  Scenario Outline: Retrieve empty archive history when nothing has been archived
    When I GET "/compas-scl-data-service/api/archive/resources/<resource>/versions"
    Then the response status should be 200
    And the response field "versions.length" should equal "0"

    Examples:
      | resource     |
      | {resourceId} |
      | {{UUID}}     |

  Scenario Outline: Archive a non-existing resource
    When I POST "/compas-scl-data-service/api/archive/<kind>/{{UUID}}/versions/1.0.0" with XML body:
      """
      <Attachment/>
      """
    Then the response status should be 404

    Examples:
      | kind                |
      | scl                 |
      | referenced-resource |

  Scenario: Archive an attachment for a non-existing resource version
    When I POST "/compas-scl-data-service/api/archive/referenced-resource/{resourceId}/versions/9.9.9" with XML body:
      """
      <Attachment/>
      """
    Then the response status should be 404

  Scenario: Search the archive for a non-existing resource
    When I POST "/compas-scl-data-service/api/archive/resources/search" with JSON body:
      """
      {
        "uuid": "{{UUID}}"
      }
      """
    Then the response status should be 200
    And the response field "resources.length" should equal "0"

  Scenario: Reject an invalid archive search date
    When I POST "/compas-scl-data-service/api/archive/resources/search" with JSON body:
      """
      {
        "from": "not-a-date"
      }
      """
    Then the response status should be 400

  Rule: Search for archived resources

    Background:
      Given I POST "/compas-scl-data-service/api/archive/scl/{resourceId}/versions/1.0.0" with JSON body:
        """
        {}
        """
      Then the response status should be 200

    Scenario Outline: Search archived resources by UUID, name, location or type
      When I POST "/compas-scl-data-service/api/archive/resources/search" with JSON body:
        """
        <body>
        """
      Then the response status should be 200
      And the response field "resources.length" should equal "1"
      And the response field "resources.0.uuid" should equal "{resourceId}"
      And the response field "resources.0.name" should equal "archive-resource-{__UUID__}"
      And the response field "resources.0.location" should equal "{locationKey}"
      And the response field "resources.0.type" should equal "SCD"
      And the response field "resources.0.version" should equal "1.0.0"
      And the response field "resources.0.archivedAt" should not be empty

      Examples:
        | body                                                           |
        | {"uuid": "{resourceId}"}                                       |
        | {"name": "archive-resource-{{UUID}}"}                          |
        | {"location": "{locationKey}"}                                  |
        | {"name": "archive-resource-{{UUID}}", "type": "SCD"}           |
