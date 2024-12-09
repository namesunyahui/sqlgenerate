package org.example.sqlgenerate.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.web.method.HandlerMethod;


/**
 * 访问 接口地址
 * <a href="http://localhost:8081/swagger-ui/index.html">...</a>
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI springBootAPI() {
        return new OpenAPI()
                .info(new Info().title("SpringBoot2.7.2 Study API")
                        .description("springboot2.7.2-ai")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        /**
         * 设置全局请求头参数
         */
        return (Operation operation, HandlerMethod handlerMethod) -> {
            Parameter token = new Parameter()
                    .in(ParameterIn.HEADER.toString())
                    .schema(new StringSchema())
                    .name("sessionid")
                    .description("sessionid")
                    .required(true);
            operation.addParametersItem(token);
            return operation;
        };
    }

    @Bean
    public GroupedOpenApi publicApi() {
        /**
         * 设置ftp功能分组
         */
        return GroupedOpenApi.builder()
                .group("ftp")
                .pathsToMatch("/ftp/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        /**
         * 设置admin分组
         */
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/admin/**")
                .build();
    }
}

