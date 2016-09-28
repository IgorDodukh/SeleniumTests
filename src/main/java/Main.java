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

    @BeforeClass
    public void setUp(){
        driver = new FirefoxDriver();
    }

    @Parameters("minResults")
    @Test
    public void a(String param) {
        System.out.println("VV--" + param);
    }

    @Test(dataProvider="getData")
    public void firstTest(String testData) {
        By searchFieldLocator = By.xpath("//input[@class='b_searchbox']");
        By bingLogoLocator = By.xpath("//div[@id='sbox']/div[1]");
        By searchResultsTitleLocator = By.cssSelector("ol li h2>a");

        String url = "http://www.bing.com/";
        String resultTitle;

        driver.get(url);
        Reporter.log("Navigating to: " + url);

        WebElement searchField = driver.findElement(searchFieldLocator);
        String logoText = driver.findElement(bingLogoLocator).getText();

        System.out.println(logoText + ": ");

        searchField.sendKeys(testData);
        Reporter.log("Input '" + testData + "' value to the search field");

        searchField.submit();
        Reporter.log("Start searching results");

        Wait<WebDriver> wait = new WebDriverWait(driver, 10).withMessage("Search results not found.");
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchResultsTitleLocator));

        String resultsPageTitle = driver.getTitle();
        System.out.println("Page Title: " + resultsPageTitle);

        Assert.assertTrue(resultsPageTitle.contains(testData), "Page title doesn't contain searching value.");

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

        String file = new File("src\\main\\resources\\file.txt").getAbsolutePath();
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

}

