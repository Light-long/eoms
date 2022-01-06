package com.tx.eoms.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

@Mapper
public interface UserDao {

    /**
     * 根据用户id查询权限列表
     * @param userId 用户id
     * @return 权限名集合
     */
    Set<String> searchUserPermissions(Integer userId);
}