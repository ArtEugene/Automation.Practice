import actions.Google;
import core.CSVReader;
import core.CreatorXLS;

import java.util.List;
import java.util.Map;

public class TestGoogle {
    public static void main(String[] args) {
        List<String> headers = CSVReader.getValuesFromCSV("src\\main\\resources\\LinksHeaders.txt");
        Map<String, List<String>> sheets = Google.getSheetsForWordsFromFile("src\\main\\resources\\words.txt");
        CreatorXLS.createXLSS("GoogleWords.xls", sheets, headers);
        Google.closeDriver();
    }
}
