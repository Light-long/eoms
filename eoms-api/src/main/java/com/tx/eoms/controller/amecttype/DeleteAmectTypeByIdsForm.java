package com.tx.eoms.controller.amecttype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "删除罚款类型记录")
public class DeleteAmectTypeByIdsForm {

    @NotEmpty(message = "ids不能为空")
    @Schema(description = "罚款类型编号")
    private Integer[] ids;
}
