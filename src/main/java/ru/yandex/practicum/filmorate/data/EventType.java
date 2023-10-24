package ru.yandex.practicum.filmorate.data;

import lombok.Getter;

@Getter
public enum EventType {
    LIKE(1),
    REVIEW(2),
    FRIEND(3);
    private final int id;

    EventType(int id) {
        this.id = id;
    }

    public static EventType getEventType(int id) {
        switch (id) {
            case 1:
                return LIKE;
            case 2:
                return REVIEW;
            case 3:
                return FRIEND;
        }
        return null;
    }

}
