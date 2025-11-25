package com.amit.spring.controller;

import com.amit.spring.dto.ProductDto;
import com.amit.spring.service.ProductService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping(value = "/{sku}")
    public ResponseEntity<ProductDto> get(@PathVariable String sku){
        return ResponseEntity.ok(productService.getBySku(sku));
    }

    @PostMapping()
    public ResponseEntity<ProductDto> create(@Validated  @RequestBody ProductDto dto){
        ProductDto res = productService.createOrUpdate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
    @PutMapping({"/{sku}"})
    public ResponseEntity<ProductDto> update(@PathVariable @NotNull String sku, @Validated @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.createOrUpdate(dto));
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<ProductDto> delete(@PathVariable @NotNull String sku){
        productService.deleteBySku(sku);
        return ResponseEntity.noContent().build();
    }
}
