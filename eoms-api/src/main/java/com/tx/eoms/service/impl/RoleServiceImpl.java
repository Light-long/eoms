package com.tx.eoms.service.impl;

import com.tx.eoms.dao.RoleDao;
import com.tx.eoms.service.RoleService;
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

}
