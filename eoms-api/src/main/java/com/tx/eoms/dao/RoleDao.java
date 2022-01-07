package com.tx.eoms.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleDao {

    /**
     * 查询所有角色名称
     */
    List<Map<String, Object>> searchAllRoles();
}