package com.nhnacademy.heukbaekbook_auth.service;

import com.nhnacademy.heukbaekbook_auth.dto.UserRoleAndId;
import com.nhnacademy.heukbaekbook_auth.exception.InvalidRoleException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeyMappingService {
    private static final String KEY_MAPPING_PREFIX = "keyMapping:";

    private final RedisService redisService;

    public void saveMapping(String randomKey, Long id, String role, long expirationTime) {
        String key = KEY_MAPPING_PREFIX + randomKey;
        String value = role + ":" + id;
        redisService.save(key, value, expirationTime);
    }

    public String findUserIdAndRoleByRandomKey(String randomKey) {
        String key = KEY_MAPPING_PREFIX + randomKey;
        return redisService.findByKey(key);
    }

    public void deleteByRandomKey(String randomKey) {
        String key = KEY_MAPPING_PREFIX + randomKey;
        redisService.deleteByKey(key);
    }

    public UserRoleAndId getUserRoleAndIdByRandomKey(String randomKey) {
        String userIdAndRole = findUserIdAndRoleByRandomKey(randomKey);
        if (userIdAndRole == null) {
            return null;
        }
        String[] parts = userIdAndRole.split(":");
        if (parts.length != 2) {
            throw new InvalidRoleException("역할 정보가 잘못되었습니다.");
        }
        return new UserRoleAndId(parts[0], Long.parseLong(parts[1]));
    }
}
