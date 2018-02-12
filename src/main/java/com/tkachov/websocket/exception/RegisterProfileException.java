package com.tkachov.websocket.exception;

public class RegisterProfileException extends RuntimeException {

    public RegisterProfileException() {
        super();
    }

    public RegisterProfileException(String message) {
        super(message);
    }

    public RegisterProfileException(Throwable cause) {
        super(cause);
    }

    public RegisterProfileException(String message, Throwable cause) {
        super(message, cause);
    }

}
