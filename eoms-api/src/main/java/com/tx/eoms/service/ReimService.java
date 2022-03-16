package com.tx.eoms.service;

import com.tx.eoms.pojo.Reim;
import com.tx.eoms.util.PageUtils;

import java.util.Map;

public interface ReimService {

    /**
     * 查询报销记录
     */
    PageUtils searchReimByPage(Map<String, Object> condition);

    /**
     * 添加报销申请
     */
    int addReim(Reim reim);
}
