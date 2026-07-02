package com.eventzone.eventzone_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//test
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI eventZoneOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EventZone API")
                        .description("Event Booking Management System")
                        .version("v1.0"));
    }
}
