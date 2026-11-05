package com.company.wms.putaway.exception;

import com.company.wms.putaway.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String CORRELATION_MDC_KEY = "correlationId";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex,
                                                   HttpServletRequest request) {
        return buildResponse(ex.getMessage(), ex.getErrorCode(), request, null);
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ApiError> handleInvalidState(InvalidStateException ex,
                                                       HttpServletRequest request) {
        return buildResponse(ex.getMessage(), ex.getErrorCode(), request, null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException ex,
                                                       HttpServletRequest request) {
        return buildResponse(ex.getMessage(), ex.getErrorCode(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {
        List<ApiError.FieldViolation> fieldErrors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new ApiError.FieldViolation(
                    fe.getField(),
                    fe.getDefaultMessage(),
                    fe.getRejectedValue()));
        }
        return buildResponse("Validation failed",
                ErrorCode.VALIDATION_FAILED, request, fieldErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex,
                                                              HttpServletRequest request) {
        List<ApiError.FieldViolation> fieldErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(cv ->
                fieldErrors.add(new ApiError.FieldViolation(
                        cv.getPropertyPath().toString(),
                        cv.getMessage(),
                        cv.getInvalidValue())));
        return buildResponse("Validation failed",
                ErrorCode.VALIDATION_FAILED, request, fieldErrors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex,
                                                          HttpServletRequest request) {
        return buildResponse(ex.getMessage(), ErrorCode.INVALID_REQUEST, request, null);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex,
                                                       HttpServletRequest request) {
        return buildResponse(ex.getMessage(), ErrorCode.INVALID_STATE, request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex,
                                                  HttpServletRequest request) {
        log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildResponse("Internal server error",
                ErrorCode.INTERNAL_ERROR, request, null);
    }

    private ResponseEntity<ApiError> buildResponse(String message,
                                                   ErrorCode errorCode,
                                                   HttpServletRequest request,
                                                   List<ApiError.FieldViolation> fieldErrors) {
        ApiError body = new ApiError();
        body.setTimestamp(Instant.now());
        body.setStatus(errorCode.getHttpStatus());
        body.setError(HttpStatus.valueOf(errorCode.getHttpStatus()).getReasonPhrase());
        body.setCode(errorCode.getCode());
        body.setMessage(message);
        body.setPath(request.getRequestURI());
        body.setCorrelationId(MDC.get(CORRELATION_MDC_KEY));
        body.setFieldErrors(fieldErrors);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }
}