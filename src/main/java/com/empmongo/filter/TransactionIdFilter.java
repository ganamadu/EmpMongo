package com.empmongo.filter;

import org.slf4j.MDC;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TransactionIdFilter implements WebFilter {

    private static final String TRANSACTION_ID = "X-Transaction-ID";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // Check for existing Transaction ID; otherwise, generate one
        String transactionId = request.getHeaders().getFirst(TRANSACTION_ID);
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString(); // Generate new Transaction ID
        }

        // Add Transaction ID to response headers for better traceability
        response.getHeaders().add(TRANSACTION_ID, transactionId);

        // Add Transaction ID to MDC (Mapped Diagnostic Context) for log tracing
        MDC.put(TRANSACTION_ID, transactionId);

        // Continue the request chain
        return chain.filter(exchange)
                .doFinally(signalType -> MDC.remove(TRANSACTION_ID)); // Clean up MDC after request completes
    }


}
