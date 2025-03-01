package com.example.noweat.service.exception;

import com.example.noweat.service.exception.enums.ErrorCode;

public class BadRequestException extends RuntimeException{
    private ErrorCode errorCode;

    public BadRequestException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }
}
