package com.tx.eoms.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PermissionDao {

    /**
     * 查询所有权限
     * @return 所有权限列表
     */
    List<Map<String, Object>> searchAllPermissions();
}