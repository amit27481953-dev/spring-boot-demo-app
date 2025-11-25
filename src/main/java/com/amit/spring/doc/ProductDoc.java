package com.amit.spring.doc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "products")
public class ProductDoc {
    @Id
    private String id;   // can store Postgres id as string or use sku as id
    private Long pgId;
    private String sku;
    private String name;
    private Double price;
    private String description;
    private java.time.Instant updatedAt;
}
