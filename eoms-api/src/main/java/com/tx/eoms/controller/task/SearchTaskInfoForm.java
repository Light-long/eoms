package com.tx.eoms.controller.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "查询任务信息表单")
public class SearchTaskInfoForm {

    @NotNull(message = "id不能为空")
    @Schema(description = "任务ID")
    private Integer id;
}
