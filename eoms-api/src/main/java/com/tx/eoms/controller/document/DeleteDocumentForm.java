package com.tx.eoms.controller.document;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "删除文件")
public class DeleteDocumentForm {

    @NotNull
    @Schema(description = "文件ID")
    private Integer id;

}
