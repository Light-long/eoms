package com.tx.eoms.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.dao.MeetingDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Meeting;
import com.tx.eoms.service.MeetingService;
import com.tx.eoms.task.MeetingWorkFlowTask;
import com.tx.eoms.util.PageUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class MeetingServiceImpl implements MeetingService {

    @Resource
    private MeetingDao meetingDao;

    @Resource
    private MeetingWorkFlowTask meetingWorkFlowTask;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 查询线下会议列表
     */
    @Override
    public List<Map<String, Object>> searchOfflineMeetingList(Map<String, Object> params) {
        return meetingDao.searchOfflineMeetingList(params);
    }

    @Override
    public int addMeeting(Meeting meeting) {
        int rows = meetingDao.addMeeting(meeting);
        if (rows != 1) {
            throw new EomsException("会议添加失败");
        }
        meetingWorkFlowTask.startMeetingWorkFlow(meeting.getUuid(), meeting.getCreatorId(), meeting.getTitle(),
                meeting.getDate(), meeting.getStart() + ":00", meeting.getType() == 1 ? "线上会议" : "线下会议");
        return rows;
    }

    /**
     * 查询会议信息
     * @param status 会议的状态
     * @param id 会议的id
     */
    @Override
    public Map<String, Object> searchMeetingInfo(short status, long id) {
        // 判断会议状态
        Map<String, Object> meetingInfo;
        // 进行中|结束
        if (status == 4 || status == 5) {
            meetingInfo = meetingDao.searchCurrentMeetingInfo(id);
        } else {
            meetingInfo = meetingDao.searchMeetingInfo(id);
        }
        return meetingInfo;
    }

    /**
     * 删除会议申请
     */
    @Override
    public int deleteMeetingApplication(Map<String, Object> params) {
        long id = (long) params.get("id");
        String uuid = (String) params.get("uuid");
        String instanceId = (String) params.get("instanceId");
        //查询会议详情，一会儿要判断是否距离会议开始不足20分钟
        Map<String, Object> meeting = meetingDao.searchMeetingById(params);
        if (meeting == null) {
            throw new EomsException("不存在该会议");
        }
        String date = MapUtil.getStr(meeting, "date");
        String start = MapUtil.getStr(meeting, "start");
        int status = MapUtil.getInt(meeting, "status");
        boolean isCreator = Boolean.parseBoolean(MapUtil.getStr(meeting, "isCreator"));
        DateTime dateTime = DateUtil.parse(date + " " + start);
        DateTime now = DateUtil.date();
        // 距离会议开始不足20min不能删除
        if (now.isAfterOrEquals(dateTime.offset(DateField.MINUTE, -20))) {
            throw new EomsException("距离会议开始不足20分钟，不能删除会议");
        }
        // 只能申请人删除该会议
        if (!isCreator) {
            throw new EomsException("只能申请人删除该会议");
        }
        // 待审批和未开始的会议可以删除
        if (status == 1 || status == 3) {
            int rows = meetingDao.deleteMeetingApplication(params);
            if (rows == 1) {
                String reason = (String) params.get("reason");
                // 关闭对应工作流
                meetingWorkFlowTask.deleteMeetingApplication(uuid, instanceId, reason);
            }
            return rows;
        } else {
            throw new EomsException("只能删除待审批和未开始的会议");
        }
    }

    /**
     * 查询线上会议列表
     */
    @Override
    public List<Map<String, Object>> searchOnlineMeetingList(Map<String, Object> params) {
        return meetingDao.searchOnlineMeetingList(params);
    }

    /**
     * 根据uuid获取在线会议roomId
     * 工作流项目会在会议开始前15min生成roomId，缓存在redis
     */
    @Override
    public Long searchRoomIdByUuid(String uuid) {
        if (redisTemplate.hasKey(uuid)) {
            Object roomId = redisTemplate.opsForValue().get(uuid);
            assert roomId != null;
            return Long.parseLong(roomId.toString());
        }
        return null;
    }

    /**
     * 查询在线会议参会人员信息
     * @param params meetingId|userId
     */
    @Override
    public List<Map<String, Object>> searchOnlineMeetingMembers(Map<String, Object> params) {
        return meetingDao.searchOnlineMeetingMembers(params);
    }

    /**
     * 判断是否能签到
     * 会议开始前15min，会议开始后15min之间
     */
    @Override
    public boolean searchCanCheckinMeeting(Map<String, Object> params) {
        long count = meetingDao.searchCanCheckinMeeting(params);
        return count == 1;
    }

    /**
     * 更新参会人--签到
     */
    @Override
    public int updateMeetingPresent(Map<String, Object> params) {
        return meetingDao.updateMeetingPresent(params);
    }

}
