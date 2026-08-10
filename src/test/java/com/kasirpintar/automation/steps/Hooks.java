package com.kasirpintar.automation.steps;

import com.kasirpintar.automation.config.ConfigReader;
import com.kasirpintar.automation.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class Hooks {

    @After
    public void tearDown(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                byte[] png = DriverManager.getDriver().getScreenshotAs(OutputType.BYTES);
                scenario.attach(png, "image/png", "failure-" + scenario.getName());
                Allure.addAttachment("failure-" + scenario.getName(),
                        new ByteArrayInputStream(png));
            }
        } catch (Exception e) {
            System.err.println("Could not capture failure screenshot: " + e.getMessage());
        } finally {
            closeAppUnderTest();
            DriverManager.quitDriver();
        }
    }

    private void closeAppUnderTest() {
        try {
            AndroidDriver driver = DriverManager.getDriver();
            String pkg = ConfigReader.get("app.package");
            driver.executeScript("mobile: terminateApp", Map.of("appId", pkg));
        } catch (Exception ignored) {
        }
    }
}
