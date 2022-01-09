package com.tx.eoms.controller.dept;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "根据id查询部门信息")
public class SearchDeptByIdForm {

    @NotNull(message = "部门id不能为空")
    @Min(value = 1, message = "部门id最小为1")
    @Schema(description = "部门id")
    private Integer id;
}
