package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.tx.eoms.config.tencent.oss.CosTypeEnum;
import com.tx.eoms.config.tencent.oss.CosUtil;
import com.tx.eoms.controller.cos.DeleteCosFileForm;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.util.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/cos")
@Slf4j
@Tag(name = "CosController", description = "对象存储Web接口")
public class CosController {

    @Resource
    private CosUtil cosUtil;

    @PostMapping("/uploadCosFile")
    @Operation(summary = "上传文件")
    @SaCheckLogin
    public CommonResult uploadCosFile(@Param("file")MultipartFile file, @Param("type") String type) {
        CosTypeEnum typeEnum = CosTypeEnum.findByKey(type);
        if (typeEnum == null) {
            throw new EomsException("type类型错误");
        }
        try {
            Map<String, Object> map = cosUtil.uploadFile(file, typeEnum);
            return CommonResult.ok(map);
        } catch (IOException e) {
            log.error("文件上传到腾讯云错误", e);
            throw new EomsException("文件上传到腾讯云错误");
        }
    }

    @PostMapping("/deleteCosFile")
    @Operation(summary = "删除文件")
    @SaCheckLogin
    public CommonResult deleteCosFile(@Valid @RequestBody DeleteCosFileForm form) {
        cosUtil.deleteFile(form.getPaths());
        return CommonResult.ok();
    }
}
