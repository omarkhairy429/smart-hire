Feature: Super Admin Account Status Management

  As a Super Admin
  I want to activate and deactivate staff members
  So that I can control who has access to the platform

  @superadmin @status
  Scenario: Super Admin successfully deactivates an active staff member
    Given an active staff member exists with id "11111111-1111-1111-1111-111111111111" and role "HR_MANAGER"
    When the Super Admin requests to deactivate the staff member
    Then the staff account should become inactive
    And an audit log for "STAFF_DEACTIVATED" should be created

  @superadmin @status
  Scenario: Super Admin successfully reactivates a deactivated staff member
    Given a deactivated staff member exists with id "22222222-2222-2222-2222-222222222222" and role "INTERVIEWER"
    When the Super Admin requests to reactivate the staff member
    Then the staff account should become active
    And an audit log for "STAFF_REACTIVATED" should be created

  @superadmin @validation @status
  Scenario: Super Admin cannot deactivate an already inactive staff member
    Given a deactivated staff member exists with id "22222222-2222-2222-2222-222222222222" and role "INTERVIEWER"
    When the Super Admin requests to deactivate the staff member
    Then the action should fail with a conflict error "This account is already deactivated"

  @superadmin @validation @status
  Scenario: Super Admin cannot change status of Candidates
    Given a user exists with id "33333333-3333-3333-3333-333333333333" and role "CANDIDATE"
    When the Super Admin requests to deactivate the staff member
    Then the action should fail with a bad request error "Only HR Managers and Interviewers can be managed"