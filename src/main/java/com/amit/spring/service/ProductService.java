package com.amit.spring.service;

import com.amit.spring.doc.ProductDoc;
import com.amit.spring.dto.ProductDto;
import com.amit.spring.entity.ProductEntity;
import com.amit.spring.exception.ProductNotFoundException;
import com.amit.spring.mapper.Mapper;
import com.amit.spring.repository.ProductMongoRepo;
import com.amit.spring.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository pgRepo;
    private final ProductMongoRepo mongoRepo;
    private final ProductCacheService cacheService;
    private final Mapper mapper;

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
    public ProductDto createOrUpdate(ProductDto dto){
        ProductEntity ent = mapper.toEntity(dto);
        ProductEntity saved = pgRepo.save(ent);
        cacheService.evictCache(saved.getSku());
        ProductDoc doc = mapper.toDoc(saved);
        mongoRepo.save(doc);
        cacheService.putCache(doc);
        return mapper.toDto(doc);
    }
    @Transactional
    public void deleteBySku(String sku) {
        pgRepo.findBySku(sku).ifPresent(pgRepo::delete);
        cacheService.evictCache(sku);
        mongoRepo.findBySku(sku).ifPresent(mongoRepo::delete);
    }
}
