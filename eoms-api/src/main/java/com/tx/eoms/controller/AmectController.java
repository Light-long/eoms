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
import com.tx.eoms.service.UserService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import com.tx.eoms.websocket.WebSocketService;
import com.tx.eoms.wxpay.WXPayUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/amect")
@Tag(name = "AmectController", description = "罚款Web接口")
@Slf4j
public class AmectController {

    @Resource
    private AmectService amectService;

    @Resource
    private UserService userService;

    @Value("${wx.key}")
    private String key;

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
        // ROOT查询所有
        // 部门经理（AMECT：SELECT）查询该部门的
        // 普通员工查询自己的
        if (!StpUtil.hasPermission("ROOT")) {
            if (StpUtil.hasPermission("AMECT:SELECT")) {
                // 查询该部门的部门id
                int deptId = userService.searchDeptIdByUid(StpUtil.getLoginIdAsInt());
                condition.put("deptId", deptId);
            }  else {
                condition.put("userId", StpUtil.getLoginIdAsInt());
            }
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

    @PostMapping("/createNativeAmectPayOrder")
    @Operation(summary = "创建罚款订单")
    @SaCheckLogin
    public CommonResult createNativeAmectPayOrder(@Valid @RequestBody CreateNativeAmectPayOrderForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        Map<String, Object> params = new HashMap<>();
        params.put("amectId", form.getAmectId());
        params.put("userId", userId);
        String qrCode = amectService.createNativeAmectPayOrder(params);
        return CommonResult.ok().put("qrCode", qrCode);
    }

    @RequestMapping("/receiveMessage")
    @Operation(summary = "接受微信支付的消息")
    public void receiveMessage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("utf-8");
        // 获取输入流
        BufferedReader reader = request.getReader();
        String line = reader.readLine();
        StringBuffer temp = new StringBuffer();
        // 读取输入流数据
        while (line != null) {
            temp.append(line);
            line = reader.readLine();
        }
        reader.close();
        // 接受到的结果
        String xml = temp.toString();
        // 利用数字证书校验响应是否是微信支付发送的
        if (WXPayUtil.isSignatureValid(xml, key)) {
            // xml --> map
            Map<String, String> result = WXPayUtil.xmlToMap(xml);
            String resultCode = result.get("result_code");
            String returnCode = result.get("return_code");
            if (Objects.equals("SUCCESS", resultCode) && Objects.equals("SUCCESS", returnCode)) {
                // 付款成功
                // 获取罚款单uuid
                String uuid = result.get("out_trade_no");
                // 更新订单状态
                Map<String, Object> params = new HashMap<>();
                params.put("status", 2);
                params.put("uuid", uuid);
                int rows = amectService.updatePayStatus(params);
                if (rows == 1) {
                    // 根据罚款单uuid查询userId
                    Integer userId = amectService.searchUserIdByUUID(uuid);
                    // 向前端推送付款结果
                    WebSocketService.sendInfo("付款成功", userId + "");
                    // 给微信平台返回响应
                    response.setCharacterEncoding("utf-8");
                    response.setContentType("application/xml");
                    Writer writer = response.getWriter();
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);
                    bufferedWriter.write("<xml><return_code><![CDATA[SUCCESS]]></return_code> <return_msg><![CDATA[OK]]></return_msg></xml>");
                    bufferedWriter.close();
                    writer.close();
                } else {
                    log.error("更新订单状态失败");
                    response.sendError(500, "更新订单状态失败");
                }
            }
        } else {
            log.error("数字签名异常");
            response.sendError(500, "数字签名异常");
        }
    }

    @PostMapping("/searchAmectPayResult")
    @Operation(summary = "查询罚款支付结果")
    @SaCheckLogin
    public CommonResult searchAmectPayResult(@Valid @RequestBody SearchAmectPayResultForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        int amectId = form.getAmectId();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("amectId", amectId);
        boolean result = amectService.searchAmectPayResult(params);
        return CommonResult.ok().put("result", result);
    }

    @PostMapping("/searchChart")
    @Operation(summary = "查询echarts图表数据")
    @SaCheckPermission(value = {"ROOT", "AMECT:SELECT"}, mode = SaMode.OR)
    public CommonResult searchChart(@Valid @RequestBody SearchChartForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        Map<String, Object> map = amectService.searchChart(params);
        return CommonResult.ok(map);
    }
}
