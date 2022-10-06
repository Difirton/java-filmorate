package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.Genre;

import java.util.List;

public interface EventRepository extends StandardCRUDRepository<Event> {

    List<Event> findEventsByUserId(Long userId);

}
