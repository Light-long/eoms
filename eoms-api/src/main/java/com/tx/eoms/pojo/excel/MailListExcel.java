package com.tx.eoms.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
// 头字体设置成20
@HeadFontStyle(fontHeightInPoints = 10)
public class MailListExcel {

    @ExcelProperty(value = "部门名称", index = 2)
    private String deptName;

    @ExcelProperty(value = "姓名", index = 0)
    private String name;

    @ExcelProperty(value = "性别", index = 1)
    private String sex;

    @ExcelProperty(value = "手机号码", index = 3)
    private String tel;

    @ExcelProperty(value = "电子邮箱", index = 4)
    private String email;

}
