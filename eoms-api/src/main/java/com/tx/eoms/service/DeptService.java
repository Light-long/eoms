package com.tx.eoms.service;

import java.util.List;
import java.util.Map;

public interface DeptService {

    /**
     * 查询所有部门信息
     */
    List<Map<String, Object>> searchAllDept();
}
