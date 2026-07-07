Feature: manage books

  Scenario: the user creates two books and retrieves both sorted by name
    Given the user creates the book with name "Les Misérables" and author "Victor Hugo"
    And the user creates the book with name "Hamlet" and author "William Shakespeare"
    When the user gets all books
    Then the list should contain the following books
      | name           | author              |
      | Hamlet         | William Shakespeare |
      | Les Misérables | Victor Hugo         |
