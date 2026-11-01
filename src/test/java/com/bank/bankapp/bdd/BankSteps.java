package com.bank.bankapp.bdd;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class BankSteps {

    private int firstId;
    private int secondId;
    private Response lastResponse;

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    private int create(String name, int balance) {
        return given().contentType(ContentType.JSON)
                .body("{\"holderName\":\"" + name + "\",\"initialDeposit\":" + balance + "}")
                .post("/accounts").then().statusCode(201).extract().path("id");
    }

    private double balanceOf(int id) {
        return given().get("/accounts/" + id).then().statusCode(200)
                .extract().jsonPath().getDouble("balance");
    }

    @Given("an account exists for {string} with balance {int}")
    public void anAccountExists(String name, int balance) {
        firstId = create(name, balance);
    }

    @Given("another account exists for {string} with balance {int}")
    public void anotherAccountExists(String name, int balance) {
        secondId = create(name, balance);
    }

    @When("the customer deposits {int}")
    public void deposit(int amount) {
        lastResponse = given().contentType(ContentType.JSON)
                .body("{\"amount\":" + amount + "}")
                .post("/accounts/" + firstId + "/deposit");
    }

    @When("the customer withdraws {int}")
    public void withdraw(int amount) {
        lastResponse = given().contentType(ContentType.JSON)
                .body("{\"amount\":" + amount + "}")
                .post("/accounts/" + firstId + "/withdraw");
    }

    @When("the customer transfers {int} from the first account to the second")
    public void transfer(int amount) {
        lastResponse = given().contentType(ContentType.JSON)
                .body("{\"fromAccountId\":" + firstId + ",\"toAccountId\":" + secondId
                        + ",\"amount\":" + amount + "}")
                .post("/transfers");
    }

    @Then("the balance should be {int}")
    public void balanceShouldBe(int expected) {
        assertEquals(expected, balanceOf(firstId), 0.001);
    }

    @Then("the first account balance should be {int}")
    public void firstBalance(int expected) {
        assertEquals(expected, balanceOf(firstId), 0.001);
    }

    @Then("the second account balance should be {int}")
    public void secondBalance(int expected) {
        assertEquals(expected, balanceOf(secondId), 0.001);
    }

    @Then("the request should be rejected with status {int}")
    public void rejectedWithStatus(int status) {
        assertEquals(status, lastResponse.statusCode());
    }
}