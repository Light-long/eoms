package com.tx.eoms.config;

import cn.dev33.satoken.stp.StpInterface;
import com.tx.eoms.dao.UserDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Sa-Token框架拦截HTTP请求之后调用的类
 * 在这个类中，我们一共要声明两个方法分别用来查询用户实际的权限和角色
 */
@Component
public class SaTokenConfig implements StpInterface {

    @Resource
    private UserDao userDao;

    /**
     * 用户的权限集合
     * @param loginId 账号id
     * @param loginType 账号类型
     * @return 用户的权限集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        int userId = Integer.parseInt(loginId.toString());
        Set<String> permissions = userDao.searchUserPermissions(userId);
        return new ArrayList<>(permissions);
    }

    /**
     * 用户的角色集合
     * @param loginId 账号id
     * @param loginType 账号类型
     * @return 用户的角色集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 不需要角色判定
        return new ArrayList<>();
    }
}
