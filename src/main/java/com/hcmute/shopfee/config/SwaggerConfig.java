package com.hcmute.shopfee.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@OpenAPIDefinition
@Configuration
public class SwaggerConfig {
//    @Bean
//    public OpenAPI baseOpenAPI() {
//        return new OpenAPI().info(new Info().title("Drinks API")
//                .version("1.0.0")
//                .description("My api")
//                .contact(new Contact().email("nva6112002@gmail.com").name("An Nguyen"))
//        );
//    }
    private final String moduleName = "Drinks API";
    private final String apiVersion = "1.0.0";

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final String apiTitle = String.format("%s API", StringUtils.capitalize(moduleName));
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                .info(new Info().title(apiTitle)
                        .version("1.0.0")
                        .description("My api")
                        .contact(new Contact().email("nva6112002@gmail.com").name("An Nguyen")));
    }
}