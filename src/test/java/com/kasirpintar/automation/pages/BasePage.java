package com.kasirpintar.automation.pages;

import com.kasirpintar.automation.config.ConfigReader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;


public abstract class BasePage {

    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getInt("explicit.wait", 20)));
    }

    protected static By id(String idName) {
        return AppiumBy.id(resId(idName));
    }

    protected static String resId(String idName) {
        return ConfigReader.get("app.package") + ":id/" + idName;
    }

    protected WebElement waitVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void tap(By locator) {
        waitClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected String textOf(By locator) {
        return waitVisible(locator).getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return waitVisible(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected boolean isPresentWithin(By locator, int seconds) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(seconds));
        try {
            shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    protected boolean tapIfAppears(By locator, int timeoutSeconds) {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        try {
            shortWait.until(ExpectedConditions.elementToBeClickable(locator)).click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    protected void scrollToId(String idName) {
        try {
            driver.findElement(AppiumBy.androidUIAutomator(
                    "new UiScrollable(new UiSelector().scrollable(true))"
                            + ".scrollIntoView(new UiSelector().resourceId(\"" + resId(idName) + "\"))"));
        } catch (Exception ignored) {
            // not scrollable or element not present
        }
    }

    protected WebElement scrollToText(String text) {
        return driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))"
                        + ".scrollIntoView(new UiSelector().textContains(\"" + text + "\"))"));
    }

    protected void gestureTap(By locator) {
        org.openqa.selenium.WebElement el = driver.findElement(locator);
        org.openqa.selenium.Rectangle r = el.getRect();
        int cx = r.getX() + r.getWidth() / 2;
        int cy = r.getY() + r.getHeight() / 2;
        driver.executeScript("mobile: clickGesture", java.util.Map.of("x", cx, "y", cy));
    }

    protected boolean tapIfPresent(By locator) {
        if (isPresent(locator)) {
            try { tap(locator); return true; } catch (Exception ignored) { }
        }
        return false;
    }

    protected boolean gestureTapIfPresent(By locator) {
        if (isPresent(locator)) {
            try { gestureTap(locator); return true; } catch (Exception ignored) { }
        }
        return false;
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void hideKeyboardIfPresent() {
        try {
            driver.hideKeyboard();
        } catch (Exception ignored) {
            // keyboard was not shown
        }
    }
}
