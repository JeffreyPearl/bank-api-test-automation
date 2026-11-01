package com.bank.bankapp.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BankPageTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
    	org.openqa.selenium.chrome.ChromeOptions options = new org.openqa.selenium.chrome.ChromeOptions();
    	if (System.getenv("CI") != null) {
    	    options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
    	}
    	driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:8081");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    // Creates an account through the page and returns the new ID
    private String createAccountViaUi(String name, String deposit) {
        driver.findElement(By.id("holderName")).sendKeys(name);
        driver.findElement(By.id("initialDeposit")).sendKeys(deposit);
        driver.findElement(By.id("createBtn")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("message"), "Account created"));
        String text = driver.findElement(By.id("message")).getText();
        return text.substring(text.indexOf("ID:") + 3).trim();
    }

    @Test
    void shouldCreateAccountFromUi() {
        String id = createAccountViaUi("Selenium User", "3000");
        assertTrue(Integer.parseInt(id) > 0);
    }

    @Test
    void shouldDepositFromUi() {
        String id = createAccountViaUi("Deposit User", "1000");

        driver.findElement(By.id("depositId")).sendKeys(id);
        driver.findElement(By.id("depositAmount")).sendKeys("500");
        driver.findElement(By.id("depositBtn")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("message"), "Deposit done"));
        String text = driver.findElement(By.id("message")).getText();
        assertTrue(text.contains("1500"), "Unexpected message: " + text);
    }

    @Test
    void shouldShowErrorForMissingAccount() {
        driver.findElement(By.id("balanceId")).sendKeys("999999");
        driver.findElement(By.id("balanceBtn")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("message"), "Account not found"));
        assertTrue(driver.findElement(By.id("message")).getAttribute("class").contains("error"));
    }
    @Test
    void shouldShowErrorWhenDepositIdIsEmpty() {
        driver.findElement(By.id("depositBtn")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("message"), "Account ID is required"));
        assertTrue(driver.findElement(By.id("message")).getAttribute("class").contains("error"));
    }
}