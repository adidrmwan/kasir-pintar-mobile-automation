package com.kasirpintar.automation.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    private final By emailField    = id("edt_email");
    private final By passwordField = id("edt_password");
    private final By loginButton   = id("btn_login");

    private final By accountInUseYes = id("ya");
    private final By deviceTypeSkip  = AppiumBy.androidUIAutomator("new UiSelector().text(\"SKIP\")");
    private final By notifDeny       = AppiumBy.androidUIAutomator("new UiSelector().textMatches(\"(?i)don.?t allow\")");
    private final By reportPeriod    = AppiumBy.androidUIAutomator(
            "new UiSelector().textMatches(\"This Month.*|This 3 months|This Year.*|This year|Last year until now\")");
    private final By skipGuide       = id("btn_skip_guide");
    private final By salesReady      = id("bars");               // Sales home hamburger

    public LoginPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isAt() {
        return isDisplayed(emailField);
    }

    public SalesHomePage login(String email, String password) {
        type(emailField, email);
        type(passwordField, password);
        hideKeyboardIfPresent();
        tap(loginButton);
        driveThroughOnboarding();
        return new SalesHomePage(driver);
    }

    private void driveThroughOnboarding() {
        long deadline = System.currentTimeMillis() + 180000;
        while (System.currentTimeMillis() < deadline) {
            if (isPresent(salesReady)) {
                return;
            }
            if (gestureTapIfPresent(deviceTypeSkip)) { sleep(600); continue; }
            if (tapIfPresent(accountInUseYes))       { sleep(600); continue; }
            if (tapIfPresent(notifDeny))             { sleep(600); continue; }
            if (tapIfPresent(reportPeriod))          { sleep(1500); continue; }  // sync takes time
            if (tapIfPresent(skipGuide))             { sleep(600); continue; }
            sleep(600);
        }
    }
}
