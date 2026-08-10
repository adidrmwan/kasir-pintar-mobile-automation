package com.kasirpintar.automation.steps;

import com.kasirpintar.automation.config.ConfigReader;
import com.kasirpintar.automation.driver.DriverManager;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;

public class Hooks {

    @BeforeAll
    public static void setUpAppOncePerRun() {
        if (!Boolean.parseBoolean(ConfigReader.get("app.autoInstall", "true"))) {
            return;
        }
        String serial = ConfigReader.get("device.udid");      // optional specific device
        String pkg = ConfigReader.get("app.package");
        runScript("scripts/install-app.sh", serial);          // fresh install
        runScript("scripts/prepare-device.sh", pkg, serial);  // grant perms, etc.
    }

    /** Runs a shell script (relative to the project root) with the given args. */
    private static void runScript(String path, String... args) {
        File script = new File(path);
        if (!script.exists()) {
            System.err.println("Skipping missing script: " + path);
            return;
        }
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", script.getPath());
            for (String a : args) {
                if (a != null && !a.isBlank()) {
                    pb.command().add(a);
                }
            }
            pb.inheritIO();
            int code = pb.start().waitFor();
            if (code != 0) {
                throw new IllegalStateException(path + " failed with exit code " + code);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed running " + path + ": " + e.getMessage(), e);
        }
    }

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
