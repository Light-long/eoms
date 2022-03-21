package com.tx.eoms.pojo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 待办事项
 */
@Data
public class Todo implements Serializable {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 待办事项名
     */
    private String title;

    /**
     * 待办事项细节
     */
    private String desc;

    /**
     * 开始时间
     */
    private Date start;

    /**
     * 结束时间
     */
    private Date end;

    /**
     * 1: 待完成，2: 已过期，3:已完成
     */
    private Byte status;

    /**
     *1最小，3最大
     */
    private Byte priority;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 是否通知，0没有通知，1已经通知过
     */
    private Byte isNotify;

    private static final long serialVersionUID = 1L;
}