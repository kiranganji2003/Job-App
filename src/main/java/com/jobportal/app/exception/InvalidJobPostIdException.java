package com.jobportal.app.exception;

public class InvalidJobPostIdException extends RuntimeException {
    public InvalidJobPostIdException(String msg) {
        super(msg);
    }
}
