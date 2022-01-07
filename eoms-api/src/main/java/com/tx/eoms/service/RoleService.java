package com.tx.eoms.service;

import java.util.List;
import java.util.Map;

public interface RoleService {

    /**
     * 查询所有角色名称
     */
    List<Map<String, Object>> searchAllRoles();
}
