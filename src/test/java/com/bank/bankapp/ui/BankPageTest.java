package com.bank.bankapp.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.bank.bankapp.ui.pages.BankPage;

public class BankPageTest {

    private WebDriver driver;
    private BankPage page;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        if (System.getenv("CI") != null) {
            options.addArguments("--headless=new", "--no-sandbox",
                    "--disable-dev-shm-usage", "--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        page = new BankPage(driver).open();
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    void shouldCreateAccountFromUi() {
        String id = page.createAccountAndGetId("Selenium User", "3000");
        assertTrue(Integer.parseInt(id) > 0);
    }

    @Test
    void shouldDepositFromUi() {
        String id = page.createAccountAndGetId("Deposit User", "1000");

        page.deposit(id, "500").waitForMessage("Deposit done");

        assertTrue(page.getMessage().contains("1500"),
                "Unexpected message: " + page.getMessage());
    }

    @Test
    void shouldShowErrorForMissingAccount() {
        page.checkBalance("999999").waitForMessage("Account not found");

        assertTrue(page.isErrorShown());
    }

    @Test
    void shouldShowErrorWhenDepositIdIsEmpty() {
        page.deposit("", "").waitForMessage("Account ID is required");

        assertTrue(page.isErrorShown());
    }
}