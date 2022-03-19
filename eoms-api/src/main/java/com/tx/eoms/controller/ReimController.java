package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.reim.AddReimForm;
import com.tx.eoms.controller.reim.CreateReimReportForm;
import com.tx.eoms.controller.reim.DeleteReimByIdForm;
import com.tx.eoms.controller.reim.SearchReimByPageForm;
import com.tx.eoms.pojo.Reim;
import com.tx.eoms.service.ReimService;
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
import java.util.Map;

@RestController
@RequestMapping("/reim")
@Slf4j
@Tag(name = "ReimController", description = "报销管理Web接口")
public class ReimController {

    @Resource
    private ReimService reimService;

    @PostMapping("/searchReimByPage")
    @Operation(summary = "查询报销记录")
    @SaCheckLogin
    public CommonResult searchReimByPage(@Valid @RequestBody SearchReimByPageForm form) {
        // startDate和endDate有且只有一个不为空，不能查询
        if ((form.getStartDate() == null && form.getEndDate() != null) || (form.getStartDate() != null && form.getEndDate() == null)) {
            return CommonResult.error("startDate和endDate只能同时为空，或者不为空");
        }
        int start = (form.getPage() - 1) * form.getLength();
        int userId = StpUtil.getLoginIdAsInt();
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        condition.put("start", start);
        condition.put("currentUserId", userId);
        // 没有Reim：Select || ROOT 权限，只能查看自己的报销信息
        if (!(StpUtil.hasPermission("REIM:SELECT") || StpUtil.hasPermission("ROOT"))) {
            condition.put("userId", userId);
        }
        PageUtils reimPage = reimService.searchReimByPage(condition);
        return CommonResult.ok().put("page", reimPage);
    }

    @PostMapping("/addReim")
    @Operation(summary = "添加报销记录")
    @SaCheckLogin
    public CommonResult addReim(@Valid @RequestBody AddReimForm form) {
        // 判断content是不是json形式
        if (!JSONUtil.isJsonArray(form.getContent())) {
            return CommonResult.error("content内容不是json格式");
        }
        // 把转义的 “转回来
        String content = form.getContent().replaceAll("&quot;", "\"");
        Reim reim = JSONUtil.parse(form).toBean(Reim.class);
        reim.setContent(content);
        reim.setUserId(StpUtil.getLoginIdAsInt());
        reim.setStatus((byte) 1);
        int rows = reimService.addReim(reim);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/createReimReport")
    @Operation(summary = "生成报销单")
    @SaCheckLogin
    public CommonResult createReimReport(@Valid @RequestBody CreateReimReportForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        if (!(StpUtil.hasPermission("ROOT") || StpUtil.hasPermission("REIM:SELECT"))) {
            params.put("userId", StpUtil.getLoginIdAsInt());
        }
        Map<String, Object> reimInfo = reimService.searchReimById(params);
        return CommonResult.ok(reimInfo);
    }

    @PostMapping("/deleteReimById")
    @Operation(summary = "删除报销记录")
    @SaCheckLogin
    public CommonResult deleteReimById(@Valid @RequestBody DeleteReimByIdForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        params.put("userId", StpUtil.getLoginIdAsInt());
        int rows = reimService.deleteReimById(params);
        return CommonResult.ok().put("rows", rows);
    }
}
