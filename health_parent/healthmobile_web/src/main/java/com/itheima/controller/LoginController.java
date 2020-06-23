package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 登录操作
 */

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private JedisPool jedisPool;

    @Reference
    private MemberService memberService;
    //登录方法
    @RequestMapping("/check")
    public Result check(HttpServletResponse response,@RequestBody Map map){
        String validateCode = (String) map.get("validateCode");
        String telephone = (String) map.get("telephone");

        //从redis获取保存的验证码
        String codeInRedis = jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);

        if(codeInRedis == null || !codeInRedis.equals(validateCode)){
            //验证码输入错误
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        }

        //检查用户是否为会员，如果不是会员自动完成注册
        Member member = memberService.findByTelephone(telephone);
        if(member == null){
            //不是会员，自动注册
            member = new Member();
            member.setPhoneNumber(telephone);
            member.setRegTime(new Date());
            memberService.add(member);
        }

        //如果验证码输入正确，通过Cookie保存用户手机号
        Cookie cookie = new Cookie("login_member_telephone",telephone);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
        return new Result(true,MessageConstant.LOGIN_SUCCESS);
    }
}
