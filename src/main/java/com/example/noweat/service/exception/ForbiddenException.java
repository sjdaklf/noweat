package com.example.noweat.service.exception;

import com.example.noweat.service.exception.enums.ErrorCode;
// 403 예외
public class ForbiddenException extends RuntimeException{
    private ErrorCode errorCode;

    public ForbiddenException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }
}
