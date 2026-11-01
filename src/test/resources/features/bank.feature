Feature: Bank account operations

  Scenario: Deposit money into an account
    Given an account exists for "Priya" with balance 1000
    When the customer deposits 500
    Then the balance should be 1500

  Scenario: Withdraw more than the balance
    Given an account exists for "Ravi" with balance 100
    When the customer withdraws 5000
    Then the request should be rejected with status 409

  Scenario: Transfer between accounts
    Given an account exists for "Sender" with balance 5000
    And another account exists for "Receiver" with balance 1000
    When the customer transfers 1000 from the first account to the second
    Then the first account balance should be 4000
    And the second account balance should be 2000