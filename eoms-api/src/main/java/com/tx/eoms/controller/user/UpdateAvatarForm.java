package com.tx.eoms.controller.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "更换头像")
public class UpdateAvatarForm {

    @NotBlank(message = "头像不能为空")
    @Schema(description = "头像")
    private String avatar;

}
