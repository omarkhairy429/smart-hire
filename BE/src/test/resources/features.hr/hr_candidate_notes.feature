Feature: HR Candidate Notes Management

  As an HR Manager
  I want to add and view internal notes on candidate profiles
  So that I can keep track of evaluations and share them with the team

  @hr @notes
  Scenario: HR Manager successfully adds a note to a candidate profile
    Given the logged-in user is an HR Manager named "Alice HR"
    And a candidate exists with id "11111111-1111-1111-1111-111111111111"
    When the HR Manager adds a note with content "Strong Java skills" for candidate "11111111-1111-1111-1111-111111111111"
    Then the note should be saved successfully
    And the note response should contain the author name "Alice HR" and content "Strong Java skills"

  @hr @notes @validation
  Scenario: HR Manager cannot add a note for a non-existent candidate
    Given the logged-in user is an HR Manager named "Alice HR"
    And a candidate with id "99999999-9999-9999-9999-999999999999" does not exist
    When the HR Manager adds a note with content "Candidate did not show up" for candidate "99999999-9999-9999-9999-999999999999"
    Then the action should fail with a not found error "Candidate not found"

  @hr @notes
  Scenario: HR Manager views notes for a specific candidate
    Given a candidate exists with id "11111111-1111-1111-1111-111111111111"
    And the candidate has 2 existing notes
    When the HR Manager requests notes for candidate "11111111-1111-1111-1111-111111111111"
    Then exactly 2 notes should be returned