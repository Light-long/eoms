package com.tx.eoms.controller.meetingroom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(description = "查询会议室分页表单")
public class SearchMeetingRoomByPageForm {

    @Pattern(regexp = "^[0-9a-zA-Z\\u4e00-\\u9fa5]{1,20}$", message = "name内容不正确")
    @Schema(description = "会议室名称")
    private String name;

    @Schema(description = "状态")
    private Integer status;

}
