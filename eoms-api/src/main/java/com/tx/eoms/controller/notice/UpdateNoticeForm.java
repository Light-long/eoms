package com.tx.eoms.controller.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "更新公告")
public class UpdateNoticeForm {

    @NotNull(message = "id不能为空")
    @Min(value = 1,message = "id不能小于1")
    @Schema(description = "公告ID")
    private Integer id;

    @NotBlank(message = "标题不能为空")
    @Schema(description = "公告标题")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Schema(description = "公告内容")
    private String content;

    @NotNull(message = "状态不能为空")
    @Schema(description = "状态")
    private Byte status;

    @NotNull(message = "置顶方式不能为空")
    @Schema(description = "是否置顶")
    private Byte isTopping;
}
