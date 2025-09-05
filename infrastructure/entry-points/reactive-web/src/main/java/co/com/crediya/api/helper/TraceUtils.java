package co.com.crediya.api.helper;

import lombok.NoArgsConstructor;
import org.slf4j.MDC;

@NoArgsConstructor
public final class TraceUtils {

    private static final String TRACE_KEY = "X-FLOW-ID";

    public static String getCurrentTrace() {
        return MDC.get(TRACE_KEY);
    }
}