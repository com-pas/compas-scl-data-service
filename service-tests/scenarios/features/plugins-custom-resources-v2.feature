@scl-data-service @plugins-custom-resources @v2
Feature: Plugins Custom Resources V2

  Background:
    Given I am authenticated as "scl-data-editor"

  Scenario: Post a new valid plugin resource
    When I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
      """
      {
        "name": "engineering-wizard-process-1-{{UUID}}",
        "description": "A description for the engineering wizard process",
        "contentType": "application/json",
        "version": "1.0.1",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    Then the response status should be 200
    And the response body should not be empty
    And the response should contain field "id"
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-1-{{UUID}}"

  Scenario: Post a new plugin resource with invalid plugin name
    When I POST "/compas-scl-data-service/plugins-resources/plugins/invalid-plugin-name/types/process" with JSON body:
      """
      {
        "name": "invalid-plugin-process-1-{{UUID}}",
        "description": "A description for the invalid plugin process",
        "contentType": "application/json",
        "version": "1.0.1",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    Then the response status should be 400

  @remaining-endpoints
  Scenario: Delete every resource version of a type without affecting other types or plugins
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/delete-type-{{UUID}}" with JSON body:
      """
      {
        "name": "resource-a",
        "contentType": "application/json",
        "version": "1.0.0",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    Then the response status should be 200
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/delete-type-{{UUID}}"
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/delete-type-{{UUID}}" with JSON body:
      """
      {
        "name": "resource-a",
        "contentType": "application/json",
        "version": "1.0.1",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    Then the response status should be 200
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/delete-type-{{UUID}}" with JSON body:
      """
      {
        "name": "resource-b",
        "contentType": "application/json",
        "version": "1.0.0",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    Then the response status should be 200
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/retained-type-{{UUID}}" with JSON body:
      """
      {
        "name": "retained-resource",
        "contentType": "application/json",
        "version": "1.0.0",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    Then the response status should be 200
    And I save the response field "id" as "retainedTypeId"
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/retained-type-{{UUID}}"
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/retained_plugin/types/delete-type-{{UUID}}" with JSON body:
      """
      {
        "name": "retained-resource",
        "contentType": "application/json",
        "version": "1.0.0",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    Then the response status should be 200
    And I save the response field "id" as "retainedPluginId"
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/retained_plugin/types/delete-type-{{UUID}}"

    When I DELETE "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/delete-type-{{UUID}}"
    Then the response status should be 204

    When I GET "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/delete-type-{{UUID}}/resources/resource-a/versions"
    Then the response status should be 404

    When I GET "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/delete-type-{{UUID}}/resources/resource-b/versions"
    Then the response status should be 404

    When I GET "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/retained-type-{{UUID}}/{retainedTypeId}"
    Then the response status should be 200
    And the response field "id" should equal "{retainedTypeId}"

    When I GET "/compas-scl-data-service/plugins-resources/plugins/retained_plugin/types/delete-type-{{UUID}}/{retainedPluginId}"
    Then the response status should be 200
    And the response field "id" should equal "{retainedPluginId}"

  @remaining-endpoints
  Scenario: Delete resources of a non-existing type
    When I DELETE "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/missing-type-{{UUID}}"
    Then the response status should be 404

  Scenario: Post an existing plugin resource
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
      """
      {
        "name": "engineering-wizard-process-1-{{UUID}}",
        "description": "A description for the engineering wizard process",
        "contentType": "application/json",
        "version": "1.0.1",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    When I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
      """
      {
        "name": "engineering-wizard-process-1-{{UUID}}",
        "description": "A description for the engineering wizard process",
        "contentType": "application/json",
        "version": "1.0.1",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    Then the response status should be 409
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-1-{{UUID}}"

  Scenario: Get one plugin resource by ID
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
      """
      {
        "name": "engineering-wizard-process-1-{{UUID}}",
        "description": "A description for the engineering wizard process",
        "contentType": "application/json",
        "version": "1.0.1",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    And I save the response field "id" as "resourceId"
    When I GET "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/{resourceId}"
    Then the response status should be 200
    And the response body should not be empty
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-1-{{UUID}}"

  Scenario: Delete all plugin resources by name
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
      """
      {
        "name": "engineering-wizard-process-1-{{UUID}}",
        "description": "A description for the engineering wizard process",
        "contentType": "application/json",
        "version": "1.0.1",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    And I save the response field "id" as "processId1"
    And I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
      """
      {
        "name": "engineering-wizard-process-2-{{UUID}}",
        "description": "A description for the engineering wizard process",
        "contentType": "application/json",
        "version": "1.0.2",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    And I save the response field "id" as "processId2"
    When I DELETE "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-1-{{UUID}}"
    Then the response status should be 204

    When I GET "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/{processId1}"
    Then the response status should be 404

    When I GET "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/{processId2}"
    Then the response status should be 200
    And the response should contain field "id"
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-2-{{UUID}}"

  Scenario: Get latest version of a plugin resource by name
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
        """
        {
          "name": "engineering-wizard-process-1-{{UUID}}",
          "description": "A description for the engineering wizard process",
          "contentType": "application/json",
          "version": "1.0.0",
          "dataCompatibilityVersion": "1.0.0",
          "content": "{}"
        }
        """
    And I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
        """
        {
          "name": "engineering-wizard-process-1-{{UUID}}",
          "description": "A description for the engineering wizard process",
          "contentType": "application/json",
          "version": "1.0.2",
          "dataCompatibilityVersion": "1.0.0",
          "content": "{}"
        }
        """
    When I GET "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-1-{{UUID}}/latest"
    Then the response status should be 200
    And the response body should not be empty
    And the response field "version" should equal "1.0.2"
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-1-{{UUID}}"

  Scenario: Get latest version of a plugin resource by type
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
        """
        {
          "name": "engineering-wizard-process-1-{{UUID}}",
          "description": "A description for the engineering wizard process",
          "contentType": "application/json",
          "version": "1.0.0",
          "dataCompatibilityVersion": "1.0.0",
          "content": "{}"
        }
        """
    And I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
        """
        {
          "name": "engineering-wizard-process-1-{{UUID}}",
          "description": "A description for the engineering wizard process",
          "contentType": "application/json",
          "version": "1.0.2",
          "dataCompatibilityVersion": "1.0.0",
          "content": "{}"
        }
        """
    When I GET "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/latest"
    Then the response status should be 200
    And the response body should not be empty
    And the response should be an array
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-1-{{UUID}}"

  Scenario: Get all versions of a plugin resource by name
    Given I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
      """
      {
        "name": "engineering-wizard-process-1-{{UUID}}",
        "description": "A description for the engineering wizard process",
        "contentType": "application/json",
        "version": "1.0.0",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    And I POST "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process" with JSON body:
      """
      {
        "name": "engineering-wizard-process-1-{{UUID}}",
        "description": "A description for the engineering wizard process",
        "contentType": "application/json",
        "version": "1.0.2",
        "dataCompatibilityVersion": "1.0.0",
        "content": "{}"
      }
      """
    When I GET "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-1-{{UUID}}/versions"
    Then the response status should be 200
    And the response should be an array
    And the response body path "0.version" should be "1.0.2"
    And the response body path "1.version" should be "1.0.0"
    And I schedule a DELETE for "/compas-scl-data-service/plugins-resources/plugins/engineering_wizard/types/process/resources/engineering-wizard-process-1-{{UUID}}"
