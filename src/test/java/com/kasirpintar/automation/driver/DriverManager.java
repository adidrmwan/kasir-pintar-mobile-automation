package com.kasirpintar.automation.driver;

import com.kasirpintar.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.File;
import java.net.URL;
import java.time.Duration;

public final class DriverManager {

    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static AndroidDriver getDriver() {
        if (DRIVER.get() == null) {
            DRIVER.set(createDriver());
        }
        return DRIVER.get();
    }

    private static AndroidDriver createDriver() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(ConfigReader.get("platform.name", "Android"))
                .setAutomationName(ConfigReader.get("automation.name", "UiAutomator2"))
                .setDeviceName(ConfigReader.get("device.name", "Android Emulator"))
                .setNewCommandTimeout(Duration.ofSeconds(ConfigReader.getInt("new.command.timeout", 120)))
                .setNoReset(ConfigReader.getBoolean("no.reset"))
                .setFullReset(ConfigReader.getBoolean("full.reset"))
                .setAutoGrantPermissions(ConfigReader.getBoolean("auto.grant.permissions"));

        String platformVersion = ConfigReader.get("platform.version");
        if (platformVersion != null && !platformVersion.isBlank()) {
            options.setPlatformVersion(platformVersion);
        }

        String appPath = ConfigReader.get("app.path");
        File apk = (appPath == null || appPath.isBlank()) ? null : new File(appPath);
        if (apk != null && apk.exists()) {
            options.setApp(apk.getAbsolutePath());
        } else {
            options.setAppPackage(ConfigReader.get("app.package"));
            String appActivity = ConfigReader.get("app.activity");
            if (appActivity != null && !appActivity.isBlank()) {
                options.setAppActivity(appActivity);
            }
        }

        try {
            URL serverUrl = new URL(ConfigReader.get("appium.server.url", "http://127.0.0.1:4723"));
            AndroidDriver driver = new AndroidDriver(serverUrl, options);
            driver.manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicit.wait", 5)));
            return driver;
        } catch (Exception e) {
            throw new RuntimeException("Could not start AndroidDriver. Is the Appium server running "
                    + "at " + ConfigReader.get("appium.server.url") + " ?", e);
        }
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
