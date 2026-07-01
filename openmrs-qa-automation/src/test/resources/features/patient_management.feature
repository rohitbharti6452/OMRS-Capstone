@patient-management
Feature: Patient Management
  As a clinician using OpenMRS
  I want to manage patients in the system
  So that I can deliver accurate and efficient care

  Background:
    Given the OpenMRS login page is displayed

  @smoke @login
  Scenario: Successful login with valid credentials
    When I enter username "admin" and password "Admin123"
    And I select the session location
    And I click the login button
    Then I should be on the dashboard

  @regression @login
  Scenario: Failed login with invalid password shows an error
    When I enter username "admin" and password "wrongpassword"
    And I select the session location
    And I click the login button
    Then I should see the error message "Invalid username/password. Please try again."

  @regression @login
  Scenario Outline: Login outcome varies by credential validity
    When I enter username "<username>" and password "<password>"
    And I select the session location
    And I click the login button
    Then the login outcome should be "<outcome>"

    Examples:
      | username | password    | outcome |
      | admin    | Admin123    | success |
      | admin    | badpassword | failure |
      | nobody   | Admin123    | failure |

  @smoke @registration
  Scenario: Register a new patient through the demographics wizard
    Given I am logged in as "admin" with password "Admin123"
    When I navigate to the patient registration page
    And I enter a generated first and last name
    And I click next to the gender section
    And I select the gender "M"
    And I click next to the birthdate section
    And I enter birthdate day "10" month "6" year "1985"
    And I click next to the confirmation section
    And I confirm the patient registration
    Then the patient should be registered successfully

  @smoke @search
  Scenario: Search for an existing patient by name returns results
    Given I am logged in as "admin" with password "Admin123"
    When I navigate to the find patient page
    And I search for the patient "Daniel"
    Then at least one search result should be returned
