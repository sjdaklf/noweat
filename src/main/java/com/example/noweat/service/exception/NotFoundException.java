package com.example.noweat.service.exception;

import com.example.noweat.service.exception.enums.ErrorCode;
// 404 예외
public class NotFoundException extends RuntimeException{
    private ErrorCode errorCode;

    public NotFoundException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }
}
