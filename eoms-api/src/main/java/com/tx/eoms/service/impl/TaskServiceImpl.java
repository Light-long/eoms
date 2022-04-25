package com.tx.eoms.service.impl;

import cn.hutool.core.map.MapUtil;
import com.tx.eoms.dao.TaskDao;
import com.tx.eoms.pojo.Task;
import com.tx.eoms.service.TaskService;
import com.tx.eoms.util.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskDao taskDao;

    /**
     * 查询任务管理界面任务列表
     */
    @Override
    public PageUtils searchTaskAdminListByPage(Map<String, Object> condition) {
        List<Map<String, Object>> taskList = taskDao.searchTaskAdminListByPage(condition);
        long count = taskDao.searchTaskAdminListCount(condition);
        int start = MapUtil.getInt(condition, "start");
        int length = MapUtil.getInt(condition, "length");
        return new PageUtils(taskList, count, start, length);
    }

    /**
     * 发布任务
     */
    @Override
    public int publishTask(Task task) {
        return taskDao.publishTask(task);
    }

    /**
     * 发布人取消任务
     */
    @Override
    public int cancelTask(Map<String, Object> params) {
        return taskDao.cancelTask(params);
    }

    /**
     * 删除任务
     */
    @Override
    public int deleteTask(Integer[] ids) {
        return taskDao.deleteTask(ids);
    }

    /**
     * 查询任务信息
     */
    @Override
    public Map<String, Object> searchTaskInfo(Integer id) {
        return taskDao.searchTaskInfo(id);
    }

    /**
     * 给任务评分
     */
    @Override
    public int taskRate(Map<String, Object> params) {
        return taskDao.taskRate(params);
    }

    /**
     * 查询我的任务列表
     */
    @Override
    public PageUtils searchMyTaskList(Map<String, Object> condition) {
        List<Map<String, Object>> taskList = taskDao.searchMyTaskList(condition);
        long count = taskDao.searchMyTaskListCount(condition);
        int start = MapUtil.getInt(condition, "start");
        int length = MapUtil.getInt(condition, "length");
        return new PageUtils(taskList, count, start, length);
    }

    /**
     * 更新任务进度
     */
    @Override
    public int updateTaskDegree(Map<String, Object> params) {
        return taskDao.updateTaskDegree(params);
    }
}
