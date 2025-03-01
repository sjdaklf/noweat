package com.example.noweat.global.controller;

import com.example.noweat.service.exception.UnauthorizedException;
import com.example.noweat.service.exception.ForbiddenException;
import com.example.noweat.service.exception.NotFoundException;
import com.example.noweat.service.exception.ConflictException;
import com.example.noweat.service.exception.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex){
        Map<String, Object> responseMap = createResponseMap(ex.getErrorCode());
        return new ResponseEntity<>(responseMap, ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleForbiddenException(ForbiddenException ex){
        Map<String, Object> responseMap = createResponseMap(ex.getErrorCode());
        return new ResponseEntity<>(responseMap, ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handlerNotFoundException(NotFoundException ex){
        Map<String, Object> responseMap = createResponseMap(ex.getErrorCode());
        return new ResponseEntity<>(responseMap, ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleConflictException(ConflictException ex){
        Map<String, Object> responseMap = createResponseMap(ex.getErrorCode());
        return new ResponseEntity<>(responseMap, ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex){
        Map<String, Object> responseMap = new HashMap<>();

        BindingResult bindingResult = ex.getBindingResult();
        if(bindingResult.hasFieldErrors()){
            for(FieldError fieldError : bindingResult.getFieldErrors()){
                responseMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        }

        return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> createResponseMap(ErrorCode errorCode){
        HttpStatus httpStatus = errorCode.getHttpStatus();

        Map<String, Object> reponseMap = new HashMap<>();
        reponseMap.put("status", httpStatus.value());
        reponseMap.put("message", errorCode.getMessage());

        return reponseMap;
    }
}
