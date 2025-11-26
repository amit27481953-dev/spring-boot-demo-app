package com.amit.spring.service;

import com.amit.spring.doc.ProductDoc;
import com.amit.spring.dto.ProductDto;
import com.amit.spring.entity.ProductEntity;
import com.amit.spring.enu.Operation;
import com.amit.spring.event.ProductChangeEvent;
import com.amit.spring.exception.ProductNotFoundException;
import com.amit.spring.mapper.Mapper;
import com.amit.spring.repository.ProductMongoRepo;
import com.amit.spring.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductService {
    private final ProductRepository pgRepo;
    private final ProductMongoRepo mongoRepo;
    private final ProductCacheService cacheService;
    private final Mapper mapper;
    private ApplicationEventPublisher publisher;

    public ProductDto getBySku(String sku){
        Optional<ProductDoc> cached = cacheService.getFromCache(sku);
        if(cached.isPresent()){
            return mapper.toDto(cached.get());
        }
        Optional<ProductDoc> productDoc = cacheService.loadFromMongoAndCache(sku);
        if (productDoc.isPresent()){
            return mapper.toDto(productDoc.get());
        }
        Optional<ProductEntity> pentity = pgRepo.findBySku(sku);
        if (pentity.isPresent()){
            ProductDoc doc = mapper.toDoc(pentity.get());
            mongoRepo.save(doc);
            cacheService.putCache(doc);
            return mapper.toDto(doc);
        }
        throw new ProductNotFoundException("Product Not Found");
    }

    @Transactional
    public ProductDto create(ProductDto dto){
        ProductEntity ent = mapper.toEntity(dto);
        ProductEntity saved = pgRepo.save(ent);
        ProductDoc doc = mapper.toDoc(saved);
        publisher.publishEvent(new ProductChangeEvent(dto.sku(),doc, Operation.CREATE));
        return mapper.toDto(doc);
    }
    @Transactional
    public ProductDto update(ProductDto dto){
        Optional<ProductEntity> productEntityContainer = pgRepo.findBySku(dto.sku());
        if (productEntityContainer.isEmpty()){
            log.error("NO product Found fot the Sku {} ", dto.sku());
            throw new ProductNotFoundException("No product Found");
        }
        ProductEntity productEntity = productEntityContainer.get();
        mapper.updateEntity(dto, productEntity);
        ProductEntity saved = pgRepo.save(productEntity);
        ProductDoc doc = mapper.toDoc(saved);
        publisher.publishEvent(new ProductChangeEvent(dto.sku(),doc, Operation.CREATE));
        return mapper.toDto(doc);
    }
    @Transactional
    public void deleteBySku(String sku) {
        pgRepo.findBySku(sku).ifPresent(pgRepo::delete);
        publisher.publishEvent(new ProductChangeEvent(sku,null, Operation.CREATE));
    }
}
