package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 预约设置服务
 */
@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {
    @Autowired
    private OrderSettingDao orderSettingDao;
    //批量保存预约设置信息
    public void add(List<OrderSetting> data) {
        if(data != null && data.size()> 0){
            for (OrderSetting orderSetting : data) {
                //判断当前日期是否已经设置过了
                Date orderDate = orderSetting.getOrderDate();//预约日期
                //根据日期统计
                long count = orderSettingDao.findCountByOrderDate(orderDate);
                if(count > 0){
                    //已经设置过，执行修改操作
                    orderSettingDao.editNumberByOrderDate(orderSetting);
                }else{
                    //没有设置过，执行插入操作
                    orderSettingDao.add(orderSetting);
                }
            }
        }
    }

    //根据年月查询对应的预约设置数据
    public List<Map> getOrderSettingsByMonth(String date) {//2019-5
        String begin = date + "-1";//2019-5-1
        String end = date + "-31";//2019-5-31
        Map<String,String> map = new HashMap<>();//封装查询条件，根据日期范围查询
        map.put("begin",begin);
        map.put("end",end);
        List<OrderSetting> list = orderSettingDao.getOrderSettingsByMonth(map);
        List<Map> data = new ArrayList<>();
        if(list != null && list.size() > 0){
            for (OrderSetting orderSetting : list) {
                int dateNum = orderSetting.getOrderDate().getDate();
                int number = orderSetting.getNumber();
                int reservations = orderSetting.getReservations();
                Map<String,Integer> map1 = new HashMap<>();
                map1.put("date",dateNum);
                map1.put("number",number);
                map1.put("reservations",reservations);
                data.add(map1);
            }
        }
        return data;
    }

    //根据日期修改可预约人数
    public void editNumberByDate(OrderSetting orderSetting) {
        //根据日期查询
        long count = orderSettingDao.findCountByOrderDate(orderSetting.getOrderDate());
        if(count > 0){
            //已经设置过，执行修改操作
            orderSettingDao.editNumberByOrderDate(orderSetting);
        }else{
            //没有设置过，执行插入操作
            orderSettingDao.add(orderSetting);
        }
    }
}
