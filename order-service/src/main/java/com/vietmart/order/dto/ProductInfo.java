package com.vietmart.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInfo {

    private Long id;
    private String name;
    private Double price;
    private boolean available;

    /** Giá trị dự phòng khi product-service lỗi / timeout */
    public static ProductInfo fallback(Long id) {
        return ProductInfo.builder()
                .id(id)
                .name("UNKNOWN_PRODUCT")
                .price(0.0)
                .available(false)
                .build();
    }
}
