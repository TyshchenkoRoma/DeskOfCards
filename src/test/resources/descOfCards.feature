@SmokeTest
Feature: Cucumber run test

  I want to play cards

  Scenario Outline: Check if card count in deck is correct after drawing X cards from it

    Given I have new game with "6" decks
    When I drow "<times>" cards
    Then card count in deck is changed to "<remaining>"
    Examples:
      | times | remaining |
      | 1     | 310       |
      | 2     | 308       |
      | 10    | 292       |

  Scenario:  Check if user can get only Aceses from desc with Aceses only

    Given User has deck with Aceses only
    Then User can get Aceses only

  Scenario:  Check that cards not repeat in the desk after drawning

    Given User has new game with one deck
    When User gets five cards
    Then card amound was changed
    Then cards not repeat in the desk after drawning