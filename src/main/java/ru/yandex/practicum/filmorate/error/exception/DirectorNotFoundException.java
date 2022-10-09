package ru.yandex.practicum.filmorate.error.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirectorNotFoundException extends RuntimeException {
    public DirectorNotFoundException(Long id) {
        super("Director's id not found : " + id);
        log.error("Director's id not found : {}", id);
    }
}
