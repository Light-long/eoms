package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.meeting.*;
import com.tx.eoms.controller.workflow.ReceiveNotifyForm;
import com.tx.eoms.pojo.Meeting;
import com.tx.eoms.service.MeetingService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.*;

@RestController
@RequestMapping("/meeting")
@Tag(name = "MeetingController", description = "会议Web接口")
@Slf4j
public class MeetingController {

    @Resource
    private MeetingService meetingService;

    @PostMapping("/searchOfflineMeetingByPage")
    @Operation(summary = "查询线下会议分页数据")
    @SaCheckLogin
    public CommonResult searchOfflineMeetingByPage(@Valid @RequestBody SearchOfflineMeetingByPageForm form) {
        int start = (form.getPage() - 1) * form.getLength();
        Map<String, Object> condition = new HashMap<>();
        condition.put("date", form.getDate());
        condition.put("mold", form.getMold());
        condition.put("userId", StpUtil.getLoginIdAsInt());
        condition.put("start", start);
        condition.put("length", form.getLength());
        PageUtils pageUtils = meetingService.searchOfflineMeetingByPage(condition);
        return CommonResult.ok().put("page", pageUtils);
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

    @PostMapping("/searchOfflineMeetingInWeek")
    @Operation(summary = "查询某个会议室一周的会议")
    @SaCheckLogin
    public CommonResult searchOfflineMeetingInWeek(@Valid @RequestBody SearchOfflineMeetingInWeekForm form) {
        String date = form.getDate();
        DateTime startDate, endDate;
        if (date != null && date.length() > 0) {
            // 从date开始，生成七天日期
            startDate = DateUtil.parseDate(date);
            endDate = startDate.offsetNew(DateField.DAY_OF_WEEK, 6);
        } else {
            // 查询当前日期，生成本周七天
            startDate = DateUtil.beginOfWeek(new Date());
            endDate = DateUtil.endOfWeek(new Date());
        }
        Map<String, Object> params = new HashMap<>();
        params.put("place", form.getName());
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("mold", form.getMold());
        params.put("userId", StpUtil.getLoginIdAsInt());
        List<Map<String, Object>> offlineMeetingInWeek = meetingService.searchOfflineMeetingInWeek(params);
        // 生成周日历水平表头的文字
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_WEEK);
        List<JSONObject> days = new ArrayList<>();
        range.forEach(one -> {
            JSONObject json = new JSONObject();
            json.set("date", one.toString("MM/dd"));
            json.set("day", one.dayOfWeekEnum().toChinese("周"));
            days.add(json);
        });
        return Objects.requireNonNull(CommonResult.ok().put("list", offlineMeetingInWeek)).put("days", days);
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

    @PostMapping("/searchOnlineMeetingByPage")
    @Operation(summary = "查询线上会议分页数据")
    @SaCheckLogin
    public CommonResult searchOnlineMeetingByPage(@Valid @RequestBody SearchOnlineMeetingByPageForm form) {
        int start = (form.getPage() - 1) * form.getLength();
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        params.put("start", start);
        params.put("userId", StpUtil.getLoginIdAsInt());
        PageUtils onlineMeetingPage = meetingService.searchOnlineMeetingByPage(params);
        return CommonResult.ok().put("page", onlineMeetingPage);
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
