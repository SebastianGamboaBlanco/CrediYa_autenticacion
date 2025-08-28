package co.com.crediya.api.config;

import co.com.crediya.api.logging.StructuredLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebFilterConfig {

    @Bean
    public StructuredLoggingFilter structuredLoggingFilter() {
        return new StructuredLoggingFilter();
    }
}