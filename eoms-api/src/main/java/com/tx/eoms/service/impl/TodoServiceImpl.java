package com.tx.eoms.service.impl;

import cn.hutool.core.map.MapUtil;
import com.tx.eoms.dao.TodoDao;
import com.tx.eoms.service.TodoService;
import com.tx.eoms.util.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TodoServiceImpl implements TodoService {

    @Resource
    private TodoDao todoDao;

    @Override
    public PageUtils searchTodoList(Map<String, Object> params) {
        List<Map<String, Object>> todoList = todoDao.searchTodoList(params);
        long count = todoDao.searchTodoListCount(params);
        int start = MapUtil.getInt(params, "start");
        int length = MapUtil.getInt(params, "length");
        return new PageUtils(todoList, count, start, length);
    }

    /**
     * 完成待办
     */
    @Override
    public int finishTask(Map<String, Object> params) {
        return todoDao.finishTask(params);
    }

    /**
     * 添加待办事项
     */
    @Override
    public int addTodoList(Map<String, Object> params) {
        return todoDao.addTodoList(params);
    }

    /**
     * 删除待办事项
     */
    @Override
    public int deleteTodoList(Map<String, Object> params) {
        return todoDao.deleteTodoList(params);
    }
}
