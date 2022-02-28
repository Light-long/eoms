package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.leave.AddLeaveForm;
import com.tx.eoms.controller.leave.DeleteLeaveByIdForm;
import com.tx.eoms.controller.leave.SearchLeaveByPageForm;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Leave;
import com.tx.eoms.service.LeaveService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/leave")
@Tag(name = "LeaveController", description = "员工请假Web接口")
public class LeaveController {

    @Resource
    private LeaveService leaveService;

    @PostMapping("/searchLeaveByPage")
    @Operation(summary = "查询请假分页数据")
    @SaCheckLogin
    public CommonResult searchLeaveByPage(@Valid @RequestBody SearchLeaveByPageForm form) {
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        int start = (form.getPage() - 1) * form.getLength();
        condition.put("start", start);
        condition.put("myId", StpUtil.getLoginIdAsInt());
        // 没有LEAVE：SELECT 或者 ROOT 权限，只能查到自己的请假数据
        if (!(StpUtil.hasPermission("ROOT") || StpUtil.hasPermission("LEAVE:SELECT"))) {
            condition.put("userId", StpUtil.getLoginIdAsInt());
        }
        PageUtils page = leaveService.searchLeaveByPage(condition);
        return CommonResult.ok().put("page", page);
    }

    @PostMapping("/addLeave")
    @Operation(summary = "添加请假记录")
    @SaCheckLogin
    public CommonResult addLeave(@Valid @RequestBody AddLeaveForm form) {
        // 判断请假开始时间和结束时间是否合理
        DateTime start = DateUtil.parse(form.getStart());
        DateTime end = DateUtil.parse(form.getEnd());
        if (start.isAfterOrEquals(end)) {
            return CommonResult.error("请假结束时间必须大于开始时间");
        }
        // 判断请假的时间是否冲突（请假记录之间有交集）
        Map<String, Object> params = new HashMap<>();
        params.put("userId", StpUtil.getLoginIdAsInt());
        params.put("start", form.getStart());
        params.put("end", form.getEnd());
        boolean isTimeConflict = leaveService.searchConflict(params);
        if (isTimeConflict) {
            return CommonResult.error("请假时间段出现重复");
        }
        // 计算请假天数
        long hours = start.between(end, DateUnit.HOUR);
        String days = new BigDecimal(hours).divide(new BigDecimal(24), 1, RoundingMode.CEILING).toString();
        if (days.contains(".0")) {
            days = days.replace(".0", "0");
        }
        if (days.equals("0")) {
            days = "0.1";
        }
        // 添加请假记录
        Leave leave = JSONUtil.parse(form).toBean(Leave.class);
        leave.setUserId(StpUtil.getLoginIdAsInt());
        leave.setDays(days);
        int rows = leaveService.AddLeave(leave);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteLeaveById")
    @Operation(summary = "删除请假记录")
    @SaCheckLogin
    public CommonResult deleteLeaveById(@Valid @RequestBody DeleteLeaveByIdForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", form.getId());
        params.put("userId", StpUtil.getLoginIdAsInt());
        int rows = leaveService.deleteLeaveById(params);
        return CommonResult.ok().put("rows", rows);
    }
}
