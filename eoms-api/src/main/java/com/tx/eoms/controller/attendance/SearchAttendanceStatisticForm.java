package com.tx.eoms.controller.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "查询个人考勤记录表单")
public class SearchAttendanceStatisticForm {

    @Min(value = 1, message = "用户id")
    private Integer userId;

    @NotBlank
    private String month;
}
