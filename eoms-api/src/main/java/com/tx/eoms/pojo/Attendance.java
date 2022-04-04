package com.tx.eoms.pojo;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 考勤打卡
 */
@Data
@Builder
public class Attendance implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 考勤结果
        签到type=1：1正常，2迟到
        签退type=2：1正常，2早退，3加班
     */
    private Byte status;

    /**
     * 签到，签退日期
     */
    private String date;

    /**
     * 签到，签退时间
     */
    private Date createTime;

    /**
     * 类型：1签到，2签退
     */
    private Byte type;

    private static final long serialVersionUID = 1L;
}