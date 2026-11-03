@scl-data-service @plugins-custom-resources @v1
Feature: Plugins Custom Resources V1

  Background:
    Given I am authenticated as "scl-data-editor"

  Scenario: Post a new valid plugin data resource
    When I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 1 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    Then the response status should be 200
    And the response body should not be empty
    And the response should contain field "id"
    And I schedule a DELETE for "/compas-scl-data-service/plugins/resources/engineering-wizard_process_{{UUID}}"

  @remaining-endpoints
  Scenario: Get the latest version of each plugin resource by type
    Given I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "latest-type-{{UUID}}",
        "name": "Resource A {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "version": "1.0.0"
      }
      """
    Then the response status should be 200
    And I schedule a DELETE for "/compas-scl-data-service/plugins/resources/latest-type-{{UUID}}"
    Given I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "latest-type-{{UUID}}",
        "name": "Resource A {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "version": "1.0.2"
      }
      """
    Then the response status should be 200
    Given I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "latest-type-{{UUID}}",
        "name": "Resource B {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "version": "1.0.0"
      }
      """
    Then the response status should be 200

    When I GET "/compas-scl-data-service/plugins/resources/latest-type-{{UUID}}/latest"
    Then the response status should be 200
    And the response should be an array
    And the response field "length" should equal "2"
    And the response field "0.name" should equal "Resource A {__UUID__}"
    And the response field "0.version" should equal "1.0.2"
    And the response field "0.type" should equal "latest-type-{__UUID__}"
    And the response field "1.name" should equal "Resource B {__UUID__}"
    And the response field "1.version" should equal "1.0.0"

  @remaining-endpoints
  Scenario: Get latest resources for a non-existing type
    When I GET "/compas-scl-data-service/plugins/resources/missing-type-{{UUID}}/latest"
    Then the response status should be 404

  Scenario: Post a new plugin resource with invalid content-type
    When I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "name": "Engineering Wizard Process 1 {{UUID}}",
        "type": "engineering-wizard_process_{{UUID}}",
        "content-type": "text/plain",
        "content": "text content",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "minor"
      }
      """
    Then the response status should be 400

  Scenario: Post an existing plugin resource
    Given I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 1 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    When I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 1 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    Then the response status should be 409
    And I schedule a DELETE for "/compas-scl-data-service/plugins/resources/engineering-wizard_process_{{UUID}}"

  Scenario: Get all plugin resources of a specific type
    Given I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 2 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    And I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 3 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    When I GET "/compas-scl-data-service/plugins/resources?type=engineering-wizard_process_{{UUID}}"
    Then the response status should be 200
    And the response body should not be empty
    And the response field "totalElements" should equal "2"
    And I schedule a DELETE for "/compas-scl-data-service/plugins/resources/engineering-wizard_process_{{UUID}}"


  Scenario: Get one plugin resource by its ID
    Given I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 1 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    And I save the response field "id" as "resourceId"
    And I save the response field "name" as "resourceName"
    When I GET "/compas-scl-data-service/plugins/resources/{resourceId}"
    Then the response status should be 200
    And the response body should not be empty
    And the response field "name" should equal "{resourceName}"
    And I schedule a DELETE for "/compas-scl-data-service/plugins/resources/engineering-wizard_process_{{UUID}}"

  Scenario: Get latest version of a plugin resource by type and name
    Given I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 1 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    And I save the response field "name" as "resourceName"
    And I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 1 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.1",
        "nextVersionType": "something"
      }
      """
    And I save the response field "id" as "resourceId"
    When I GET "/compas-scl-data-service/plugins/resources/engineering-wizard_process_{{UUID}}/Engineering Wizard Process 1 {{UUID}}/latest"
    Then the response status should be 200
    And the response body should not be empty
    And the response field "name" should equal "{resourceName}"
    And the response field "version" should equal "1.0.1"
    And I schedule a DELETE for "/compas-scl-data-service/plugins/resources/engineering-wizard_process_{{UUID}}"


  Scenario: Delete all plugin resources by type
    Given I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 1 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    And I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "template-generator_type_{{UUID}}",
        "name": "Template Generator Type 1 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    When I DELETE "/compas-scl-data-service/plugins/resources/engineering-wizard_process_{{UUID}}"
    Then the response status should be 204

    When I GET "/compas-scl-data-service/plugins/resources?type=engineering-wizard_process_{{UUID}}"
    Then the response status should be 200
    And the response field "totalElements" should equal "0"

    When I GET "/compas-scl-data-service/plugins/resources?type=template-generator_type_{{UUID}}"
    Then the response status should be 200
    And the response field "totalElements" should equal "1"
    And I schedule a DELETE for "/compas-scl-data-service/plugins/resources/template-generator_type_{{UUID}}"

  Scenario: Delete all plugin resources by type and name
    Given I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 1 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    And I POST "/compas-scl-data-service/plugins/resources" with multipart form data:
      """
      {
        "type": "engineering-wizard_process_{{UUID}}",
        "name": "Engineering Wizard Process 2 {{UUID}}",
        "content-type": "application/json",
        "content": "{}",
        "data-compatibility-version": "1.0.0",
        "description": "some description",
        "version": "1.0.0",
        "nextVersionType": "something"
      }
      """
    When I DELETE "/compas-scl-data-service/plugins/resources/engineering-wizard_process_{{UUID}}/Engineering Wizard Process 1 {{UUID}}"
    Then the response status should be 204

    When I GET "/compas-scl-data-service/plugins/resources?type=engineering-wizard_process_{{UUID}}"
    Then the response status should be 200
    And the response field "totalElements" should equal "1"
    And I schedule a DELETE for "/compas-scl-data-service/plugins/resources/engineering-wizard_process_{{UUID}}"
