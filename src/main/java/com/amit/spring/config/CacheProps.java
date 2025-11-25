package com.amit.spring.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Getter
@ConfigurationProperties(prefix= "app.cache")
public class CacheProps {
    private final  long ttlSecond;
    private final String topnkey;
}
