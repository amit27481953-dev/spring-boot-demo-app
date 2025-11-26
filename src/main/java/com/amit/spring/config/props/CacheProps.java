package com.amit.spring.config.props;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@Getter
@ConfigurationProperties(prefix= "app.cache")
public class CacheProps {
    private final  long ttlSecond;
    private final String topnkey;
}
