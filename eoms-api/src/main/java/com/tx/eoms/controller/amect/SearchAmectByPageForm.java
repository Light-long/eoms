package com.tx.eoms.controller.amect;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "查询罚款分页记录表单")
public class SearchAmectByPageForm {

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,10}$", message = "name内容不正确")
    @Schema(description = "姓名")
    private String name;

    @Min(value = 1, message = "deptId不能小于1")
    @Schema(description = "部门编号")
    private Integer deptId;

    @Min(value = 1, message = "typeId不能小于1")
    @Schema(description = "罚款类型编号")
    private Integer typeId;

    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$", message = "startDate内容不正确")
    @Schema(description = "开始日期")
    private String startDate;

    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$", message = "endDate内容不正确")
    @Schema(description = "截止日期")
    private String endDate;

    @Min(value = 1, message = "status不能小于1")
    @Schema(description = "状态")
    private Byte status;

    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    @Schema(description = "页数")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 5, max = 20, message = "length必须为5~20之间")
    @Schema(description = "每页记录数")
    private Integer length;

}
