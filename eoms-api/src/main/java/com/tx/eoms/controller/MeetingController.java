package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.config.tencent.TrtcUtil;
import com.tx.eoms.controller.meeting.*;
import com.tx.eoms.controller.workflow.ReceiveNotifyForm;
import com.tx.eoms.pojo.Meeting;
import com.tx.eoms.service.MeetingService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/meeting")
@Tag(name = "MeetingController", description = "会议Web接口")
@Slf4j
public class MeetingController {

    @Value("${tencent.trtc.appId}")
    private int appId;

    @Resource
    private TrtcUtil trtcUtil;

    @Resource
    private MeetingService meetingService;

    @PostMapping("/searchOfflineMeetingList")
    @Operation(summary = "查询线下会议列表")
    @SaCheckLogin
    public CommonResult searchOfflineMeetingList(@Valid @RequestBody SearchOfflineMeetingListForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", form.getDate());
        params.put("mold", form.getMold());
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("roomName", form.getRoomName());
        List<Map<String, Object>> offlineMeetingList = meetingService.searchOfflineMeetingList(params);
        return CommonResult.ok().put("list", offlineMeetingList);
    }

    @PostMapping("/addMeeting")
    @Operation(summary = "添加会议")
    @SaCheckLogin
    public CommonResult addMeeting(@Valid @RequestBody AddMeetingForm form) {
        DateTime start = DateUtil.parse(form.getDate() + " " + form.getStart());
        DateTime end = DateUtil.parse(form.getDate() + " " + form.getEnd());
        if (start.isAfterOrEquals(end)) {
            return CommonResult.error("会议结束时间必须大于开始时间");
        } else if (new DateTime().isAfterOrEquals(start)) {
            return CommonResult.error("会议开始时间不能早于当前时间");
        }
        Meeting meeting = JSONUtil.parse(form).toBean(Meeting.class);
        meeting.setUuid(UUID.randomUUID().toString(true));
        meeting.setCreatorId(StpUtil.getLoginIdAsInt());
        meeting.setStatus((short) 1);
        int rows = meetingService.addMeeting(meeting);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchMeetingInfo")
    @Operation(summary = "查询会议信息")
    @SaCheckLogin
    public CommonResult searchMeetingInfo(@Valid @RequestBody SearchMeetingInfoForm form) {
        Map<String, Object> meetingInfo = meetingService.searchMeetingInfo(form.getStatus(), form.getId());
        return CommonResult.ok().put("meetingInfo", meetingInfo);
    }

    @PostMapping("/deleteMeetingApplication")
    @Operation(summary = "删除会议申请")
    @SaCheckLogin
    public CommonResult deleteMeetingApplication(@Valid @RequestBody DeleteMeetingApplicationForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("creatorId", StpUtil.getLoginIdAsInt());
        int rows = meetingService.deleteMeetingApplication(params);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchOnlineMeetingList")
    @Operation(summary = "查询线上会议列表")
    @SaCheckLogin
    public CommonResult searchOnlineMeetingList(@Valid @RequestBody SearchOnlineMeetingListForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        params.put("userId", StpUtil.getLoginIdAsInt());
        List<Map<String, Object>> onlineMeetingList = meetingService.searchOnlineMeetingList(params);
        return CommonResult.ok().put("list", onlineMeetingList);
    }

    @GetMapping("/searchMyUserSig")
    @Operation(summary = "获取用户签名")
    @SaCheckLogin
    public CommonResult searchMyUserSig() {
        int userId = StpUtil.getLoginIdAsInt();
        String userSig = trtcUtil.getUserSig(userId + "");
        return CommonResult.ok().put("userSig", userSig).put("userId", userId).put("appId", appId);
    }

    @PostMapping("/searchRoomIdByUUID")
    @Operation(summary = "查询会议房间RoomID")
    @SaCheckLogin
    public CommonResult searchRoomIdByUUID(@Valid @RequestBody SearchRoomIdByUUIDForm form) {
        Long roomId = meetingService.searchRoomIdByUuid(form.getUuid());
        return CommonResult.ok().put("roomId", roomId);
    }

    @PostMapping("/searchOnlineMeetingMembers")
    @Operation(summary = "查询参加线上会议人员信息-照片墙")
    @SaCheckLogin
    public CommonResult searchOnlineMeetingMembers(@Valid @RequestBody SearchOnlineMeetingMembersForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        params.put("userId", StpUtil.getLoginIdAsInt());
        List<Map<String, Object>> membersInfo = meetingService.searchOnlineMeetingMembers(params);
        return CommonResult.ok().put("list", membersInfo);
    }

    @PostMapping("/searchCanCheckin")
    @Operation(summary = "判断是否能签到")
    @SaCheckLogin
    public CommonResult searchCanCheckin(@Valid @RequestBody UpdateMeetingPresentForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("meetingId", form.getMeetingId());
        params.put("userId", StpUtil.getLoginIdAsInt());
        // 判断能不能签到
        boolean flag = meetingService.searchCanCheckinMeeting(params);
        return CommonResult.ok().put("flag", flag);
    }

    @PostMapping("/updateMeetingPresent")
    @Operation(summary = "执行会议签到")
    @SaCheckLogin
    public CommonResult updateMeetingPresent(@Valid @RequestBody UpdateMeetingPresentForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("meetingId", form.getMeetingId());
        params.put("userId", StpUtil.getLoginIdAsInt());
        // 判断能不能签到
        boolean canCheckin = meetingService.searchCanCheckinMeeting(params);
        if (canCheckin) {
            int rows = meetingService.updateMeetingPresent(params);
            return CommonResult.ok().put("rows", rows);
        }
        return CommonResult.ok().put("rows", 0);
    }

    @PostMapping("/receiveNotify")
    @Operation(summary = "接收工作流通知")
    public CommonResult receiveNotify(@Valid @RequestBody ReceiveNotifyForm form) {
        if ("同意".equals(form.getResult())) {
            // 发送邮件
            log.info(form.getUuid() + "的审批通过");
        } else {
            log.warn(form.getUuid() + "的审批不通过");
        }
        return CommonResult.ok();
    }
}
