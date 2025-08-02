package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.MainPage;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class AccordionTest {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/";

    private WebDriver driver;
    private MainPage mainPage;

    @Parameterized.Parameter
    public int questionIndex;

    @Parameterized.Parameter(1)
    public String expectedAnswer;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        mainPage = new MainPage(driver);
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
    public void testAccordionItem() {
        mainPage.clickAccordionItem(questionIndex);
        String actualAnswer = mainPage.getAccordionAnswerText(questionIndex);
        Assert.assertEquals(
                String.format("Неверный ответ для вопроса #%d", questionIndex + 1),
                expectedAnswer,
                actualAnswer
        );
    }

    @Parameterized.Parameters(name = "Вопрос #{0}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {0, "Сутки — 400 рублей. Оплата курьеру — наличными или картой."},
                {1, "Пока что у нас так: один заказ — один самокат. Если хотите покататься с друзьями, можете просто сделать несколько заказов — один за другим."},
                {2, "Допустим, вы оформляете заказ на 8 мая. Мы привозим самокат 8 мая в течение дня. Отсчёт времени аренды начинается с момента, когда вы оплатите заказ курьеру. Если мы привезли самокат 8 мая в 20:30, суточная аренда закончится 9 мая в 20:30."},
                {3, "Только начиная с завтрашнего дня. Но скоро станем расторопнее."},
                {4, "Пока что нет! Но если что-то срочное — всегда можно позвонить в поддержку по красивому номеру 1010."},
                {5, "Самокат приезжает к вам с полной зарядкой. Этого хватает на восемь суток — даже если будете кататься без передышек и во сне. Зарядка не понадобится."},
                {6, "Да, пока самокат не привезли. Штрафа не будет, объяснительной записки тоже не попросим. Все же свои."},
                {7, "Да, обязательно. Всем самокатов! И Москве, и Московской области."}
        });
    }
}