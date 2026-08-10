package com.kasirpintar.automation.pages;

import com.kasirpintar.automation.model.Barang;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class BarangFormPage extends BasePage {

    private final By nameField        = id("editNamaBarang");
    private final By itemTypeValue    = id("text_itemlist_spinner");
    private final By codeField        = id("editKodeBarang");
    private final By basicPriceField  = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"" + resId("et_input") + "\").instance(0)");
    private final By sellingPriceField = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"" + resId("et_input") + "\").instance(1)");
    private final By stockField       = id("editJumlah");
    private final By saveButton       = id("btnTambahDataBarang");

    public BarangFormPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isAt() {
        return isDisplayed(nameField);
    }

    public String selectedItemType() {
        return textOf(itemTypeValue);
    }

    private final By savingSpinner =
            AppiumBy.androidUIAutomator("new UiSelector().textContains(\"Please wait\")");

    public BarangListPage createDefaultBarang(Barang barang) {
        type(nameField, barang.name());
        type(codeField, barang.code());
        if (barang.stock() != null && !barang.stock().isBlank()) {
            type(stockField, barang.stock());
        }
        hideKeyboardIfPresent();
        scrollToId("et_input");
        type(basicPriceField, barang.basicPrice());
        type(sellingPriceField, barang.sellingPrice());
        hideKeyboardIfPresent();
        tap(saveButton);
        waitUntilSaved();
        return new BarangListPage(driver);
    }

    public BarangListPage updateName(String newName) {
        type(nameField, newName);
        hideKeyboardIfPresent();
        tap(saveButton);
        waitUntilSaved();
        ensureOnList();
        return new BarangListPage(driver);
    }

    private void waitUntilSaved() {
        long deadline = System.currentTimeMillis() + 30000;
        while (System.currentTimeMillis() < deadline && isPresent(savingSpinner)) {
            sleep(400);
        }
    }

    private void ensureOnList() {
        By listFab = id("fab");
        long deadline = System.currentTimeMillis() + 12000;
        while (System.currentTimeMillis() < deadline && !isPresent(listFab)) {
            driver.navigate().back();
            sleep(800);
        }
    }
}
