package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.tx.eoms.config.init.SystemConstants;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Attendance;
import com.tx.eoms.service.AttendanceService;
import com.tx.eoms.service.UserService;
import com.tx.eoms.util.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/attendance")
@Tag(name = "AttendanceController", description = "考勤打卡Web接口")
@Slf4j
public class AttendanceController {

    @Resource
    private AttendanceService attendanceService;

    @Resource
    private SystemConstants systemConstants;

    @Resource
    private UserService userService;

    @GetMapping("/getAllAttendanceTime")
    @Operation(summary = "查询系统设置的考勤打卡时间")
    @SaCheckLogin
    public CommonResult getAllAttendanceTime() {
        Map<String, Object> attendanceTimeMap = new HashMap<>();
        attendanceTimeMap.put("signInStartTime", systemConstants.signInStartTime);
        attendanceTimeMap.put("signInEndTime", systemConstants.signInEndTime);
        attendanceTimeMap.put("signOutStartTime", systemConstants.signOutStartTime);
        attendanceTimeMap.put("signOutEndTime", systemConstants.signOutEndTime);
        return CommonResult.ok().put("map", attendanceTimeMap);
    }

    @GetMapping("/validCanSignIn")
    @Operation(summary = "检查是否可以签到")
    @SaCheckLogin
    public CommonResult validCanSignIn() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("date", DateUtil.today());
        Map<String, Object> result = attendanceService.validCanSignIn(params);
        return CommonResult.ok().put("result", result);
    }

    @GetMapping("/signIn")
    @Operation(summary = "签到")
    @SaCheckLogin
    public CommonResult signIn() {
        // 判断正常还是迟到，设置status
        DateTime now = DateUtil.date();
        DateTime signInEndTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.signInEndTime);
        int status;
        if (now.isAfter(signInEndTime)) {
            status = 2;
        } else {
            status = 1;
        }
        Attendance attendance = Attendance.builder()
                .userId(StpUtil.getLoginIdAsInt())
                .createTime(now)
                .date(DateUtil.today())
                .status((byte) status)
                .build();
        // 判断是否重复签到
        Map<String, Object> params = new HashMap<>();
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("date", DateUtil.today());
        if (!MapUtil.getBool(attendanceService.validCanSignIn(params), "flag")) {
            return CommonResult.error(MapUtil.getStr(attendanceService.validCanSignIn(params), "message"));
        }
        // 签到
        Integer row = attendanceService.signIn(attendance);
        if (row == 1) {
            return status == 1 ?
                    CommonResult.ok().put("flag", "正常签到")
                    : CommonResult.ok().put("flag", "迟到签到");
        } else {
            return CommonResult.error("签到失败");
        }
    }

    @GetMapping("/searchSignInResult")
    @Operation(summary = "查询该日签到结果")
    @SaCheckLogin
    public CommonResult searchSignInResult() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("date", DateUtil.today());
        Map<String, Object> result = attendanceService.searchSignInResult(params);
        return CommonResult.ok().put("info", result);
    }

    @GetMapping("/validCanSignOut")
    @Operation(summary = "检查是否可以签退")
    @SaCheckLogin
    public CommonResult validCanSignOut() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("date", DateUtil.today());
        Map<String, Object> result = attendanceService.validCanSignOut(params);
        return CommonResult.ok().put("result", result);
    }

    @GetMapping("/signOut")
    @Operation(summary = "签退")
    @SaCheckLogin
    public CommonResult signOut() {
        // 判断是早退，还是正常签退，还是加班
        DateTime now = DateUtil.date();
        DateTime signOutStartTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.signOutStartTime);
        DateTime signOutEndTime = DateUtil.parse(DateUtil.today() + " " + systemConstants.signOutEndTime);
        int status = 0;
        if (now.isBefore(signOutStartTime)) {
            status = 2;
        } else if (now.isBeforeOrEquals(signOutEndTime) && now.isAfterOrEquals(signOutEndTime)) {
            status = 1;
        } else if (now.isAfter(signOutEndTime)) {
            status = 3;
        }
        Attendance attendance = Attendance.builder()
                .userId(StpUtil.getLoginIdAsInt())
                .createTime(now)
                .date(DateUtil.today())
                .status((byte) status)
                .build();
        // 判断是否重复签退
        Map<String, Object> params = new HashMap<>();
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("date", DateUtil.today());
        if (!MapUtil.getBool(attendanceService.validCanSignOut(params), "flag")) {
            return CommonResult.error(MapUtil.getStr(attendanceService.validCanSignOut(params), "message"));
        }
        // 签退
        Integer row = attendanceService.signOut(attendance);
        if (row == 1) {
            return status == 1 ?
                    CommonResult.ok().put("flag", "正常签退") : status == 2 ?
                    CommonResult.ok().put("flag", "早退") : CommonResult.ok().put("flag", "加班");
        } else {
            return CommonResult.error("签退失败");
        }
    }

    @GetMapping("/searchSignOutResult")
    @Operation(summary = "查询该日签退结果")
    @SaCheckLogin
    public CommonResult searchSignOutResult() {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("date", DateUtil.today());
        Map<String, Object> result = attendanceService.searchSignOutResult(params);
        return CommonResult.ok().put("info", result);
    }

    @GetMapping("/searchAttendanceInMonth")
    @Operation(summary = "查询本月的考勤统计")
    @SaCheckLogin
    public CommonResult searchAttendanceInMonth() {
        // 当前年月
        Date date = new Date();
        int year = DateUtil.year(date);
        int thisMonth = DateUtil.month(date) + 1;
        // 将月份转换成两位数
        String month = thisMonth < 10 ? "0" + thisMonth : thisMonth+"";
        System.out.println(year + "-"+ month);
        // 查询入职日期
        DateTime hiredate = DateUtil.parse(userService.searchHiredate(StpUtil.getLoginIdAsInt()));
        // 查询这个月的第一天
        DateTime startDate = DateUtil.parse(year + "-" + month + "-01");
        // 如果查询的这个月，早于入职当月，抛异常
        if (startDate.isBefore(DateUtil.beginOfMonth(hiredate))) {
            throw new EomsException("还没入职，没有签到数据");
        }
        // 从入职月的入职日期开始算
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }
        // 查询今天的前一天
        DateTime endDate = DateUtil.endOfMonth(startDate);
        if (DateUtil.date().isBefore(endDate)) {
            endDate = DateUtil.date().offset(DateField.DAY_OF_MONTH, -1);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("startDate", DateUtil.formatDate(startDate));
        params.put("endDate", DateUtil.formatDate(endDate));
        Map<String, Object> attendanceInfo =  attendanceService.searchAttendanceInMonth(params);
        long absence = DateUtil.between(startDate, endDate, DateUnit.DAY) + 1
                - MapUtil.getInt(attendanceInfo, "successSignIn")
                - MapUtil.getInt(attendanceInfo, "late");
        attendanceInfo.put("absence", absence);
        return CommonResult.ok().put("attendanceInfo", attendanceInfo);
    }
}
