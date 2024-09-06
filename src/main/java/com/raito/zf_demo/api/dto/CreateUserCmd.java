package com.raito.zf_demo.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * @author raito
 * @since 2024/09/06
 */
public record CreateUserCmd(@NotBlank String username, @NotBlank String password, @Email String email) {
}
