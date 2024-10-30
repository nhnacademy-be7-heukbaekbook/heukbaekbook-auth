package com.nhnacademy.heukbaekbook_auth.exception;

public class InvalidLoginRequestException extends RuntimeException {
    public InvalidLoginRequestException(String message) {
        super(message);
    }
}
