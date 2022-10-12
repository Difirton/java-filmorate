package ru.yandex.practicum.filmorate.error.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkNotFoundException extends RuntimeException {

    public MarkNotFoundException(Long userId, Long filmId) {
        super("Mark of user with id: " + userId + " of film with id: " + filmId + " not found");
        log.error("Mark of user with id: {} of film with id: {} not found", userId, filmId);
    }
}
