package core;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class CreatorXLS {

    public static void createXLSS(String xlsFileName, Map<String,List<String>> sheets, List<String> headers){
        HSSFWorkbook workbook = new HSSFWorkbook();

        for(Map.Entry<String,List<String>> sheetEntry: sheets.entrySet()){
            HSSFSheet currentSheet = workbook.createSheet(sheetEntry.getKey());

            int rowCounter = 0;
            for (String currentString : sheetEntry.getValue()){
                if (rowCounter == 0){
                    HSSFRow headerRow = currentSheet.createRow(rowCounter);

                    int index = 0;
                    for (String header : headers){
                        headerRow.createCell(index++).setCellValue(header);
                    }
                    rowCounter++;
                }

                HSSFRow currentRow = currentSheet.createRow(rowCounter);

                int index = 0;
                for (String token : currentString.split("\t")){
                    currentRow.createCell(index++).setCellValue(token);
                }
                rowCounter++;
            }
        }
        try{
            FileOutputStream fileOut = new FileOutputStream(xlsFileName);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
