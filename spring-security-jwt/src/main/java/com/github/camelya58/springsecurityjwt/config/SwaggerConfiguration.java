package com.github.camelya58.springsecurityjwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * Class SwaggerConfiguration sets up settings for swagger.
 *
 * @author Kamila Meshcheryakova
 * created 28.07.2020
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/", "/swagger-ui.html");
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.camelya58.springsecurityjwt.controller"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(true);
    }

    /* Describe APIs */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Authentication")
                .description("Example of spring security with JWT")
                .build();
    }
    private ApiKey apiKey() {
        return new ApiKey("JWT", "Authorization", "header");
    }
}
