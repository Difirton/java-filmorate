package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.constant.EventType;
import ru.yandex.practicum.filmorate.entity.constant.Operation;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event createEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        return eventRepository.save(Event.builder()
                .timestamp(System.currentTimeMillis())
                .userId(userId)
                .eventType(eventType)
                .operation(operation)
                .entityId(entityId)
                .build());
    }

    public List<Event> getEventsByUserId(Long userId) {
        return eventRepository.findEventsByUserId(userId);
    }
}
