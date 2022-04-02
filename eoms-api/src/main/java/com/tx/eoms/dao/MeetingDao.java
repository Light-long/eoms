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
    Map<String, Object> searchMeetingById(Map<String, Object> params);

    /**
     * 查询线下会议列表
     */
    List<Map<String, Object>> searchOfflineMeetingList(Map<String, Object> params);

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

    /**
     * 查询未开始的会议
     * @param id 会议id
     * @return 会议信息
     */
    Map<String, Object> searchMeetingInfo(long id);

    /**
     * 查询正在进行中的会议
     * @param id 会议id
     * @return 会议信息+出席会议的人+未出席会议的人
     */
    Map<String, Object> searchCurrentMeetingInfo(long id);

    /**
     * 删除会议申请
     */
    int deleteMeetingApplication(Map<String, Object> params);

    /**
     * 查询线上会议列表
     */
    List<Map<String, Object>> searchOnlineMeetingByPage(Map<String, Object> params);

    /**
     * 查询会议数量
     */
    long searchOnlineMeetingCount(Map<String, Object> params);

    /**
     * 查询在线会议参会人员信息
     * @param params meetingId|userId
     */
    List<Map<String, Object>> searchOnlineMeetingMembers(Map<String, Object> params);

    /**
     * 判断是否能签到
     * 会议开始前15min，会议开始后15min之间
     */
    long searchCanCheckinMeeting(Map<String, Object> params);

    /**
     * 更新参会人--签到
     */
    int updateMeetingPresent(Map<String, Object> params);
}