package com.tx.eoms.pojo;

import java.io.Serializable;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Role implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 权限集合
     */
    private Object permissions;

    /**
     * 描述
     */
    private String desc;

    /**
     * 系统角色内置权限
     */
    private Object defaultPermissions;

    /**
     * 是否为系统内置角色
     */
    private Boolean systemic;

    private static final long serialVersionUID = 1L;
}