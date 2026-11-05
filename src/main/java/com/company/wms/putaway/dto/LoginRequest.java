package com.company.wms.putaway.dto;

public record LoginRequest(
        String username,
        String password
) {
}