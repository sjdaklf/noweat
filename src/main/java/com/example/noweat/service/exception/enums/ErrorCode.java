package com.example.noweat.service.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 400 에러
    INVALID_USER_ROLE("유효하지 않은 사용자 역할입니다.", HttpStatus.BAD_REQUEST),

    // 401 에러

    // 403 에러

    // 404 에러

    //409 에러
    EMAIL_ALREADY_EXISTS("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT);

    private String message;
    private HttpStatus httpStatus;

    private ErrorCode(String message, HttpStatus httpStatus){
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus(){
        return httpStatus;
    }
}
