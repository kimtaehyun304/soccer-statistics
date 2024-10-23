package com.daelim.sfa.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// localhost:8080/swagger-ui/index.html
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                //컴포넌트(스키마) 주석하면 응답 예시가 안 나옴
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("SSS API 문서") // API의 제목
                .description("last-modified: 2024-09-26") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}