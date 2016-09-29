import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by Ihor on 9/21/2016. All rights reserved!
 */
public class Main {
    By searchFieldLocator = By.xpath("//input[@class='b_searchbox']");
    By bingLogoLocator = By.xpath("//div[@id='sbox']/div[1]");
    By searchResultsTitleLocator = By.cssSelector("ol li h2>a");
    By searchResultsQuantityLocator = By.xpath("//div[@id='b_tween']//span");
    By similarRequestsBlockLocator = By.xpath("//li/ul[@class='b_vList']");
    By similarRequestsLinksLocator = By.xpath("//li/ul[@class='b_vList']/li/a");


    WebDriver driver;
    int searchResultsQuantity;
    int foundResultsQuantity;

    @Parameters("minResults")
    @BeforeClass
    public void setUp(int parameterValue){
        driver = new FirefoxDriver();
        searchResultsQuantity = parameterValue;
    }

    @Test(dataProvider="getData")
    public void firstTest(String testData) {
        String url = "http://www.bing.com/";
        String resultTitle;

        log("Navigate to: " + url);
        driver.get(url);

        WebElement searchField = driver.findElement(searchFieldLocator);

        log("Checking site logo");
        String siteLogo = driver.findElement(bingLogoLocator).getText();
        Assert.assertEquals(siteLogo, "Bing", "Site logo has not expected value");

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

        log("Getting number of found search results");
        String foundResultsString = driver.findElement(searchResultsQuantityLocator).getText();

        foundResultsString = foundResultsString.replace(",", "");
        try {
            Assert.assertEquals(foundResultsString.charAt(0), "р");
            foundResultsString = foundResultsString.replace("результаты: ", "");

        } catch (AssertionError e) {
            System.out.println("---Exception message: ");
            foundResultsString = foundResultsString.replace(" RESULTS", "");
        }
        foundResultsQuantity = Integer.valueOf(foundResultsString);

        log("Checking number of found results to be less than expect");
        Assert.assertTrue(foundResultsQuantity >= searchResultsQuantity,
                "Number of found results is " + foundResultsQuantity +  ", it's less than " + searchResultsQuantity);

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

    private void explicitWait(By locator, String textMessage) {
        Wait<WebDriver> wait = new WebDriverWait(driver, 10).withMessage(textMessage);
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

    @DataProvider
    public Object[][] getData() throws IOException {
        int numLines = 0;
        int currentLine = 0;

        return getRowsFromFile(numLines, currentLine);
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

