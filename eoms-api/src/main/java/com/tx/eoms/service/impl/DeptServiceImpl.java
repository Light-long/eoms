package com.tx.eoms.service.impl;

import com.tx.eoms.dao.DeptDao;
import com.tx.eoms.service.DeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DeptServiceImpl implements DeptService {

    @Resource
    private DeptDao deptDao;

    /**
     * 查询所有部门信息
     */
    @Override
    public List<Map<String, Object>> searchAllDept() {
        return deptDao.searchAllDept();
    }
}
