package com.tx.eoms.service;

import com.tx.eoms.pojo.Dept;
import com.tx.eoms.util.PageUtils;

import java.util.List;
import java.util.Map;

public interface DeptService {

    /**
     * 查询所有部门信息
     */
    List<Map<String, Object>> searchAllDept();

    /**
     * 条件分页查询部门信息
     * @param condition 查询条件
     */
    PageUtils searchDeptByPage(Map<String, Object> condition);

    /**
     * 添加部门
     */
    int addDept(Dept dept);

    /**
     * 根据ID查询部门
     */
    Map<String, Object> searchDeptById(Integer id);

    /**
     * 更新部门信息
     */
    int updateDept(Dept dept);

    /**
     * 根据id删除部门
     */
    int deleteDeptByIds(Integer[] ids);
}
