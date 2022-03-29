package com.tx.eoms.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
@HeadFontStyle(fontHeightInPoints = 10)
public class DeptExcel {

    @ExcelProperty("部门名称")
    private String deptName;

    @ExcelProperty("部门电话")
    private String tel;

    @ExcelProperty("部门邮箱")
    private String email;

    @ExcelProperty("员工人数")
    private String emps;

    @ExcelProperty("备注")
    private String desc;

}
