package com.tx.eoms.controller.amect;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
@Schema(description = "添加罚款记录表单")
public class AddAmectForm {

    @NotEmpty(message = "userId不能为空")
    @Schema(description = "用户ID")
    private Integer[] userId;

    @NotBlank(message = "amount不能为空")
    @Pattern(regexp = "(^[1-9]([0-9]+)?(\\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\\.[0-9]([0-9])?$)", message = "amount内容不正确")
    @Schema(description = "罚款金额")
    private String amount;

    @NotNull(message = "typeId不能为空")
    @Min(value = 1, message = "typeId不能小于1")
    @Schema(description = "罚款类型编号")
    private Byte typeId;

    @NotBlank(message = "reason不能为空")
    @Length(max = 200, message = "reason不能超过200个字符")
    @Schema(description = "罚款原因")
    private String reason;

}
