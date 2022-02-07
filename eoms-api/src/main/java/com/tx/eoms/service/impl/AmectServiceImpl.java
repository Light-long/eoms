package com.tx.eoms.service.impl;

import com.tx.eoms.dao.AmectDao;
import com.tx.eoms.pojo.Amect;
import com.tx.eoms.service.AmectService;
import com.tx.eoms.util.PageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AmectServiceImpl implements AmectService {

    @Resource
    private AmectDao amectDao;

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
}
