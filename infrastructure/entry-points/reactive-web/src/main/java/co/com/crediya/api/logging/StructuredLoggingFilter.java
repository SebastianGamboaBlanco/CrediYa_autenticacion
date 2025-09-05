package co.com.crediya.api.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.publisher.Signal;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.UUID;

@Component
@Order(-1)
@Slf4j
public class StructuredLoggingFilter implements WebFilter, Ordered {

    String traceId = UUID.randomUUID().toString();
    private static final String MDC_CONTEXT_REACTOR_KEY = "MDC_CONTEXT_REACTOR_KEY";

    @PostConstruct
    public void contextOperatorHook() {
        Hooks.onEachOperator(MDC_CONTEXT_REACTOR_KEY, Operators.lift((scannable, coreSubscriber) -> 
            new MdcContextLifter<>(coreSubscriber)
        ));
    }

    @PreDestroy
    public void cleanupHook() {
        Hooks.resetOnEachOperator(MDC_CONTEXT_REACTOR_KEY);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String xFlowId = exchange.getRequest().getHeaders().getFirst("X-Flow-Id");
        var correlationId =
                null != xFlowId && !xFlowId.isEmpty() ? xFlowId : traceId;

        // Set MDC immediately for this request thread
        MDC.put("X-FLOW-ID", correlationId);
        MDC.put("PATH", exchange.getRequest().getURI().getPath());

        return chain
                .filter(exchange)
                .contextWrite(
                        context ->
                                context
                                        .put("X-FLOW-ID", correlationId)
                                        .put("PATH", exchange.getRequest().getURI().getPath()))
                .doOnEach(this::logOnEach)
                .doFinally(signalType -> MDC.clear());
    }
    
    private void logOnEach(Signal<?> signal) {
        try {
            var contextView = signal.getContextView();
            String xFlowId = contextView.getOrDefault("X-FLOW-ID", "");
            String path = contextView.getOrDefault("PATH", "");
            
            if (!xFlowId.isEmpty()) {
                MDC.put("X-FLOW-ID", xFlowId);
            }
            if (!path.isEmpty()) {
                MDC.put("PATH", path);
            }
        } catch (Exception e) {
            // Ignore MDC errors to prevent blocking reactive stream
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private static class MdcContextLifter<T> implements reactor.core.CoreSubscriber<T> {
        private final reactor.core.CoreSubscriber<T> coreSubscriber;

        public MdcContextLifter(reactor.core.CoreSubscriber<T> coreSubscriber) {
            this.coreSubscriber = coreSubscriber;
        }

        @Override
        public void onSubscribe(org.reactivestreams.Subscription subscription) {
            coreSubscriber.onSubscribe(subscription);
        }

        @Override
        public void onNext(T obj) {
            coreSubscriber.currentContext().getOrEmpty("X-FLOW-ID")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(xFlowId -> MDC.put("X-FLOW-ID", xFlowId));
                
            coreSubscriber.currentContext().getOrEmpty("PATH")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(path -> MDC.put("PATH", path));
                
            try {
                coreSubscriber.onNext(obj);
            } finally {
                MDC.clear();
            }
        }

        @Override
        public void onError(Throwable throwable) {
            coreSubscriber.currentContext().getOrEmpty("X-FLOW-ID")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(xFlowId -> MDC.put("X-FLOW-ID", xFlowId));
                
            try {
                coreSubscriber.onError(throwable);
            } finally {
                MDC.clear();
            }
        }

        @Override
        public void onComplete() {
            coreSubscriber.currentContext().getOrEmpty("X-FLOW-ID")
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .ifPresent(xFlowId -> MDC.put("X-FLOW-ID", xFlowId));
                
            try {
                coreSubscriber.onComplete();
            } finally {
                MDC.clear();
            }
        }

        @Override
        public reactor.util.context.Context currentContext() {
            return coreSubscriber.currentContext();
        }
    }
}