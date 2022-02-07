package com.tx.eoms.dao;

import com.tx.eoms.pojo.Amect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AmectDao {

    /**
     * 查询罚款记录
     * @param condition 分页条件|查询条件
     * @return 该用户能查询的罚款记录
     */
    List<Map<String, Object>> searchAmectByPage(Map<String, Object> condition);

    /**
     * 罚款记录数
     */
    long searchAmectCount(Map<String, Object> condition);

    /**
     * 添加罚款记录
     * @param amect 罚款记录
     * @return 添加成功的罚款记录数
     */
    int addAmect(Amect amect);

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