package com.vietmart.order.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private Long id;
    private String fullName;
    private String email;
    private String phone;

    public static UserInfo fallback(Long id) {
        return UserInfo.builder()
                .id(id)
                .fullName("UNKNOWN_USER")
                .email("unknown@example.com")
                .phone("")
                .build();
    }
}
