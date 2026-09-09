Feature: Candidate interviews

  As a candidate
  I want to see my scheduled interviews
  So that I know when and where my interviews take place

  @candidate @interview
  Scenario: Candidate can view scheduled interviews for their applications
    Given an interview candidate exists with id "11111111-1111-1111-1111-111111111111"
    And the candidate has an application with id "33333333-3333-3333-3333-333333333333"
    And an interview is scheduled for that application
    When the candidate requests their interviews
    Then 1 interview should be returned
    And the returned interview should belong to the candidate application
