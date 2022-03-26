package com.tx.eoms.controller.excel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "导出excel")
public class ExcelExportForm {

    @Schema(description = "表名")
    private String title;

    @Schema(description = "数据")
    private String data;
}
