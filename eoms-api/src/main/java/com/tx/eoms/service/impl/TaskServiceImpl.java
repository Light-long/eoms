package com.tx.eoms.service.impl;

import cn.hutool.core.map.MapUtil;
import com.tx.eoms.dao.TaskDao;
import com.tx.eoms.dao.UserDao;
import com.tx.eoms.pojo.Task;
import com.tx.eoms.service.TaskService;
import com.tx.eoms.task.MailTask;
import com.tx.eoms.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskDao taskDao;

    @Resource
    private MailTask mailTask;

    @Resource
    private UserDao userDao;

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
        // 创建任务记录
        int i = taskDao.publishTask(task);
        // 接收邮件的邮箱
        String email = userDao.searchEmailById(task.getExecutorId());
        Map<String, Object> publisher = userDao.searchUserInfo(task.getPublisherId());
        // 发送邮件
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("有新任务了");
        mailMessage.setText(MapUtil.getStr(publisher, "name") + "给你发布了主题为:" + task.getTheme() + "的任务,"
                + "规定时间为" + task.getStartTime() + "~" + task.getEndTime() + "，请您尽快完成。");
        mailTask.sendMailAsync(mailMessage);
        log.info("已经发送通知");
        return i;
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
