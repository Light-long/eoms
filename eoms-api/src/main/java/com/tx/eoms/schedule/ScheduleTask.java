package com.tx.eoms.schedule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.date.DateTime;
import com.tx.eoms.dao.TodoDao;
import com.tx.eoms.dao.UserDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.task.MailTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    @Scheduled(cron = "0 0/1 * * * ?")
    public void autoExpireTodoList() {
        // 查询出所有的结束时间
        List<Map<String, Object>> endTimeList = todoDao.searchAllEndTime();
        // 现在的时间
        DateTime now = new DateTime();
        // 判断是否超过截止时间
        for (Map<String, Object> task : endTimeList) {
            DateTime endTime = DateUtil.parse(MapUtil.getStr(task, "end"));
            Integer status = MapUtil.getInt(task, "status");
            // 如果超过截止时间，并且装填还是待完成，则自动修改为 已过期
            if (now.isAfterOrEquals(endTime) && status == 1) {
                todoDao.updateStatusById(MapUtil.getInt(task, "id"));
                log.info("修改状态成功");
            } else {
                log.info("暂无需要修改的待办");
            }
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void sendStartEmail() {
        // 查询所有的开始时间
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
}
