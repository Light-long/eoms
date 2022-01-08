package com.tx.eoms.controller.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "根据id查询用户信息")
public class SearchUserByIdForm {

    @NotNull
    @Schema(description = "用户id")
    private Integer userId;
}
