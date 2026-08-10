package com.kasirpintar.automation.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class BarangDetailPage extends BasePage {

    private final By nameLabel  = id("tvNama");

    private final By editButton   = id("fab_edit");
    private final By deleteButton =
            AppiumBy.androidUIAutomator("new UiSelector().text(\"Delete\")");
    private final By backButton   = id("l_back");

    private final By confirmYes = id("ya");
    private final By confirmNo  = id("tidak");
    private final By savingSpinner =
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Please wait\")");

    public BarangDetailPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isAt() {
        return isDisplayed(nameLabel);
    }

    public String readName()  { return textOf(nameLabel).trim(); }

    public String readValueByLabel(String label) {
        By value = AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + label + "\").fromParent("
                        + "new UiSelector().resourceId(\"" + resId("tvValue") + "\"))");
        return textOf(value).trim();
    }

    public BarangFormPage tapEdit() {
        tap(editButton);
        return new BarangFormPage(driver);
    }

    public BarangListPage delete() {
        tap(deleteButton);
        waitVisible(confirmYes);
        tap(confirmYes);
        long deadline = System.currentTimeMillis() + 30000;
        while (System.currentTimeMillis() < deadline && isPresent(savingSpinner)) {
            sleep(400);
        }

        By listFab = id("fab");
        long navDeadline = System.currentTimeMillis() + 12000;
        while (System.currentTimeMillis() < navDeadline && !isPresent(listFab)) {
            driver.navigate().back();
            sleep(800);
        }
        return new BarangListPage(driver);
    }

    public BarangListPage goBackToList() {
        tap(backButton);
        return new BarangListPage(driver);
    }
}
