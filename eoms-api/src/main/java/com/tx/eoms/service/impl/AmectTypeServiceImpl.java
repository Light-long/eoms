package com.tx.eoms.service.impl;

import com.tx.eoms.dao.AmectTypeDao;
import com.tx.eoms.pojo.AmectType;
import com.tx.eoms.service.AmectTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
}
