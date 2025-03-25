package com.empmongo.config;

import jakarta.annotation.PostConstruct;
import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;
import reactor.util.context.Context;

@Configuration
public class ReactiveMdcConfig {
    private static final String TRANSACTION_ID = "X-Transaction-ID";

    @PostConstruct
    public void initContextPropagation() {
        Hooks.onEachOperator("mdcContextOperator", Operators.lift((scannable, coreSubscriber) ->
                new MdcContextLifter<>(coreSubscriber)));
    }

    static class MdcContextLifter<T> implements CoreSubscriber<T> {

        private final CoreSubscriber<? super T> coreSubscriber;

        MdcContextLifter(CoreSubscriber<? super T> coreSubscriber) {
            this.coreSubscriber = coreSubscriber;
        }

        @Override
        public Context currentContext() {
            return coreSubscriber.currentContext();
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            copyContextToMdc(currentContext());
            coreSubscriber.onSubscribe(subscription);
        }

        @Override
        public void onNext(T t) {
            copyContextToMdc(currentContext());
            coreSubscriber.onNext(t);
        }

        @Override
        public void onError(Throwable throwable) {
            copyContextToMdc(currentContext());
            coreSubscriber.onError(throwable);
        }

        @Override
        public void onComplete() {
            copyContextToMdc(currentContext());
            coreSubscriber.onComplete();
        }

        private void copyContextToMdc(Context context) {
            if (!context.isEmpty() && context.hasKey(TRANSACTION_ID)) {
                MDC.put(TRANSACTION_ID, context.get(TRANSACTION_ID));
            } else {
                MDC.remove(TRANSACTION_ID);
            }
        }
    }
}
