package actions;

import core.CSVReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
                .implicitlyWait(10,TimeUnit.SECONDS)
                .pageLoadTimeout(5,TimeUnit.MINUTES);
        driver.manage().window().maximize();
        return driver;
    }

    public static List<String> getSearchingResultLinksForWord(String word){
        String url = "https://www.google.com";
        log.info("Navigate to '" + url + "'");
        driver.get(url);
        log.info("Current URL: '" + driver.getCurrentUrl() + "'");

        WebElement submitBtn = driver.findElement(By.xpath("//input[@name='btnK']"));
        driver.findElement(By.xpath("//input[@name='q']")).sendKeys(word);
        log.info("Click on 'submitBtn' element");
        submitBtn.click();

        List<String> resultLinks = new ArrayList<>();
        int pageCounter = 0;

        do{
            pageCounter++;
            log.info("Get references from search results for page #" + pageCounter);
            List <String> currentPageLinks = driver.findElements(By.xpath("//div[@class='r']/a[h3]"))
                    .stream()
                    .map(elem -> elem.getText().replaceAll("\\s+", " ") + "\t" + elem.getAttribute("href").trim())
                    .map(String::trim)
                    .collect(Collectors.toList());
            log.info("Number of references found is: " + currentPageLinks.size());

            resultLinks.addAll(currentPageLinks);
            try {
                driver.findElement(By.xpath("//a[@id='pnnext']")).click();
            } catch (Exception exc){
                log.error("Exception caught: " + exc.getClass() + ": " + exc.getMessage());
                break;
            }
        } while (pageCounter < 1); // 10);
        return resultLinks;
    }

    public static int getCountMatchesWordAtUrl(String word, String url){
        log.info("Navigate to '" + url + "'");
        driver.get(url);
        log.info("Current URL: '" + driver.getCurrentUrl() + "'");

        String textFromHtml = driver.getPageSource().replaceAll("<[^<>]*>","");

        int countMatches = StringUtils.countMatches(textFromHtml, word);
        log.info("Count of matches of word '" + word + "' at: '" + url + "' is " + countMatches);
        return countMatches;
    }

    public static int getCountMatchesWordAtUrlIgnorCase(String word, String url){
        log.info("Navigate to '" + url + "'");
        driver.get(url);
        log.info("Current URL: '" + driver.getCurrentUrl() + "'");

        String textFromHtml = driver.getPageSource().replaceAll("<[^<>]*>","").toLowerCase();

        int countMatches = StringUtils.countMatches(textFromHtml, word.toLowerCase());
        log.info("Count of matches (ignore case) of word '" + word + "' at: '" + url + "' is " + countMatches);
        return countMatches;
    }

    public static List<String> getLinksToStringForWord(String word){
        List<String> links = getSearchingResultLinksForWord(word);

        List<String> resultList = new ArrayList<>();
        for (String lnk : links){
            String countMatches = getCountMatchesWordAtUrlIgnorCase(word, lnk.split("\t")[1]) + "";

            resultList.add(lnk.trim() + "\t" + countMatches);
        }
        return resultList;
    }

    public static Map<String, List<String>> getSheetsForWordsFromFile(String pathToWordsFile){
        List<String> words = CSVReader.getValuesFromCSV(pathToWordsFile);

        Map<String, List<String>> map = new TreeMap<>();

        for(String word : words) {
            List<String> linksString = getLinksToStringForWord(word);
            map.put(word, linksString);
        }

        return map;
    }

    public static void closeDriver(){
        if (driver != null){
            log.info("Closing driver");
            driver.quit();
        }
    }
}
