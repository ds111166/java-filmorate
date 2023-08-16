package ru.yandex.practicum.filmorate.exception;

import java.io.IOException;

public class ValidationException extends RuntimeException {
    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }
}
