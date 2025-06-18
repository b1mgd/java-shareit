package ru.practicum.shareit.exception;

public class ArgumentsNotValidException extends RuntimeException {
    public ArgumentsNotValidException(String message) {
        super(message);
    }
}
