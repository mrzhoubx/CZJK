package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class test{
    @Reference
    private SetmealService setmealService;
    //查询所有的套餐信息
    @RequestMapping("/getSetmeal")
    public Result getSetmeal(){
        List<Setmeal> list = setmealService.findAll();
        return new Result(true, MessageConstant.GET_SETMEAL_LIST_SUCCESS,list);
    }

    //根据套餐id查询套餐详情
    @RequestMapping("/findById")
    public Result findById(Integer id){
        Setmeal setmeal = setmealService.findById(id);
        return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
    }
}
