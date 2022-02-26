package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.amecttype.*;
import com.tx.eoms.pojo.AmectType;
import com.tx.eoms.service.AmectTypeService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/amectType")
@Slf4j
@Tag(name = "AmectTypeController", description = "罚款类型Web接口")
public class AmectTypeController {

    @Resource
    private AmectTypeService amectTypeService;

    @GetMapping("/searchAllAmectType")
    @Operation(summary = "查询所有罚款类型")
    @SaCheckLogin
    public CommonResult searchAllAmectType() {
        List<AmectType> amectTypeList = amectTypeService.searchAllAmectType();
        return CommonResult.ok().put("list", amectTypeList);
    }

    @PostMapping("/searchAmectTypeByPage")
    @Operation(summary = "查询罚款类型分页记录")
    @SaCheckPermission(value = {"ROOT"})
    public CommonResult searchAmectTypeByPage(@Valid @RequestBody SearchAmectTypeByPageForm form) {
        int start = (form.getPage() - 1) * form.getLength();
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        condition.put("start", start);
        PageUtils pageUtils = amectTypeService.searchAmectTypeByPage(condition);
        return CommonResult.ok().put("page", pageUtils);
    }

    @PostMapping("/addAmectType")
    @Operation(summary = "添加罚款类型")
    @SaCheckPermission(value = {"ROOT"})
    public CommonResult addAmectType(@Valid @RequestBody AddAmectTypeForm form) {
        AmectType amectType = JSONUtil.parse(form).toBean(AmectType.class);
        int rows = amectTypeService.addAmectType(amectType);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchAmectTypeById")
    @Operation(summary = "根据id查询罚款类型信息")
    @SaCheckPermission(value = {"ROOT"})
    public CommonResult searchAmectTypeById(@Valid @RequestBody SearchAmectTypeByIdForm form) {
        Map<String, Object> amectType = amectTypeService.searchAmectTypeById(form.getId());
        return CommonResult.ok().put("amectType", amectType);
    }

    @PostMapping("/updateAmectType")
    @Operation(summary = "修改罚款类型")
    @SaCheckPermission(value = {"ROOT"})
    public CommonResult updateAmectType(@Valid @RequestBody UpdateAmectTypeByIdForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        int rows = amectTypeService.updateAmectType(params);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteAmectTypeByIds")
    @Operation(summary = "删除罚款类型")
    @SaCheckPermission(value = {"ROOT"})
    public CommonResult deleteAmectTypeByIds(@Valid @RequestBody DeleteAmectTypeByIdsForm form) {
        int rows = amectTypeService.deleteAmectTypeByIds(form.getIds());
        return CommonResult.ok().put("rows", rows);
    }
}
