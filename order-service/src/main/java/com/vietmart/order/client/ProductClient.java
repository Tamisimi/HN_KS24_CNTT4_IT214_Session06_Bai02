package com.vietmart.order.client;

import com.vietmart.order.client.fallback.ProductClientFallbackFactory;
import com.vietmart.order.dto.ProductInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * FeignClient thay thế hoàn toàn ProductServiceClientRT (RestTemplate).
 * Không cần class implementation — Feign tự sinh proxy lúc runtime.
 */
@FeignClient(
        name = "product-service",
        fallbackFactory = ProductClientFallbackFactory.class
)
public interface ProductClient {

    /**
     * GET /api/products/{id}
     */
    @GetMapping("/api/products/{id}")
    ProductInfo getById(@PathVariable("id") Long id);

    /**
     * GET /api/products
     */
    @GetMapping("/api/products")
    List<ProductInfo> getAll();
}
