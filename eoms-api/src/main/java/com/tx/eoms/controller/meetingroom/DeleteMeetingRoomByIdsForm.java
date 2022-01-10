package com.tx.eoms.controller.meetingroom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "删除会议室表单")
public class DeleteMeetingRoomByIdsForm {

    @NotEmpty(message = "ids不能为空")
    @Schema(description = "会议室ID")
    private Integer[] ids;
}