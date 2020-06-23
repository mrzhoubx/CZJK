package com.itheima.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 按照SpringSecurity框架要求提供类，负责查询数据库获取用户信息
 */

@Component
public class SpringSecurityUserService implements UserDetailsService {
    @Reference
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findUserByUsername(username);
        if(user == null){
            return null;
        }

        List<GrantedAuthority> list = new ArrayList<>();

        //动态授权
        Set<Role> roles = user.getRoles();
        for (Role role : roles) {
            list.add(new SimpleGrantedAuthority(role.getKeyword()));//授予角色
            Set<Permission> permissions = role.getPermissions();
            for (Permission permission : permissions) {
                String keyword = permission.getKeyword();//权限关键字（权限标识）
                list.add(new SimpleGrantedAuthority(keyword));
            }
        }

        return new org.springframework.security.core.userdetails.User(username,user.getPassword(),list);
    }
}
