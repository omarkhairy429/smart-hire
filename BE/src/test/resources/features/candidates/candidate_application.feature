Feature: Candidate job applications

  As a candidate
  I want to apply for jobs and review my applications
  So that I can manage my job applications on SmartHire

  @candidate @application
  Scenario: Candidate successfully applies for a published job
    Given a candidate exists with id "11111111-1111-1111-1111-111111111111"
    And a job posting exists with id "22222222-2222-2222-2222-222222222222"
    When the candidate applies with resume "https://example.com/resume.pdf" and cover letter "I am interested in this role"
    Then the application should be created
    And the application stage should be "APPLIED"
    And the application status should be "IN_REVIEW"

  @candidate @application
  Scenario: Candidate cannot apply twice to the same job
    Given a candidate exists with id "11111111-1111-1111-1111-111111111111"
    And a job posting exists with id "22222222-2222-2222-2222-222222222222"
    And the candidate has already applied to the job
    When the candidate applies with resume "https://example.com/resume.pdf" and cover letter "Second application"
    Then the application should be rejected with message "Candidate has already applied to this posting"

  @candidate @application
  Scenario: Candidate can view only their own applications
    Given a candidate exists with id "11111111-1111-1111-1111-111111111111"
    And the candidate has 2 applications
    When the candidate requests their applications
    Then 2 applications should be returned
    And every returned application should belong to the candidate
