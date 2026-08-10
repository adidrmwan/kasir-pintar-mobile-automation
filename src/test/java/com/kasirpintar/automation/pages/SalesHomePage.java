package com.kasirpintar.automation.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class SalesHomePage extends BasePage {

    private final By hamburger  = id("bars");
    private final By skipGuide  = id("btn_skip_guide");
    private final By managementDrawerItem =
            AppiumBy.androidUIAutomator("new UiSelector().text(\"Management\")");

    public SalesHomePage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isAt() {
        long deadline = System.currentTimeMillis() + 30000;
        while (System.currentTimeMillis() < deadline) {
            dismissTutorialIfPresent();
            if (isPresent(hamburger)) {
                return true;
            }
            sleep(500);
        }
        return isPresent(hamburger);
    }

    public void dismissTutorialIfPresent() {
        for (int i = 0; i < 3 && isPresent(skipGuide); i++) {
            try { gestureTap(skipGuide); } catch (Exception ignored) { }
            sleep(600);
        }
    }

    public ManagementPage openManagement() {
        dismissTutorialIfPresent();
        tap(hamburger);
        wait.until(d -> isPresent(managementDrawerItem));
        tap(managementDrawerItem);
        return new ManagementPage(driver);
    }
}
