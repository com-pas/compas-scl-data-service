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
