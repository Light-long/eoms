package com.tx.eoms.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.tx.eoms.controller.excel.ExcelExportForm;
import com.tx.eoms.pojo.excel.MailListExcel;
import com.tx.eoms.pojo.excel.UserExcel;
import com.tx.eoms.util.CommonResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/excel")
@Tag(name = "ExcelController", description = "excel相关接口")
public class ExcelController {

    private static final String PATH = "E:\\excel\\";

    @PostMapping("/exportUserExcel")
    public CommonResult exportUserExcel(@Valid @RequestBody ExcelExportForm form) {
        // 写法1
        String fileName = PATH +form.getTitle() + IdUtil.simpleUUID() +".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // write(fileName, 格式类)
        //sheet (表名)
        //doWrite(数据)
        String data = form.getData().replaceAll("&quot;", "\"");
        List<UserExcel> userList = JSONUtil.parseArray(data).toList(UserExcel.class);
        EasyExcel.write(fileName, UserExcel.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet("模板").doWrite(userList);
        return CommonResult.ok().put("path", fileName);
    }

    @PostMapping("/exportMailListExcel")
    public CommonResult exportMailListExcel(@Valid @RequestBody ExcelExportForm form) {
        // 写法1
        String fileName = PATH +form.getTitle() + IdUtil.simpleUUID() +".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // write(fileName, 格式类)
        //sheet (表名)
        //doWrite(数据)
        String data = form.getData().replaceAll("&quot;", "\"");
        List<MailListExcel> mailList = JSONUtil.parseArray(data).toList(MailListExcel.class);
        EasyExcel.write(fileName, MailListExcel.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet("模板").doWrite(mailList);
        return CommonResult.ok().put("path", fileName);
    }
}
