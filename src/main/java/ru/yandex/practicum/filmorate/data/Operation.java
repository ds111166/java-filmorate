package ru.yandex.practicum.filmorate.data;

import lombok.Getter;

@Getter
public enum Operation {
    ADD(1),
    REMOVE(2),
    UPDATE(3);
    private final int id;

    Operation(int id) {
        this.id = id;
    }

    public static Operation getOperation(int id) {
        switch (id) {
            case 1:
                return ADD;
            case 2:
                return REMOVE;
            case 3:
                return UPDATE;
        }
        return null;
    }

}
