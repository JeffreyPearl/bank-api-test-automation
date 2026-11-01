# Bank API Test Automation

A small banking application built with Spring Boot, with a multi-layer automated
test suite covering the API, the web UI and business-readable BDD scenarios.

## What it does
- Create accounts, deposit, withdraw and transfer money through a REST API
- Rejects invalid input (zero or negative amounts, blank names, missing fields)
- Returns clear JSON errors: 400 validation, 404 not found, 409 insufficient funds
- Transfers are transactional, so a failed transfer never loses money
- Simple web page (HTML + JavaScript) that uses the same API

## Tech stack
Java 21, Spring Boot 4, Spring Data JPA, H2, Maven

## Testing approach
| Layer | Tool | Tests |
|-------|------|-------|
| Manual / exploratory | Postman | 9 positive and negative cases |
| API automation | REST Assured + JUnit | 8 |
| UI automation | Selenium WebDriver | 4 |
| BDD | Cucumber (Gherkin) | 3 scenarios |

Highlights: rollback test for failed transfers, explicit waits instead of
sleeps, independent tests that create their own data, and an HTML Cucumber report.

## How to run
1. Start the app: `mvn spring-boot:run` (runs on port 8081)
2. In a second terminal: `mvn test`
3. Cucumber report: `target/cucumber.html`

## Project structure
- `controller`, `service`, `repository`, `model`, `exception`: application layers
- `src/test/java/.../api`: REST Assured tests
- `src/test/java/.../ui`: Selenium tests
- `src/test/java/.../bdd` and `src/test/resources/features`: Cucumber steps and scenarios