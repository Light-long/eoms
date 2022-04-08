package com.tx.eoms.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
// 头字体设置成20
@HeadFontStyle(fontHeightInPoints = 10)
public class AmectExcel {

    @ExcelProperty(value = "所属部门", index = 0)
    private String deptName;

    @ExcelProperty(value = "当事人", index = 1)
    private String name;

    @ExcelProperty(value = "罚款类型", index = 2)
    private String type;

    @ExcelProperty(value = "罚款原因", index = 3)
    private String reason;

    @ExcelProperty(value = "罚款金额（元）", index = 4)
    private String amount;

    @ExcelProperty(value = "缴纳状态", index = 5)
    private String status;

    @ExcelProperty(value = "创建时间", index = 6)
    private String createTime;
}
