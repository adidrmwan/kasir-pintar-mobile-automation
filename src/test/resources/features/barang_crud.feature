@barang
Feature: CRUD of Default-type Products in Kasir Pintar Pro

  As a Kasir Pintar Pro user
  I want to manage Default-type product data
  So that my product catalog stays accurate

  Background:
    Given the user is logged in to Kasir Pintar Pro
    And the user opens the product list page

  @create @smoke
  Scenario: Create a new Default product
    When the user adds a Default product named "Kopi Susu" with price "18000" and stock "50"
    Then product "Kopi Susu" appears in the product list

  @create @data-driven
  Scenario Outline: Create several Default products from data
    When the user adds a Default product named "<name>" with price "<price>" and stock "<stock>"
    Then product "<name>" appears in the product list

    Examples:
      | name        | price | stock |
      | Nasi Goreng | 22000 | 30    |
      | Air Mineral | 5000  | 100   |
      | Ayam Bakar  | 27500 | 15    |

  @read
  Scenario: Read a Default product's detail
    Given a Default product named "Teh Manis" exists
    When the user opens the detail of product "Teh Manis"
    Then the product detail shows the name "Teh Manis"

  @update
  Scenario: Update a Default product's name
    Given a Default product named "Roti Bakar" exists
    When the user changes the name of product "Roti Bakar" to "Roti Bakar Coklat"
    Then product "Roti Bakar Coklat" appears in the product list

  @delete
  Scenario: Delete a Default product
    Given a Default product named "Es Jeruk" exists
    When the user deletes product "Es Jeruk"
    Then product "Es Jeruk" does not appear in the product list
