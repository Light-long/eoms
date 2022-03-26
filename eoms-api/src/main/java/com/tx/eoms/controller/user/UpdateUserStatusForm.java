package com.tx.eoms.controller.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;

@Data
@Schema(description = "更改用户状态表单")
public class UpdateUserStatusForm {

    @NotNull(message = "userId不能为空")
    @Min(value = 1, message = "userId不能小于1")
    @Schema(description = "用户ID")
    private Integer id;

    @NotNull
    @Min(value = 1, message = "status不能小于1")
    @Schema(description = "状态，1正常，2停用")
    private Integer status;
}
