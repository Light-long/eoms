package com.tx.eoms.controller.leave;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "查询请假分页表单")
public class SearchLeaveByPageForm {

    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    @Schema(description = "页数")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 5, max = 20, message = "length必须在5~20之间")
    @Schema(description = "每页记录数")
    private Integer length;

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,10}$", message = "name内容不正确")
    @Schema(description = "姓名")
    private String name;

    @Min(value = 1, message = "deptId不能小于1")
    @Schema(description = "部门编号")
    private String deptId;

    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$", message = "date内容不正确")
    @Schema(description = "日期")
    private String date;

    @Range(min = 1, max = 2, message = "type只能是1或者2")
    @Schema(description = "类型")
    private Byte type;

    @Range(min = 1, max = 3, message = "状态只能是1~3范围")
    @Schema(description = "状态")
    private Byte status;
}
