package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.data.EventType;
import ru.yandex.practicum.filmorate.data.Operation;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    @Qualifier("eventDbStorage")
    private final EventStorage eventStorage;
    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public List<Event> getFeed(Long userId) {
        userStorage.getUserById(userId);
        return eventStorage.getFeed(userId);
    }

    public void createEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        userStorage.getUserById(userId);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build();
        eventStorage.createEvent(event);
    }
}
