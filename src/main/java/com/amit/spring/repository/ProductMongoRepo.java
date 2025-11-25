package com.amit.spring.repository;

import com.amit.spring.doc.ProductDoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductMongoRepo extends MongoRepository<ProductDoc, String> {
    Optional<ProductDoc> findBySku(String sku);
}
