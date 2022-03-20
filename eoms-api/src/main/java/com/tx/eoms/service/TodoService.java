package com.tx.eoms.service;

import com.tx.eoms.util.PageUtils;

import java.util.List;
import java.util.Map;

public interface TodoService {

    /**
     * 查询待办列表
     */
    PageUtils searchTodoList(Map<String, Object> params);

    /**
     * 完成待办
     */
    int finishTask(Map<String, Object> params);

    /**
     * 添加待办事项
     */
    int addTodoList(Map<String, Object> params);

    /**
     * 删除待办事项
     */
    int deleteTodoList(Map<String, Object> params);
}
