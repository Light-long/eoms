package com.tx.eoms.service.impl;

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
}
