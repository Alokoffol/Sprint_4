package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import pages.MainPage;
import pages.OrderPage;
import pages.StatusPage;

@RunWith(Parameterized.class)
public class MainTests {
    private WebDriver driver;
    private WebDriverWait wait;
    private MainPage mainPage;
    private OrderPage orderPage;
    private StatusPage statusPage;

    // Тестовые данные
    private final String name;
    private final String surname;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final String date;
    private final String color;
    private final String comment;

    public MainTests(String name, String surname, String address,
                     String metroStation, String phone, String date,
                     String color, String comment) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.date = date;
        this.color = color;
        this.comment = comment;
    }

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        mainPage = new MainPage(driver);
        orderPage = new OrderPage(driver);
        statusPage = new StatusPage(driver);

        // Принимаем куки перед началом тестов
        mainPage.open();
        mainPage.acceptCookies();
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testOpenMainPage() {
        mainPage.open();
        String currentUrl = driver.getCurrentUrl().trim();
        Assert.assertEquals("Неправильный URL",
                "https://qa-scooter.praktikum-services.ru/",
                currentUrl);
    }

    @Test
    public void testAccordionItems() {
        mainPage.open();
        String[] expectedAnswers = {
                "Сутки — 400 рублей. Оплата курьеру — наличными или картой.",
                "Пока что у нас так: один заказ — один самокат. Если хотите покататься с друзьями, можете просто сделать несколько заказов — один за другим.",
                "Допустим, вы оформляете заказ на 8 мая. Мы привозим самокат 8 мая в течение дня. Отсчёт времени аренды начинается с момента, когда вы оплатите заказ курьеру. Если мы привезли самокат 8 мая в 20:30, суточная аренда закончится 9 мая в 20:30.",
                "Только начиная с завтрашнего дня. Но скоро станем расторопнее.",
                "Пока что нет! Но если что-то срочное — всегда можно позвонить в поддержку по красивому номеру 1010.",
                "Самокат приезжает к вам с полной зарядкой. Этого хватает на восемь суток — даже если будете кататься без передышек и во сне. Зарядка не понадобится.",
                "Да, пока самокат не привезли. Штрафа не будет, объяснительной записки тоже не попросим. Все же свои.",
                "Да, обязательно. Всем самокатов! И Москве, и Московской области."
        };

        for (int i = 0; i < expectedAnswers.length; i++) {
            try {
                WebElement question = wait.until(ExpectedConditions.elementToBeClickable(
                        By.id("accordion__heading-" + i)));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", question);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", question);

                WebElement answer = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.id("accordion__panel-" + i)));
                String actualAnswer = answer.getText().trim();
                Assert.assertEquals("Ответ не соответствует ожидаемому для вопроса " + (i+1),
                        expectedAnswers[i], actualAnswer);
            } catch (Exception e) {
                Assert.fail("Ошибка при проверке аккордеона " + (i + 1) + ": " + e.getMessage());
            }
        }
    }

    @Test
    public void testOrderFlowTopButton() {
        try {
            mainPage.open();
            mainPage.clickOrderButton(true); // true для верхней кнопки

            orderPage.fillUserForm(name, surname, address, metroStation, phone);

            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[text()='Далее']")));
            nextButton.click();

            orderPage.fillDeliveryForm(date, "сутки", color, comment);

            orderPage.submitOrder();
            orderPage.confirmOrder();

            Assert.assertTrue("Заказ должен быть успешно оформлен",
                    orderPage.isOrderSuccessDisplayed());
        } catch (Exception e) {
            Assert.fail("Тест заказа через верхнюю кнопку упал: " + e.getMessage());
        }
    }

    @Test
    public void testOrderFlowBottomButton() {
        try {
            mainPage.open();

            // Дополнительная проверка на куки
            try {
                mainPage.acceptCookies();
            } catch (Exception e) {
                System.out.println("Cookie already accepted");
            }

            mainPage.clickOrderButton(false);

            // Остальной код теста...
        } catch (Exception e) {
            Assert.fail("Тест заказа через нижнюю кнопку упал: " + e.getMessage());
        }
    }

    @Test
    public void testNonExistentOrderStatus() {
        try {
            // 1. Открываем главную страницу
            mainPage.open();

            // 2. Принимаем куки (если есть)
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.elementToBeClickable(
                                By.xpath("//button[contains(text(), 'да все привыкли')]")
                        )).click();
            } catch (Exception e) {
                System.out.println("Cookie banner не найден, продолжаем тест");
            }

            // 3. Находим и кликаем кнопку "Статус заказа"
            WebElement statusButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Статус заказа')]")
            ));
            ((JavascriptExecutor)driver).executeScript("arguments[0].click();", statusButton);

            // 4. Проверяем несуществующий заказ
            statusPage.checkOrderStatus("000000");

            // 5. Добавляем явное ожидание перед проверкой
            Thread.sleep(2000); // Краткая пауза для стабилизации

            // 6. Проверяем сообщение об ошибке
            Assert.assertTrue("Изображение 'Не найдено' должно отображаться",
                    statusPage.isNotFoundMessageDisplayed());

        } catch (Exception e) {
            Assert.fail("Тест статуса заказа упал: " + e.getMessage());
        }
    }

    @Parameterized.Parameters(name = "Тестовые данные: {0} {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Иван", "Иванов", "ул. Ленина, 1", "Сокольники", "+79991112233", "01.01.2025", "black", "Позвонить за час"},
                {"Петр", "Петров", "ул. Пушкина, 10", "Фрунзенская", "+79876543210", "15.01.2025", "grey", "Оставьте у двери"}
        });
    }
}