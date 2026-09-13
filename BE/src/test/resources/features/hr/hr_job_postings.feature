Feature: HR Job Postings Management

  As an HR Manager
  I want to create, publish, and close job postings
  So that I can manage the recruitment needs of my company

  Background:
    Given the logged-in user is an HR Manager named "Ahmed HR" with ID "11111111-1111-1111-1111-111111111111"

  @hr @postings
  Scenario: HR Manager successfully creates a draft posting
    When the HR Manager creates a draft posting titled "Backend Developer" for company "TalentBridge"
    Then the posting should be saved successfully
    And the posting status should be "DRAFT"

  @hr @postings
  Scenario: HR Manager successfully publishes a valid draft posting
    Given a draft posting exists with ID "22222222-2222-2222-2222-222222222222" owned by the HR Manager
    And the posting has all required fields filled
    When the HR Manager publishes the posting
    Then the posting status should become "PUBLISHED"

  @hr @postings @validation
  Scenario: HR Manager cannot publish a draft without a description
    Given a draft posting exists with ID "33333333-3333-3333-3333-333333333333" owned by the HR Manager
    And the posting is missing a description
    When the HR Manager publishes the posting
    Then the posting action should fail with a bad-request error "A published posting must have a description"

  @hr @postings @security
  Scenario: HR Manager cannot close a posting owned by someone else
    Given a published posting exists with ID "44444444-4444-4444-4444-444444444444" owned by another HR Manager
    When the HR Manager closes the posting
    Then the posting action should fail with a forbidden error "you cannot modify this posting"