package com.hcmute.shopfee.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;

@Slf4j
public class HandleFileUtils {
    public static void readFileToCreateProduct(InputStream inputStream) {
        try {
            Workbook workbook = new XSSFWorkbook(inputStream);

            int rowIndex = 0;
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {

                for (Cell cell : row) {
                    System.out.print(cell + "\t");
                }
                System.out.println();
            }
            inputStream.close();
            workbook.close();
        } catch (Exception e) {
            log.error("Error reading" + e.getMessage());
        }
    }
}
