package com.tx.eoms.dao;

import com.tx.eoms.pojo.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DeptDao {

    /**
     * 查询所有部门信息
     */
    List<Map<String, Object>> searchAllDept();

    /**
     * 条件分页查询部门信息
     * @param condition 查询条件
     */
    List<Map<String, Object>> searchDeptByPage(Map<String, Object> condition);

    /**
     * 条件查询部门数量
     */
    long searchDeptCount(Map<String, Object> condition);

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
     * 如果该部门还有员工，这个部门不能删除
     */
    boolean searchCanDelete(Integer[] ids);

    /**
     * 根据id删除部门
     */
    int deleteDeptByIds(Integer[] ids);
}