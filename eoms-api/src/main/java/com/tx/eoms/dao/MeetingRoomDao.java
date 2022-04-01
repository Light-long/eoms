package com.tx.eoms.dao;

import com.tx.eoms.pojo.MeetingRoom;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MeetingRoomDao {

    /**
     * 查询所有会议室
     */
    List<Map<String, Object>> searchAllMeetingRoom();

    /**
     * 根据条件查询会议室列表
     */
    List<Map<String, Object>> searchMeetingRoomByPage(Map<String, Object> condition);

    /**
     * 添加会议室
     */
    int addMeetingRoom(MeetingRoom meetingRoom);

    /**
     * 根据id查找会议室
     * @param id 会议室id
     */
    Map<String, Object> searchById(int id);

    /**
     * 更新会议室
     */
    int updateMeetingRoom(MeetingRoom meetingRoom);

    /**
     * 查找空闲的会议室
     * @param condition 开始结束时间
     */
    List<String> searchFreeMeetingRoom(Map<String, Object> condition);

    /**
     * 判断是否能删除
     * 如果有关联的会议则不能删除
     */
    boolean searchCanDelete(Integer[] ids);

    /**
     * 根据id删除会议室
     */
    int deleteMeetingRoomByIds(Integer[] ids);
}