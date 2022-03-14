package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.approval.ApprovalTaskForm;
import com.tx.eoms.controller.approval.ArchiveTaskForm;
import com.tx.eoms.controller.approval.SearchApprovalContentForm;
import com.tx.eoms.controller.approval.SearchTaskByPageForm;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.service.ApprovalService;
import com.tx.eoms.service.UserService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/approval")
@Tag(name = "ApprovalController", description = "任务审批Web接口")
@Slf4j
public class ApprovalController {

    @Value("${eoms.code}")
    private String code;

    @Value("${eoms.tcode}")
    private String tcode;

    @Value("${workflow.url}")
    private String workflowUrl;

    @Resource
    private UserService userService;

    @Resource
    private ApprovalService approvalService;

    @PostMapping("/searchTaskByPage")
    @Operation(summary = "查询分页任务列表")
    @SaCheckPermission(value = {"WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public CommonResult searchTaskByPage(@Valid @RequestBody SearchTaskByPageForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        int userId = StpUtil.getLoginIdAsInt();
        params.put("userId", userId);
        params.put("role", userService.searchUserRoles(userId));
        PageUtils taskPage = approvalService.searchTaskByPage(params);
        return CommonResult.ok().put("page", taskPage);
    }

    @PostMapping("/searchApprovalContent")
    @Operation(summary = "查询任务详情")
    @SaCheckPermission(value = {"WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public CommonResult searchApprovalContent(@Valid @RequestBody SearchApprovalContentForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        int userId = StpUtil.getLoginIdAsInt();
        params.put("userId", userId);
        params.put("role", userService.searchUserRoles(userId));
        Map<String, Object> content = approvalService.searchApprovalContent(params);
        return CommonResult.ok().put("content", content);
    }

    @GetMapping("/searchApprovalBPMN")
    @Operation(summary = "获取BPMN图")
    @SaCheckPermission(value = {"WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public void searchApprovalBPMN(String instanceId, HttpServletResponse response) {
        if (StrUtil.isBlankIfStr(instanceId)) {
            throw new EomsException("instanceId不能为空");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("tcode", tcode);
        params.put("instanceId", instanceId);
        String url = workflowUrl + "/workflow/searchApprovalBpmn";
        HttpResponse result = HttpRequest.post(url).header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(params)).execute();
        if (result.getStatus() == 200) {
            try(
                    InputStream in = result.bodyStream();
                    BufferedInputStream bin = new BufferedInputStream(in);
                    OutputStream out = response.getOutputStream();
                    BufferedOutputStream bout = new BufferedOutputStream(out);
               ) {
                // 将读取到的图写入response
                IOUtils.copy(bin, bout);
            } catch (Exception e) {
                log.error("执行异常", e);
            }
        } else {
            log.error("获取工作流BPMN失败");
            throw new EomsException("获取工作流BPMN失败");
        }
    }

    @PostMapping("/approvalTask")
    @Operation(summary = "审批任务")
    @SaCheckPermission(value = {"WORKFLOW:APPROVAL"})
    public CommonResult approvalTask(@Valid @RequestBody ApprovalTaskForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        approvalService.approvalTask(params);
        return CommonResult.ok();
    }

    @PostMapping("/archiveTask")
    @Operation(summary = "归档任务")
    @SaCheckPermission(value = {"FILE:ARCHIVE"})
    public CommonResult archiveTask(@Valid @RequestBody ArchiveTaskForm form) {
        if (!JSONUtil.isJsonArray(form.getFiles())) {
            return CommonResult.error("files不是json数组");
        }
        String files = form.getFiles().replaceAll("&quot;", "\"");
        Map<String, Object> params = new HashMap<>();
        params.put("taskId", form.getTaskId());
        params.put("files", files);
        params.put("userId", StpUtil.getLoginIdAsInt());
        approvalService.archiveTask(params);
        return CommonResult.ok();
    }

}
