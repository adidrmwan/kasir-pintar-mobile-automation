package com.kasirpintar.automation.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class BarangListPage extends BasePage {

    private final By searchContainer = id("cari_barang");
    private final By searchField = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"" + resId("label") + "\").className(\"android.widget.EditText\")");
    private final By addFab      = id("fab");
    private final By backButton  = id("l_back");

    public BarangListPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isAt() {
        return isDisplayed(addFab);
    }

    public BarangFormPage tapAdd() {
        tap(addFab);
        return new BarangFormPage(driver);
    }

    private By rowByName(String name) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().resourceId(\"" + resId("tvNamaBarang") + "\")"
                        + ".textContains(\"" + name + "\")");
    }

    public void search(String name) {
        for (int i = 0; i < 4 && !isPresent(searchField); i++) {
            if (isPresent(searchContainer)) {
                try { tap(searchContainer); } catch (Exception ignored) { }
            }
            sleep(600);
        }
        type(searchField, name);
        hideKeyboardIfPresent();
    }

    public boolean hasItem(String name) {
        search(name);
        return isPresentWithin(rowByName(name), 5);
    }

    public BarangDetailPage openItem(String name) {
        search(name);
        tap(rowByName(name));
        return new BarangDetailPage(driver);
    }
}
