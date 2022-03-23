package com.tx.eoms.controller.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "删除公告")
public class DeleteNoticeByIdsForm {

    @NotEmpty(message = "ids不能为空")
    @Schema(description = "公告id")
    private Integer[] ids;
}
