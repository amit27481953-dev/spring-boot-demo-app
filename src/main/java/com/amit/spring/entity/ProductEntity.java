package com.amit.spring.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String sku;
    private String name;
    private Double price;
    @Column(columnDefinition = "text")
    private String description;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @PrePersist
    @PreUpdate
    public void touch(){
        this.updatedAt = Instant.now();
    }
}
