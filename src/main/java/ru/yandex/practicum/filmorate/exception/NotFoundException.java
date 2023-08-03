package ru.yandex.practicum.filmorate.exception;

import java.io.IOException;

public class NotFoundException extends IOException {
    public NotFoundException(String message) {
        super(message);
    }
}
