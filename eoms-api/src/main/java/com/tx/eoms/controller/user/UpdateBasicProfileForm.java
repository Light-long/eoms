package com.tx.eoms.controller.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Schema(description = "修改用户基本信息")
public class UpdateBasicProfileForm {

    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,10}$", message = "name内容不正确")
    @Schema(description = "姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickname;

    @NotBlank(message = "sex不能为空")
    @Pattern(regexp = "^男$|^女$", message = "sex内容不正确")
    @Schema(description = "性别")
    private String sex;

    @Schema(description = "电话")
    private String tel;

    @NotBlank(message = "邮箱不能为空")
    @Schema(description = "邮箱")
    private String email;

}
