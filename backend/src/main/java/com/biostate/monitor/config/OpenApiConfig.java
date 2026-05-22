package com.biostate.monitor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bio-State Fermentation Monitor API")
                        .version("1.0.0")
                        .description("API for analyzing fermentation cultures using multimodal AI")
                        .contact(new Contact()
                                .name("Bio-State Project")
                                .url("https://github.com/siddhant16/bio-state-monitor")));
    }
}
