import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ihor on 9/21/2016. All rights reserved!
 */
public class Main {
    By searchFieldLocator = By.xpath("//input[@class='b_searchbox']");
    By mobileSearchFieldLocator = By.xpath("//input[@id='sb_form_q']");
    By bingLogoLocator = By.xpath("//div[@id='sbox']/div[1]");
    By mobileBingLogoLocator = By.xpath("//img[@id='bLogoExp']");
    By searchResultsTitleLocator = By.cssSelector("ol li h2>a");
    By searchResultsLinkLocator = By.cssSelector("ol li div>cite");
    By cachedPageLinkLocator = By.xpath("//div[@class='b_vPanel']//strong/a");
    By searchResultsQuantityLocator = By.xpath("//div[@id='b_tween']//span");
    By similarRequestsBlockLocator = By.xpath("//li/ul[@class='b_vList']");
    By relatedSearchesLinksLocator = By.xpath("//li/ul[@class='b_vList']/li/a");

    WebDriver driver;
    int searchResultsQuantity;
    double foundResultsQuantity;
    int index = 0;
    boolean isMobileVersion;
    String url = "http://www.bing.com/";

    String chromeDriverPath = System.getProperty("chrome.driver.executable");
    String ieDriverPath = System.getProperty("ie.driver.executable");
    String phantomjsDriverPath = System.getProperty("phantomjs.driver.executable");
    String browser = System.getProperty("browser");
    String hubUrl = System.getProperty("hubUrl");
    String reportsDirectory = System.getProperty("reportsDirectory");
    String reportsPath = System.getProperty("reportsPath");

    @Parameters("minResults")
    @BeforeClass
    public void setUp(int parameterValue){

// if run the test without any parameters - firefox driver will be used by default
// if run the test with specifying a browser using -Dbrowser - specified webdriver will be used
// if run the test with specifying a URL for selenium hub using -DhubUrl - test will be started on the selected hub
        try {
            if (hubUrl != null)
                System.setProperty("webdriver.chrome.driver", hubUrl);
            DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
            driver = new RemoteWebDriver(new URL(hubUrl), capabilities);
        } catch (MalformedURLException e) {
            System.out.println("---URL is not specified :)");
            if (chromeDriverPath == null) {
                throw new SkipException("Path to ChromeDriver is not specified");
            }
            if (ieDriverPath == null) {
                throw new SkipException("Path to IEDriver is not specified");
            }
            if (phantomjsDriverPath == null) {
                throw new SkipException("Path to PhantomJSDriver is not specified");
            }

            if (browser != null) {
                if (browser.equalsIgnoreCase("Firefox")) {
                    driver = new FirefoxDriver();
                } else if (browser.equalsIgnoreCase("Chrome")) {
                    System.setProperty("webdriver.chrome.driver", chromeDriverPath);
                    driver = new ChromeDriver();
                } else if (browser.equalsIgnoreCase("IE")) {
                    System.setProperty("webdriver.ie.driver", ieDriverPath);
                    driver = new InternetExplorerDriver();
                } else if (browser.equalsIgnoreCase("Phantomjs")) {
                    System.setProperty("phantomjs.binary.path", phantomjsDriverPath);
                    driver = new PhantomJSDriver();
                } else if (browser.equalsIgnoreCase("selendroid")) {
                    driver = new RemoteWebDriver(DesiredCapabilities.android());
                } else if (browser.equalsIgnoreCase("chromeEmulator")) {
                    System.setProperty("webdriver.chrome.driver", chromeDriverPath);

                    Map<String, String> mobileEmulation = new HashMap<String, String>();
                    mobileEmulation.put("deviceName", "Google Nexus 5");

                    Map<String, Object> chromeOptions = new HashMap<String, Object>();
                    chromeOptions.put("mobileEmulation", mobileEmulation);
                    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
                    driver = new ChromeDriver(capabilities);
                } else driver = new FirefoxDriver();
            } else driver = new FirefoxDriver();
        }

        searchResultsQuantity = parameterValue;
    }

