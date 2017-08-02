package com.nirvana.common.utils;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by InThEnd on 2016/4/7.
 * Excel工具类。
 */
public class ExcelUtils {

    private static final DecimalFormat format = new DecimalFormat("0.###");

    /**
     * CSV文件转List<T>
     *
     * @param is          输入流
     * @param clazz       T的类型
     * @param fieldsIndex T类型与CSV文件的列位置对应
     * @param ignore      要跳过的记录数
     */
    public static <T> List<T> readCsv(InputStream is, Class<T> clazz, String[] fieldsIndex, int ignore) throws IOException {
        try {
            Field[] fields = new Field[fieldsIndex.length];
            for (int i = 0; i < fieldsIndex.length; i++) {
                fields[i] = clazz.getDeclaredField(fieldsIndex[i]);
                fields[i].setAccessible(true);
            }
            CSVFormat format = CSVFormat.EXCEL;
            CSVParser parser = new CSVParser(new InputStreamReader(is, "GBK"), format);
            List<CSVRecord> recordList = parser.getRecords();
            List<T> list = new ArrayList<>();
            for (int i = ignore; i < recordList.size(); i++) {
                CSVRecord record = recordList.get(i);
                T t = clazz.newInstance();
                for (int j = 0; j < fields.length; j++) {
                    String value = record.get(j);
                    if (value != null && value.equals("")) {
                        value = null;
                    }
                    fields[j].set(t, value);
                }
                list.add(t);
            }
            return list;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("fieldsIndex参数与clazz参数不匹配：fieldsIndex参数中\"" + e.getMessage() + "\"字段未在" + clazz.getName() + "类中找到。");
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(clazz.getName() + "类中未找到默认无参构造方法。");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("请保证" + clazz.getName() + "类中默认构造方法可见。");
        }
    }

    /**
     * CSV文件转List<T>
     *
     * @param is          输入流
     * @param clazz       T的类型
     * @param fieldsIndex T类型与CSV文件的列位置对应
     */
    public static <T> List<T> readCsv(InputStream is, Class<T> clazz, String[] fieldsIndex) throws IOException {
        return readCsv(is, clazz, fieldsIndex, 0);
    }

    /**
     * 导出简单表格
     *
     * @param maps 表头
     * @param list 表身的list
     */
    public static <T> Workbook excelExport(Map<String, String> maps, List<T> list) {
        return excelExport(maps, list, 0);
    }

