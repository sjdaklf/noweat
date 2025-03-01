package com.example.noweat.service.exception;

import com.example.noweat.service.exception.enums.ErrorCode;
// 409 예외
public class ConflictException extends RuntimeException{
    private ErrorCode errorCode;

    public ConflictException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }
}
