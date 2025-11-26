package com.amit.spring.config.props;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@Getter
@ConfigurationProperties(prefix = "app.sync")
public class SyncProps {

    private final int batchSize;
    private final String lastSyncKey;
    private final long everyMs;
}
