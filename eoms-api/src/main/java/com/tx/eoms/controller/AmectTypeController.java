package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.tx.eoms.pojo.AmectType;
import com.tx.eoms.service.AmectTypeService;
import com.tx.eoms.util.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
}
