package ru.yandex.practicum.filmorate.error;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RatingMpaNotFoundException extends RuntimeException {

    public RatingMpaNotFoundException(Long id) {
        super("Genre id not found : " + id);
        log.warn("Genre id not found : {}", id);
    }
}
