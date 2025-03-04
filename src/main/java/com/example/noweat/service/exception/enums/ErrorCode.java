package com.example.noweat.service.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 400 에러
    INVALID_USER_ROLE("유효하지 않은 사용자 역할입니다.", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_REQUIRED("AccessToken 이 필요합니다.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_REQUIRED("RefreshToken 이 필요합니다.", HttpStatus.BAD_REQUEST),

    // 401 에러
    INVALID_PASSWORD("잘못된 비밀번호입니다.", HttpStatus.UNAUTHORIZED),
    ACCESS_TOKEN_EXPIRED("만료된 AccessToken 입니다.", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_EXPIRED("만료된 RefreshToken 입니다.", HttpStatus.UNAUTHORIZED),
    MISSING_AUTHORIZATION_HEADER("Authorization 헤더가 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_BEARER_TOKEN("유효한 Bearer 토큰이 필요합니다.", HttpStatus.UNAUTHORIZED),
    JWT_ERROR("JWT 처리 중 오류가 발생했습니다.", HttpStatus.UNAUTHORIZED),
    NOT_OWNER("사장 권한을 가지고 있지않습니다.", HttpStatus.UNAUTHORIZED),
    NOT_USER("유저 권한을 가지고 있지않습니다.", HttpStatus.UNAUTHORIZED),
    DELETED_USER("탈퇴한 유저입니다.", HttpStatus.UNAUTHORIZED),


    // 403 에러

    // 404 에러
    NOT_FOUND_USER("존재하지 않는 유저입니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_REFRESH_TOKEN("존재하지 않는 RefreshToken 입니다.", HttpStatus.NOT_FOUND),

    //409 에러
    EMAIL_ALREADY_EXISTS("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    USER_ALREADY_LOGGED_IN("이미 로그인된 사용자입니다.", HttpStatus.CONFLICT),
    SAME_AS_PREVIOUS_PASSWORD("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", HttpStatus.CONFLICT);

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
