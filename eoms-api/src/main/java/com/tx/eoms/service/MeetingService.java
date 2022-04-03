package com.tx.eoms.service;

import com.tx.eoms.pojo.Meeting;
import com.tx.eoms.util.PageUtils;

import java.util.List;
import java.util.Map;

public interface MeetingService {

    /**
     * 查询线下会议列表
     */
    List<Map<String, Object>> searchOfflineMeetingList(Map<String, Object> params);

    /**
     * 添加会议
     */
    int addMeeting(Meeting meeting);

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
    List<Map<String, Object>> searchOnlineMeetingList(Map<String, Object> params);

    /**
     * 根据uuid获取在线会议roomId
     * 工作流项目会在会议开始前15min生成roomId，缓存在redis
     */
    Long searchRoomIdByUuid(String uuid);

    /**
     * 查询在线会议参会人员信息
     * @param params meetingId|userId
     */
    List<Map<String, Object>> searchOnlineMeetingMembers(Map<String, Object> params);

    /**
     * 判断是否能签到
     * 会议开始前15min，会议开始后15min之间
     */
    boolean searchCanCheckinMeeting(Map<String, Object> params);

    /**
     * 更新参会人--签到
     */
    int updateMeetingPresent(Map<String, Object> params);
}
