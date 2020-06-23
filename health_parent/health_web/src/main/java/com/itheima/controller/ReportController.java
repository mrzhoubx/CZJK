package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.MemberService;
import com.itheima.service.OrderService;
import com.itheima.service.ReportService;
import com.itheima.utils.DateUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.*;

/**
 * Echarts报表统计
 */
@RestController
@RequestMapping("/report")
public class ReportController {
    @Reference
    private MemberService memberService;
    @Reference
    private OrderService orderService;
    @Reference
    private ReportService reportService;

    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){
        Map<String,Object> map = new HashMap<>();
        List<String> months = new ArrayList<>();

        //计算过去12个月，将每个月放入List
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-12);//按照月进行计算

        for(int i=0;i<12;i++){
            Date time = calendar.getTime();//2018.5.19
            try {
                String date = DateUtils.parseDate2String(time, "yyyy.MM");
                months.add(date);
                calendar.add(Calendar.MONTH,1);//按照月进行计算
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        map.put("months",months);

        List<Integer> memberCount = memberService.findMemberCountByMonth(months);

        map.put("memberCount",memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,map);
    }

    //套餐占比
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){
        Map<String,Object> map = new HashMap<>();

        List<Map> setmealCount = orderService.getSetmealReport();
        map.put("setmealCount",setmealCount);

        List<String> setmealNames = new ArrayList<>();
        for (Map map1 : setmealCount) {
            String setmealName = (String) map1.get("name");
            setmealNames.add(setmealName);
        }
        map.put("setmealNames",setmealNames);

        return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,map);
    }

    //运营数据统计
    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        Map<String,Object> map = null;
        try {
            map = reportService.getBusinessReportData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(true,MessageConstant.GET_BUSINESS_REPORT_SUCCESS,map);
    }

    //将运营数据写入Excel文件并提供给客户端下载
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        Map<String,Object> result = null;
        try {
            result = reportService.getBusinessReportData();

            //取出返回结果数据，准备将报表数据写入到Excel文件中
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map> hotSetmeal = (List<Map>) result.get("hotSetmeal");

            //获得template目录的绝对路径
            String dir = request.getSession().getServletContext().getRealPath("template");
            String filePath = dir + File.separator + "report_template.xlsx";
            //根据提供的模板文件创建一个Excel表格对象
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(new File(filePath)));
            XSSFSheet sheet = workbook.getSheetAt(0);

            XSSFRow row = sheet.getRow(2);
            row.getCell(5).setCellValue(reportDate);//日期

            row = sheet.getRow(4);
            row.getCell(5).setCellValue(todayNewMember);//新增会员数（本日）
            row.getCell(7).setCellValue(totalMember);//总会员数

            row = sheet.getRow(5);
            row.getCell(5).setCellValue(thisWeekNewMember);//本周新增会员数
            row.getCell(7).setCellValue(thisMonthNewMember);//本月新增会员数

            row = sheet.getRow(7);
            row.getCell(5).setCellValue(todayOrderNumber);//今日预约数
            row.getCell(7).setCellValue(todayVisitsNumber);//今日到诊数

            row = sheet.getRow(8);
            row.getCell(5).setCellValue(thisWeekOrderNumber);//本周预约数
            row.getCell(7).setCellValue(thisWeekVisitsNumber);//本周到诊数

            row = sheet.getRow(9);
            row.getCell(5).setCellValue(thisMonthOrderNumber);//本月预约数
            row.getCell(7).setCellValue(thisMonthVisitsNumber);//本月到诊数

            int rowNum = 12;
            for(Map map : hotSetmeal){//热门套餐
                String name = (String) map.get("name");
                Long setmeal_count = (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row = sheet.getRow(rowNum ++);
                row.getCell(4).setCellValue(name);//套餐名称
                row.getCell(5).setCellValue(setmeal_count);//预约数量
                row.getCell(6).setCellValue(proportion.doubleValue());//占比
            }

            //下载
            ServletOutputStream out = response.getOutputStream();
            //设置响应头信息
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-Disposition", "attachment;filename=report.xlsx");

            workbook.write(out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
