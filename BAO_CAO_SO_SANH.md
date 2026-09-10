# Báo cáo so sánh RestTemplate vs FeignClient

## 1. Code RestTemplate (ProductServiceClientRT) — tham khảo Bài 1

```java
@Component
public class ProductServiceClientRT {
    private final RestTemplate restTemplate;

    public ProductServiceClientRT(@LoadBalanced RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductInfo getById(Long id) {
        try {
            return restTemplate.getForObject(
                    "http://product-service/api/products/{id}",
                    ProductInfo.class, id);
        } catch (ResourceAccessException e) {
            return ProductInfo.fallback(id);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException(id);
        }
    }

    public List<ProductInfo> getAll() {
        try {
            ProductInfo[] arr = restTemplate.getForObject(
                    "http://product-service/api/products",
                    ProductInfo[].class);
            return arr != null ? Arrays.asList(arr) : Collections.emptyList();
        } catch (ResourceAccessException e) {
            return Collections.emptyList();
        }
    }
}
```

**Ước lượng:** ~30–35 dòng (không tính import).

---

## 2. Code FeignClient (ProductClient)

```java
@FeignClient(name = "product-service", fallbackFactory = ProductClientFallbackFactory.class)
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductInfo getById(@PathVariable("id") Long id);

    @GetMapping("/api/products")
    List<ProductInfo> getAll();
}
```

**Ước lượng:** ~12 dòng (interface) + FallbackFactory riêng.

---

## 3. Bảng so sánh số dòng & đặc điểm

| Tiêu chí                    | RestTemplate                  | FeignClient                          |
|----------------------------|-------------------------------|--------------------------------------|
| Số dòng phần gọi API       | ~30–35 dòng                   | ~12 dòng (interface)                 |
| Cần class implementation?  | Có                            | Không (proxy tự sinh)                |
| Xử lý lỗi                  | try-catch rải rác             | FallbackFactory tập trung            |
| URL cứng                   | Có (`http://product-service/...`) | Không (khai báo path thôi)        |
| Load Balancing             | Cần `@LoadBalanced`           | Tự động qua Eureka                   |
| Dễ thêm endpoint mới       | Phải viết thêm method + try-catch | Chỉ thêm 1 method annotation      |

---

## 4. Khi nào dùng cái nào?

### Nên dùng **FeignClient** khi:
- Gọi nhiều API của cùng một service (CRUD đầy đủ).
- Team muốn code declarative, dễ đọc, dễ review.
- Cần tích hợp Circuit Breaker / Fallback thống nhất.
- Dự án đã dùng Spring Cloud đầy đủ (Eureka, Gateway…).

### Nên dùng **RestTemplate / WebClient** khi:
- Cần kiểm soát rất chi tiết (header động phức tạp, timeout từng request khác nhau).
- Upload/download file lớn, streaming.
- Logic xử lý response không thể map đơn giản sang DTO.
- Dự án cũ chưa sẵn sàng chuyển sang OpenFeign.

**Kết luận bài học:**  
Với hầu hết giao tiếp inter-service thông thường trong microservice, **FeignClient** giúp giảm đáng kể boilerplate và tăng khả năng bảo trì, đồng thời vẫn giữ được khả năng xử lý lỗi tốt thông qua `FallbackFactory`.
