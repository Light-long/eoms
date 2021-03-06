package com.tx.eoms.schedule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.date.DateTime;
import com.tx.eoms.dao.AttendanceDao;
import com.tx.eoms.dao.TaskDao;
import com.tx.eoms.dao.TodoDao;
import com.tx.eoms.dao.UserDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Attendance;
import com.tx.eoms.task.MailTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ScheduleTask {

    @Resource
    private TodoDao todoDao;

    @Resource
    private UserDao userDao;

    @Resource
    private MailTask mailTask;

    @Resource
    private AttendanceDao attendanceDao;

    @Resource
    private TaskDao taskDao;

    /**
     * 待办过期
     * 没分钟执行一次
     * 判断如果有待办过期了，改变待办的状态
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void autoExpireTodoList() {
        // 查询出所有待完成待办结束时间
        List<Map<String, Object>> endTimeList = todoDao.searchAllEndTime();
        // 现在的时间
        DateTime now = new DateTime();
        // 判断是否超过截止时间
        for (Map<String, Object> task : endTimeList) {
            DateTime endTime = DateUtil.parse(MapUtil.getStr(task, "end"));
            // 如果超过截止时间，并且装填还是待完成，则自动修改为 已过期
            if (now.isAfterOrEquals(endTime)) {
                todoDao.updateStatusById(MapUtil.getInt(task, "id"));
                log.info("修改待办状态成功");
            } else {
                log.info("暂无需要修改的待办");
            }
        }
    }

    /**
     * 没分钟执行一次
     * 判断如果有待办开始了，则给该用户发送一封邮件
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void sendStartEmail() {
        // 查询所有待完成待办的开始时间
        List<Map<String, Object>> startTimeList = todoDao.searchAllStartTime();
        // 现在的时间
        DateTime now = new DateTime();
        // 判断是否已经开始
        for (Map<String, Object> task : startTimeList) {
            DateTime start = DateUtil.parse(MapUtil.getStr(task, "start"));
            Integer isNotify = MapUtil.getInt(task, "isNotify");
            // 开始后，只通知一次
            if (now.isAfterOrEquals(start) && isNotify == 0) {
                // 获取该用户的信息
                Integer userId = MapUtil.getInt(task, "userId");
                String title = MapUtil.getStr(task, "title");
                // 接收邮件的邮箱
                String email = userDao.searchEmailById(userId);
                // 发送邮件
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(email);
                mailMessage.setSubject("您的待办：" + title + "，开始了");
                mailMessage.setText("您的待办：" + title + "已于" + start + "开始了，请您尽快完成");
                mailTask.sendMailAsync(mailMessage);
                log.info("已经发送通知");
                // 修改通知状态
                int flag = todoDao.updateNotifyStatus(MapUtil.getInt(task, "id"));
                if (flag != 1) {
                    throw new EomsException("修改通知状态异常");
                }
            } else {
                log.info("暂无开始的待办");
            }
        }
    }

    /**
     * 每天晚上23:59分执行
     * 如果签到了，没有签退 ，则生成一条 早退的考勤
     */
    @Scheduled(cron = "0 59 23 * * ?")
    public void addLeaveEarlyAttendance() {
        // 查询今天所有的签到用户id
        Map<String, Object> params = new HashMap<>();
        params.put("date", DateUtil.today());
        List<Integer> ids = attendanceDao.searchAllSignInUserByDate(params);
        // 判断这些签到有没有签退
        for (Integer id : ids) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", DateUtil.today());
            map.put("userId", id);
            // 没有签退，则添加早退的考勤
            if (attendanceDao.isAlreadySignOut(map) == null) {
                // 生成 早退的考勤
                Attendance attendance = Attendance.builder()
                        .userId(id)
                        .createTime(DateUtil.date())
                        .date(DateUtil.today())
                        .status((byte) 2)
                        .build();
                Integer row = attendanceDao.signOut(attendance);
                if (row == 1) {
                    log.info("给id为" + id + "的用户生成了早退的考勤");
                } else {
                    log.error("给id为" + id + "的用户生成了早退的考勤失败");
                }
            } else {
                log.info("id为" + id + "的用户已经完成了签到和签退");
            }
        }
    }

    /**
     * 自动过期任务
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void autoExpireTask() {
        // 查询出新任务，进行中的结束时间
        List<Map<String, Object>> endTimeList = taskDao.searchTaskEndTime();
        // 现在的时间
        DateTime now = new DateTime();
        // 判断是否超过截止时间
        for (Map<String, Object> task : endTimeList) {
            DateTime endTime = DateUtil.parse(MapUtil.getStr(task, "end"));
            // 如果超过截止时间则自动修改为 已过期
            if (now.isAfterOrEquals(endTime)) {
                taskDao.updateStatusToExpire(MapUtil.getInt(task, "id"));
                log.info("修改任务状态成功");
            } else {
                log.info("暂无需要修改状态的任务");
            }
        }
    }
}
