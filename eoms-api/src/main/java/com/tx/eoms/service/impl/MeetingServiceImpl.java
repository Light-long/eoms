package com.tx.eoms.service.impl;

import cn.hutool.json.JSONUtil;
import com.tx.eoms.dao.MeetingDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Meeting;
import com.tx.eoms.service.MeetingService;
import com.tx.eoms.task.MeetingWorkFlowTask;
import com.tx.eoms.util.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class MeetingServiceImpl implements MeetingService {

    @Resource
    private MeetingDao meetingDao;

    @Resource
    private MeetingWorkFlowTask meetingWorkFlowTask;

    @Override
    public PageUtils searchOfflineMeetingByPage(Map<String, Object> condition) {
        List<Map<String, Object>> offlineMeetingList = meetingDao.searchOfflineMeetingByPage(condition);
        long offlineMeetingCount = meetingDao.searchOfflineMeetingCount(condition);
        int start = (int) condition.get("start");
        int length = (int) condition.get("length");
        // 将meeting字段转换为json数组格式
        offlineMeetingList.forEach(offlineMeeting -> {
            String meeting = (String) offlineMeeting.get("meeting");
            if (meeting != null && meeting.length() > 0) {
                offlineMeeting.replace("meeting", JSONUtil.parseArray(meeting));
            }
        });
        return new PageUtils(offlineMeetingList, offlineMeetingCount, start, length);
    }

    @Override
    public int addMeeting(Meeting meeting) {
        int rows = meetingDao.addMeeting(meeting);
        if (rows != 1) {
            throw new EomsException("会议添加失败");
        }
        meetingWorkFlowTask.startMeetingWorkFlow(meeting.getUuid(), meeting.getCreatorId(), meeting.getTitle(),
                meeting.getDate(), meeting.getStart() + ":00", "线下会议");
        return rows;
    }

}
