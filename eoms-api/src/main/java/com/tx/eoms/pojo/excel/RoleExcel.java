package com.tx.eoms.pojo.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(20)
@HeadFontStyle(fontHeightInPoints = 10)
public class RoleExcel {

    @ExcelProperty("角色名称")
    private String roleName;

    @ExcelProperty("权限数量")
    private String permissions;

    @ExcelProperty("关联用户数")
    private String users;

    @ExcelProperty("备注")
    private String desc;

    @ExcelProperty("内置角色")
    private String systemic;

}
