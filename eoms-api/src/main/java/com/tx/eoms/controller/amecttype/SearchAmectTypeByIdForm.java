package com.tx.eoms.controller.amecttype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "根据ID查找罚款类型表单")
public class SearchAmectTypeByIdForm {

    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    @Schema(description = "罚款类型ID")
    private Integer id;
}
