package com.tx.eoms.service;

import com.tx.eoms.pojo.User;
import com.tx.eoms.util.PageUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserService {

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
    PageUtils searchUserByPage(Map<String, Object> condition);

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

    /**
     * 查询所有用户
     */
    List<Map<String, Object>> searchAllUser();

    /**
     * 根据userId查询用户所有角色
     */
    List<String> searchUserRoles(int userId);

    /**
     * 查询用户名和对应部门名
     * meeting-video显示
     */
    Map<String, Object> searchNameAndDept(int userId);

    /**
     * 根据userId查找用户信息
     * 用户中心的数据
     */
    Map<String, Object> searchUserProfile(int userId);

    /**
     * 更换头像
     */
    int updateAvatar(Map<String, Object> params);

    /**
     * 修改基本信息
     */
    int updateBasicProfile(Map<String, Object> params);

    /**
     * 注册用户
     */
    int register(Map<String, Object> params);

    /**
     * 查询企业通讯录
     */
    List<Map<String, Object>> searchMailList(Map<String, Object> params);

    /**
     * 根据用户id查询部门id
     */
    int searchDeptIdByUid(int id);

    /**
     * 更改用户状态
     */
    int updateUserStatus(Map<String, Object> params);

    /**
     * 查询用户入职日期
     */
    String searchHiredate(int userId);

    List<Map<String, Object>> searchUserByDeptId(int deptId);
}
