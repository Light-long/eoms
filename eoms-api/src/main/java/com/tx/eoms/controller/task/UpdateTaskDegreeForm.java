package com.tx.eoms.controller.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "修改任务进度")
public class UpdateTaskDegreeForm {

    @NotNull(message = "id不能为空")
    @Schema(description = "任务ID")
    private Integer id;

    @NotNull
    @Schema(description = "百分比")
    private Integer percent;
}
