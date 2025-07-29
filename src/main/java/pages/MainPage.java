package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class MainPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // Логотип Самоката
    private final By scooterLogo = By.xpath("//a[@class='Header_LogoScooter__3lsAR']");

    // Логотип Яндекса
    private final By yandexLogo = By.xpath("//a[@class='Header_LogoYandex__3TSOI']");

    // Кнопка "Заказать" (верхняя)
    private final By topOrderButton = By.xpath("(//button[text()='Заказать'])[1]");

    // Кнопка "Заказать" (нижняя)
    private final By bottomOrderButton = By.xpath("(//button[text()='Заказать'])[2]");

    // Кнопка "Статус заказа"
    private final By orderStatusButton = By.xpath("//button[contains(@class, 'Header_Link') and text()='Статус заказа']");

    // Поле ввода номера заказа (в хедере)
    private final By orderNumberInput = By.xpath("//input[@placeholder='Введите номер заказа']");

    // Кнопка "Go!" (для проверки статуса)
    private final By goButton = By.xpath("//button[text()='Go!']");

    // Блок с вопросами и ответами (аккордеон)
    private final By accordionItem = By.className("accordion__item");

    // Вопросы в аккордеоне (по индексу)
    private By getAccordionQuestion(int index) {
        return By.id("accordion__heading-" + index);
    }

    // Ответы в аккордеоне (по индексу)
    private By getAccordionAnswer(int index) {
        return By.id("accordion__panel-" + index);
    }

    // Кнопка "Принять куки"
    private final By acceptCookiesButton = By.id("rcc-confirm-button");

    // Основные методы страницы
    public void open() {
        driver.get("https://qa-scooter.praktikum-services.ru/");
    }

    public void clickOrderButton(boolean isTopButton) {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
                isTopButton ? topOrderButton : bottomOrderButton));
        button.click();
    }

    public void clickStatusButton() {
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(orderStatusButton));
        button.click();
    }

    public void acceptCookies() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement cookieButton = shortWait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'да все привыкли') or @id='rcc-confirm-button']")
            ));
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", cookieButton);
        } catch (Exception e) {
            System.out.println("Cookie banner not found or not clickable: " + e.getMessage());
        }
    }

    public void clickAccordionItem(int index) {
        WebElement item = wait.until(ExpectedConditions.elementToBeClickable(getAccordionQuestion(index)));
        item.click();
    }

    public String getAccordionAnswerText(int index) {
        WebElement answer = wait.until(ExpectedConditions.visibilityOfElementLocated(getAccordionAnswer(index)));
        return answer.getText();
    }
}