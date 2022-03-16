package com.tx.eoms.dao;

import com.tx.eoms.pojo.Reim;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReimDao {

    /**
     * 分页查询 报销记录
     * @param condition 分页条件
     * @return 报销记录
     */
    List<Map<String, Object>> searchReimByPage(Map<String, Object> condition);

    long searchReimCount(Map<String, Object> condition);

    /**
     * 更新报销的 instanceId
     */
    int updateReimInstanceId(Map<String, Object> params);

    /**
     * 添加报销申请
     */
    int addReim(Reim reim);
}