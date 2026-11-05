package com.company.wms.putaway.exception;

public class InvalidStateException extends RuntimeException {

    private final ErrorCode errorCode;

    public InvalidStateException(String message) {
        super(message);
        this.errorCode = ErrorCode.INVALID_STATE;
    }

    public InvalidStateException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}