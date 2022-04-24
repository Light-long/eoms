package com.tx.eoms.controller.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "查询文件列表表单")
public class SearchDocumentByPageForm {

    @Schema(description = "文件名")
    private String documentName;

    @Schema(description = "上传日期")
    private String uploadDate;

    @Schema(description = "公开||私有")
    @NotNull
    private Integer isPublic;

    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    @Schema(description = "页数")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 5, max = 20, message = "length必须为5~20之间")
    @Schema(description = "每页记录数")
    private Integer length;
}
