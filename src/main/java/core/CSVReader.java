package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVReader {
    public static List<String> getValuesFromCSV(String pathToDatesFile) {
        File file = new File(pathToDatesFile);
        List<String> values = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String tmpStr;
            while ((tmpStr = br.readLine()) != null){
                values.addAll(Arrays.asList(tmpStr.split(";")));
            }
            values.replaceAll(String::trim);

        } catch (Exception e){
            System.out.println("[x] Error!");
            System.exit(404);
        }

        return values;
    }
}
