package com.amit.spring.config.props;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    private final RabbitmqProps props;

    @Bean
    public TopicExchange productExchange(){
        return new TopicExchange(props.getExchange(), Boolean.TRUE, Boolean.FALSE);
    }
    @Bean
    public Queue productQueue(){
        return QueueBuilder.durable(props.getQueue())
                .withArgument("x-dead-letter-exchange", props.getDlx())
                .withArgument("x-dead-letter-routing-key", props.getDlq())
                .build();
    }
    @Bean
    public Binding productBinding(){
        return BindingBuilder.bind(productQueue())
                .to(productExchange())
                .with(props.getRouting_key());
    }
    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(props.getDlx());
    }

    @Bean
    public Queue deadLetterQueue(){
        return QueueBuilder.durable(props.getDlq()).build();
    }

    public Binding deadLetterQueueBinding(){
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(props.getDlq());
    }

}
