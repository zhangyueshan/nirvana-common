package com.nirvana.common.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.List;

/**
 * Created by InThEnd on 2017/1/20.
 * 当excel导出数据超出63225条时候，HSSFWORKBOOK超出数据量上限,用SXSSFXSSFWorkbook替代原工作薄
 */
public class ExcelUtilXssf {

    public static SXSSFWorkbook excelExport(List<String> heads, List<List<String>> contents, int ignoreColumn, int ignoreRow, String sheetName) {
        SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet(sheetName);
        sheet.setDefaultColumnWidth((short) 20);
        Row row = sheet.createRow(ignoreRow);

        // 定义表头
        for (int i = 0; i < heads.size(); i++) {
            Cell cell = row.createCell(i + ignoreColumn);
            String value = heads.get(i);
            cell.setCellValue(value == null ? "" : value);
        }
        if (contents != null) {
            for (int i = 0; i < contents.size(); i++) {
                Row row1 = sheet.createRow(ignoreRow + i + 1);
                List<String> content = contents.get(i);
                for (int j = 0; j < content.size(); j++) {
                    Cell cell = row1.createCell(j + ignoreColumn);
                    String value = contents.get(i).get(j);
                    cell.setCellValue(value == null ? "" : value);
                }
            }
        }
        return wb;
    }


}
