package com.amit.spring.scheduler;

import com.amit.spring.config.SyncProps;
import com.amit.spring.entity.ProductEntity;
import com.amit.spring.mapper.Mapper;
import com.amit.spring.repository.ProductMongoRepo;
import com.amit.spring.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class PostgresToMongoSyncJob {
    private final ProductRepository pgRepo;
    private final ProductMongoRepo mongoRepo;
    private final Mapper mapper;
    private final RedisTemplate<String,String> redisTemplate;
    private final SyncProps syncProps;
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("ApplicationReadyEvent Started ....");
        runSync();
    }

    @Scheduled(fixedDelayString = "${app.sync.every-ms}")
    public void runSync() {
        log.info("Sync Job Started......");
        String last = redisTemplate.opsForValue().get(syncProps.getLastSyncKey());
        Instant lastSync = last == null ? Instant.EPOCH : Instant.parse(last);
        Pageable pageable = PageRequest.of(0, syncProps.getBatchSize(), Sort.by("updatedAt").ascending());
        List<ProductEntity> page;
        Instant maxSeen = lastSync;

        do{
            page = pgRepo.findByUpdatedAtAfter(lastSync, pageable);
            for (ProductEntity entity : page){
                mongoRepo.save(mapper.toDoc(entity));
                if (entity.getUpdatedAt().isAfter(maxSeen)) maxSeen = entity.getUpdatedAt();
            }
        }while (page.size() == syncProps.getBatchSize());
        if (maxSeen.isAfter(lastSync)) {
            redisTemplate.opsForValue().set(syncProps.getLastSyncKey(), maxSeen.toString());
        }
        log.info("Sync Job Finished......");
    }

}
