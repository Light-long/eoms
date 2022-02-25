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

    /**
     * 创建支付订单
     * @param params 罚款单id|userId
     * @return base64格式图片
     */
    String createNativeAmectPayOrder(Map<String, Object> params);

    /**
     * 更新支付状态
     */
    int updatePayStatus(Map<String, Object> params);

    /**
     * 根据uuid查询用户罚款订单用户id
     */
    Integer searchUserIdByUUID(String uuid);

    /**
     * 主动查询该订单的支付结果
     */
    boolean searchAmectPayResult(Map<String, Object> params);

    /**
     * 查询 echarts 图表数据
     */
    Map<String, Object> searchChart(Map<String, Object> params);
}
