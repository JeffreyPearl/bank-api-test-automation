package com.bank.bankapp.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AccountApiTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8081;
    }

    // Helper: creates an account and returns its id
    private int createAccount(String name, int initialDeposit) {
        return given()
                .contentType(ContentType.JSON)
                .body("{\"holderName\":\"" + name + "\",\"initialDeposit\":" + initialDeposit + "}")
        .when()
                .post("/accounts")
        .then()
                .statusCode(201)
                .extract().path("id");
    }

    private double getBalance(int id) {
        return given().when().get("/accounts/" + id)
                .then().statusCode(200)
                .extract().jsonPath().getDouble("balance");
    }

    @Test
    void shouldCreateAccount() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"holderName\":\"Jeffrey\",\"initialDeposit\":5000}")
        .when()
                .post("/accounts")
        .then()
                .statusCode(201)
                .body("holderName", equalTo("Jeffrey"));
    }

    @Test
    void shouldDepositMoney() {
        int id = createAccount("DepositUser", 1000);

        given()
                .contentType(ContentType.JSON)
                .body("{\"amount\":500}")
        .when()
                .post("/accounts/" + id + "/deposit")
        .then()
                .statusCode(200);

        assertEquals(1500.0, getBalance(id), 0.001);
    }

    @Test
    void shouldRejectWithdrawMoreThanBalance() {
        int id = createAccount("PoorUser", 100);

        given()
                .contentType(ContentType.JSON)
                .body("{\"amount\":999999}")
        .when()
                .post("/accounts/" + id + "/withdraw")
        .then()
                .statusCode(409)
                .body("message", equalTo("Insufficient funds in account " + id));
    }

    @Test
    void shouldRejectNegativeDeposit() {
        int id = createAccount("NegUser", 100);

        given()
                .contentType(ContentType.JSON)
                .body("{\"amount\":-50}")
        .when()
                .post("/accounts/" + id + "/deposit")
        .then()
                .statusCode(400)
                .body("message", equalTo("Validation failed"));
    }

    @Test
    void shouldReturn404ForMissingAccount() {
        given()
        .when()
                .get("/accounts/999999")
        .then()
                .statusCode(404)
                .body("message", equalTo("Account not found: 999999"));
    }

    @Test
    void shouldTransferMoney() {
        int from = createAccount("Sender", 5000);
        int to = createAccount("Receiver", 1000);

        given()
                .contentType(ContentType.JSON)
                .body("{\"fromAccountId\":" + from + ",\"toAccountId\":" + to + ",\"amount\":1000}")
        .when()
                .post("/transfers")
        .then()
                .statusCode(200);

        assertEquals(4000.0, getBalance(from), 0.001);
        assertEquals(2000.0, getBalance(to), 0.001);
    }

    @Test
    void shouldRejectTransferToSameAccount() {
        int id = createAccount("SameUser", 1000);

        given()
                .contentType(ContentType.JSON)
                .body("{\"fromAccountId\":" + id + ",\"toAccountId\":" + id + ",\"amount\":100}")
        .when()
                .post("/transfers")
        .then()
                .statusCode(400)
                .body("message", equalTo("Cannot transfer to the same account"));
    }

    @Test
    void failedTransferShouldNotLoseMoney() {
        int from = createAccount("RollbackUser", 5000);

        given()
                .contentType(ContentType.JSON)
                .body("{\"fromAccountId\":" + from + ",\"toAccountId\":999999,\"amount\":100}")
        .when()
                .post("/transfers")
        .then()
                .statusCode(404);

        assertEquals(5000.0, getBalance(from), 0.001);
    }
}