package co.com.crediya.api.config;

import co.com.crediya.api.security.JwtAuthenticationFilter;
import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class WebFilterConfig {

    @PostConstruct
    public void registerMdcAccessors() {
        ContextRegistry.getInstance()
                .registerThreadLocalAccessor(
                        "X-FLOW-ID",
                        () -> MDC.get("X-FLOW-ID"),
                        valueCorrelationId -> MDC.put("X-FLOW-ID", valueCorrelationId),
                        () -> MDC.remove("X-FLOW-ID"));
        ContextRegistry.getInstance()
                .registerThreadLocalAccessor(
                        "PATH",
                        () -> MDC.get("PATH"),
                        path -> MDC.put("PATH", path),
                        () -> MDC.remove("PATH"));
    }

}