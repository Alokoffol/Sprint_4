package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class OrderPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы формы пользователя
    private final By nameInput = By.xpath("//input[@placeholder='* Имя']");
    private final By surnameInput = By.xpath("//input[@placeholder='* Фамилия']");
    private final By addressInput = By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']");
    private final By metroInput = By.xpath("//input[@placeholder='* Станция метро']");
    private final By phoneInput = By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']");
    private final By nextButton = By.xpath("//button[text()='Далее']");

    // Локаторы формы доставки
    private final By dateInput = By.xpath("//input[@placeholder='* Когда привезти самокат']");
    private final By rentalPeriodDropdown = By.xpath("//div[contains(text(),'Срок аренды')]");
    private final By blackCheckbox = By.id("black");
    private final By greyCheckbox = By.id("grey");
    private final By commentInput = By.xpath("//input[@placeholder='Комментарий для курьера']");
    private final By orderButton = By.xpath("//button[contains(@class, 'Button_Middle__1CSJM') and contains(text(), 'Заказать')]");

    // Локаторы модального окна подтверждения
    private final By confirmModal = By.xpath("//div[contains(@class, 'Order_Modal__YZ-d3')]");
    private final By yesButton = By.xpath("//div[@class='Order_Buttons__1xGrp']/button[not(contains(@class, 'Button_Inverted__3IF-i'))]");
    private final By successMessage = By.xpath("//div[contains(@class, 'Order_ModalHeader__3FDaJ')][contains(., 'Заказ оформлен')]");

    // Локаторы куки-баннера
    private final List<By> cookieBanners = List.of(
            By.id("rcc-confirm-button"),
            By.xpath("//button[contains(text(), 'да все привыкли')]"),
            By.cssSelector("button.App_CookieButton")
    );

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public boolean isOrderSuccessDisplayed() {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void fillUserForm(String name, String surname, String address, String metroStation, String phone) {
        setInputValue(nameInput, name);
        setInputValue(surnameInput, surname);
        setInputValue(addressInput, address);
        selectMetroStation(metroStation);
        setInputValue(phoneInput, phone);
    }

    public void fillDeliveryForm(String date, String rentalPeriod, String color, String comment) {
        setDate(date);
        selectRentalPeriod(rentalPeriod);
        selectColor(color);
        setComment(comment);
    }

    public void submitOrder() {
        clickWithRetry(orderButton);
    }

    public void confirmOrder() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmModal));
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(yesButton));
            clickWithJavaScript(button);
            verifyOrderSuccess();
        } catch (Exception e) {
            takeScreenshot("confirm_order_error");
            throw new RuntimeException("Order confirmation failed: " + e.getMessage());
        }
    }

    public void acceptCookies() {
        for (By locator : cookieBanners) {
            try {
                WebElement cookieBanner = new WebDriverWait(driver, Duration.ofSeconds(2))
                        .until(ExpectedConditions.elementToBeClickable(locator));
                clickWithJavaScript(cookieBanner);
                return;
            } catch (Exception ignored) {}
        }
        System.out.println("Cookie banner not found with any locator");
    }

    private void setInputValue(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(value);
    }

    private void setDate(String date) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(dateInput));
        element.clear();
        element.sendKeys(date, Keys.ENTER);
    }

    private void selectMetroStation(String stationName) {
        wait.until(ExpectedConditions.elementToBeClickable(metroInput)).click();
        WebElement station = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[text()='" + stationName + "']")));
        scrollAndClick(station);
    }

    private void selectRentalPeriod(String rentalPeriod) {
        scrollAndClick(wait.until(ExpectedConditions.elementToBeClickable(rentalPeriodDropdown)));
        WebElement period = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'Dropdown-option') and text()='" + rentalPeriod + "']")));
        scrollAndClick(period);
    }

    private void selectColor(String color) {
        By colorLocator = color.equals("black") ? blackCheckbox : greyCheckbox;
        scrollAndClick(wait.until(ExpectedConditions.elementToBeClickable(colorLocator)));
    }

    private void setComment(String comment) {
        setInputValue(commentInput, comment);
    }

    private void verifyOrderSuccess() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(45));
        WebElement element = longWait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
        if (!element.isDisplayed()) {
            throw new RuntimeException("Order success message not displayed");
        }
    }

    private void scrollAndClick(WebElement element) {
        ((JavascriptExecutor)driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
        try {
            element.click();
        } catch (WebDriverException e) {
            clickWithJavaScript(element);
        }
    }

    private void clickWithJavaScript(WebElement element) {
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", element);
    }

    private void clickWithRetry(By locator) {
        for (int i = 0; i < 3; i++) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                scrollAndClick(element);
                return;
            } catch (StaleElementReferenceException e) {
                if (i == 2) throw e;
            }
        }
    }

    private void takeScreenshot(String name) {
        try {
            ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            System.out.println("Failed to take screenshot: " + e.getMessage());
        }
    }
}