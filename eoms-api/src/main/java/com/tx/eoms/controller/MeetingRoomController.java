package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.meetingroom.*;
import com.tx.eoms.pojo.MeetingRoom;
import com.tx.eoms.service.MeetingRoomService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/meetingRoom")
@Tag(name = "MeetingRoomController", description = "会议室Web接口")
public class MeetingRoomController {

    @Resource
    private MeetingRoomService meetingRoomService;

    @GetMapping("/searchAllMeetingRoom")
    @Operation(summary = "查询所有会议室")
    @SaCheckLogin
    public CommonResult searchAllMeetingRoom() {
        List<Map<String, Object>> roomList = meetingRoomService.searchAllMeetingRoom();
        return CommonResult.ok().put("list", roomList);
    }

    @PostMapping("/searchMeetingRoomByPage")
    @Operation(summary = "查询会议室列表")
    @SaCheckLogin
    public CommonResult searchMeetingRoomByPage(@Valid @RequestBody SearchMeetingRoomByPageForm form) {
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        int start = (form.getPage() - 1) * form.getLength();
        condition.put("start", start);
        PageUtils roomPage = meetingRoomService.searchMeetingRoomByPage(condition);
        return CommonResult.ok().put("page", roomPage);
    }

    @PostMapping("/addMeetingRoom")
    @Operation(summary = "添加会议室")
    @SaCheckPermission(value = {"ROOT", "MEETING_ROOM:INSERT"}, mode = SaMode.OR)
    public CommonResult addMeetingRoom(@Valid @RequestBody AddMeetingRoomForm form) {
        MeetingRoom meetingRoom = JSONUtil.parse(form).toBean(MeetingRoom.class);
        int rows = meetingRoomService.addMeetingRoom(meetingRoom);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @Operation(summary = "根据ID查找会议室")
    @SaCheckPermission(value = {"ROOT", "MEETING_ROOM:SELECT"}, mode = SaMode.OR)
    public CommonResult searchById(@Valid @RequestBody SearchMeetingRoomByIdForm form) {
        Map<String, Object> meetingRoom = meetingRoomService.searchById(form.getId());
        return CommonResult.ok().put("meetingRoom", meetingRoom);
    }

    @PostMapping("/updateMeetingRoom")
    @Operation(summary = "修改会议室")
    @SaCheckPermission(value = {"ROOT", "MEETING_ROOM:UPDATE"}, mode = SaMode.OR)
    public CommonResult updateMeetingRoom(@Valid @RequestBody UpdateMeetingRoomForm form) {
        MeetingRoom meetingRoom = JSONUtil.parse(form).toBean(MeetingRoom.class);
        int rows = meetingRoomService.updateMeetingRoom(meetingRoom);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchFreeMeetingRoom")
    @Operation(summary = "查询空闲会议室")
    @SaCheckLogin
    public CommonResult searchFreeMeetingRoom(@Valid @RequestBody SearchFreeMeetingRoomForm form) {
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        List<String> roomList = meetingRoomService.searchFreeMeetingRoom(condition);
        return CommonResult.ok().put("list", roomList);
    }

    @PostMapping("/deleteMeetingRoomByIds")
    @Operation(summary = "删除会议室")
    @SaCheckPermission(value = {"ROOT", "MEETING_ROOM:DELETE"}, mode = SaMode.OR)
    public CommonResult deleteMeetingRoomByIds(@Valid @RequestBody DeleteMeetingRoomByIdsForm form) {
        int rows = meetingRoomService.deleteMeetingRoomByIds(form.getIds());
        return CommonResult.ok().put("rows", rows);
    }

}
