Feature: Super Admin User Management

  As a Super Admin
  I want to create and view HR Managers and Interviewers
  So that the platform is managed by authorized personnel

  @superadmin @create
  Scenario Outline: Super Admin successfully creates new staff members
    Given an email "new_staff@talentbridge.com" does not exist
    When the Super Admin creates a user with role "<Role>", email "new_staff@talentbridge.com", and company "TalentBridge"
    Then the staff account should be created successfully
    And a welcome email should be sent to "new_staff@talentbridge.com"

    Examples:
      | Role        |
      | HR_MANAGER  |
      | INTERVIEWER |

  @superadmin @validation
  Scenario: Super Admin cannot create a user with an existing email
    Given an email "existing@talentbridge.com" already exists
    When the Super Admin creates a user with role "HR_MANAGER", email "existing@talentbridge.com", and company "TalentBridge"
    Then the user creation should fail with message "User with this email already exists: existing@talentbridge.com"

  @superadmin @validation
  Scenario: Super Admin cannot create a user without a company name
    Given an email "hr@talentbridge.com" does not exist
    When the Super Admin creates a user with role "HR_MANAGER", email "hr@talentbridge.com", and company ""
    Then the user creation should fail with message "A company name is required for HR Managers and Interviewers"

  @superadmin @validation
  Scenario Outline: Super Admin cannot create users with unauthorized roles
    Given an email "unauthorized@talentbridge.com" does not exist
    When the Super Admin creates a user with role "<InvalidRole>", email "unauthorized@talentbridge.com", and company "TalentBridge"
    Then the user creation should fail with message "Super Admin can only create HR Managers or Interviewers"

    Examples:
      | InvalidRole |
      | CANDIDATE   |
      | SUPER_ADMIN |

  @superadmin @view
  Scenario: Super Admin retrieves a precise list of only authorized staff members
    Given the system contains 2 "HR_MANAGER" and 3 "INTERVIEWER" accounts
    When the Super Admin requests to view all staff
    Then exactly 5 staff members should be returned
    And the returned list should only contain "HR_MANAGER" and "INTERVIEWER" roles