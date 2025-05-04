package org.example.redis_pr2.controller;

import lombok.RequiredArgsConstructor;
import org.example.redis_pr2.entity.Product;
import org.example.redis_pr2.payload.user.CreateProductRequest;
import org.example.redis_pr2.service.ExternalProductService;
import org.example.redis_pr2.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final ExternalProductService externalProductService;

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        Product createdProduct = productService.createProduct(createProductRequest);
        externalProductService.createExternalProduct(createdProduct);
        return ResponseEntity.ok(createdProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        externalProductService.deleteExternalProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String title) {
        return ResponseEntity.ok(productService.searchProductsWithCacheAndElastic(title));
    }

    @GetMapping("/search/pg")
    public ResponseEntity<List<Product>> searchInPostgres(@RequestParam String title) {
        return ResponseEntity.ok(productService.searchProductsInPostgres(title));
    }

}
