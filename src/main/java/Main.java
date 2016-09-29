import org.openqa.selenium.By;
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
        By searchFieldLocator = By.xpath("//input[@class='b_searchbox']");
        By bingLogoLocator = By.xpath("//div[@id='sbox']/div[1]");
        By searchResultsTitleLocator = By.cssSelector("ol li h2>a");
        By searchResultsQuantityLocator = By.xpath("//div[@id='b_tween']//span");

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


        Wait<WebDriver> wait = new WebDriverWait(driver, 10).withMessage("Search results not found");
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchResultsTitleLocator));

        log("Getting page title value");
        String resultsPageTitle = driver.getTitle();
        System.out.println("Page Title: " + resultsPageTitle);

        log("Checking Page title to contain searching value");
        Assert.assertTrue(resultsPageTitle.contains(testData), "Page title doesn't contain searching value");

        log("Getting number of found search results");
        String foundResultsString = driver.findElement(searchResultsQuantityLocator).getText();

        foundResultsString = foundResultsString.replace(",", "");
        try {
            foundResultsString = foundResultsString.replace(" RESULTS", "");
        } catch (NumberFormatException e) {
            foundResultsString = foundResultsString.replace("результаты: ", "");
        }
        foundResultsQuantity = Integer.valueOf(foundResultsString);

        log("Checking number of found results (" + foundResultsQuantity + ")to be less than (" + searchResultsQuantity + ")");
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

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

    @DataProvider
    public Object[][] getData() throws IOException {
        int numLines = 0;
        int currentLine = 0;
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

    private void log(String logMessage) {
        Reporter.log(logMessage + "<br>");
    }

}

