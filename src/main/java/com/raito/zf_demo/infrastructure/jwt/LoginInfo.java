package com.raito.zf_demo.infrastructure.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author raito
 * @since 2024/09/06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginInfo {
    private Long userId;
    private String username;
    private String email;
}
