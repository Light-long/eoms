package com.tx.eoms.controller.amecttype;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "更新罚款类型数据表单")
public class UpdateAmectTypeByIdForm {

    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    @Schema(description = "罚款类型ID")
    private Integer id;

    @NotBlank(message = "type不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$", message = "type内容不正确")
    @Schema(description = "违纪类型")
    private String type;

    @NotBlank(message = "money不能为空")
    @Pattern(regexp = "(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)", message = "money内容不正确")
    @Schema(description = "罚款金额")
    private String money;
}
