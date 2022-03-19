package com.tx.eoms.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import com.tx.eoms.dao.ReimDao;
import com.tx.eoms.exception.EomsException;
import com.tx.eoms.pojo.Reim;
import com.tx.eoms.service.ReimService;
import com.tx.eoms.task.ReimWorkFlowTask;
import com.tx.eoms.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReimServiceImpl implements ReimService {

    @Resource
    private ReimDao reimDao;

    @Resource
    private ReimWorkFlowTask reimWorkFlowTask;

    /**
     * 查询报销记录
     */
    @Override
    public PageUtils searchReimByPage(Map<String, Object> condition) {
        List<Map<String, Object>> reimList = reimDao.searchReimByPage(condition);
        long count = reimDao.searchReimCount(condition);
        int start = (int) condition.get("start");
        int length = (int) condition.get("length");
        return new PageUtils(reimList, count, start, length);
    }

    /**
     * 添加报销申请
     */
    @Override
    public int addReim(Reim reim) {
        int rows = reimDao.addReim(reim);
        if (rows == 1) {
            // 开启工作流
            reimWorkFlowTask.startReimWorkFlow(reim.getId(), reim.getUserId());
        } else {
            throw new EomsException("添加报销申请失败");
        }
        return rows;
    }

    /**
     * 根据报销id查询整个报销信息
     * 生成报销单pdf
     */
    @Override
    public Map<String, Object> searchReimById(Map<String, Object> params) {
        Map<String, Object> reimInfo = reimDao.searchReimById(params);
        String instanceId = MapUtil.getStr(reimInfo, "instanceId");
        // 把支付订单的url生成二维码
        QrConfig qrConfig = new QrConfig();
        qrConfig.setWidth(70);
        qrConfig.setHeight(70);
        qrConfig.setMargin(2);
        String qrCode = QrCodeUtil.generateAsBase64(instanceId, qrConfig, "jpg");
        reimInfo.put("qrCode", qrCode);
        return reimInfo;
    }

    /**
     * 删除报销记录
     */
    @Override
    public int deleteReimById(Map<String, Object> params) {
        int id = MapUtil.getInt(params, "id");
        String instanceId = reimDao.searchReimInstanceIdById(id);
        int rows = reimDao.deleteReimById(params);
        if (rows == 1) {
            // 关闭工作流
            reimWorkFlowTask.deleteReimWorkFlow(instanceId, "报销申请", "删除报销申请");
        }
        return rows;
    }
}
