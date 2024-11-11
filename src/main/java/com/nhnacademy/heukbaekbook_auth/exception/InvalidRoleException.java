package com.nhnacademy.heukbaekbook_auth.exception;

public class InvalidRoleException extends RuntimeException{
    public InvalidRoleException(String message) {
        super(message);
    }
}
