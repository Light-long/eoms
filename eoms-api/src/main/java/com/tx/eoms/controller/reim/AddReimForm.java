package com.tx.eoms.controller.reim;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(description = "添加报销申请表单")
public class AddReimForm {

    @NotBlank(message = "content不能为空")
    @Schema(description = "报销项目")
    private String content;

    @NotNull(message = "amount不能为空")
    @Schema(description = "报销总金额")
    private BigDecimal amount;

    @NotNull(message = "anleihen不能为空")
    @Schema(description = "借款金额")
    private BigDecimal anleihen;

    @NotNull(message = "balance不能为空")
    @Schema(description = "差额")
    private BigDecimal balance;

}
