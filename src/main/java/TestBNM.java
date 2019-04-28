import actions.BNM;
import core.CSVReader;
import core.CreatorXLS;

import java.util.List;
import java.util.Map;

public class TestBNM {
    public static void main(String[] args) {
        List<String> headers = CSVReader.getValuesFromCSV("src\\main\\resources\\CurrencyHeaders.txt");
        Map<String, List<String>> sheets = BNM.getCurrencySheetsForDatesFromFile("src\\main\\resources\\dates.txt");
        CreatorXLS.createXLSS("currencyRates.xls", sheets, headers);
    }
}