    @Test(dataProvider="getData")
    public void firstTest(String testData) {
        String resultTitle;

        log("Navigate to: " + url);
        driver.get(url);

        WebElement searchField = recognizePageType();

        log("Input '" + testData + "' value to the search field.");
        searchField.sendKeys(testData);

        log("Start searching results");
        searchField.submit();

        explicitWait(searchResultsTitleLocator, "Search results not found");

        log("Getting page title value");
        String resultsPageTitle = driver.getTitle();
        System.out.println("Page Title: " + resultsPageTitle);

        log("Checking Page title to contain searching value");
        Assert.assertTrue(resultsPageTitle.contains(testData), "Page title doesn't contain searching value");

        if (!isMobileVersion) {
            log("Getting number of found search results");
            String foundResultsString = driver.findElement(searchResultsQuantityLocator).getText();
            foundResultsString = foundResultsString.replace(",", "");
            try {
                Assert.assertEquals(foundResultsString.charAt(0), 'р');
                foundResultsString = foundResultsString.replace("результаты: ", "");

            } catch (AssertionError e) {
                System.out.println("Exception message: " + e.getMessage());
                foundResultsString = foundResultsString.replace(" RESULTS", "");
            }
            foundResultsQuantity = Double.valueOf(foundResultsString);

            log("Checking number of found results to be less than expect");
            Assert.assertTrue(foundResultsQuantity >= searchResultsQuantity,
                    "Number of found results is " + foundResultsQuantity +  ", it's less than " + searchResultsQuantity);

        } else foundResultsQuantity = searchResultsQuantity + 1;

        log("Getting titles and links of the all found values on the page");
        List<WebElement> allResultsTitles = driver.findElements(searchResultsTitleLocator);
        for(WebElement eachResultTitle : allResultsTitles) {
            resultTitle = eachResultTitle.getText().toLowerCase();
            Assert.assertTrue(resultTitle.contains(testData),
                    "'" + resultTitle + "'" + " result doesn't contain '" + testData + "' phrase.");

            System.out.println("Title: " + resultTitle);
            System.out.println("Link: " + eachResultTitle.getAttribute("href") + "\n");
        }
    }

    private WebElement recognizePageType() {
        WebElement searchField;
        try {
            searchField = driver.findElement(searchFieldLocator);
            System.out.println("Desktop version");
            isMobileVersion = false;

            log("Checking site logo");
            String siteLogo = driver.findElement(bingLogoLocator).getText();
            Assert.assertEquals(siteLogo, "Bing", "Site logo has not expected value");
        } catch (NoSuchElementException e) {
            searchField = driver.findElement(mobileSearchFieldLocator);
            System.out.println("Mobile version");
            isMobileVersion = true;

            log("Checking site logo");
            String siteLogo = driver.findElement(mobileBingLogoLocator).getAttribute("alt");
            Assert.assertTrue(siteLogo.contains("Bing"), "Site logo has not expected value");
        }
        return searchField;
    }

    @Test(dataProvider = "getData")
    public void secondTest(String testData) throws InterruptedException, IOException {
        String selectedRandomValue;
        String currentSearchValue;

        log("Navigate to: " + url);
        driver.get(url);

        WebElement searchField = recognizePageType();
//        WebElement searchField = driver.findElement(searchFieldLocator);

        log("Input '" + testData + "' value to the search field.");
        searchField.sendKeys(testData);

        log("Start searching results");
        searchField.submit();

        log("Waiting of appearing search results");
        explicitWait(searchResultsTitleLocator, "Search results not found");

        List<WebElement> allFirstResultsTitles = driver.findElements(searchResultsTitleLocator);

        log("Run checking links ");
        System.out.println("Run checking links ");
        for(WebElement eachResultTitle : allFirstResultsTitles) {
            allFirstResultsTitles = linksChecker(allFirstResultsTitles, eachResultTitle);
        }

        log("Checking presence of 'Related searches' block");
        isElementPresent(driver, similarRequestsBlockLocator);

        log("Clicking random link from the 'Related searches' block");
        List <WebElement> listings = driver.findElements(relatedSearchesLinksLocator);
        Random random = new Random();
        int randomValue = random.nextInt(listings.size());
        selectedRandomValue = listings.get(randomValue).getText();
        listings.get(randomValue).click();

        explicitWait(searchResultsTitleLocator, "Search results not found");

        currentSearchValue = driver.findElement(searchFieldLocator).getAttribute("value");

        log("Comparing selected random link value with searching value");
        Assert.assertEquals(currentSearchValue, selectedRandomValue,
                "Selected link value doesn't match searching value");

        List<WebElement> allRelatedResultsTitles = driver.findElements(searchResultsTitleLocator);

        System.out.println("--- Run checking links from Related searches");
        log("Run checking links from Related searches");
        index = 0;
        for(WebElement eachResultTitle : allRelatedResultsTitles) {
            allRelatedResultsTitles = linksChecker(allRelatedResultsTitles, eachResultTitle);
        }
    }

    @AfterClass
    public void tearDown(){
        if (reportsPath != null) {
            reportsDirectory = reportsPath;
            System.setProperty("reportsDirectory", reportsDirectory);
        }
        driver.quit();
    }

    @DataProvider
    public Object[][] getData() throws IOException {
        int numLines = 0;
        int currentLine = 0;

        return getRowsFromFile(numLines, currentLine);
    }

