package com.itheima.service;

import com.itheima.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {
    public void add(List<OrderSetting> data);
    public List<Map> getOrderSettingsByMonth(String date);
    public void editNumberByDate(OrderSetting orderSetting);
}
