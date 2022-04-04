package com.tx.eoms.config.init;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * 保存系统常量，SpringBoot初始化时就赋值
 */
@Data
@Component
public class SystemConstants {

    public String signInStartTime;
    public String signInEndTime;
    public String signOutStartTime;
    public String signOutEndTime;
}
