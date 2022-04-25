package com.tx.eoms.pojo;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Task implements Serializable {
    /**
     * 任务ID
     */
    private Integer id;

    /**
     * 任务主题
     */
    private String theme;

    /**
     * 任务描述
     */
    private String desc;

    /**
     * 发布人ID
     */
    private Integer publisherId;

    /**
     * 执行人ID
     */
    private Integer executorId;

    /**
     * 任务发布时间
     */
    private Date publishTime;

    /**
     * 任务开始时间
     */
    private Date startTime;

    /**
     * 任务结束时间
     */
    private Date endTime;

    /**
     * 任务状态：
        status=0:已过期，超过截止时间没有完成
        status=1:新任务，完成度0%
        status=2:已取消，发布者取消任务
        status=3:进行中，1%~99%
        status=4:已完成，100%
     */
    private Byte taskStatus;

    /**
     * 评分：0~5
     */
    private Integer rate;

    /**
     * 任务完成进度
     */
    private Integer percent;

    private static final long serialVersionUID = 1L;
}