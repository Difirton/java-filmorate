package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{id}/feed")
    public List<Event> getEvents(@PathVariable("id") Long id) {
        return eventService.getEventsByUserId(id);
    }
}
