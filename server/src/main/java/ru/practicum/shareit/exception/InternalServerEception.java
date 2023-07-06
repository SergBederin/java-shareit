package ru.practicum.shareit.exception;

public class InternalServerEception extends Throwable {
    public InternalServerEception(String message) {
        super(message);
    }
}
