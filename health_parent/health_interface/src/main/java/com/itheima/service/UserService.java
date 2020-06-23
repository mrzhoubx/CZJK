package com.itheima.service;

import com.itheima.pojo.User;

public interface UserService {
    public User findUserByUsername(String username);
}
