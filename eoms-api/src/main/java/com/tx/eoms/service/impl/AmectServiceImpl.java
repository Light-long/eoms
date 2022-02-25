package com.tx.eoms.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.tx.eoms.dao.AmectDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Amect;
import com.tx.eoms.service.AmectService;
import com.tx.eoms.util.PageUtils;
import com.tx.eoms.wxpay.MyWXPayConfig;
import com.tx.eoms.wxpay.WXPay;
import com.tx.eoms.wxpay.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class AmectServiceImpl implements AmectService {

    @Resource
    private AmectDao amectDao;

    @Resource
    private MyWXPayConfig myWXPayConfig;

    @Value("${pay.receive-url}")
    private String receiveUrl;

    /**
     * 查询罚款记录
     * @param condition 分页条件|查询条件
     * @return 该用户能查询的罚款记录
     */
    @Override
    public PageUtils searchAmectByPage(Map<String, Object> condition) {
        List<Map<String, Object>> amectList = amectDao.searchAmectByPage(condition);
        long amectCount = amectDao.searchAmectCount(condition);
        int start = (int) condition.get("start");
        int length = (int) condition.get("length");
        return new PageUtils(amectList, amectCount, start, length);
    }

    /**
     * 添加罚款记录
     * @param amectList 罚款记录列表
     * @return 添加成功的罚款记录数
     */
    @Override
    @Transactional
    public int addAmect(List<Amect> amectList) {
        amectList.forEach(amect -> amectDao.addAmect(amect));
        return amectList.size();
    }

    /**
     * 根据id查询罚款记录（回显）
     */
    @Override
    public Map<String, Object> searchAmectById(Integer id) {
        return amectDao.searchAmectById(id);
    }

    /**
     * 更新罚款记录
     */
    @Override
    public int updateAmect(Map<String, Object> params) {
        return amectDao.updateAmect(params);
    }

    /**
     * 删除罚款记录
     */
    @Override
    public int deleteAmectByIds(Integer[] ids) {
        return amectDao.deleteAmectByIds(ids);
    }

    /**
     * 创建支付订单
     * @param params 罚款单id|userId
     * @return base64格式图片
     */
    @Override
    public String createNativeAmectPayOrder(Map<String, Object> params) {
        Integer amectId = (Integer) params.get("amectId");
        // 根据罚款单id和userId查询罚款记录
        Map<String, Object> amectInfo = amectDao.searchAmectByCondition(params);
        if (amectInfo != null && amectInfo.size() > 0) {
            // 获取罚款金额--转换成分
            String amount = new BigDecimal(MapUtil.getStr(amectInfo, "amount")).multiply(new BigDecimal("100")).intValue() + "";
            // 创建支付订单
            try {
                WXPay wxPay = new WXPay(myWXPayConfig);
                Map<String, String> orderInfo = new HashMap<>();
                //随机字符串
                orderInfo.put("nonce_str", WXPayUtil.generateNonceStr());
                orderInfo.put("body", "缴纳罚款");
                orderInfo.put("out_trade_no", MapUtil.getStr(amectInfo, "uuid"));
                orderInfo.put("total_fee", amount);
                orderInfo.put("spbill_create_ip", "127.0.0.1");
                orderInfo.put("notify_url", receiveUrl + "/eoms-api/amect/receiveMessage");
                orderInfo.put("trade_type", "NATIVE");
                String sign = WXPayUtil.generateSignature(orderInfo, myWXPayConfig.getKey());
                orderInfo.put("sign", sign);
                // 创建支付订单
                Map<String, String> result = wxPay.unifiedOrder(orderInfo);
                // 微信订单id
                String prepayId = result.get("prepay_id");
                // 生成支付二维码的url
                String codeUrl = result.get("code_url");
                if (prepayId != null) {
                    params.clear();
                    params.put("prepayId", prepayId);
                    params.put("amectId", amectId);
                    int rows = amectDao.updatePrepayId(params);
                    if (rows != 1) {
                        log.error("更新罚款单的支付订单ID失败");
                        throw new EomsException("更新罚款单的支付订单ID失败");
                    }
                    // 把支付订单url生成二维码
                    QrConfig qrConfig = new QrConfig();
                    qrConfig.setWidth(255);
                    qrConfig.setHeight(255);
                    qrConfig.setMargin(2);
                    return QrCodeUtil.generateAsBase64(codeUrl, qrConfig, "jpg");
                } else {
                    log.error("创建支付订单失败");
                    throw new EomsException("创建支付订单失败");
                }
            } catch (Exception e) {
                log.error("创建支付订单失败");
                throw new EomsException("创建支付订单失败");
            }
        } else {
            throw new EomsException("没有查询到相关罚款单");
        }
    }

    /**
     * 更新支付状态
     */
    @Override
    public int updatePayStatus(Map<String, Object> params) {
        return amectDao.updatePayStatus(params);
    }

    /**
     * 根据uuid查询用户罚款订单用户id
     */
    @Override
    public Integer searchUserIdByUUID(String uuid) {
        return amectDao.searchUserIdByUUID(uuid);
    }

    /**
     * 主动查询该订单的支付结果
     */
    @Override
    public boolean searchAmectPayResult(Map<String, Object> params) {
        Map<String, Object> amect = amectDao.searchAmectByCondition(params);
        if (MapUtil.isNotEmpty(amect)) {
            String uuid = MapUtil.getStr(amect, "uuid");
            Map<String, String> info = new HashMap<>();
            info.put("appid", myWXPayConfig.getAppID());
            info.put("mch_id", myWXPayConfig.getMchID());
            info.put("out_trade_no", uuid);
            info.put("nonce_str", WXPayUtil.generateNonceStr());
            try {
                String sign = WXPayUtil.generateSignature(info, myWXPayConfig.getKey());
                info.put("sign", sign);
                WXPay wxPay = new WXPay(myWXPayConfig);
                // 查询订单支付结果
                Map<String, String> result = wxPay.orderQuery(info);
                String returnCode = result.get("return_code");
                String resultCode = result.get("result_code");
                if ("SUCCESS".equals(resultCode) && "SUCCESS".equals(returnCode)) {
                    String tradeState = result.get("trade_state");
                    // 查询到订单支付成功
                    if ("SUCCESS".equals(tradeState)) {
                        Map<String, Object> condition = new HashMap<>();
                        condition.put("uuid", uuid);
                        condition.put("status", 2);
                        amectDao.updatePayStatus(condition);
                        return true;
                    }
                }
            } catch (Exception e) {
                log.error("查询支付订单失败", e);
                throw new EomsException("查询支付订单失败");
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> searchChart(Map<String, Object> params) {
        // 查询饼状图
        List<Map<String, Object>> chart1 = amectDao.searchAmectTypePerChart(params);
        List<Map<String, Object>> chart2 = amectDao.searchMoneyPerChart(params);
        List<Map<String, Object>> chart3 = amectDao.searchAmectResultPerChart(params);
        params.clear();
        // 查询统计图
        int year = DateUtil.year(new Date());
        params.put("year", year);
        params.put("status", 1);
        List<Map<String, Object>> list1 = amectDao.searchAmectResultStatisChart(params);
        params.replace("status", 2);
        List<Map<String, Object>> list2 = amectDao.searchAmectResultStatisChart(params);
        List<Map<String, Object>> chart4_1 = new ArrayList<>();
        List<Map<String, Object>> chart4_2 = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("month", i);
            map.put("ct", 0);
            chart4_1.add(map);
            chart4_2.add(new HashMap<>(map));
        }
        list1.forEach(one -> chart4_1.forEach(temp -> {
            if (MapUtil.getInt(one, "month").equals(MapUtil.getInt(temp, "month"))) {
                temp.replace("ct", MapUtil.getInt(one, "ct"));
            }
        }));
        list2.forEach(one -> chart4_2.forEach(temp -> {
            if (MapUtil.getInt(one, "month").equals(MapUtil.getInt(temp, "month"))) {
                temp.replace("ct", MapUtil.getInt(one, "ct"));
            }
        }));
        Map<String, Object> result = new HashMap<>();
        result.put("chart1", chart1);
        result.put("chart2", chart2);
        result.put("chart3", chart3);
        result.put("chart4_1", chart4_1);
        result.put("chart4_2", chart4_2);
        return result;
    }
}
