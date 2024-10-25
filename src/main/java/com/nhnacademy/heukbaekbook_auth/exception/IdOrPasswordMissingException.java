package com.nhnacademy.heukbaekbook_auth.exception;

public class IdOrPasswordMissingException extends RuntimeException {
    private static final String MESSAGE = "로그인 요청이 올바르지 않습니다.";

    public IdOrPasswordMissingException() {
        super(MESSAGE);
    }
}
