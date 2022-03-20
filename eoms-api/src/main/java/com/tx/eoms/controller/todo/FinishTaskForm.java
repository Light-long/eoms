package com.tx.eoms.controller.todo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "完成待办事项")
public class FinishTaskForm {

    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    @Schema(description = "待办事项ID")
    private Integer id;

    @NotNull
    @Range(min = 1, max = 3, message = "状态只能是1~3范围")
    @Schema(description = "状态")
    private Byte status;
}
