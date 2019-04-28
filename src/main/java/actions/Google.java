package actions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Google {
    private static Logger log = LogManager.getLogger();
    private static WebDriver driver = loadDriver();
    private static WebDriverWait wait = new WebDriverWait(driver, 30);

    private static WebDriver loadDriver(){
        log.info("Loading driver");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts()
                .implicitlyWait(5,TimeUnit.SECONDS)
                .pageLoadTimeout(60,TimeUnit.SECONDS);
        return driver;
    }

    public static List<String> getResultLinksForWord(String word){
        String url = "https://www.google.com";
        log.info("Navigate to '" + url + "'");
        driver.get(url);

        WebElement submitBtn = driver.findElement(By.xpath("//input[@name='btnK']"));
        driver.findElement(By.xpath("//input[@name='q']")).sendKeys(word);
        submitBtn.click();

        List<String> urls = new ArrayList<>();
        int pageCounter = 0;

        do{
            pageCounter++;
            urls.add("From page: " + pageCounter);
            urls.addAll(driver.findElements(By.xpath("//div[@class='r']/a[h3]"))
                    .stream()
                    .map(elem -> elem.getAttribute("href"))
                    .map(String::trim)
                    .collect(Collectors.toList()));
            try {
                driver.findElement(By.xpath("//a[@id='pnnext']")).click();
            } catch (Exception exc){
                log.error("Exception caught: " + exc.getClass() + ": " + exc.getMessage());
                break;
            }

        } while (pageCounter < 10);
        return urls;
    }



    public static void main (String[] arg){
        getResultLinksForWord("cheetah").forEach(System.out::println);
    }
}
