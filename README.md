# Bài 2 Session 06 — Chuyển đổi ProductServiceClientRT sang FeignClient

**Cấp độ:** Vận dụng cơ bản / chuyên sâu  
**Session 06 — Giao tiếp đồng bộ (RestTemplate → OpenFeign)**

---

## 1. Mục tiêu

- Chuyển từ gọi **imperative** (`RestTemplate`) sang **declarative** (`@FeignClient`).
- Bổ sung `UserClient` để lấy thông tin người mua.
- Cài đặt `FallbackFactory` xử lý lỗi đầy đủ (log exception + trả giá trị dự phòng).

---

## 2. So sánh RestTemplate vs FeignClient

| Tiêu chí | RestTemplate (ProductServiceClientRT) | FeignClient (ProductClient) |
|----------|---------------------------------------|-----------------------------|
| **Số dòng code** | ~25–30 dòng (class + try-catch) | ~10–15 dòng (chỉ interface) |
| **Cách viết** | Imperative (tự viết URL, gọi method) | Declarative (khai báo method như local) |
| **Xử lý lỗi** | try-catch thủ công từng chỗ | Fallback / FallbackFactory tập trung |
| **Load Balancing** | Cần `@LoadBalanced RestTemplate` | Tự tích hợp với Eureka + LoadBalancer |
| **Dễ đọc / bảo trì** | Trung bình | Cao (giống gọi method nội bộ) |
| **Phù hợp khi** | Logic phức tạp, cần fine-control HTTP | Gọi service đơn giản, nhiều endpoint, team muốn code gọn |

**Lập luận chọn lựa:**

- **Dùng FeignClient** khi: gọi nhiều API của service khác, muốn code gọn, dễ đọc, có sẵn Fallback/Circuit Breaker.
- **Dùng RestTemplate (hoặc WebClient)** khi: cần tùy biến sâu (header động phức tạp, streaming, upload file lớn), hoặc logic xử lý response rất đặc thù.

Trong hầu hết microservice hiện đại → **FeignClient được ưu tiên** vì giảm boilerplate và tích hợp tốt với Spring Cloud.

---

## 3. Cấu trúc code đã tạo

```
order-service/
└── src/main/java/com/vietmart/order/
    ├── OrderServiceApplication.java          # @EnableFeignClients
    ├── client/
    │   ├── ProductClient.java                # @FeignClient(name="product-service")
    │   ├── UserClient.java                   # @FeignClient(name="user-service")
    │   └── fallback/
    │       └── ProductClientFallbackFactory.java
    ├── dto/
    │   ├── ProductInfo.java
    │   └── UserInfo.java
    └── exception/
        └── ProductNotFoundException.java
```

---

## 4. Cách chạy thử

1. Chạy Eureka Server.
2. Chạy `product-service` và `user-service` (đăng ký Eureka).
3. Chạy `order-service`.
4. Gọi API order-service có sử dụng `ProductClient` / `UserClient`.
5. Tắt `product-service` → quan sát log FallbackFactory và giá trị fallback được trả về.

---

## 5. Dependency cần có (build.gradle)

```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer'
```
