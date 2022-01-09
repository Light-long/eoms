package com.tx.eoms.service.impl;

import com.tx.eoms.dao.PermissionDao;
import com.tx.eoms.service.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Resource
    private PermissionDao permissionDao;

    /**
     * 查询所有权限
     * @return 所有权限列表
     */
    @Override
    public List<Map<String, Object>> searchAllPermissions() {
        return permissionDao.searchAllPermissions();
    }
}
