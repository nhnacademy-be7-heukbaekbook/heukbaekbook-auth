package com.nhnacademy.heukbaekbook_auth.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequest(@NotBlank String accessToken) {
}
