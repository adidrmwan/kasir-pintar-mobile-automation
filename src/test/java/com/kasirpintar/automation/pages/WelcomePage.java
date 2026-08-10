package com.kasirpintar.automation.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class WelcomePage extends BasePage {

    private final By loginButton    = id("tv_login");
    private final By registerButton = id("btn_register");

    public WelcomePage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isAt() {
        return isDisplayed(loginButton) && isDisplayed(registerButton);
    }

    public LoginPage tapLogin() {
        tap(loginButton);
        return new LoginPage(driver);
    }
}
