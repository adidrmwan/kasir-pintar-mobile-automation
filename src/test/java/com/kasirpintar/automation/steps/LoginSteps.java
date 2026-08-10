package com.kasirpintar.automation.steps;

import com.kasirpintar.automation.config.ConfigReader;
import com.kasirpintar.automation.context.ScenarioContext;
import com.kasirpintar.automation.driver.DriverManager;
import com.kasirpintar.automation.pages.LoginPage;
import com.kasirpintar.automation.pages.SalesHomePage;
import com.kasirpintar.automation.pages.WelcomePage;
import io.cucumber.java.en.Given;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step definitions for authentication (login). Shares state with the other step
 * classes through the injected {@link ScenarioContext}.
 */
public class LoginSteps {

    private final ScenarioContext ctx;

    public LoginSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @Given("the user is logged in to Kasir Pintar Pro")
    public void userIsLoggedIn() {
        var driver = DriverManager.getDriver();
        String pkg = ConfigReader.get("app.package");
        try {
            driver.executeScript("mobile: clearApp", java.util.Map.of("appId", pkg));
            driver.executeScript("mobile: activateApp", java.util.Map.of("appId", pkg));
        } catch (Exception ignored) {
        }

        WelcomePage welcome = new WelcomePage(DriverManager.getDriver());
        LoginPage login = new LoginPage(DriverManager.getDriver());

        if (welcome.isAt()) {
            ctx.salesHome = welcome.tapLogin().login(
                    ConfigReader.get("login.email"),
                    ConfigReader.get("login.password"));
        } else if (login.isAt()) {
            ctx.salesHome = login.login(
                    ConfigReader.get("login.email"),
                    ConfigReader.get("login.password"));
        } else {
            ctx.salesHome = new SalesHomePage(DriverManager.getDriver());
        }
        assertTrue(ctx.salesHome.isAt(), "Failed to reach the Sales home after login");
    }
}