    private List<WebElement> linksChecker(List<WebElement> allResultsTitles, WebElement eachResultTitle) throws IOException {
        String resultUrl;
        WebElement eachResultLink;
        int resultsCount = allResultsTitles.size();
        int bIndex;

        while(index < resultsCount){
            System.out.println("Index: " + index);
            System.out.println("All elements: " + resultsCount);
            log("Getting the text of the all found items titles from the results list");
            allResultsTitles = driver.findElements(searchResultsTitleLocator);
            List<WebElement> allResultLinks = driver.findElements(searchResultsLinkLocator);
            log("Getting the links of the all found items from the results list");
            eachResultLink = allResultLinks.get(index);
            bIndex = index + 1;

            log("Getting the text of the current Title from results list");
            resultUrl = eachResultLink.getText();

            if(!resultUrl.contains("bing.com/news")) {
                log("Click current Title from results list");
                try {
                    eachResultTitle.click();
                } catch (StaleElementReferenceException e) {
                    isElementPresent(driver, searchResultsTitleLocator);
                    eachResultTitle = allResultsTitles.get(index);
                    eachResultTitle.click();
                }

                log("Waiting of loading the page");
                driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
                String currentUrl = driver.getCurrentUrl();

                linksFormatter(resultUrl, currentUrl);

                index++;
                log("Navigating back to the results list");
                driver.navigate().back();

                testCachedPage(resultUrl, bIndex);
            } else index++;
        }
        return allResultsTitles;
    }

    private void testCachedPage(String resultUrl, int bIndex) {
        String cachedUrl;
        try {
            log("Checking that link to the cached version of current website exist");
            WebElement resultLinkToolButton = driver.findElement(By.xpath("//ol/li[" + bIndex + "]//div/a/span"));
            Assert.assertTrue(resultLinkToolButton.isDisplayed(), "The Link has no cached version");

            log("Click the tool button of the link to the cached page");
            resultLinkToolButton.click();

            log("Click the link to the cached page");
            WebElement linkToCachedPage = driver.findElement(By.xpath("//ol/li[" + bIndex + "]//div/div/div//a"));
            linkToCachedPage.click();

            log("Getting link of the cached page");
            cachedUrl = driver.findElement(cachedPageLinkLocator).getText();
            linksFormatter(resultUrl, cachedUrl);

            log("Navigating back to the results list");
            driver.navigate().back();
        } catch (NoSuchElementException e) {
            log("Current link has no cached version");
            System.out.println("Current link has no cached version");
            System.out.println("Exception cause: " + e.getCause());
        }
    }

    private void linksFormatter(String resultsListUrl, String currentUrl) {
        log("Get the link of the opened page");

        try {
            log("Checking that URL starts from 'https://'");
            Assert.assertTrue(currentUrl.startsWith("https://"), "URL doesn't start from 'https'");
            if (!resultsListUrl.startsWith("https://")) {
                log("Adding 'https://' to the link from the results list");
                String bufferedUrl = "https://";
                bufferedUrl = bufferedUrl.concat(resultsListUrl);
                resultsListUrl = bufferedUrl;
            }
        } catch (AssertionError error) {
            if (!resultsListUrl.startsWith("http://")) {
                log("Adding 'http://' to the link from the results list");
                String bufferedUrl = "http://";
                bufferedUrl = bufferedUrl.concat(resultsListUrl);
                resultsListUrl = bufferedUrl;
            }
        }
        if(!resultsListUrl.endsWith("/")&& currentUrl.endsWith("/")){
            log("Adding '/' to the end of the link from the results list");
            resultsListUrl = resultsListUrl.concat("/");
        }
        if(resultsListUrl.contains("...")){
            int dotsIndex = resultsListUrl.indexOf("...");
            currentUrl = currentUrl.substring(0, dotsIndex);
            resultsListUrl = resultsListUrl.substring(0, dotsIndex);
        }
        System.out.println("---");
        System.out.println("--- Results list URL: " + resultsListUrl);
        System.out.println("--- Current URL: " + currentUrl);
        log("Results list URL: " + resultsListUrl);
        log("Current URL: " + currentUrl);


        log("Comparing URL from the search list with the URL from the reached page");
        try {
            Assert.assertTrue(resultsListUrl.equals(currentUrl),
                    "URL from the reached page doesn't match URL from the search page");
        } catch (AssertionError error) {
            Assert.assertTrue(currentUrl.startsWith(resultsListUrl),
                    "URL from the reached page doesn't start from URL from the search page");
        }
    }

    private void explicitWait(By locator, String textMessage) {
        log("Waiting appearing of element: " + locator.toString());
        Wait<WebDriver> wait = new WebDriverWait(driver, 20).withMessage(textMessage);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private String[][] getRowsFromFile(int numLines, int currentLine) throws IOException {
        String rowValue;
        String file = new File("src" + File.separator + "main" + File.separator +
                "resources" + File.separator + "file.txt").getAbsolutePath();
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((br.readLine()) != null){
            numLines++;
        }
        br.close();

        String[][] testData = new String[numLines][1];
        BufferedReader br2 = new BufferedReader(new FileReader(file));
        while ((rowValue = br2.readLine()) != null){
            testData[currentLine][0] = rowValue;
            currentLine++;
        }
        br2.close();
        return testData;
    }

    public boolean isElementPresent(WebDriver driver, By locator) {
        try {
            log("Checking that element present: " + locator.toString());
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            log("Searching element '" + driver.findElement(locator) + "' was not found");
            return false;
        }
    }

    private void log(String logMessage) {
        Reporter.log(logMessage + "<br>");
    }

}

