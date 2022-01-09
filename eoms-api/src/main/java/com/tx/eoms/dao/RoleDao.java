package com.tx.eoms.dao;

import com.tx.eoms.pojo.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RoleDao {

    /**
     * 查询所有角色名称
     */
    List<Map<String, Object>> searchAllRoles();

    /**
     * 条件查询角色列表
     * @param condition 分页条件
     * @return 角色集合
     */
    List<Map<String, Object>> searchRolesByPage(Map<String, Object> condition);

    /**
     * 条件查询角色数量
     */
    long searchRolesCount(Map<String, Object> condition);

    /**
     * 添加角色
     */
    int addRole(Role role);

    /**
     * 根据id查询角色
     * @param id 角色id
     */
    Map<String, Object> searchRoleById(Integer id);

    /**
     * 根据role_id查出所有该角色的员工
     * @param id 角色id
     * @return 该角色员工id集合
     */
    List<Integer> searchUserIdsByRoleId(Integer id);

    /**
     * 修改角色信息
     */
    int updateRole(Role role);

    /**
     * 判断被选中的角色能不能删除
     * 如果该角色有用户关联则不能删除
     * 系统内置用户不能删除
     */
    boolean searchCanDelete(Integer[] ids);

    /**
     * 删除选中的角色
     */
    int deleteRoleByIds(Integer[] ids);
}