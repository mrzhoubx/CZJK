package com.itheima.security;

import com.itheima.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService implements UserDetailsService {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    //模拟数据库中的用户数据
    public  static Map<String, User> map = new HashMap<>();
    public void initData(){
        com.itheima.pojo.User user1 = new com.itheima.pojo.User();
        user1.setUsername("admin");
        user1.setPassword(passwordEncoder.encode("admin"));

        com.itheima.pojo.User user2 = new com.itheima.pojo.User();
        user2.setUsername("xiaoming");
        user2.setPassword(passwordEncoder.encode("1234"));

        map.put(user1.getUsername(),user1);
        map.put(user2.getUsername(),user2);
    }

    //根据用户名查询数据库，获得用户信息
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        initData();
        User user = map.get(username);
        if(user == null){
            //没有查询到用户信息
            return null;
        }

        //为用户授权，后期可以查询数据库
        List<GrantedAuthority> list = new ArrayList<>();
        //授权，后期需要改为查询数据库动态获得用户拥有的权限和角色
        if(username.equals("admin")){
            list.add(new SimpleGrantedAuthority("add"));//权限
        }
        list.add(new SimpleGrantedAuthority("delete"));//授权
        list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));//授予角色

        String passwordInDb = user.getPassword();//明文

        //查询到了用户信息
        return new org.springframework.security.core.userdetails.User(username,passwordInDb,list);
    }

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pwd1 = encoder.encode("1234");
        String pwd2 = encoder.encode("1234");
        boolean matches = encoder.matches("1234",pwd2);
        System.out.println(matches);
    }
}
