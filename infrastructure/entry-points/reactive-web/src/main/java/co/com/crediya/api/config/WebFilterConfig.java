package co.com.crediya.api.config;

import io.micrometer.context.ContextRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;


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