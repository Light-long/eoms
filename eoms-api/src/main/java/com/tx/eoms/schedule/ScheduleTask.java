package com.tx.eoms.schedule;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.date.DateTime;
import com.tx.eoms.dao.TodoDao;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleTask {

    @Resource
    private TodoDao todoDao;

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
                System.out.println("修改状态成功");
            } else {
                System.out.println("暂无需要修改的待办");
            }
        }
    }
}
