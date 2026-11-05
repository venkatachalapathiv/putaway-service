package com.company.wms.putaway.exception;

public enum ErrorCode {

    VALIDATION_FAILED("WMS-400-VALIDATION", 400),
    INVALID_REQUEST("WMS-400-INVALID", 400),
    UNAUTHORIZED("WMS-401", 401),
    FORBIDDEN("WMS-403", 403),
    RESOURCE_NOT_FOUND("WMS-404", 404),
    DUPLICATE_RESOURCE("WMS-409-DUPLICATE", 409),
    INVALID_STATE("WMS-409-STATE", 409),
    BUSINESS_RULE_VIOLATION("WMS-422", 422),
    DOWNSTREAM_SERVICE_ERROR("WMS-502", 502),
    INTERNAL_ERROR("WMS-500", 500);

    private final String code;
    private final int httpStatus;

    ErrorCode(String code, int httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}