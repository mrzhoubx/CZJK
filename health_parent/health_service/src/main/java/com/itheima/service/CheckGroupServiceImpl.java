package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检查组服务
 */
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {
    @Autowired
    private CheckGroupDao checkGroupDao;
    //新增检查组，同时需要关联检查项
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
        //新增检查组
        checkGroupDao.add(checkGroup);
        Integer checkGroupId = checkGroup.getId();
        //检查组关联检查项（操作中间表）
        this.setCheckGroupAndCheckItem(checkGroupId,checkitemIds);
    }

    //分页查询
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckGroup> page = checkGroupDao.pageQuery(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    //编辑检查组基本信息，重新设置检查组和检查项的关联关系
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
        //修改基本信息
        checkGroupDao.edit(checkGroup);

        //删除检查组和检查项的关联关系
        checkGroupDao.deleteAssociationByCheckGroupId(checkGroup.getId());

        //重新设置检查组和检查项的关联关系
        this.setCheckGroupAndCheckItem(checkGroup.getId(),checkitemIds);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    //设置检查组关联检查项（操作中间表）
    public void setCheckGroupAndCheckItem(Integer checkGroupId,Integer[] checkitemIds){
        if(checkitemIds != null && checkitemIds.length > 0){
            Map<String,Integer> map = new HashMap<>();
            for(Integer checkItemId:checkitemIds){
                map.put("checkGroupId",checkGroupId);
                map.put("checkItemId",checkItemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }
}
