package ru.yandex.practicum.filmorate.error.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(Long id) {
        super("Film id not found : " + id);
        log.error("Film id not found : {}", id);
    }
}
