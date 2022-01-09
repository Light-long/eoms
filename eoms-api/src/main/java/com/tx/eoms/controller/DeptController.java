package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.dept.*;
import com.tx.eoms.pojo.Dept;
import com.tx.eoms.service.DeptService;
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
@RequestMapping("/dept")
@Tag(name = "DeptController", description = "部门Web接口")
public class DeptController {

    @Resource
    private DeptService deptService;

    @GetMapping("/searchAllDept")
    @Operation(summary = "查询所有部门信息")
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    public CommonResult searchAllDept() {
        List<Map<String, Object>> deptList = deptService.searchAllDept();
        return CommonResult.ok().put("deptList", deptList);
    }

    @PostMapping("/searchDeptByPage")
    @Operation(summary = "条件分页查询部门列表")
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    public CommonResult searchDeptByPage(@Valid @RequestBody SearchDeptByPageForm form) {
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        int start = (form.getPage() - 1) * form.getLength();
        condition.put("start", start);
        PageUtils deptPage = deptService.searchDeptByPage(condition);
        return CommonResult.ok().put("deptPage", deptPage);
    }

    @PostMapping("/addDept")
    @Operation(summary = "添加部门")
    @SaCheckPermission(value = {"ROOT", "DEPT:INSERT"}, mode = SaMode.OR)
    public CommonResult addDept(@Valid @RequestBody AddDeptForm form) {
        Dept dept = JSONUtil.parse(form).toBean(Dept.class);
        int rows = deptService.addDept(dept);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchDeptById")
    @Operation(summary = "根据ID查询部门")
    @SaCheckPermission(value = {"ROOT", "DEPT:SELECT"}, mode = SaMode.OR)
    public CommonResult searchDeptById(@Valid @RequestBody SearchDeptByIdForm form) {
        Map<String, Object> dept = deptService.searchDeptById(form.getId());
        return CommonResult.ok().put("dept", dept);
    }

    @PostMapping("/updateDept")
    @Operation(summary = "更新部门信息")
    @SaCheckPermission(value = {"ROOT", "DEPT:UPDATE"}, mode = SaMode.OR)
    public CommonResult updateDept(@Valid @RequestBody UpdateDeptForm form) {
        Dept dept = JSONUtil.parse(form).toBean(Dept.class);
        int rows = deptService.updateDept(dept);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteDeptByIds")
    @Operation(summary = "根据id删除部门信息")
    @SaCheckPermission(value = {"ROOT", "DEPT:DELETE"}, mode = SaMode.OR)
    public CommonResult deleteDeptByIds(@Valid @RequestBody DeleteDeptByIdsForm form) {
        int rows = deptService.deleteDeptByIds(form.getIds());
        return CommonResult.ok().put("rows", rows);
    }
}
