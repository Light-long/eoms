package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.role.*;
import com.tx.eoms.pojo.Role;
import com.tx.eoms.service.RoleService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.micrometer.core.instrument.util.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/role")
@Tag(name = "RoleController", description = "角色Web接口")
public class RoleController {

    @Resource
    private RoleService roleService;

    @GetMapping("/searchAllRoles")
    @Operation(summary = "查询所有角色信息")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public CommonResult searchAllRoles() {
        List<Map<String, Object>> roles = roleService.searchAllRoles();
        return CommonResult.ok().put("roles", roles);
    }

    @PostMapping("/searchRolesByPage")
    @Operation(summary = "查询角色列表")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public CommonResult searchRolesByPage(@Valid @RequestBody SearchRolesByPageForm form) {
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        int start = (form.getPage() - 1) * form.getLength();
        condition.put("start", start);
        PageUtils pageUtils = roleService.searchRolesByPage(condition);
        return CommonResult.ok().put("rolePage", pageUtils);
    }

    @PostMapping("/searchRoleById")
    @Operation(summary = "根据id查询角色")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public CommonResult searchRoleById(@Valid @RequestBody SearchRoleByIdForm form) {
        Map<String, Object> role = roleService.searchRoleById(form.getId());
        return CommonResult.ok().put("role", role);
    }


    @PostMapping("/addRole")
    @Operation(summary = "添加角色")
    @SaCheckPermission(value = {"ROOT", "ROLE:INSERT"}, mode = SaMode.OR)
    public CommonResult addRole(@Valid @RequestBody AddRoleForm form) {
        Role role = JSONUtil.parse(form).toBean(Role.class);
        role.setPermissions(JSONUtil.parseArray(form.getPermissions()).toString());
        int rows = roleService.addRole(role);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/updateRole")
    @Operation(summary = "更新角色")
    @SaCheckPermission(value = {"ROOT", "ROLE:UPDATE"}, mode = SaMode.OR)
    public CommonResult updateRole(@Valid @RequestBody UpdateRoleForm form) {
        Role role = Role.builder()
                .id(form.getId())
                .roleName(form.getRoleName())
                .desc(form.getDesc())
                .permissions(JSONUtil.parseArray(form.getPermissions()).toString())
                .build();
        int rows = roleService.updateRole(role);
        //如果用户修改成功，并且用户修改了该角色的关联权限
        if (rows == 1 && form.getChanged()) {
            // 把相关用户踢下线
            List<Integer> userIds = roleService.searchUserIdsByRoleId(form.getId());
            userIds.forEach(StpUtil::logoutByLoginId);
        }
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteRoleByIds")
    @Operation(summary = "删除角色")
    @SaCheckPermission(value = {"ROOT", "ROLE:DELETE"}, mode = SaMode.OR)
    public CommonResult deleteRoleByIds(@Valid @RequestBody DeleteRoleByIdsForm form) {
        int rows = roleService.deleteRoleByIds(form.getIds());
        return CommonResult.ok().put("rows", rows);
    }
}
