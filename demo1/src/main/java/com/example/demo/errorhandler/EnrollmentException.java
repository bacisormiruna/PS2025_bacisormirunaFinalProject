package com.example.demo.errorhandler;

public class EnrollmentException extends Exception{
    public EnrollmentException(String message) {
        super(message);
    }
    public EnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
