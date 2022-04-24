package com.tx.eoms.pojo;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Document implements Serializable {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 文件名
     */
    private String documentName;

    /**
     * 上传文件人的id
     */
    private Integer uploadUserId;

    /**
     * 云存储完整路径
     */
    private String url;

    /**
     * 云存储相对路径，删除文件时使用
     */
    private String path;

    /**
     * 是否公开，0私有，1公开
     */
    private Byte isPublic;

    /**
     * 上传日期
     */
    private String uploadDate;

    /**
     * 上传时间
     */
    private Date uploadTime;

    private static final long serialVersionUID = 1L;
}