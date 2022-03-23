package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.controller.notice.*;
import com.tx.eoms.pojo.Notice;
import com.tx.eoms.service.NoticeService;
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
@RequestMapping("/notice")
@Slf4j
@Tag(name = "NoticeController", description = "公告管理Web接口")
public class NoticeController {

    @Resource
    private NoticeService  noticeService;

    @GetMapping("/searchNoticeHome")
    @Operation(summary = "查询公告栏中的公告")
    @SaCheckLogin
    public CommonResult searchNoticeHome() {
        List<Map<String, Object>> noticeList = noticeService.searchNoticeHome();
        return CommonResult.ok().put("noticeList", noticeList);
    }

    @PostMapping("/searchNoticeByPage")
    @Operation(summary = "查询公告列表")
    @SaCheckLogin
    public CommonResult searchNoticeByPage(@Valid @RequestBody SearchNoticeByPageForm form) {
        int start = (form.getPage() - 1) * form.getLength();
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        params.put("start", start);
        PageUtils page = noticeService.searchNoticeByPage(params);
        return CommonResult.ok().put("page", page);
    }

    @PostMapping("/addNotice")
    @Operation(summary = "添加公告")
    @SaCheckPermission(value = {"ROOT"})
    public CommonResult addNotice(@Valid @RequestBody AddNoticeForm form) {
        Notice notice = JSONUtil.parse(form).toBean(Notice.class);
        // 生成时间
        notice.setCreateTime(DateUtil.parse(DateUtil.now()));
        notice.setUpdateTime(DateUtil.parse(DateUtil.now()));
        int rows = noticeService.addNotice(notice);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/searchNoticeById")
    @Operation(summary = "根据id查询公告信息")
    @SaCheckPermission(value = {"ROOT"})
    public CommonResult addNotice(@Valid @RequestBody SearchNoticeByIdForm form) {
        Map<String, Object> notice = noticeService.searchNoticeById(form.getId());
        return CommonResult.ok().put("notice", notice);
    }

    @PostMapping("/updateNotice")
    @Operation(summary = "更新公告")
    @SaCheckPermission(value = {"ROOT"})
    public CommonResult updateNotice(@Valid @RequestBody UpdateNoticeForm form) {
        Map<String, Object> params = JSONUtil.parse(form).toBean(Map.class);
        // 更新时间
        params.put("updateTime", DateUtil.parse(DateUtil.now()));
        int rows = noticeService.updateNotice(params);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteNoticeByIds")
    @Operation(summary = "删除公告")
    @SaCheckPermission(value = {"ROOT"})
    public CommonResult deleteNoticeByIds(@Valid @RequestBody DeleteNoticeByIdsForm form) {
        int rows = noticeService.deleteNoticeByIds(form.getIds());
        return CommonResult.ok().put("rows", rows);
    }
}
