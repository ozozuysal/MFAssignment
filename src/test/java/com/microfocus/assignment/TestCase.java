package com.microfocus.assignment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestCase {

    Properties prop = new Properties();
    private final String changeLanguageArrow = "//*[@id=\"languageList\"]/div[7]/span";

    private final String langEn = "//*[@id=\"languageList\"]/div[3]/a[2]";
    private final String searchButtonId = "btnSearch";
    private final String searchInputTextId = "edtSearch";
    private final String priceFilterId = "filterPrice";
    private final String stockInFilterButtonXpath = "//*[@id=\"parametrization\"]/div[1]/div[2]/div[1]/div";


    private final String languageXpath = "//*[@id=\"languageSwitch\"]";
    private String homeUrl;
    private int timeOutInSecond;

    WebDriver driver;
    JavascriptExecutor jsExecutor;


    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/driver/chromedriver.exe");

        InputStream input = null;

        try {
            input = getClass().getClassLoader().getResourceAsStream("config.properties");

            // loading property file to our test
            prop.load(input);
            homeUrl = prop.getProperty("HOME_URL");
            timeOutInSecond = Integer.parseInt(prop.getProperty("timeOutInSecond"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //defining browser
        driver = new ChromeDriver();
        jsExecutor = (JavascriptExecutor) driver;
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void mainPageShouldContainLanguageSelector() {


        driver.get(homeUrl);
        driver.manage().window().fullscreen();

        Assert.assertTrue("There should be a language chooser on main page", isClickable(languageXpath, 10));
    }

    @Test
    public void whenSelectEnglish_UrlShouldContainEN() {


        driver.get(homeUrl);
        driver.manage().window().fullscreen();
        waitUntilClickableByXpath(languageXpath).click();
        waitUntilClickableByXpath(langEn).click();

        Assert.assertTrue(validateCurrentUrl("https://www.alza.cz/EN/", 5, 1));
    }

    @Test
    public void whenSearchText_ResulUrlShouldContainsSearchText() {
        driver.get(homeUrl);
        driver.manage().window().fullscreen();

        WebElement searchButton = waitUntilClickableById(searchButtonId);
        setFieldById("car", searchInputTextId);
        searchButton.click();

        Assert.assertTrue("Url should be lıke exps=<searchKey>",
                validateCurrentUrl("https://www.alza.cz/search.htm?exps=car", 5, 1));
    }

    @Test
    public void whenSearchText_ResultPageShouldContainsPriceFilter() {
        driver.get(homeUrl);
        driver.manage().window().fullscreen();

        WebElement searchButton = waitUntilClickableById(searchButtonId);
        setFieldById("car", searchInputTextId);
        searchButton.click();

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id(priceFilterId))));


    }

    @Test
    public void whenClıckInStockCheckBox_UrlShouldContainsStockEqualsOk() {
        driver.get(homeUrl);
        driver.manage().window().fullscreen();

        WebElement searchButton = waitUntilClickableById(searchButtonId);
        setFieldById("car", searchInputTextId);
        searchButton.click();
        waitUntilClickableByXpath(stockInFilterButtonXpath).click();
        Assert.assertTrue("Url should be contains stock=ok", driver.getCurrentUrl().contains("stock=ok"));
    }

    private WebElement waitUntilClickableByXpath(String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSecond);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        return driver.findElement(By.xpath(xpath));
    }

    private boolean isClickable(String xpath, int timeOutInSecond) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSecond);

        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            return true;
        } catch (TimeoutException ex) {
            return false;
        }
    }

    private WebElement waitUntilClickableById(String id) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSecond);
        wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        return driver.findElement(By.id(id));
    }

    private WebElement setFieldById(String value, String id) {
        WebElement element = driver.findElement(By.id(id));
        element.sendKeys(value);
        return element;
    }

    private boolean validateCurrentUrl(String url, int timeOutInSecond, int period) {
        boolean result = false;
        for (int i = 0; i < timeOutInSecond; i += period) {
            if (driver.getCurrentUrl().equals(url)) {
                result = true;
                break;
            }
            try {
                Thread.sleep(period * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}


