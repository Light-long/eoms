package com.tx.eoms.controller.approval;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "查询审批任务信息")
public class SearchApprovalContentForm {

    @NotBlank(message = "instanceId不能为空")
    @Pattern(regexp = "^[0-9A-Za-z\\-]{36}$", message = "instanceId内容不正确")
    private String instanceId;

    @NotBlank(message = "type不能为空")
    @Pattern(regexp = "^员工请假$|^会议申请$|^报销申请$", message = "type内容不正确")
    private String type;

    @NotBlank(message = "status不能为空")
    @Pattern(regexp = "^待审批$|^已审批$|^已结束$", message = "status内容不正确")
    private String status;
}
