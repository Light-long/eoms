package com.tx.eoms.controller.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "添加文件表单")
public class AddDocumentForm {

    @NotBlank
    @Schema(description = "文件名")
    private String documentName;

    @NotBlank
    @Schema(description = "云存储相对路径")
    private String path;

    @NotBlank
    @Schema(description = "云存储完整路径")
    private String url;

    @NotNull
    @Schema(description = "是否公开")
    private int isPublic;
}
