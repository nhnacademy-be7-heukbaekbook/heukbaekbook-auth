package com.nhnacademy.heukbaekbook_auth.dto;

import java.io.Serializable;

public record UserInfoResponse(Long id, String loginId, String password) implements Serializable {
}
