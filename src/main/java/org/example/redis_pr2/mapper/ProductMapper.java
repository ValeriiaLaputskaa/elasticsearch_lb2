package org.example.redis_pr2.mapper;

import org.example.redis_pr2.entity.Product;
import org.example.redis_pr2.elasticsearch.ProductDocument;

public class ProductMapper {

    public static ProductDocument mapToDocument(Product product) {
        return ProductDocument.builder()
                .id(product.getId().toString())
                .title(product.getTitle())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }

    public static Product mapToEntity(ProductDocument doc) {
        return Product.builder()
                .id(Long.parseLong(doc.getId()))
                .title(doc.getTitle())
                .description(doc.getDescription())
                .price(doc.getPrice())
                .build();
    }
}
