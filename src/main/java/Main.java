import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Created by Ihor on 9/21/2016. All rights reserved!
 */
public class Main {
    public static void main(String[] args) {
        By searchFieldLocator = By.xpath("//input[@class='b_searchbox']");
        By searchResultsTitleLocator = By.cssSelector("ol li h2>a");

        WebDriver driver = new FirefoxDriver();
        driver.get("http://www.bing.com/");

        WebElement searchField = driver.findElement(searchFieldLocator);
        searchField.sendKeys("automation");
        searchField.submit();

        Wait<WebDriver> wait = new WebDriverWait(driver, 10).withMessage("Search results not found");
        wait.until(ExpectedConditions.visibilityOfElementLocated(searchResultsTitleLocator));

        String pageTitle = driver.getTitle();
        System.out.println("Page Title: " + pageTitle);

        List<WebElement> allResultsTitles = driver.findElements(searchResultsTitleLocator);

        for(WebElement eachResultTitle : allResultsTitles) {
            System.out.println("Title: " + eachResultTitle.getText());
            System.out.println("Link: " + eachResultTitle.getAttribute("href") + "\n");
        }

        driver.quit();
    }
}
