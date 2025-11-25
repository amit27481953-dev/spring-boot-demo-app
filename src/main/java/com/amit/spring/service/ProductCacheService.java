package com.amit.spring.service;

import com.amit.spring.config.CacheProps;
import com.amit.spring.doc.ProductDoc;
import com.amit.spring.repository.ProductMongoRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductCacheService {
    private final RedisTemplate<String, String> redis;
    private final ProductMongoRepo mongoRepo;
    private final ObjectMapper mapper;
    private final CacheProps cacheProps;

    private String cacheKey(String sku) {
        return "product:" + sku;
    }

    public Optional<ProductDoc> getFromCache(String sku) {
        String key = cacheKey(sku);
        String json = redis.opsForValue().get(key);
        if (null == json) {
            return Optional.empty();
        }
        try {
            redis.opsForZSet().incrementScore(cacheProps.getTopnkey(), sku, 1);
            ProductDoc productDoc = mapper.readValue(json, ProductDoc.class);
            return Optional.of(productDoc);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<ProductDoc> loadFromMongoAndCache(String sku) {
        Optional<ProductDoc> doc = mongoRepo.findBySku(sku);
        if (doc.isPresent()) {
            try {
                String json = mapper.writeValueAsString(doc.get());
                redis.opsForValue().set(cacheKey(sku), json, Duration.ofSeconds(cacheProps.getTtlSecond()));
                redis.opsForZSet().incrementScore(cacheProps.getTopnkey(), sku, 1);
            } catch (Exception e) {
                log.error("Error While caching from mango the sku {} in redis", sku);
            }
        }
        return doc;
    }

    public void evictCache(String sku) {
        redis.delete(cacheKey(sku));
        redis.opsForZSet().remove(cacheProps.getTopnkey(), sku);
    }

    public void putCache(ProductDoc doc) {
        try {
            String sku = doc.getSku();
            ;
            String json = mapper.writeValueAsString(doc);
            redis.opsForValue().set(cacheKey(sku), json, Duration.ofSeconds(cacheProps.getTtlSecond()));
            redis.opsForZSet().incrementScore(cacheProps.getTopnkey(), sku, 1);
        } catch (Exception e) {
            log.error("Error While caching the sku {} in redis", doc.getSku());
        }
    }

    public List<String> topN(int n) {
        Set<String> s = redis.opsForZSet().reverseRange(cacheProps.getTopnkey(), 0, n - 1);
        if (s == null) return List.of();
        return new ArrayList<>(s);
    }
}
