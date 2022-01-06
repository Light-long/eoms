package com.tx.eoms.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class Workday implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 日期
     */
    private Date date;

    private static final long serialVersionUID = 1L;
}