    /**
     * Excel导出
     *
     * @param keyMap <String,String> maps 属性表，成员属性为KEY，中文名称为VALUE
     * @param list   <T> list 需要导出的数据列表对象
     */
    public static <T> Workbook excelExport(Map<String, String> keyMap, List<T> list, int ignore) {
        HSSFWorkbook wb = new HSSFWorkbook();
        try {
            CreationHelper createHelper = wb.getCreationHelper();
            Sheet sheet = wb.createSheet("sheet1");
            sheet.setDefaultColumnWidth((short) 20);
            Set<String> sets = keyMap.keySet();
            Row row = sheet.createRow(ignore);

            int i = 0;
            // 定义表头
            for (String key : sets) {
                Cell cell = row.createCell(i++);
                cell.setCellValue(createHelper.createRichTextString(keyMap.get(key)));
            }
            // 填充表单内容
            for (int j = 0; j < list.size(); j++) {
                T p = list.get(j);
                Class classType = p.getClass();
                int index = 0;
                Row row1 = sheet.createRow(j + 1 + ignore);
                for (String key : sets) {
                    Field field = classType.getDeclaredField(key);
                    field.setAccessible(true);
                    Cell cell = row1.createCell(index++);
                    Object value = field.get(p);
                    /*if (field.getType() == BigDecimal.class) {
                        cell.setCellValue(BigDecimal.ZERO.toString());
                    }*/
                    if (value == null) {
                        if (field.getType() == BigDecimal.class) {
                            cell.setCellValue("");
                        } else if (field.getType() == Long.class) {
                            cell.setCellValue("");
                        } else if (field.getType() == Integer.class) {
                            cell.setCellValue("");
                        } else {
                            cell.setCellValue("");
                        }
                    } else {
                        if (field.getType() == Date.class) {
                            Date date = (Date) value;
                            cell.setCellValue(DateUtils.format(date, "yyyy/MM/dd HH:mm:ss"));
                        } else if (field.getType() == Boolean.class) {
                            Boolean booValue = (Boolean) value;
                            if (booValue) {
                                cell.setCellValue("是");
                            } else {
                                cell.setCellValue("否");
                            }
                        } else if (value.equals("0")) {
                            cell.setCellValue("否");
                        } else if (value.equals("1")) {
                            cell.setCellValue("是");
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
            return wb;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("不可能出现这个错误！");
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("参数错误，输出Excel失败。");
        }
    }

    /**
     * Excel文件转List<List<String>>
     *
     * @param is     输入流
     * @param ignore 要跳过的记录数
     */
    public static List<List<String>> readExcel(InputStream is, int ignore) throws IOException, InvalidFormatException {
        Workbook book = WorkbookFactory.create(is);

        Sheet sheet = book.getSheetAt(0);

        List<List<String>> list = new ArrayList<>();
        Row row1 = sheet.getRow(sheet.getFirstRowNum());
        int min = row1.getFirstCellNum();
        int max = row1.getLastCellNum();
        for (int i = sheet.getFirstRowNum() + ignore; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            List<String> contentList = new ArrayList<>();
            for (int j = min; j < max; j++) {
                Cell cell = row.getCell(j);
                String stringValue = readCell(cell);
                contentList.add(stringValue);
            }
            list.add(contentList);
        }
        return list;
    }

    /**
     * Excel文件转List<List<String>>
     *
     * @param is 输入流
     */
    public static List<List<String>> readExcel(InputStream is) throws IOException, InvalidFormatException {
        return readExcel(is, 0);
    }

    public static HSSFWorkbook excelExport(List<String> heads, List<List<String>> contents) {
        return excelExport(heads, contents, "sheet1", 0);
    }


    /**
     * Excel导出
     *
     * @param heads    表头
     * @param contents 表内容
     */
    public static HSSFWorkbook excelExport(List<String> heads, List<List<String>> contents, String sheetName, int ignore) {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName);
        Row row = sheet.getRow(ignore);
        // 定义表头
        for (int i = 0; i < heads.size(); i++) {
            Cell cell = row.getCell(i);
            String value = heads.get(i);
            cell.setCellValue(value == null ? "" : value);
        }
        // 填充表单内容
        if (contents != null) {
            for (int i = 0; i < contents.size(); i++) {
                Row row1 = sheet.getRow(i + 1);
                List<String> content = contents.get(i);
                for (int j = 0; j < content.size(); j++) {
                    Cell cell = row1.getCell(j);
                    String value = contents.get(i).get(j);
                    cell.setCellValue(value == null ? "" : value);
                }
                if (i % 1000 == 0) {
                    System.out.println("处理到第#" + i + "#条");
                }
            }
        }
        return wb;
    }

    private static String readCell(Cell cell) {
        if (cell == null) {
            return "";
        }
        int cellType = cell.getCellType();
        switch (cellType) {
            case Cell.CELL_TYPE_NUMERIC:
                return format.format(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            default:
                return "";
        }
    }

    //根据数据量查询所在列
    public static String getColumnCode(int num) {
        String columnFiled = "";
        int chuNum;
        int yuNum;
        if (num >= 1 && num <= 26) {
            columnFiled = doHandle(num);
        } else {
            chuNum = num / 26;
            yuNum = num % 26;

            columnFiled += doHandle(chuNum);
            columnFiled += doHandle(yuNum);
        }
        return columnFiled;
    }

    private static String doHandle(final int num) {
        String[] charArr = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M"
                , "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        return charArr[num - 1];
    }

}