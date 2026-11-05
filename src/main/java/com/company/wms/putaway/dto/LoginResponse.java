package com.company.wms.putaway.dto;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresIn
) {
}