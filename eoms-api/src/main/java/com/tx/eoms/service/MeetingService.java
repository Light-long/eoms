package com.tx.eoms.service;

import com.tx.eoms.pojo.Meeting;
import com.tx.eoms.util.PageUtils;

import java.util.List;
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

    /**
     * 查询一周的现在会议--日历图
     */
    List<Map<String, Object>> searchOfflineMeetingInWeek(Map<String, Object> params);

    /**
     * 查询会议信息
     * @param status 会议的状态
     * @param id 会议的id
     */
    Map<String, Object> searchMeetingInfo(short status, long id);

    /**
     * 删除会议申请
     */
    int deleteMeetingApplication(Map<String, Object> params);

    /**
     * 查询线上会议列表
     */
    PageUtils searchOnlineMeetingByPage(Map<String, Object> params);
}
