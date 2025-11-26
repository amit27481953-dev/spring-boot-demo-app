package com.amit.spring.config.props;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "app.mq")
public class RabbitmqProps {
    private final String exchange;
    private final String queue;
    private final String routing_key;
    private final String dlx;
    private final String dlq;
}
