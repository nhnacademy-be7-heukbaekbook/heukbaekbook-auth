package com.nhnacademy.heukbaekbook_auth.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberLoginRequest(
        @NotBlank String loginId,
        @NotBlank String password
) {
}
