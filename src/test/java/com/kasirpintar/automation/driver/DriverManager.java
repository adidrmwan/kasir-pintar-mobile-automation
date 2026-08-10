package com.kasirpintar.automation.driver;

import com.kasirpintar.automation.config.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.MutableCapabilities;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    /** Chooses the driver by target: "local" (default) or "kobiton" (cloud). */
    private static AndroidDriver createDriver() {
        String target = ConfigReader.get("driver.target", "local");
        if ("kobiton".equalsIgnoreCase(target)) {
            return createKobitonDriver();
        }
        return createLocalDriver();
    }

    // ------------------------------------------------------------------
    //  Kobiton cloud (real devices) — activate with: mvn test -Denv=kobiton
    //  See src/test/resources/config/config-kobiton.properties for the values
    //  you need to fill in (username, apiKey, app id, device).
    // ------------------------------------------------------------------
    private static AndroidDriver createKobitonDriver() {
        String username = required("kobiton.username");
        String apiKey = required("kobiton.apiKey");
        String base = ConfigReader.get("kobiton.server.url", "https://api.kobiton.com/wd/hub");

        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("sessionName", ConfigReader.get("kobiton.sessionName", "Kasir Pintar CRUD"));
        caps.setCapability("sessionDescription", "Appium + Cucumber BDD");
        caps.setCapability("deviceGroup", ConfigReader.get("kobiton.deviceGroup", "KOBITON"));
        caps.setCapability("platformName", "Android");
        caps.setCapability("deviceName", required("kobiton.deviceName"));
        caps.setCapability("platformVersion", required("kobiton.platformVersion"));
        caps.setCapability("captureScreenshots", true);
        // App uploaded to Kobiton (Apps -> upload). Value like: kobiton-store:<appVersionId>
        caps.setCapability("app", required("kobiton.app"));
        caps.setCapability("appium:automationName", ConfigReader.get("automation.name", "UiAutomator2"));
        caps.setCapability("appium:autoGrantPermissions", true);
        caps.setCapability("appium:newCommandTimeout", ConfigReader.getInt("new.command.timeout", 120));

        try {
            // Basic-auth is embedded in the hub URL: https://user:key@api.kobiton.com/wd/hub
            String auth = URLEncoder.encode(username, StandardCharsets.UTF_8)
                    + ":" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
            URL hub = new URL(base.replaceFirst("://", "://" + auth + "@"));
            AndroidDriver driver = new AndroidDriver(hub, caps);
            driver.manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("implicit.wait", 0)));
            return driver;
        } catch (Exception e) {
            throw new RuntimeException("Could not start Kobiton session. Check kobiton.* config "
                    + "(username/apiKey/app/device).", e);
        }
    }

    private static String required(String key) {
        String v = ConfigReader.get(key);
        if (v == null || v.isBlank() || v.startsWith("CHANGE_ME")) {
            throw new IllegalStateException("Missing required config '" + key
                    + "'. Fill it in config-kobiton.properties or config.local.properties.");
        }
        return v;
    }

    // ------------------------------------------------------------------
    //  Local device / emulator (default)
    // ------------------------------------------------------------------
    private static AndroidDriver createLocalDriver() {
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
