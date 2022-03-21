package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.user.*;
import com.tx.eoms.pojo.User;
import com.tx.eoms.service.UserService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import com.tx.eoms.util.VerifyUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用户Web接口")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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

    @PostMapping("/getVerifyCode")
    @Operation(summary = "生成验证码")
    public CommonResult getVerifyCode(HttpServletResponse response, HttpServletRequest request) {
        // 获取到session
        HttpSession session = request.getSession();
        // 取到 sessionId
        String id = session.getId();
        // 利用图片工具生成图片
        // 返回的数组第一个参数是生成的验证码，第二个参数是生成的图片
        Object[] objs = VerifyUtil.newBuilder()
                .setWidth(120)   //设置图片的宽度
                .setHeight(35)   //设置图片的高度
                .setSize(4)      //设置字符的个数
                .setLines(5)    //设置干扰线的条数
                .setFontSize(25) //设置字体的大小
                .setTilt(true)   //设置是否需要倾斜
                .setBackgroundColor(Color.LIGHT_GRAY) //设置验证码的背景颜色
                .build()         //构建VerifyUtil项目
                .createImage();  //生成图片
        // 将验证码存入Session
        session.setAttribute("SESSION_VERIFY_CODE_" + id, objs[0]);
        // 打印验证码
        System.out.println(objs[0]);
        // 设置redis值的序列化方式
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        // 在redis中保存一个验证码最多尝试次数
        redisTemplate.opsForValue().set(("VERIFY_CODE_" + id), "3", 5 * 60, TimeUnit.SECONDS);
        // 将图片输出给浏览器
        BufferedImage image = (BufferedImage) objs[1];
        String imageString = null;
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
            imageString = Base64.encode(os.toByteArray());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommonResult.ok().put("code", imageString);
    }

    @PostMapping("/checkCode")
    @Operation(summary = "校验验证码")
    public CommonResult checkCode(@Valid @RequestBody CheckCodeForm form, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String id = session.getId();
        // 将redis中的尝试次数减一
        String verifyCodeKey = "VERIFY_CODE_" + id;
        long num = redisTemplate.opsForValue().decrement(verifyCodeKey);
        // 如果次数次数小于0 说明验证码已经失效
        if (num < 0) {
            return CommonResult.error("验证码失效");
        }
        String code = form.getCode();
        // 将session中的取出对应session id生成的验证码
        String serverCode = (String) session.getAttribute("SESSION_VERIFY_CODE_" + id);
        // 校验验证码
        if (null == serverCode || null == code || !serverCode.toUpperCase().equals(code.toUpperCase())) {
            return CommonResult.error("验证码错误");
        }
        // 验证通过之后手动将验证码失效
        redisTemplate.delete(verifyCodeKey);
        return CommonResult.ok();
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

    @PostMapping("/searchNameAndDept")
    @Operation(summary = "查找员工姓名和部门")
    @SaCheckLogin
    public CommonResult searchNameAndDept(@Valid @RequestBody SearchNameAndDeptForm form) {
        Map<String, Object> userInfo = userService.searchNameAndDept(form.getId());
        return CommonResult.ok(userInfo);
    }
}
