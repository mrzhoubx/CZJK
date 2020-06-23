package com.itheima.jobs;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class QuartzTest {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("application-jobs.xml");
    }
}
