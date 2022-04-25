package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.task.*;
import com.tx.eoms.pojo.Task;
import com.tx.eoms.service.TaskService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/task")
@Tag(name = "TaskController", description = "任务Web接口")
public class TaskController {

    @Resource
    private TaskService taskService;

    @PostMapping("/searchTaskAdminListByPage")
    @Operation(summary = "查询任务管理界面的任务列表")
    @SaCheckPermission(value = {"ROOT", "TASK:LIST"}, mode = SaMode.OR)
    public CommonResult searchTaskAdminListByPage(@Valid @RequestBody TaskAdminListForm form) {
        int start = (form.getPage() - 1) * form.getLength();
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        condition.put("start", start);
        // 部门经理只能查看自己发布的任务
        if (!StpUtil.hasPermission("ROOT")) {
            condition.put("publisherId", StpUtil.getLoginIdAsInt());
        }
        PageUtils page = taskService.searchTaskAdminListByPage(condition);
        return CommonResult.ok().put("page", page);
    }

    @PostMapping("/publishTask")
    @Operation(summary = "发布任务")
    @SaCheckPermission(value = {"ROOT", "TASK:LIST"}, mode = SaMode.OR)
    public CommonResult publishTask(@Valid @RequestBody PublishTaskForm form) {
        // 时间转换
        DateTime publishTime = DateUtil.date();
        Task task = Task.builder()
                .theme(form.getTheme())
                .desc(form.getDesc())
                .publisherId(StpUtil.getLoginIdAsInt())
                .publishTime(publishTime)
                .executorId(form.getExecutorId())
                .startTime(DateUtil.parse(form.getStartTime()))
                .endTime(DateUtil.parse(form.getEndTime()))
                .taskStatus((byte) 1)
                .build();
        int rows = taskService.publishTask(task);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/cancelTask")
    @Operation(summary = "任务发布人员取消任务")
    @SaCheckPermission(value = {"ROOT", "TASK:LIST"}, mode = SaMode.OR)
    public CommonResult cancelTask(@Valid @RequestBody CancelTaskForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", form.getId());
        params.put("taskStatus", 2);
        int rows = taskService.cancelTask(params);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteTask")
    @Operation(summary = "删除任务")
    @SaCheckPermission(value = {"ROOT", "TASK:LIST"}, mode = SaMode.OR)
    public CommonResult deleteTask(@Valid @RequestBody DeleteTaskForm form) {
        int rows = taskService.deleteTask(form.getIds());
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchTaskInfo")
    @Operation(summary = "查询任务信息")
    @SaCheckLogin
    public CommonResult searchTaskInfo(@Valid @RequestBody SearchTaskInfoForm form) {
        Map<String, Object> taskInfo = taskService.searchTaskInfo(form.getId());
        return CommonResult.ok().put("taskInfo", taskInfo);
    }

    @PostMapping("/taskRate")
    @Operation(summary = "评分")
    @SaCheckPermission(value = {"ROOT", "TASK:LIST"}, mode = SaMode.OR)
    public CommonResult taskRate(@Valid @RequestBody TaskRateForm form) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", form.getId());
        params.put("rate", form.getRate());
        params.put("taskStatus", 5);
        int rows = taskService.taskRate(params);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchMyTaskList")
    @Operation(summary = "查询我的任务列表")
    @SaCheckPermission(value = {"ROOT", "TASK:LIST"}, mode = SaMode.OR)
    public CommonResult searchMyTaskList(@Valid @RequestBody SearchMyTaskListForm form) {
        int start = (form.getPage() - 1) * form.getLength();
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        condition.put("start", start);
        condition.put("executorId", StpUtil.getLoginIdAsInt());
        PageUtils page = taskService.searchMyTaskList(condition);
        return CommonResult.ok().put("page", page);
    }

    @PostMapping("/updateTaskDegree")
    @Operation(summary = "修改任务进度")
    @SaCheckLogin
    public CommonResult updateTaskDegree(@Valid @RequestBody UpdateTaskDegreeForm form) {
        Integer percent = form.getPercent();
        int taskStatus = 0;
        // 新任务
        if (percent == 0) {
            // 进行中
            taskStatus = 1;
        } else if (percent >= 1 && percent <= 99) {
            taskStatus = 3;
        } else if (percent == 100) {
            // 已完成
            taskStatus = 4;
        }
        Map<String, Object> params = new HashMap<>();
        params.put("id", form.getId());
        params.put("percent", form.getPercent());
        params.put("taskStatus", taskStatus);
        int rows = taskService.updateTaskDegree(params);
        return CommonResult.ok().put("rows", rows);
    }
}
