package com.tx.eoms.service;

import com.tx.eoms.pojo.Task;
import com.tx.eoms.util.PageUtils;

import java.util.List;
import java.util.Map;

public interface TaskService {

    /**
     * 查询任务管理界面任务列表
     */
    PageUtils searchTaskAdminListByPage(Map<String, Object> condition);

    /**
     * 发布任务
     */
    int publishTask(Task task);

    /**
     * 发布人取消任务
     */
    int cancelTask(Map<String, Object> params);

    /**
     * 删除任务
     */
    int deleteTask(Integer[] ids);

    /**
     * 查询任务信息
     */
    Map<String, Object> searchTaskInfo(Integer id);

    /**
     * 给任务评分
     */
    int taskRate(Map<String, Object> params);

    /**
     * 查询我的任务列表
     */
    PageUtils searchMyTaskList(Map<String, Object> condition);

    /**
     * 更新任务进度
     */
    int updateTaskDegree(Map<String, Object> params);
}
