package com.tx.eoms.service.impl;

import com.tx.eoms.dao.UserDao;
import com.tx.eoms.pojo.User;
import com.tx.eoms.service.UserService;
import com.tx.eoms.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    /**
     * 根据用户id查询权限列表
     * @param userId 用户id
     * @return 权限名集合
     */
    @Override
    public Set<String> searchUserPermissions(Integer userId) {
        return userDao.searchUserPermissions(userId);
    }

    /**
     * 添加用户
     * @param user 用户信息
     */
    @Override
    public int addUser(User user) {
        return userDao.addUser(user);
    }

    /**
     * 根据用户id查询用户信息
     */
    @Override
    public Map<String, Object> searchUserSummary(Integer userId) {
        return userDao.searchUserSummary(userId);
    }

    /**
     * 修改密码
     * @param params 用户id，密码
     */
    @Override
    public int updatePassword(Map<String, Object> params) {
        return userDao.updatePassword(params);
    }

    /**
     * 根据条件分页查询
     * @param condition 查询条件
     */
    @Override
    public PageUtils searchUserByPage(Map<String, Object> condition) {
        List<Map<String, Object>> userList = userDao.searchUserByPage(condition);
        long count = userDao.searchUserCount(condition);
        int start = (int) condition.get("start");
        int length = (int) condition.get("length");
        return new PageUtils(userList, count, start, length);
    }

    /**
     * 根据id查询用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    @Override
    public Map<String, Object> searchUserById(Integer userId) {
        return userDao.searchUserById(userId);
    }

    /**
     * 更新用户信息
     */
    @Override
    public int updateUser(Map<String, Object> params) {
        return userDao.updateUser(params);
    }

    /**
     * 根据id删除用户
     * @param ids 用户id集合
     * @return 删除用户记录数
     */
    @Override
    public int deleteUserByIds(Integer[] ids) {
        return userDao.deleteUserByIds(ids);
    }

    /**
     * 查询所有用户
     */
    @Override
    public List<Map<String, Object>> searchAllUser() {
        return userDao.searchAllUser();
    }

    /**
     * 根据userId查询用户所有角色
     */
    @Override
    public List<String> searchUserRoles(int userId) {
        return userDao.searchUserRoles(userId);
    }

    /**
     * 查询用户名和对应部门名
     * meeting-video显示
     */
    @Override
    public Map<String, Object> searchNameAndDept(int userId) {
        return userDao.searchNameAndDept(userId);
    }

    /**
     * 根据userId查找用户信息
     * 用户中心的数据
     */
    @Override
    public Map<String, Object> searchUserProfile(int userId) {
        return userDao.searchUserProfile(userId);
    }

    /**
     * 更换头像
     */
    @Override
    public int updateAvatar(Map<String, Object> params) {
        return userDao.updateAvatar(params);
    }

    /**
     * 修改基本信息
     */
    @Override
    public int updateBasicProfile(Map<String, Object> params) {
        return userDao.updateBasicProfile(params);
    }

    /**
     * 登录
     * @param params 用户名，密码
     * @return 用户id
     */
    @Override
    public Integer login(Map<String, Object> params) {
        return userDao.login(params);
    }
}
