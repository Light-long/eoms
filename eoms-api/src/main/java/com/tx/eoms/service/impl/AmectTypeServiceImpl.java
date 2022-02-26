package com.tx.eoms.service.impl;

import com.tx.eoms.dao.AmectTypeDao;
import com.tx.eoms.pojo.AmectType;
import com.tx.eoms.service.AmectTypeService;
import com.tx.eoms.util.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class AmectTypeServiceImpl implements AmectTypeService {

    @Resource
    private AmectTypeDao amectTypeDao;

    /**
     * 查询所有的罚款类型
     */
    @Override
    public List<AmectType> searchAllAmectType() {
        return amectTypeDao.searchAllAmectType();
    }

    /**
     * 分页查询 罚款类型
     */
    @Override
    public PageUtils searchAmectTypeByPage(Map<String, Object> condition) {
        List<Map<String, Object>> amectTypeList = amectTypeDao.searchAmectTypeByPage(condition);
        long count = amectTypeDao.searchAmectTypeCount(condition);
        Integer start = (Integer) condition.get("start");
        Integer length = (Integer) condition.get("length");
        return new PageUtils(amectTypeList, count, start, length);
    }

    /**
     * 添加罚款类型
     */
    @Override
    public int addAmectType(AmectType amectType) {
        return amectTypeDao.addAmectType(amectType);
    }

    @Override
    public Map<String, Object> searchAmectTypeById(int id) {
        return amectTypeDao.searchAmectTypeById(id);
    }

    @Override
    public int updateAmectType(Map<String, Object> params) {
        return amectTypeDao.updateAmectType(params);
    }

    @Override
    public int deleteAmectTypeByIds(Integer[] ids) {
        return amectTypeDao.deleteAmectTypeByIds(ids);
    }
}
