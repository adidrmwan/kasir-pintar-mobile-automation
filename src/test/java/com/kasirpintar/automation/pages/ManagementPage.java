package com.kasirpintar.automation.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class ManagementPage extends BasePage {

    private final By productOrServiceMenu =
            AppiumBy.androidUIAutomator("new UiSelector().text(\"Product or Service\")");

    private final By deviceDialogSkip =
            AppiumBy.androidUIAutomator("new UiSelector().text(\"SKIP\")");

    public ManagementPage(AndroidDriver driver) {
        super(driver);
    }

    private final By productListMarker = id("cari_barang");

    public boolean isAt() {
        long deadline = System.currentTimeMillis() + 60000;
        while (System.currentTimeMillis() < deadline) {
            if (isPresent(deviceDialogSkip)) {
                try { gestureTap(deviceDialogSkip); } catch (Exception ignored) { }
                sleep(700);
            } else if (isPresent(productOrServiceMenu)) {
                return true;
            } else {
                sleep(500);   // login still in progress
            }
        }
        return isPresent(productOrServiceMenu);
    }

    public BarangListPage openProductOrService() {
        for (int attempt = 0; attempt < 6; attempt++) {
            if (isPresent(deviceDialogSkip)) {
                try { gestureTap(deviceDialogSkip); } catch (Exception ignored) { }
                sleep(700);
                continue;
            }
            try { gestureTap(productOrServiceMenu); } catch (Exception ignored) { }
            if (isPresentWithin(productListMarker, 4)) {
                return new BarangListPage(driver);
            }
        }
        return new BarangListPage(driver);
    }
}
