package co.com.crediya.api.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.util.Random;

@Slf4j
public class StructuredLoggingFilter implements WebFilter, Ordered {
    
    private static final Random random = new Random();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = String.valueOf(random.nextLong() & Long.MAX_VALUE);
        
        return chain.filter(exchange)
                .contextWrite(Context.of("traceId", traceId))
                .doOnEach(this::logOnEach);
    }
    
    private void logOnEach(Signal<?> signal) {
        try {
            String traceId = signal.getContextView().getOrDefault("traceId", "");
            if (!traceId.isEmpty()) {
                MDC.put("traceId", traceId);
            }
        } catch (Exception e) {
            // Ignore MDC errors
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}