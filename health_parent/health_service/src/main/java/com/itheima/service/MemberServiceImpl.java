package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.pojo.Member;
import com.itheima.utils.MD5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员服务
 */

@Service(interfaceClass = MemberService.class)
@Transactional
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberDao memberDao;

    public Member findByTelephone(String telephone) {
        return memberDao.findByTelephone(telephone);
    }

    //添加会员信息
    public void add(Member member) {
        String password = member.getPassword();
        if(password != null){
            //使用MD5对密码进行加密
            String md5 = MD5Utils.md5(password);
            member.setPassword(md5);
        }
        memberDao.add(member);
    }

    //根据月份进行会员数量统计
    public List<Integer> findMemberCountByMonth(List<String> months) {
        List<Integer> list = new ArrayList<>();
        for (String month : months) {//2019.05.31
            month = month + ".31";
            Integer count = memberDao.findMemberCountBeforeDate(month);
            list.add(count);
        }
        return list;
    }
}
