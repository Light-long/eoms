package com.tx.eoms.controller.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "发布人取消任务")
public class CancelTaskForm {

    @NotNull(message = "任务id不能为空")
    @Schema(description = "任务id")
    private Integer id;

}
