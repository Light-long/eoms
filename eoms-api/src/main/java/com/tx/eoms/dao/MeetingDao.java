package com.tx.eoms.dao;

import com.tx.eoms.pojo.Meeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MeetingDao {

    /**
     * 查询会议成员是否来自同一部门
     */
    boolean searchMeetingMembersInSameDept(String uuid);

    /**
     * 根据会议id查询会议信息
     */
    Map<String, Object> searchMeetingById(long id);

    /**
     * 分页查询线下会议
     */
    List<Map<String, Object>> searchOfflineMeetingByPage(Map<String, Object> condition);

    /**
     * 线下会议条数
     */
    long searchOfflineMeetingCount(Map<String, Object> condition);

    /**
     * 更新会议的instanceId
     * @param params instanceId|uuid
     */
    int updateMeetingInstanceId(Map<String, Object> params);

    /**
     * 添加会议
     */
    int addMeeting(Meeting meeting);
}