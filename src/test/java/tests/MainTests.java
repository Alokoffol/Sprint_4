package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.MainPage;
import pages.OrderPage;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MainTests {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";

    private WebDriver driver;
    private WebDriverWait wait;
    private MainPage mainPage;
    private OrderPage orderPage;

    // Тестовые данные
    private final String name;
    private final String surname;
    private final String address;
    private final String metroStation;
    private final String phone;
    private final String date;
    private final String color;
    private final String comment;
    private final boolean useTopButton;

    public MainTests(String name, String surname, String address,
                     String metroStation, String phone, String date,
                     String color, String comment, boolean useTopButton) {
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.date = date;
        this.color = color;
        this.comment = comment;
        this.useTopButton = useTopButton;
    }

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        mainPage = new MainPage(driver);
        orderPage = new OrderPage(driver);
        mainPage.open();
        mainPage.acceptCookies();
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testSuccessfulOrder() {
        mainPage.clickOrderButton(useTopButton);
        orderPage.fillUserForm(name, surname, address, metroStation, phone);
        orderPage.clickNextButton(); // Теперь метод доступен
        orderPage.fillDeliveryForm(date, "сутки", color, comment);
        orderPage.submitOrder();
        orderPage.confirmOrder();

        Assert.assertTrue("Заказ должен быть успешно оформлен",
                orderPage.isOrderSuccessDisplayed());
    }

    @Parameterized.Parameters(name = "Данные: {0} {1}, кнопка: {8}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Иван", "Иванов", "ул. Ленина, 1", "Сокольники", "+79991112233",
                        "01.01.2025", "black", "Позвонить за час", true},
                {"Петр", "Петров", "ул. Пушкина, 10", "Фрунзенская", "+79876543210",
                        "15.01.2025", "grey", "Оставьте у двери", false}
        });
    }
}