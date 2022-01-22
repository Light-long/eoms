package com.tx.eoms.service;

import com.tx.eoms.pojo.Meeting;
import com.tx.eoms.util.PageUtils;

import java.util.Map;

public interface MeetingService {

    /**
     * 分页查询线下会议
     */
    PageUtils searchOfflineMeetingByPage(Map<String, Object> condition);

    /**
     * 添加会议
     */
    int addMeeting(Meeting meeting);
}
