package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class StatusPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы страницы статуса
    private final By orderNumberInput = By.xpath("//input[contains(@class, 'Input_Input__') and @placeholder='Введите номер заказа']");
    private final By goButton = By.xpath("//button[contains(text(), 'Go!')]");
    private final By notFoundBlock = By.cssSelector("div.Track_NotFound__6oaoY");
    private final By notFoundImage = By.cssSelector("div.Track_NotFound__6oaoY img");

    public StatusPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void checkOrderStatus(String orderNumber) {
        enterOrderNumber(orderNumber);
        clickGoButton();
    }

    public void enterOrderNumber(String number) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(orderNumberInput));
        input.clear();
        input.sendKeys(number);
    }

    public void clickGoButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(goButton));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", button);
    }

    public boolean isNotFoundMessageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(notFoundBlock));
            WebElement image = driver.findElement(notFoundImage);
            return image.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}