package ru.yandex.practicum.filmorate.error;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(Long id) {
        super("Film id not found : " + id);
    }
}
