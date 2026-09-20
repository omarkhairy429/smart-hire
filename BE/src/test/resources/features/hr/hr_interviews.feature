Feature: HR Interview Scheduling

  As an HR Manager
  I want to schedule and cancel interviews
  So that candidates can be evaluated by interviewers

  Background:
    Given the logged-in user is an HR Manager

  @hr @interviews
  Scenario: HR Manager successfully schedules an in-person interview
    Given an application exists with ID "11111111-1111-1111-1111-111111111111"
    And an interviewer exists with ID "22222222-2222-2222-2222-222222222222"
    When the HR Manager schedules an "IN_PERSON" interview
    Then the interview should be saved successfully
    And notifications should be sent to both candidate and interviewer

  @hr @interviews @validation
  Scenario: HR Manager cannot schedule a video interview without a link
    Given an application exists with ID "11111111-1111-1111-1111-111111111111"
    And an interviewer exists with ID "22222222-2222-2222-2222-222222222222"
    When the HR Manager schedules a "VIDEO" interview without providing a meeting link
    Then the interview action should fail with a bad-request error "Meeting link is required for VIDEO interviews"

  @hr @interviews
  Scenario: HR Manager cancels an interview
    Given a scheduled interview exists with ID "33333333-3333-3333-3333-333333333333"
    When the HR Manager cancels the interview
    Then the interview should be deleted
    And cancellation notifications should be sent
