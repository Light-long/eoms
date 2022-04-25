package com.tx.eoms.controller.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "任务评分")
public class TaskRateForm {

    @NotNull(message = "id不能为空")
    @Schema(description = "任务ID")
    private Integer id;

    @NotNull
    @Schema(description = "分数")
    private Integer rate;
}
