package com.vietmart.order.client;

import com.vietmart.order.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * FeignClient gọi user-service để lấy thông tin người mua.
 */
@FeignClient(name = "user-service")
public interface UserClient {

    /**
     * GET /api/users/{userId}
     */
    @GetMapping("/api/users/{userId}")
    UserInfo getUserById(@PathVariable("userId") Long userId);
}
