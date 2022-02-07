package com.tx.eoms.service;

import com.tx.eoms.pojo.Amect;
import com.tx.eoms.util.PageUtils;

import java.util.List;
import java.util.Map;

public interface AmectService {

    /**
     * 查询罚款记录
     * @param condition 分页条件|查询条件
     * @return 该用户能查询的罚款记录
     */
    PageUtils searchAmectByPage(Map<String, Object> condition);

    /**
     * 添加罚款记录
     * @param amectList 罚款记录列表
     * @return 添加成功的罚款记录数
     */
    int addAmect(List<Amect> amectList);

    /**
     * 根据id查询罚款记录（回显）
     */
    Map<String, Object> searchAmectById(Integer id);

    /**
     * 更新罚款记录
     */
    int updateAmect(Map<String, Object> params);

    /**
     * 删除罚款记录
     */
    int deleteAmectByIds(Integer[] ids);
}
