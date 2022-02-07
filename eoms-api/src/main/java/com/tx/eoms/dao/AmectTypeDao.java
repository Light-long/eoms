package com.tx.eoms.dao;

import com.tx.eoms.pojo.AmectType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AmectTypeDao {

    /**
     * 查询所有的罚款类型
     */
    List<AmectType> searchAllAmectType();
}