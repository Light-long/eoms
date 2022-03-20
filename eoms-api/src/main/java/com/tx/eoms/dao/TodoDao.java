package com.tx.eoms.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TodoDao {

    /**
     * 查询待办列表
     */
    List<Map<String, Object>> searchTodoList(Map<String, Object> params);

    /**
     * 待办事项数量
     */
    long searchTodoListCount(Map<String, Object> params);

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

    /**
     * 查询所有待办事项的结束时间
     */
    List<Map<String, Object>> searchAllEndTime();

    /**
     * 过了截止时间的时间自动过期（状态值修改为2）
     */
    int updateStatusById(int id);
}