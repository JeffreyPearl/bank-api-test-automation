package com.bank.bankapp.ui.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BankPage {

    private static final String URL = "http://localhost:8081";

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators live here and nowhere else
    private final By holderName = By.id("holderName");
    private final By initialDeposit = By.id("initialDeposit");
    private final By createBtn = By.id("createBtn");
    private final By depositId = By.id("depositId");
    private final By depositAmount = By.id("depositAmount");
    private final By depositBtn = By.id("depositBtn");
    private final By balanceId = By.id("balanceId");
    private final By balanceBtn = By.id("balanceBtn");
    private final By message = By.id("message");

    public BankPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public BankPage open() {
        driver.get(URL);
        return this;
    }

    public BankPage createAccount(String name, String deposit) {
        driver.findElement(holderName).sendKeys(name);
        driver.findElement(initialDeposit).sendKeys(deposit);
        driver.findElement(createBtn).click();
        return this;
    }

    public BankPage deposit(String id, String amount) {
        driver.findElement(depositId).sendKeys(id);
        driver.findElement(depositAmount).sendKeys(amount);
        driver.findElement(depositBtn).click();
        return this;
    }

    public BankPage checkBalance(String id) {
        driver.findElement(balanceId).sendKeys(id);
        driver.findElement(balanceBtn).click();
        return this;
    }

    public BankPage waitForMessage(String expectedText) {
        wait.until(ExpectedConditions.textToBePresentInElementLocated(message, expectedText));
        return this;
    }

    public String getMessage() {
        return driver.findElement(message).getText();
    }

    public boolean isErrorShown() {
        return driver.findElement(message).getAttribute("class").contains("error");
    }

    // Creates an account and returns its new ID, read from the success message
    public String createAccountAndGetId(String name, String deposit) {
        createAccount(name, deposit);
        waitForMessage("Account created");
        String text = getMessage();
        return text.substring(text.indexOf("ID:") + 3).trim();
    }
}