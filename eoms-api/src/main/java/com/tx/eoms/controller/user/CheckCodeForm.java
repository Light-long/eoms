package com.tx.eoms.controller.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "校验验证码")
public class CheckCodeForm {

    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码")
    private String code;
}
