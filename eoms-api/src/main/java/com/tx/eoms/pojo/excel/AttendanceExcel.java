package com.tx.eoms.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
@HeadFontStyle(fontHeightInPoints = 10)
public class AttendanceExcel {

    @ExcelProperty("考勤日期")
    private String date;

    @ExcelProperty("考勤类型")
    private String type;

    @ExcelProperty("考勤时间")
    private String createTime;

    @ExcelProperty("状态")
    private String status;

}
