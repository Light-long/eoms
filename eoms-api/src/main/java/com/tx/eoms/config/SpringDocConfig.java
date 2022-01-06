package com.tx.eoms.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * 使用JavaDoc代替Swagger
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "eoms-api",
                description = "EOMS管理系统后端Java项目",
                version = "1.0"
        )
)
@SecurityScheme(
        name = "token",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SpringDocConfig {

}