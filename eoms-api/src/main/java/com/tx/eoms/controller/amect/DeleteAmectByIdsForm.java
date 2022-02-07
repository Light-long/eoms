package com.tx.eoms.controller.amect;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "删除罚款记录表单")
public class DeleteAmectByIdsForm {

    @NotEmpty(message = "ids不能为空")
    @Schema(description = "罚款记录主键")
    private Integer[] ids;
}
