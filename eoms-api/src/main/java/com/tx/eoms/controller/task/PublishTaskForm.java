package com.tx.eoms.controller.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Schema(description = "发布任务表单")
public class PublishTaskForm {

    @NotBlank(message = "任务主题不能为空")
    @Schema(description = "主题")
    private String theme;

    @NotBlank(message = "任务描述不能为空")
    @Schema(description = "任务描述")
    private String desc;

    @NotNull(message = "执行人id不能为空")
    @Schema(description = "执行人id")
    private Integer executorId;

    @NotBlank
    private String startTime;

    @NotBlank
    private String endTime;

}
