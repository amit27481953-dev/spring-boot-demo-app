package com.amit.spring.listener;

import com.amit.spring.config.props.RabbitmqProps;
import com.amit.spring.event.ProductChangeEvent;
import com.amit.spring.exception.IllegalDbOperation;
import com.amit.spring.exception.RedisPutException;
import com.amit.spring.service.ProductCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ProductChangeListener {
    private final ProductCacheService cacheService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitmqProps props;

    @Async
    @Retryable(retryFor = RedisPutException.class, maxAttempts = 2, backoff =  @Backoff(delay = 500))
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductChange(ProductChangeEvent event){
        switch (event.operation()) {
            case CREATE -> {
                cacheService.putCache(event.productDoc());
                break;
            }
            case DELETE -> {
                cacheService.evictCache(event.sku());
                break;
            }
            case UPDATE -> {
                cacheService.evictCache(event.sku());
                cacheService.putCache(event.productDoc());
                break;
            }
            default -> throw  new IllegalDbOperation("No Db Action matched");
        };

    }
    @Recover
    public void handleFallback(RedisPutException ex, ProductChangeEvent event){
        rabbitTemplate.convertAndSend(props.getExchange(), props.getRouting_key(), event);
    }
}
