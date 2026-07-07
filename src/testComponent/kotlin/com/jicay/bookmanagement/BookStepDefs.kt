package com.jicay.bookmanagement

import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.path.json.JsonPath
import io.restassured.response.ValidatableResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.boot.test.web.server.LocalServerPort

class BookStepDefs {

    @LocalServerPort
    private var port: Int? = 0

    @Autowired
    private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate

    private lateinit var lastBooksResult: ValidatableResponse
    private lateinit var lastReserveResult: ValidatableResponse

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        namedParameterJdbcTemplate.update("DELETE FROM book", MapSqlParameterSource())
    }

    @Given("the user creates the book with name {string} and author {string}")
    fun createBook(name: String, author: String) {
        given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {
                      "name": "$name",
                      "author": "$author"
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
            .statusCode(201)
    }

    @When("the user reserves the book with name {string}")
    fun reserveBook(name: String) {
        lastReserveResult = given()
            .`when`()
            .post("/books/{name}/reserve", name)
            .then()
    }

    @Then("the book reservation succeeds")
    fun reservationSucceeds() {
        lastReserveResult.statusCode(200)
    }

    @Then("the book reservation is rejected because the book is already reserved")
    fun reservationRejected() {
        lastReserveResult.statusCode(409)
    }

    @When("the user gets all books")
    fun getAllBooks() {
        lastBooksResult = given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
    }

    @Then("the list should contain the following books")
    fun shouldHaveListOfBooks(payload: List<Map<String, Any>>) {
        val expectedResponse = payload.joinToString(separator = ",", prefix = "[", postfix = "]") { line ->
            line.entries.joinToString(separator = ",", prefix = "{", postfix = "}") {
                val value = it.value.toString()
                if (value == "true" || value == "false") {
                    """"${it.key}": $value"""
                } else {
                    """"${it.key}": "$value""""
                }
            }
        }
        lastBooksResult.extract().body().jsonPath().prettify() shouldBe JsonPath(expectedResponse).prettify()
    }
}
