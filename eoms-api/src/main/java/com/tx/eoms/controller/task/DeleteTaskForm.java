package com.tx.eoms.controller.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "删除任务表单")
public class DeleteTaskForm {

    @NotEmpty(message = "ids不能为空")
    @Schema(description = "任务ID")
    private Integer[] ids;
}
