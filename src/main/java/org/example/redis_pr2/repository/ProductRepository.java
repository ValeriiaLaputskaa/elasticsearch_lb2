package org.example.redis_pr2.repository;

import org.example.redis_pr2.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
