@scl-data-service @common-resources @auth
Feature: Common API

  # Note: enable authentication in the service and configure bearer authentication in the
  # test environment before running these scenarios. The type list requires JWT groups.
  Background:
    Given I am authenticated as "scl-data-editor"

  Scenario: Retrieve the available SCL types
    When I GET "/compas-scl-data-service/common/v1/type/list"
    Then the response status should be 200
    And the response Content-Type should be XML
    And the XML response element "/sds:TypeListResponse" should exist
    And the XML response element "/sds:TypeListResponse/sds:Type[not(sds:Code) or not(sds:Description)]" should not exist

  Scenario: Retrieve user information and session timeouts
    When I GET "/compas-scl-data-service/common/v1/userinfo"
    Then the response status should be 200
    And the response Content-Type should be XML
    And the XML response element "/sds:UserInfoResponse/sds:SessionWarning" should exist
    And the XML response element "/sds:UserInfoResponse/sds:SessionExpires" should exist
    And the XML response element "/sds:UserInfoResponse[sds:SessionWarning >= 0 and sds:SessionExpires > sds:SessionWarning]" should exist
