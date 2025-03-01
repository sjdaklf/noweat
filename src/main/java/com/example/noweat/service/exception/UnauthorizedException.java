package com.example.noweat.service.exception;

import com.example.noweat.service.exception.enums.ErrorCode;

// 401 예외
public class UnauthorizedException extends RuntimeException{
    private ErrorCode errorCode;

    public UnauthorizedException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }
}
