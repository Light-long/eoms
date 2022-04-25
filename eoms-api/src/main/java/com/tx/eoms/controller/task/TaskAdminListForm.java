package com.tx.eoms.controller.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "查询任务管理界面的任务列表")
public class TaskAdminListForm {

    @Schema(description = "执行人id")
    private Integer executorId;

    @Schema(description = "发布时间")
    private String publishTime;

    @Schema(description = "状态")
    private Integer taskStatus;

    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    @Schema(description = "页数")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 5, max = 20, message = "length必须为5~20之间")
    @Schema(description = "每页记录数")
    private Integer length;
}
