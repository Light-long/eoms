package com.tx.eoms.service;

import java.util.List;
import java.util.Map;

public interface PermissionService {

    /**
     * 查询所有权限
     * @return 所有权限列表
     */
    List<Map<String, Object>> searchAllPermissions();
}
