package actions;

import com.thoughtworks.xstream.XStream;
import core.CSVReader;
import dto.Currency;
import dto.CurrencyRate;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BNM {
    private static String getXmlResponseFromBNMForDate(String date) {
        String xml = "";

        try {
            String bnmUrl = "https://bnm.md/en/official_exchange_rates";

            HttpClient client = HttpClientBuilder.create().build();

            URIBuilder builder = new URIBuilder(bnmUrl);
            builder.addParameter("date", date);

            HttpGet get = new HttpGet(builder.build());
            HttpResponse response = client.execute(get);

            xml = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xml;
    }

    public static List<Currency> getBNMCurrenciesForDate(String date) {
        XStream xStream = new XStream();
        xStream.processAnnotations(Currency.class);
        xStream.processAnnotations(CurrencyRate.class);

        return ((CurrencyRate) xStream.fromXML(getXmlResponseFromBNMForDate(date))).getCurrencies();
    }

    public static List<String> getBNMCurrenciesToStringForDate(String date){
        return getBNMCurrenciesForDate(date).stream().map( x -> x.toString()).collect(Collectors.toList());
    }

    public static Map<String, List<String>> getCurrencySheetsForDatesFromFile(String pathToDatesFile){
        List<String> dates = CSVReader.getValuesFromCSV(pathToDatesFile);

        Map<String, List<String>> map = new TreeMap<>();

        for(String date : dates) {
            map.put(date, getBNMCurrenciesToStringForDate(date));
        }

        return map;
    }
}
