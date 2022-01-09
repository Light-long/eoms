package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.tx.eoms.service.PermissionService;
import com.tx.eoms.util.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permission")
@Tag(name = "PermissionController", description = "权限Web接口")
public class PermissionController {

    @Resource
    private PermissionService permissionService;

    @GetMapping("/searchAllPermissions")
    @Operation(summary = "查询所有权限")
    @SaCheckPermission(value = {"ROOT","ROLE:INSERT","ROLE:UPDATE"},mode = SaMode.OR)
    public CommonResult searchAllPermissions() {
        List<Map<String, Object>> permissions = permissionService.searchAllPermissions();
        return CommonResult.ok().put("permissions", permissions);
    }
}
