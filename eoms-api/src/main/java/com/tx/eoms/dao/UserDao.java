package com.tx.eoms.dao;

import com.tx.eoms.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserDao {

    /**
     * 根据用户id查询权限列表
     * @param userId 用户id
     * @return 权限名集合
     */
    Set<String> searchUserPermissions(Integer userId);

    /**
     * 添加用户
     * @param user 用户信息
     */
    int addUser(User user);

    /**
     * 登录
     * @param params 用户名，密码
     * @return 用户id
     */
    Integer login(Map<String, Object> params);

    /**
     * 根据用户id查询用户信息
     */
    Map<String, Object> searchUserSummary(Integer userId);

    /**
     * 修改密码
     * @param params 用户id，密码
     */
    int updatePassword(Map<String, Object> params);

    /**
     * 根据条件分页查询
     * @param condition 查询条件
     */
    List<Map<String, Object>> searchUserByPage(Map<String, Object> condition);

    /**
     * 符合条件的用户数
     * @param condition 查询条件
     */
    long searchUserCount (Map<String, Object> condition);

    /**
     * 根据id查询用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    Map<String, Object> searchUserById(Integer userId);

    /**
     * 更新用户信息
     */
    int updateUser(Map<String, Object> params);

    /**
     * 根据id删除用户
     * @param ids 用户id集合
     * @return 删除用户记录数
     */
    int deleteUserByIds(Integer[] ids);
}