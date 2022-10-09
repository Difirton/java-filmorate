package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.util.List;
@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEventsByUserId(Long userId) {
        return eventRepository.findEventsByUserId(userId);
    }
}
