package com.example.noweat.service.exception;

import com.example.noweat.service.exception.enums.ErrorCode;

// 410
public class GoneException extends RuntimeException{
    private ErrorCode errorCode;

    public GoneException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }
}
