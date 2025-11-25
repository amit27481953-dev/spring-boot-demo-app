package com.amit.spring.mapper;

import com.amit.spring.doc.ProductDoc;
import com.amit.spring.dto.ProductDto;
import com.amit.spring.entity.ProductEntity;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mapper {

    public ProductDto toDto(ProductDoc doc);

    @Mapping(target = "id", source = "sku")
    @Mapping(target = "pgId", source = "id")
    @Mapping(target = "updatedAt", source = "updatedAt")
    public ProductDoc toDoc(ProductEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    public ProductEntity toEntity(ProductDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sku", ignore = true)
    public void updateEntity(ProductDto dto, @MappingTarget ProductEntity entity);
}
