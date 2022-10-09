package ru.yandex.practicum.filmorate.error.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException(Long id) {
        super("Genre id not found : " + id);
        log.error("Genre id not found : {}", id);
    }
}
