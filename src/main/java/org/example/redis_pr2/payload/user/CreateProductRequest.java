package org.example.redis_pr2.payload.user;

public record CreateProductRequest(
        String title,
        String description,
        Double price
) {
}
