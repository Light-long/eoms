package com.tx.eoms.dao;

import com.tx.eoms.pojo.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskDao {

    /**
     * 查询任务管理界面任务列表
     */
    List<Map<String, Object>> searchTaskAdminListByPage(Map<String, Object> condition);

    long searchTaskAdminListCount(Map<String, Object> condition);

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
    List<Map<String, Object>> searchMyTaskList(Map<String, Object> condition);

    long searchMyTaskListCount(Map<String, Object> condition);

    /**
     * 更新任务进度
     */
    int updateTaskDegree(Map<String, Object> params);

    /**
     * 查询所有新任务，和进行中的任务的结束时间
     * 如果过了结束时间还有有完成，则改为已过期
     */
    List<Map<String, Object>> searchTaskEndTime();

    /**
     * 把任务状态改为已过期
     */
    int updateStatusToExpire(Integer id);
}