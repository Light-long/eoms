package com.tx.eoms.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.tx.eoms.config.tencent.oss.CosUtil;
import com.tx.eoms.controller.document.AddDocumentForm;
import com.tx.eoms.controller.document.DeleteDocumentForm;
import com.tx.eoms.controller.document.SearchDocumentByPageForm;
import com.tx.eoms.pojo.Document;
import com.tx.eoms.service.DocumentService;
import com.tx.eoms.util.CommonResult;
import com.tx.eoms.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/document")
@Tag(name = "DocumentController", description = "文件管理Web接口")
@Slf4j
public class DocumentController {

    @Resource
    private DocumentService documentService;

    @Resource
    private CosUtil cosUtil;

    @PostMapping("/searchDocumentByPage")
    @Operation(summary = "查询文件列表")
    @SaCheckLogin
    public CommonResult searchDocumentByPage(@Valid @RequestBody SearchDocumentByPageForm form) {
        int start = (form.getPage() - 1) * form.getLength();
        Map<String, Object> condition = JSONUtil.parse(form).toBean(Map.class);
        condition.put("start", start);
        condition.put("userId", StpUtil.getLoginIdAsInt());
        PageUtils page = documentService.searchDocumentByPage(condition);
        return CommonResult.ok().put("page", page);
    }

    @PostMapping("/addDocument")
    @Operation(summary = "添加文件信息")
    @SaCheckLogin
    public CommonResult addDocument(@Valid @RequestBody AddDocumentForm form) {
        Document document = Document.builder()
                .documentName(form.getDocumentName())
                .isPublic((byte) form.getIsPublic())
                .path(form.getPath())
                .url(form.getUrl())
                .uploadDate(DateUtil.today())
                .uploadTime(DateUtil.date())
                .uploadUserId(StpUtil.getLoginIdAsInt())
                .build();
        int rows = documentService.addDocument(document);
        return CommonResult.ok().put("rows", rows);
    }

    @PostMapping("/deleteDocument")
    @Operation(summary = "删除文件")
    @SaCheckLogin
    public CommonResult deleteDocument(@Valid @RequestBody DeleteDocumentForm form) {
        Integer id = form.getId();
        // 根据id查出相对路径path
        String path = documentService.searchPathById(id);
        // 删除云存储
        String[] paths = new String[1];
        paths[0] = path;
        cosUtil.deleteFile(paths);
        // 删除数据库记录
        int rows = documentService.deleteDocument(id);
        return CommonResult.ok().put("rows", rows);
    }
}
