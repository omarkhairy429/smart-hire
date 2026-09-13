Feature: HR Application Pipeline Management

  As an HR Manager
  I want to move candidates through stages and export data
  So that I can effectively manage the recruitment funnel

  Background:
    Given the logged-in user is an HR Manager in company "TalentBridge"

  @hr @pipeline
  Scenario: HR Manager successfully updates an application stage
    Given an application exists with ID "11111111-1111-1111-1111-111111111111" in stage "APPLIED"
    When the HR Manager updates the application stage to "SCREENING"
    Then the application stage should become "SCREENING"
    And a notification for "APPLICATION_STAGE_CHANGED" should be sent to the candidate

  @hr @pipeline @export
  Scenario: HR Manager exports applications as CSV
    Given a posting exists for company "TalentBridge" with ID "22222222-2222-2222-2222-222222222222"
    And there are 2 applications for this posting
    When the HR Manager requests to export applications for this posting
    Then a CSV string containing the application data should be generated

  @hr @pipeline @security
  Scenario: HR Manager cannot access applications for another company's posting
    Given a posting exists for company "OtherCompany" with ID "33333333-3333-3333-3333-333333333333"
    When the HR Manager requests applications for this posting
    Then the pipeline action should fail with a forbidden error "You are not authorized to view applications for this posting"