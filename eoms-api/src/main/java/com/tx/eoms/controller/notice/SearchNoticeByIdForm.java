package com.tx.eoms.controller.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "根据ID查询公告")
public class SearchNoticeByIdForm {

    @NotNull(message = "id不能为空")
    @Min(value = 1,message = "id不能小于1")
    @Schema(description = "公告ID")
    private Integer id;
}