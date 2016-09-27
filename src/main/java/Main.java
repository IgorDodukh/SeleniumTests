import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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

//    @Parameters("exampleDesc")
//    @Test
//    public void t1(@Optional("TestNG Examples") String desc) {
//        System.out.println("t1: " + desc);
//    }

    @Test(dataProvider="getData")
    public void instanceDbProvider(String keyword) {
        System.out.println("Keywords for test: " + keyword);
    }

    @DataProvider
        public Object[][] getData() throws IOException {
        int numLines = 0;
        int currentLine = 0;
        String rowValue;

        File file = new File("C:\\appFiles\\file.txt");

        //counting lines from file
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

    @Test(dataProvider="getData")
    public void firstTest(String testData) {
        System.out.println("The Test");
        By searchFieldLocator = By.xpath("//input[@class='b_searchbox']");
        By bingLogoLocator = By.xpath("//div[@id='sbox']/div[1]");
        By searchResultsTitleLocator = By.cssSelector("ol li h2>a");

        driver.get("http://www.bing.com/");

        WebElement searchField = driver.findElement(searchFieldLocator);
        String logoText = driver.findElement(bingLogoLocator).getText();

        System.out.println(logoText + ": ");

        searchField.sendKeys(testData);
        searchField.submit();

        Wait<WebDriver> wait = new WebDriverWait(driver, 10).withMessage("Search results not found");
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchResultsTitleLocator));

        String resultsPageTitle = driver.getTitle();
        System.out.println("Page Title: " + resultsPageTitle);

        List<WebElement> allResultsTitles = driver.findElements(searchResultsTitleLocator);

        for(WebElement eachResultTitle : allResultsTitles) {
            System.out.println("Title: " + eachResultTitle.getText());
            System.out.println("Link: " + eachResultTitle.getAttribute("href") + "\n");
        }
    }

    @AfterClass
    public void tearDown(){
        driver.quit();
    }

}

