package ru.yandex.practicum.filmorate.error.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(Long id) {
        super("Review id not found : " + id);
        log.error("Review id not found : {}", id);
    }
}
