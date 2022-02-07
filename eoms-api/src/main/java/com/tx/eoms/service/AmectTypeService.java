package com.tx.eoms.service;

import com.tx.eoms.pojo.AmectType;

import java.util.List;

public interface AmectTypeService {

    /**
     * 查询所有的罚款类型
     */
    List<AmectType> searchAllAmectType();
}
