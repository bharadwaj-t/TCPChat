package dev.lifeofcode.chat.exceptions;

public class CommandNotFoundException extends RuntimeException{
    public CommandNotFoundException() {
    }

    public CommandNotFoundException(String message) {
        super(message);
    }

    public CommandNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
