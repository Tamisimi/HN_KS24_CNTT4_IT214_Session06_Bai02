package com.vietmart.order.client.fallback;

import com.vietmart.order.client.ProductClient;
import com.vietmart.order.dto.ProductInfo;
import com.vietmart.order.exception.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

/**
 * FallbackFactory cho ProductClient.
 * - Log đầy đủ exception
 * - getById()  → trả ProductInfo.fallback(id)
 * - getAll()   → trả danh sách rỗng
 * - Nếu 404    → ném ProductNotFoundException (giống RestTemplate cũ)
 */
@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    private static final Logger log = LoggerFactory.getLogger(ProductClientFallbackFactory.class);

    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {

            @Override
            public ProductInfo getById(Long id) {
                log.error("[ProductClient Fallback] getById({}) failed. Cause: {}",
                        id, cause.toString());

                // Giữ hành vi giống RestTemplate cũ: 404 → ném exception
                if (cause instanceof HttpClientErrorException.NotFound ||
                    (cause.getMessage() != null && cause.getMessage().contains("404"))) {
                    throw new ProductNotFoundException(id);
                }

                // Các lỗi khác (timeout, connection refused...) → trả fallback
                return ProductInfo.fallback(id);
            }

            @Override
            public List<ProductInfo> getAll() {
                log.error("[ProductClient Fallback] getAll() failed. Cause: {}",
                        cause.toString());
                // Trả danh sách rỗng theo yêu cầu
                return Collections.emptyList();
            }
        };
    }
}
