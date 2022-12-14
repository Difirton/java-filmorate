package ru.yandex.practicum.filmorate.error.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long id) {
        super("User id not found : " + id);
        log.error("User id not found : {}", id);
    }
}
