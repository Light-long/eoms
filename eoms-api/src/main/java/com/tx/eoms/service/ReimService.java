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

    /**
     * 根据报销id查询整个报销信息
     */
    Map<String, Object> searchReimById(Map<String, Object> params);

    /**
     * 删除报销记录
     */
    int deleteReimById(Map<String, Object> params);
}
