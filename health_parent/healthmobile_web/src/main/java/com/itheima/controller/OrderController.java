package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.aliyuncs.exceptions.ClientException;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.utils.SMSUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;

/**
 * 体检预约
 */

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @Autowired
    private JedisPool jedisPool;
    //体检预约
    @RequestMapping("/submit")
    public Result submitOrder(@RequestBody Map map){
        //判断用户输入的验证码是否正确
        String validateCode = (String)map.get("validateCode");
        //从redis中获取保存的验证码
        String telephone = (String)map.get("telephone");
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_ORDER);
        if(codeInRedis == null || !codeInRedis.equals(validateCode)){
            //验证码输入错误
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }

        //验证码输入正确，通过Dubbo远程调用预约服务实现用户预约
        map.put("orderType",Order.ORDERTYPE_WEIXIN);
        Result result = orderService.submitOrder(map);

        if(result.isFlag()){
            String orderDate = (String)map.get("orderDate");
            //如果预约成功，需要为用户发送短信
            try {
                SMSUtils.sendShortMessage(SMSUtils.ORDER_NOTICE,telephone,orderDate);
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    //根据预约id查询预约信息（包括会员信息、套餐信息）
    @RequestMapping("/findById")
    public Result findById(int id){
        try{
            Result result = orderService.findById(id);
            return result;
        }catch (Exception e){
            return new Result(false,MessageConstant.ORDER_FAIL);
        }
    }
}
