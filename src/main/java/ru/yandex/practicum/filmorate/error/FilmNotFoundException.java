package ru.yandex.practicum.filmorate.error;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(Long id) {
        super("Film id not found : " + id);
        log.warn("Film id not found : {}", id);
    }
}
