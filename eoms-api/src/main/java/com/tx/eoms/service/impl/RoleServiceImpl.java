package com.tx.eoms.service.impl;

import com.tx.eoms.dao.RoleDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Role;
import com.tx.eoms.service.RoleService;
import com.tx.eoms.util.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleDao roleDao;

    /**
     * 查询所有角色名称
     */
    @Override
    public List<Map<String, Object>> searchAllRoles() {
        return roleDao.searchAllRoles();
    }

    /**
     * 条件查询角色列表
     * @param condition 分页条件
     * @return 角色集合
     */
    @Override
    public PageUtils searchRolesByPage(Map<String, Object> condition) {
        List<Map<String, Object>> roleList = roleDao.searchRolesByPage(condition);
        long roleCount = roleDao.searchRolesCount(condition);
        Integer start  = (Integer) condition.get("start");
        Integer length = (Integer) condition.get("length");
        return new PageUtils(roleList, roleCount, start, length);
    }

    /**
     * 添加角色
     */
    @Override
    public int addRole(Role role) {
        return roleDao.addRole(role);
    }


    /**
     * 根据id查询角色
     * @param id 角色id
     */
    @Override
    public Map<String, Object> searchRoleById(Integer id) {
        return roleDao.searchRoleById(id);
    }

    /**
     * 根据role_id查出所有该角色的员工
     * @param id 角色id
     * @return 该角色员工id集合
     */
    @Override
    public List<Integer> searchUserIdsByRoleId(Integer id) {
        return roleDao.searchUserIdsByRoleId(id);
    }

    /**
     * 修改角色信息
     */
    @Override
    public int updateRole(Role role) {
        return roleDao.updateRole(role);
    }

    /**
     * 删除选中的角色
     */
    @Override
    public int deleteRoleByIds(Integer[] ids) {
        if (!roleDao.searchCanDelete(ids)) {
            throw new EomsException("不能删除已有用户关联的角色");
        }
        return roleDao.deleteRoleByIds(ids);
    }

}
