package ru.practicum.shareit.exception;

public class NotRequestException extends RuntimeException {
    public NotRequestException(String message) {
        super(message);
    }
}
