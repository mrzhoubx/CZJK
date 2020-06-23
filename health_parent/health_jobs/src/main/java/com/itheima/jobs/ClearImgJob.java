package com.itheima.jobs;

import com.itheima.constant.RedisConstant;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;
import java.util.Set;

/**
 * 定时清理垃圾图片任务
 */
public class ClearImgJob {
    @Autowired
    private JedisPool jedisPool;
    public void clearImg(){
        //获得垃圾图片集合名称，从redis获得
        Set<String> set = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        for (String imgName : set) {
            //调用七牛云工具类删除垃圾图片
            QiniuUtils.deleteFileFromQiniu(imgName);
            System.out.println("定时任务，清理图片：" + imgName);
            //将redis中图片名称清理掉
            jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,imgName);
        }
    }
}
