package org.example.redis_pr2.service;

import lombok.RequiredArgsConstructor;
import org.example.redis_pr2.elasticsearch.ProductDocument;
import org.example.redis_pr2.elasticsearch.ProductElasticsearchRepository;
import org.example.redis_pr2.entity.Product;
import org.example.redis_pr2.mapper.ProductMapper;
import org.example.redis_pr2.payload.user.CreateProductRequest;
import org.example.redis_pr2.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final RedisCacheService redisCacheService;
    private final ExternalProductService externalProductService;
    private final ProductRepository productRepository;
    private final ProductElasticsearchRepository productElasticsearchRepository;


    public Product getProductById(Long id) {
        String cacheKey = "product:" + id;
        Product cachedUser = (Product) redisCacheService.get(cacheKey);

        if (cachedUser != null) {
            return cachedUser;
        }

        Product externalProduct = externalProductService.fetchProductById(id);
        if (externalProduct != null) {
            redisCacheService.save(cacheKey, externalProduct);
        }

        return externalProduct;
    }

    public Product createProduct(CreateProductRequest createProductRequest) {
        Product product = Product.builder()
                .title(createProductRequest.title())
                .description(createProductRequest.description())
                .price(createProductRequest.price())
                .build();
        Product savedProduct = productRepository.save(product);
        redisCacheService.save("product:" + savedProduct.getId(), savedProduct);
        productElasticsearchRepository.save(ProductMapper.mapToDocument(savedProduct));
        return savedProduct;
    }

    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
        redisCacheService.delete("product:" + id);
    }

    public List<Product> searchProductsWithCacheAndElastic(String title) {
        String cacheKey = "search:title:" + title.toLowerCase();

        long startTime = System.currentTimeMillis();

        Object cached = redisCacheService.get(cacheKey);
        if (cached != null) {
            long endTime = System.currentTimeMillis();
            System.out.println("Redis search time: " + (endTime - startTime) + " ms");
            return (List<Product>) cached;
        }

        List<ProductDocument> documents = productElasticsearchRepository.findByTitleContainingIgnoreCase(title);
        List<Product> products = documents.stream()
                .map(ProductMapper::mapToEntity)
                .toList();

        long endTime = System.currentTimeMillis();
        System.out.println("Elasticsearch search time: " + (endTime - startTime) + " ms");

        System.out.println("Saving to Redis with key: " + cacheKey);
        redisCacheService.save(cacheKey, products, 1);
        return products;
    }

    public List<Product> searchProductsInPostgres(String title) {
        long startTime = System.currentTimeMillis();

        List<Product> products = productRepository.findByTitleContainingIgnoreCase(title);

        long endTime = System.currentTimeMillis();
        System.out.println("PostgreSQL search time: " + (endTime - startTime) + " ms");

        return products;
    }
}


