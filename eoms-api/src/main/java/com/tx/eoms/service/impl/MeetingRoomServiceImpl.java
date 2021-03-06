package com.tx.eoms.service.impl;

import com.tx.eoms.dao.MeetingRoomDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.MeetingRoom;
import com.tx.eoms.service.MeetingRoomService;
import com.tx.eoms.util.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class MeetingRoomServiceImpl implements MeetingRoomService {

    @Resource
    private MeetingRoomDao meetingRoomDao;

    /**
     * 查询所有会议室
     */
    @Override
    public List<Map<String, Object>> searchAllMeetingRoom() {
        return meetingRoomDao.searchAllMeetingRoom();
    }

    @Override
    public List<Map<String, Object>> searchMeetingRoomByPage(Map<String, Object> condition) {
        return meetingRoomDao.searchMeetingRoomByPage(condition);
    }

    /**
     * 添加会议室
     */
    @Override
    public int addMeetingRoom(MeetingRoom meetingRoom) {
        return meetingRoomDao.addMeetingRoom(meetingRoom);
    }

    /**
     * 根据id查找会议室
     * @param id 会议室id
     */
    @Override
    public Map<String, Object> searchById(int id) {
        return meetingRoomDao.searchById(id);
    }

    /**
     * 更新会议室
     */
    @Override
    public int updateMeetingRoom(MeetingRoom meetingRoom) {
        return meetingRoomDao.updateMeetingRoom(meetingRoom);
    }

    /**
     * 查找空闲的会议室
     * @param condition 开始结束时间
     */
    @Override
    public List<String> searchFreeMeetingRoom(Map<String, Object> condition) {
        return meetingRoomDao.searchFreeMeetingRoom(condition);
    }

    /**
     * 根据id删除会议室
     */
    @Override
    public int deleteMeetingRoomByIds(Integer[] ids) {
        if (!meetingRoomDao.searchCanDelete(ids)) {
            throw new EomsException("无法删除关联会议的会议室");
        }
        return meetingRoomDao.deleteMeetingRoomByIds(ids);
    }
}
