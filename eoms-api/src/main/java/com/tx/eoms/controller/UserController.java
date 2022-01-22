package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.user.*;
import com.tx.eoms.pojo.User;
import com.tx.eoms.service.UserService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用户Web接口")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    @Operation(summary = "登录")
    public CommonResult login(@Valid @RequestBody LoginForm form) {
        String username = form.getUsername();
        String password = form.getPassword();
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        Integer userId = userService.login(params);
        if (userId == null) {
            return CommonResult.error("用户名或密码错误");
        }
        // 生成token，通过cookie返回浏览器
        StpUtil.setLoginId(userId);
        Set<String> permissions = userService.searchUserPermissions(userId);
        return CommonResult.ok().put("permissions", permissions);
    }

    @GetMapping("/loadUserInfo")
    @Operation(summary = "登陆成功后加载用户的基本信息")
    @SaCheckLogin
    public CommonResult loadUserInfo() {
        int userId = StpUtil.getLoginIdAsInt();
        Map<String, Object> summary = userService.searchUserSummary(userId);
        return CommonResult.ok(summary);
    }

    @GetMapping("/logout")
    @Operation(summary = "退出登录")
    public CommonResult logout() {
        // sa-token会删除token
        StpUtil.logout();
        return CommonResult.ok();
    }

    @PostMapping("/updatePassword")
    @Operation(summary = "修改密码")
    @SaCheckLogin
    public CommonResult updatePassword(@Valid @RequestBody UpdatePasswordForm form) {
        // 根据token获取userId
        int userId = StpUtil.getLoginIdAsInt();
        String password = form.getPassword();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("password", password);
        int rows = userService.updatePassword(params);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchUserByPage")
    @Operation(summary = "根据条件分页查询")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public CommonResult searchUserByPage(@Valid @RequestBody SearchUserByPageForm form) {
        // 计算起始偏移
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map condition = JSONUtil.parse(form).toBean(Map.class);
        condition.put("start", start);
        PageUtils pageUtils = userService.searchUserByPage(condition);
        return CommonResult.ok().put("page", pageUtils);
    }

    @PostMapping("/addUser")
    @SaCheckPermission(value = {"ROOT", "USER:INSERT"}, mode = SaMode.OR)
    @Operation(summary = "添加用户")
    public CommonResult addUser(@Valid @RequestBody AddUserForm form) {
        // 将表单转换为user对象
        User user = JSONUtil.parse(form).toBean(User.class);
        user.setStatus((byte) 1);
        user.setPhoto("https://online-office-1257796177.cos.ap-beijing.myqcloud.com/img/avatar/user.jpg");
        user.setRole(JSONUtil.parseArray(form.getRole()).toString());
        user.setCreateTime(new Date());
        int rows = userService.addUser(user);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchUserById")
    @SaCheckPermission(value = {"ROOT", "USER:INSERT", "USER:UPDATE"}, mode = SaMode.OR)
    @Operation(summary = "更新用户时数据回显")
    public CommonResult searchUserById(@Valid @RequestBody SearchUserByIdForm form) {
        Map<String, Object> userInfo = userService.searchUserById(form.getUserId());
        return CommonResult.ok().put("user", userInfo);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "USER:UPDATE"}, mode = SaMode.OR)
    @Operation(summary = "更新用户信息")
    public CommonResult updateUser(@Valid @RequestBody UpdateUserForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        params.replace("role", JSONUtil.parseArray(form.getRole()).toString());
        int rows = userService.updateUser(params);
        if (rows == 1) {
            StpUtil.logoutByLoginId(form.getUserId());
        }
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteUserByIds")
    @SaCheckPermission(value = {"ROOT", "USER:DELETE"}, mode = SaMode.OR)
    @Operation(summary = "删除用户信息")
    public CommonResult deleteUserByIds(@Valid @RequestBody DeleteUserByIdsForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        if (ArrayUtil.contains(form.getIds(), userId)) {
            return CommonResult.error("不能删除自己的账号");
        }
        int rows = userService.deleteUserByIds(form.getIds());
        if (rows > 0) {
            for (Integer id : form.getIds()) {
                StpUtil.logoutByLoginId(id);
            }
        }
        return CommonResult.ok().put("rows", rows);
    }

    @GetMapping("/searchAllUser")
    @Operation(summary = "查询所有用户")
    @SaCheckLogin
    public CommonResult searchAllUser() {
        List<Map<String, Object>> list = userService.searchAllUser();
        return CommonResult.ok().put("list", list);
    }
}
