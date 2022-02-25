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

    /**
     * 根据条件查询罚款单信息
     */
    Map<String, Object> searchAmectByCondition(Map<String, Object> condition);

    /**
     * 更新支付订单id
     */
    int updatePrepayId(Map<String, Object> params);

    /**
     * 更新支付状态
     */
    int updatePayStatus(Map<String, Object> params);

    /**
     * 根据uuid查询用户罚款订单用户id
     */
    Integer searchUserIdByUUID(String uuid);

    /**
     * 查询 罚款种类 比例图
     */
    List<Map<String, Object>> searchAmectTypePerChart(Map<String, Object> params);

    /**
     * 查询 罚款金额 比例图
     */
    List<Map<String, Object>> searchMoneyPerChart(Map<String, Object> params);

    /**
     * 查询罚款缴纳比例图
     */
    List<Map<String, Object>> searchAmectResultPerChart(Map<String, Object> params);

    /**
     * 查询 罚款缴纳统计图
     */
    List<Map<String, Object>> searchAmectResultStatisChart(Map<String, Object> params);
}