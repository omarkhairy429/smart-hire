Feature: Super Admin Dashboard and Audit Logs

  As a Super Admin
  I want to view platform statistics and audit logs
  So that I can monitor the overall health and activity of the platform

  @superadmin @dashboard
  Scenario: Super Admin views the platform statistics
    Given the platform has 10 total users
    And 4 of them are active staff members and 2 are inactive staff members
    And the platform has 15 total job postings, 5 of which are published
    And there are 50 total applications
    When the Super Admin requests platform stats
    Then the stats should show 10 total users
    And the stats should show 4 active and 2 inactive staff
    And the stats should show 15 total postings and 5 published postings
    And the stats should show 50 total applications

  @superadmin @auditlog
  Scenario: Super Admin views paginated audit logs
    Given the system has recorded audit logs for various actions
    When the Super Admin requests page 0 of audit logs with size 20
    Then a paginated list of audit logs should be returned