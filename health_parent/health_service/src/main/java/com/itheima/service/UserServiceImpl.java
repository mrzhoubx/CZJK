package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 用户服务
 */

@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;
    //根据用户名查询用户信息，同时要查询用户关联的角色、角色关联的权限
    public User findUserByUsername(String username) {
        //只查询用户表信息
        User user = userDao.findByUsername(username);
        if(user == null){
            return null;
        }

        //根据用户id查询关联的角色
        Integer userId = user.getId();
        Set<Role> roles = roleDao.findByUserId(userId);

        if(roles != null && roles.size() > 0){
            user.setRoles(roles);
            for (Role role : roles) {
                Integer roleId = role.getId();
                //根据查询到的角色id查询关联的权限信息
                Set<Permission> permissions = permissionDao.findByRoleId(roleId);
                role.setPermissions(permissions);
            }
        }

        return user;
    }
}
