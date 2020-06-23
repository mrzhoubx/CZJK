package com.itheima.jobs;

import java.util.Date;

/**
 * 自定义任务类
 */
public class MyJob {
    public void run(){
        System.out.println("自定义任务执行了..." + new Date());
    }
}
