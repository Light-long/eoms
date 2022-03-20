package com.tx.eoms.controller.todo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "添加待办事项")
public class AddTodoListForm {

    @NotBlank(message = "title不能为空")
    @Schema(description = "待办事项名称")
    private String title;

    @Schema(description = "待办事项详细信息")
    private String desc;

    @NotBlank(message = "start不能为空")
    @Schema(description = "起始时间")
    private String start;

    @NotBlank(message = "end不能为空")
    @Schema(description = "结束时间")
    private String end;

    @NotNull
    @Range(min = 1, max = 3, message = "状态只能是1~3范围")
    @Schema(description = "状态")
    private Byte status;

    @NotNull
    @Range(min = 1, max = 3, message = "优先级只能是1~3范围")
    @Schema(description = "优先级")
    private Byte priority;
}
