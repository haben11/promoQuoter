package com.kifiya.promotion_quoter.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Promotion Engine", version = "1.0"))
public class OpenApiConfig {
}
