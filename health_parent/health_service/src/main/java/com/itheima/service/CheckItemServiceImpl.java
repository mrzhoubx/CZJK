package com.itheima.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.pojo.CheckItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 检查项服务
 */
@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {
    @Autowired
    private CheckItemDao checkItemDao;

    //新增
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    //分页查询
    public PageResult pageQuery(Integer currentPage, Integer pageSize, String queryString) {
        //调用分页助手，指定分页条件，在执行SQL之前进行拦截，处理SQL（加入分页关键字）
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckItem> page = checkItemDao.pageQuery(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //根据id删除检查项
    public void deleteById(Integer id) {
        //判断当前检查项是否被引用
        long count = checkItemDao.findCountByCheckItemId(id);
        if(count > 0){
            //引进被引用，不能删除
            throw new RuntimeException(MessageConstant.CHECKITEM_IS_ASSOCATION);
        }
        checkItemDao.deleteById(id);
    }

    //根据id查询
    public CheckItem findById(Integer id) {
        return checkItemDao.findById(id);
    }

    //根据id编辑检查项信息
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }

    //根据检查组id查询其关联的检查项id
    public Integer[] findCheckItemIdsByCheckGroupId(Integer checkGroupId) {
        return checkItemDao.findCheckItemIdsByCheckGroupId(checkGroupId);
    }
}
