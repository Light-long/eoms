package com.tx.eoms.service;

import com.tx.eoms.pojo.AmectType;
import com.tx.eoms.util.PageUtils;

import java.util.List;
import java.util.Map;

public interface AmectTypeService {

    /**
     * 查询所有的罚款类型
     */
    List<AmectType> searchAllAmectType();

    /**
     * 分页查询 罚款类型
     */
    PageUtils searchAmectTypeByPage(Map<String, Object> condition);

    /**
     * 添加罚款类型
     */
    int addAmectType(AmectType amectType);

    Map<String, Object> searchAmectTypeById(int id);

    int updateAmectType(Map<String, Object> params);

    int deleteAmectTypeByIds(Integer[] ids);
}
