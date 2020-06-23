package com.itheima.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class POITest {
    //从Excel文件读取数据
    //@Test
    public void test1() throws Exception{
        //创建一个工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook("D:\\hello.xlsx");
        //读取第一个Sheet页
        XSSFSheet sheet = workbook.getSheetAt(0);
        //遍历标签页获得每一行
        for (Row row : sheet) {
            //遍历行，获得每个单元格对象
            for (Cell cell : row) {
                //获取单元格中的数据
                String stringCellValue = cell.getStringCellValue();
                System.out.println(stringCellValue);
            }
        }
        //关闭
        workbook.close();
    }

    //从Excel文件读取数据
    //@Test
    public void test2() throws Exception{
        //创建一个工作簿对象
        XSSFWorkbook workbook = new XSSFWorkbook("D:\\hello.xlsx");
        //读取第一个Sheet页
        XSSFSheet sheet = workbook.getSheetAt(0);
        //获得最后一个行号
        int lastRowNum = sheet.getLastRowNum();
        for(int i=0;i<=lastRowNum;i++){
            //根据行号获得行对象
            XSSFRow row = sheet.getRow(i);
            //获得当前行最后一个单元格索引
            short lastCellNum = row.getLastCellNum();
            for(int j=0;j<lastCellNum;j++){
                //根据单元格索引获得单元格对象
                XSSFCell cell = row.getCell(j);
                System.out.println(cell.getStringCellValue());
            }
        }
        //关闭
        workbook.close();
    }

    //向Excel文件写入数据，通过输出流下载到磁盘
    //@Test
    public void test3() throws Exception{
        //在内存中创建一个Excel文件（工作簿）
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        //创建一个Sheet标签页
        XSSFSheet sheet = xssfWorkbook.createSheet("黑马");

        //在标签页中创建行对象
        XSSFRow row = sheet.createRow(0);
        //在行对象中创建单元格对象
        row.createCell(0).setCellValue("编号");
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("年龄");

        //在标签页中创建行对象
        XSSFRow row1 = sheet.createRow(1);
        //在行对象中创建单元格对象
        row1.createCell(0).setCellValue("001");
        row1.createCell(1).setCellValue("老王");
        row1.createCell(2).setCellValue(8);

        //输出流，用于将Excel写到磁盘
        OutputStream out = new FileOutputStream("D:\\heima.xlsx");
        xssfWorkbook.write(out);

        //关闭资源
        out.close();
        xssfWorkbook.close();
    }
}
