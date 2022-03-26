package com.tx.eoms.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@HeadRowHeight(20)
// 头字体设置成20
@HeadFontStyle(fontHeightInPoints = 10)
public class UserExcel implements Serializable {

    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("性别")
    private String sex;

    @ExcelProperty("手机号码")
    private String tel;

    @ExcelProperty("电子邮箱")
    private String email;

    @ExcelProperty("入职日期")
    private Date hiredate;

    @ExcelProperty("角色")
    private String roles;

    @ExcelProperty("部门名称")
    private String deptName;

    @ExcelProperty("状态")
    private String status;

}