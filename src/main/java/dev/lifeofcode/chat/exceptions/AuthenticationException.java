package dev.lifeofcode.chat.exceptions;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(){}

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
