package com.tx.eoms.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class Notice implements Serializable {
    /**
     * 公告id
     */
    private Integer id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告状态(0:关闭，1:正常)
     */
    private Byte status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新钻台
     */
    private Date updateTime;

    /**
     * 是否置顶(0:不置顶，1:置顶)
     */
    private Byte isTopping;

    private static final long serialVersionUID = 1L;
}