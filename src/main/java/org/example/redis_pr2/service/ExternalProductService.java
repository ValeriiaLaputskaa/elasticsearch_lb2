package org.example.redis_pr2.service;

import lombok.RequiredArgsConstructor;
import org.example.redis_pr2.entity.Product;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExternalProductService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://fakestoreapi.com/products";

    public Product fetchProductById(Long id) {
        try {
            return restTemplate.getForObject(BASE_URL + "/" + id, Product.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Product createExternalProduct(Product product) {
        HttpEntity<Product> request = new HttpEntity<>(product);
        return restTemplate.postForObject(BASE_URL, request, Product.class);
    }

    public void deleteExternalProduct(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}
