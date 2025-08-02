package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.MainPage;
import pages.StatusPage;
import java.time.Duration;

public class StatusTest {
    private WebDriver driver;
    private MainPage mainPage;
    private StatusPage statusPage;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        mainPage = new MainPage(driver);
        statusPage = new StatusPage(driver);
        mainPage.open();
        mainPage.acceptCookies();
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testNonExistentOrder() {
        mainPage.clickStatusButton();
        statusPage.checkOrderStatus("000000");
        Assert.assertTrue("Должно отображаться сообщение 'Не найдено'",
                statusPage.isNotFoundMessageDisplayed());
    }
}