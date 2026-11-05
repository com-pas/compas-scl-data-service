# SPDX-FileCopyrightText: 2026 BearingPoint GmbH
#
# SPDX-License-Identifier: Apache-2.0

@scl-data-service @scl-resources
Feature: SCL API

  Background:
    Given I am authenticated as "scl-data-editor"
    And I POST "/compas-scl-data-service/scl/v1/SCD" with XML body:
      """
      <CreateRequest xmlns="https://www.lfenergy.org/compas/SclDataService/v1">
        <Name>scl-resource-{{UUID}}</Name>
        <Comment>SCL API initial version</Comment>
        <SclData><![CDATA[<SCL xmlns="http://www.iec.ch/61850/2003/SCL"/>]]></SclData>
      </CreateRequest>
      """
    Then the response status should be 200
    And I save the XML response element "//sds:SclData" text as "sclData"
    And I save the element "//scl:Header" attribute "id" from saved XML "sclData" as "resourceId"
    And I schedule a DELETE for "/compas-scl-data-service/scl/v1/SCD/{resourceId}"

  Scenario: Create and retrieve an SCL document
    Then the response Content-Type should be XML
    And the XML response element "/sds:CreateResponse/sds:SclData" should exist
    And the XML response should contain text "scl-resource-{{UUID}}"

    When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}"
    Then the response status should be 200
    And the response Content-Type should be XML
    And the XML response element "/sds:GetResponse/sds:SclData" should have text "{sclData}"

  Scenario: List SCL documents of a specific type
    When I GET "/compas-scl-data-service/scl/v1/SCD/list"
    Then the response status should be 200
    And the response Content-Type should be XML
    And the XML response element "/sds:ListResponse" should exist
    And the XML response should contain text "{resourceId}"
    And the XML response should contain text "scl-resource-{{UUID}}"

  Scenario: List the initial SCL document version
    When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}/versions"
    Then the response status should be 200
    And the XML response should have 1 "/sds:VersionsResponse/sds:HistoryItem" elements
    And the XML response element "//sds:HistoryItem/sds:Id" should have text "{resourceId}"
    And the XML response element "//sds:HistoryItem/sds:Name" should have text "scl-resource-{{UUID}}"
    And the XML response element "//sds:HistoryItem/sds:Version" should have text "1.0.0"

  Scenario Outline: Check whether an SCL document name already exists
    When I POST "/compas-scl-data-service/scl/v1/SCD/checkname" with XML body:
      """
      <DuplicateNameCheckRequest xmlns="https://www.lfenergy.org/compas/SclDataService/v1">
        <Name><name></Name>
      </DuplicateNameCheckRequest>
      """
    Then the response status should be 200
    And the XML response element "/sds:DuplicateNameCheckResponse/sds:Duplicate" should have text "<duplicate>"

    Examples:
      | name                          | duplicate |
      | scl-resource-{{UUID}}         | true      |
      | unused-scl-resource-{{UUID}}  | false     |

  Scenario: Reject an invalid SCL document name
    When I POST "/compas-scl-data-service/scl/v1/SCD/checkname" with XML body:
      """
      <DuplicateNameCheckRequest xmlns="https://www.lfenergy.org/compas/SclDataService/v1">
        <Name>invalid/name</Name>
      </DuplicateNameCheckRequest>
      """
    Then the response status should be 400

  Scenario: Retrieve a non-existing SCL document
    When I GET "/compas-scl-data-service/scl/v1/SCD/{{UUID}}"
    Then the response status should be 404

  Scenario: Retrieve a non-existing SCL document version
    When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}/9.9.9"
    Then the response status should be 404

  Scenario: List versions of a non-existing SCL document
    When I GET "/compas-scl-data-service/scl/v1/SCD/{{UUID}}/versions"
    Then the response status should be 404

  Rule: Manage multiple SCL document versions

    Background:
      Given I PUT "/compas-scl-data-service/scl/v1/SCD/{resourceId}" with XML body:
        """
        <UpdateRequest xmlns="https://www.lfenergy.org/compas/SclDataService/v1">
          <ChangeSet>PATCH</ChangeSet>
          <Comment>SCL API updated version</Comment>
          <SclData><![CDATA[{sclData}]]></SclData>
        </UpdateRequest>
        """
      Then the response status should be 200
      And I save the XML response element "//sds:SclData" text as "updatedSclData"

    Scenario: Retrieve the latest SCL document after an update
      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}"
      Then the response status should be 200
      And the XML response element "/sds:GetResponse/sds:SclData" should have text "{updatedSclData}"
      And the XML response should contain text "SCL API updated version"

    Scenario: List all SCL document versions
      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}/versions"
      Then the response status should be 200
      And the XML response should have 2 "/sds:VersionsResponse/sds:HistoryItem" elements
      And the XML response element "//sds:HistoryItem[1]/sds:Id" should have text "{resourceId}"
      And the XML response element "//sds:HistoryItem[1]/sds:Version" should have text "1.0.0"
      And the XML response element "//sds:HistoryItem[2]/sds:Id" should have text "{resourceId}"
      And the XML response element "//sds:HistoryItem[2]/sds:Version" should have text "1.0.1"

    Scenario Outline: Retrieve a specific SCL document version
      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}/<version>"
      Then the response status should be 200
      And the XML response element "/sds:GetResponse/sds:SclData" should have text "<content>"

      Examples:
        | version | content          |
        | 1.0.0   | {sclData}        |
        | 1.0.1   | {updatedSclData} |

    Scenario: Delete all versions of an SCL document
      When I DELETE "/compas-scl-data-service/scl/v1/SCD/{resourceId}"
      Then the response status should be 204

      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}"
      Then the response status should be 404

      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}/versions"
      Then the response status should be 404

    Scenario: Delete an older SCL document version and retain the latest
      When I DELETE "/compas-scl-data-service/scl/v1/SCD/{resourceId}/1.0.0"
      Then the response status should be 204

      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}/1.0.0"
      Then the response status should be 404

      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}"
      Then the response status should be 200
      And the XML response element "/sds:GetResponse/sds:SclData" should have text "{updatedSclData}"

      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}/versions"
      Then the response status should be 200
      And the XML response should have 1 "/sds:VersionsResponse/sds:HistoryItem" elements
      And the XML response element "//sds:HistoryItem/sds:Version" should have text "1.0.1"

    Scenario: Delete the latest SCL document version and retrieve the previous one
      When I DELETE "/compas-scl-data-service/scl/v1/SCD/{resourceId}/1.0.1"
      Then the response status should be 204

      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}/1.0.1"
      Then the response status should be 404

      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}"
      Then the response status should be 200
      And the XML response element "/sds:GetResponse/sds:SclData" should have text "{sclData}"

      When I GET "/compas-scl-data-service/scl/v1/SCD/{resourceId}/versions"
      Then the response status should be 200
      And the XML response should have 1 "/sds:VersionsResponse/sds:HistoryItem" elements
      And the XML response element "//sds:HistoryItem/sds:Version" should have text "1.0.0"
