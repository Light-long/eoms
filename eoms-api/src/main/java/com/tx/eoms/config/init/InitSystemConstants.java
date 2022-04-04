package com.tx.eoms.config.init;

import cn.hutool.core.util.StrUtil;
import com.tx.eoms.dao.SysConfigDao;
import com.tx.eoms.pojo.SysConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 初始化SystemConstants的属性
 */
@Configuration
@Slf4j
public class InitSystemConstants {

    @Resource
    private SysConfigDao sysConfigDao;

    @Resource
    private SystemConstants systemConstants;

    @PostConstruct
    public void init() {
        List<SysConfig> sysConfigs = sysConfigDao.selectAllParams();
        sysConfigs.forEach(config -> {
            String key = config.getParamKey();
            key = StrUtil.toCamelCase(key);
            String value = config.getParamValue();
            try {
                Field field = systemConstants.getClass().getDeclaredField(key);
                field.set(systemConstants, value);
            } catch (Exception e) {
                log.error("初始化系统常量异常", e);
            }
        });
    }
}
