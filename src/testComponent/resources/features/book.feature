Feature: manage books

  Scenario: the user creates two books and retrieves both sorted by name
    Given the user creates the book with name "Les Misérables" and author "Victor Hugo"
    And the user creates the book with name "Hamlet" and author "William Shakespeare"
    When the user gets all books
    Then the list should contain the following books
      | name           | author              | reserved |
      | Hamlet         | William Shakespeare | false    |
      | Les Misérables | Victor Hugo         | false    |

  Scenario: the user reserves a book and it appears as reserved in the list
    Given the user creates the book with name "Dune" and author "Frank Herbert"
    When the user reserves the book with name "Dune"
    Then the book reservation succeeds
    When the user gets all books
    Then the list should contain the following books
      | name | author        | reserved |
      | Dune | Frank Herbert | true     |

  Scenario: a book already reserved cannot be reserved again
    Given the user creates the book with name "Dune" and author "Frank Herbert"
    And the user reserves the book with name "Dune"
    When the user reserves the book with name "Dune"
    Then the book reservation is rejected because the book is already reserved
