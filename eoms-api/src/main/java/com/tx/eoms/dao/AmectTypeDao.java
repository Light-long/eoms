package com.tx.eoms.dao;

import com.tx.eoms.pojo.AmectType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AmectTypeDao {

    /**
     * 查询所有的罚款类型
     */
    List<AmectType> searchAllAmectType();

    List<Map<String, Object>> searchAmectTypeByPage(Map<String, Object> condition);

    long searchAmectTypeCount(Map<String, Object> condition);

    /**
     * 添加罚款类型
     */
    int addAmectType(AmectType amectType);

    Map<String, Object> searchAmectTypeById(int id);

    int updateAmectType(Map<String, Object> params);

    int deleteAmectTypeByIds(Integer[] ids);
}