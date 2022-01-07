package com.tx.eoms.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeptDao {

    /**
     * 查询所有部门信息
     */
    List<Map<String, Object>> searchAllDept();
}