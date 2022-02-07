package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.amect.*;
import com.tx.eoms.pojo.Amect;
import com.tx.eoms.service.AmectService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/amect")
@Tag(name = "AmectController", description = "罚款Web接口")
@Slf4j
public class AmectController {

    @Resource
    private AmectService amectService;

    @PostMapping("/searchAmectByPage")
    @Operation(summary = "查询罚款分页记录")
    @SaCheckLogin
    public CommonResult searchAmectByPage(@Valid @RequestBody SearchAmectByPageForm form) {
        // startDate和endDate有且只有一个不为空，不能查询
        if ((form.getStartDate() == null && form.getEndDate() != null) || (form.getStartDate() != null && form.getEndDate() == null)) {
            return CommonResult.error("startDate和endDate只能同时为空，或者不为空");
        }
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        int start = (form.getPage() - 1) * form.getLength();
        condition.put("start", start);
        condition.put("currentUserId", StpUtil.getLoginIdAsInt());
        // 如果该用户没有 ROOT || AMECT:SELECT 权限，则只能查询和自己有关的罚款记录
        if (!(StpUtil.hasPermission("AMECT:SELECT") || StpUtil.hasPermission("ROOT"))) {
            condition.put("userId", StpUtil.getLoginIdAsInt());
        }
        PageUtils amectList = amectService.searchAmectByPage(condition);
        return CommonResult.ok().put("page", amectList);
    }

    @PostMapping("/addAmect")
    @Operation(summary = "添加罚款记录")
    @SaCheckPermission(value = {"ROOT", "AMECT:INSERT"}, mode = SaMode.OR)
    public CommonResult addAmect(@Valid @RequestBody AddAmectForm form) {
        List<Amect> amectList = new ArrayList<>();
        for (Integer userId : form.getUserId()) {
            Amect amect = Amect.builder()
                    .userId(userId)
                    .amount(new BigDecimal(form.getAmount()))
                    .typeId((int) form.getTypeId())
                    .reason(form.getReason())
                    .uuid(IdUtil.simpleUUID())
                    .build();
            amectList.add(amect);
        }
        int rows = amectService.addAmect(amectList);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchAmectById")
    @Operation(summary = "根据id查询罚款记录")
    @SaCheckPermission(value = {"ROOT", "AMECT:SELECT"}, mode = SaMode.OR)
    public CommonResult searchAmectById(@Valid @RequestBody SearchAmectByIdForm form) {
        Map<String, Object> amectInfo = amectService.searchAmectById(form.getId());
        return CommonResult.ok().put("amectInfo", amectInfo);
    }

    @PostMapping("/updateAmect")
    @Operation(summary = "更新罚款记录")
    @SaCheckPermission(value = {"ROOT", "AMECT:UPDATE"}, mode = SaMode.OR)
    public CommonResult updateAmect(@Valid @RequestBody UpdateAmectForm form) {
        Map<String, Object> amect = JSONUtil.parse(form).toBean(Map.class);
        int rows = amectService.updateAmect(amect);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteAmectByIds")
    @Operation(summary = "删除罚款记录")
    @SaCheckPermission(value = {"ROOT", "AMECT:DELETE"}, mode = SaMode.OR)
    public CommonResult deleteAmectByIds(@Valid @RequestBody DeleteAmectByIdsForm form) {
        int rows = amectService.deleteAmectByIds(form.getIds());
        return CommonResult.ok().put("rows", rows);
    }
}
